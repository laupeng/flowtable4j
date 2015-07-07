package com.ctrip.infosec.flowtable4j.biz;

import com.ctrip.infosec.flowtable4j.biz.processor.*;
import com.ctrip.infosec.flowtable4j.flowdata.TableSaveRuleManager;
import com.ctrip.infosec.flowtable4j.model.*;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

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
    FlowtableProcessor flowtableProcessor;

    @Autowired
    TableSaveRuleManager tableSaveRuleManager;

    public CheckFact process(RequestBody request) {
        CheckFact fact = new CheckFact();
        final PO po = poConverter.convert(request);
        fact.setAccountFact(accountConverter.convert(po));
        fact.setBwFact(blackWhiteConverter.convert(po));
        fact.setFlowFact(flowConverter.convert(po));
        fact.setCheckTypes(new CheckType[]{CheckType.ACCOUNT, CheckType.BW, CheckType.FLOWRULE});
        final Long  reqId =save2DbService.saveDealInfo(MapX.getMap(po.getProductinfo(), "dealinfo"));
        fact.setReqId(reqId);
        fact.getFlowFact().setReqId(fact.getReqId());

        SimpleStaticThreadPool.getInstance().submit(new Runnable() {
                    @Override
                    public void run() {
                        poConverter.saveData4Next(po);
                        save2DbService.save(po, reqId);
                    }
                });

        return fact;
    }

    public RiskResult checkRisk(RequestBody requestBody) {
        //数据准备
        CheckFact fact = process(requestBody);

        //流量校验
        RiskResult result = flowtableProcessor.handle(fact);

        final FlowFact flowFact = fact.getFlowFact();
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
