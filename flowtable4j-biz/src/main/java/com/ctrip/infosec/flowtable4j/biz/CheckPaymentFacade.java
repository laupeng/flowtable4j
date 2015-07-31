package com.ctrip.infosec.flowtable4j.biz;

import com.ctrip.infosec.flowtable4j.biz.processor.*;
import com.ctrip.infosec.flowtable4j.biz.converter.*;
import com.ctrip.infosec.flowtable4j.flowdata.TableSaveRuleManager;
import com.ctrip.infosec.flowtable4j.model.*;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by thyang on 2015-06-12.
 */
@Component
public class CheckPaymentFacade {
    @Autowired
    POConverter poConverter;

    @Autowired
    BlackWhiteConverter blackWhiteConverter;

    @Autowired
    FlowConverter flowConverter;

    @Autowired
    AccountConverter accountConverter;

    @Autowired
    Save2DbProcessor save2DbService;

    @Autowired
    PayAdaptInner payAdaptInner;

    @Autowired
    FlowtableProcessor flowtableProcessor;

    @Autowired
    TableSaveRuleManager tableSaveRuleManager;

    @Autowired
    RiskLevelDataConverter riskLevelDataConverter;

    private static Logger logger = LoggerFactory.getLogger(CheckPaymentFacade.class);

    public CheckType[] getCheckType(PO po){
        return new CheckType[]{CheckType.ACCOUNT, CheckType.BW, CheckType.FLOWRULE};
    }

    public long saveData4Offline(RequestBody requestBody)
    {
        final PO po = poConverter.convert(requestBody);

        final Long reqId = save2DbService.saveDealInfo(MapX.getMap(po.getProductinfo(), "dealinfo"));
        po.setReqid(reqId);

        SimpleStaticThreadPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                save2DbService.save(po, reqId);
            }
        });
        return reqId;
    }

    /**
     * 快速应用支付适配，分落8张表
     * @param requestBody
     * @return
     */
    public RiskResult checkRisk2(RequestBody requestBody) {
        //数据准备
        long start1 = System.nanoTime();
        final PO po = poConverter.convert(requestBody);
        if(po.getReqid()==0) { //现阶段，就Coupons已带ReqId
            po.setReqid(save2DbService.saveDealInfo(MapX.getMap(po.getProductinfo(), "dealinfo")));
        }
        else
        {
            return new RiskResult();
        }
        final Long reqId= po.getReqid();
        logger.warn("Construct PO elapse:" + (System.nanoTime() - start1) / 1000000L);
        SimpleStaticThreadPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                poConverter.saveData4Next(po);
                save2DbService.save(po, reqId);
            }
        });
        return new RiskResult();
    }


    public RiskResult checkRisk(RequestBody requestBody) {
        //数据准备
        long start1 = System.nanoTime();
        CheckFact fact = new CheckFact();
        final PO po = poConverter.convert(requestBody);
        fact.setAccountFact(accountConverter.convert(po));
        fact.setBwFact(blackWhiteConverter.convert(po));
        fact.setFlowFact(flowConverter.convert(po));

        fact.setCheckTypes(getCheckType(po));

        final Long reqId = save2DbService.saveDealInfo(MapX.getMap(po.getProductinfo(), "dealinfo"));
        po.setReqid(reqId);

        fact.setReqId(reqId);
        fact.getFlowFact().setReqId(fact.getReqId());

        logger.warn("Construct PO elapse:" + (System.nanoTime() - start1) / 1000000L);

        //TODO 最终决定哪些业务需要调用支付适配黑白名单、账户风控黑白名单
        SimpleStaticThreadPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                payAdaptInner.handle4PayAdapt(po);
            }
        });

        start1 = System.nanoTime();
        //流量校验
        RiskResult result = flowtableProcessor.handle(fact);
        logger.warn("Check Risk elapse:" + (System.nanoTime() - start1) / 1000000L);
        final FlowFact flowFact = fact.getFlowFact();
        flowFact.getContent().put("originalrisklevel", result.getOriginRiskLevel());

        //TODO 需要根据不同的订单类型做特殊处理
        riskLevelDataConverter.convertRiskLevelData(po,result,reqId);

        //TODO 只有TransFlag！=33的才需要抛送金融
        if(! MapX.getString(po.getProductinfo(),new String[]{"riskleveldata","transflag"},"").equals("33")) {
            riskLevelDataConverter.postToEasyPay(po);
        }

      SimpleStaticThreadPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                poConverter.saveData4Next(po);
                save2DbService.save(po, reqId);
                riskLevelDataConverter.saveLicenseOrder(po.getReqid(),-1,null,po.getRisklevel(),po.getOrdertype());
            }
        });

      SimpleStaticThreadPool.getInstance().submit(new Runnable() {
           @Override
            public void run() {
                //分流表数据落地
                tableSaveRuleManager.checkAndSave(flowFact);
          }
        });
        return result;
    }



}
