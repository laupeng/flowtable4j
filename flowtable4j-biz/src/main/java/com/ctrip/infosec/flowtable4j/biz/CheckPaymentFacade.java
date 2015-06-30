package com.ctrip.infosec.flowtable4j.biz;
import com.ctrip.infosec.flowtable4j.biz.processor.*;
import com.ctrip.infosec.flowtable4j.flowdispatch.TableSaveRuleManager;
import com.ctrip.infosec.flowtable4j.model.*;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
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
    FlowtableProcessor flowtableProcessor;

    @Autowired
    TableSaveRuleManager tableSaveRuleManager;
    
    public CheckFact process(RequestBody request){
        CheckFact fact =new CheckFact();
        PO po = poConverter.convert(request);
        fact.setAccountFact(accountConverter.convert(po));
        fact.setBwFact(blackWhiteConverter.convert(po));
        fact.setFlowFact(flowConverter.convert(po));
        fact.setCheckTypes(new CheckType[]{ CheckType.ACCOUNT, CheckType.BW, CheckType.FLOWRULE});
        fact.setReqId(save2DbService.saveDealInfo(MapX.getMap(po.getProductinfo(),"dealinfo")));
        save2DbService.save(po,fact.getReqId());
        return fact;
    }

    public ResponseBody checkRisk(RequestBody requestBody){
        CheckFact fact = process(requestBody);
        RiskResult result = flowtableProcessor.handle(fact);
        tableSaveRuleManager.checkAndSave(fact.getFlowFact());
        return  new ResponseBody();
    }
}
