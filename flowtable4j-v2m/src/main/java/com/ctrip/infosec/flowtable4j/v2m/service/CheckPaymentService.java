package com.ctrip.infosec.flowtable4j.v2m.service;

import com.ctrip.infosec.flowtable4j.biz.FlowtableProcessor;
import com.ctrip.infosec.flowtable4j.model.*;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.ctrip.infosec.flowtable4j.v2m.converter.AccountConverter;
import com.ctrip.infosec.flowtable4j.v2m.converter.BlackWhiteConverter;
import com.ctrip.infosec.flowtable4j.v2m.converter.FlowConverter;
import com.ctrip.infosec.flowtable4j.v2m.converter.POConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by thyang on 2015-06-12.
 */
@Component
public class CheckPaymentService {
    @Autowired
    POConverter poConverter;

    @Autowired
    BlackWhiteConverter blackWhiteConverter;

    @Autowired
    FlowConverter flowConverter;

    @Autowired
    AccountConverter accountConverter;

    @Autowired
    Save2DbService save2DbService;

    @Autowired
    FlowtableProcessor flowtableProcessor;

    public CheckFact process(RequestBody request){
        CheckFact fact =new CheckFact();
        PO po = poConverter.convert(request);
        fact.setAccountFact(accountConverter.convert(po));
        fact.setBwFact(blackWhiteConverter.convert(po));
        fact.setFlowFact(flowConverter.convert(po));
        fact.setCheckTypes(new CheckType[]{ CheckType.ACCOUNT, CheckType.BW, CheckType.FLOWRULE});
        fact.setReqId(save2DbService.saveDealInfo(po));
        return fact;
    }

    public ResponseBody checkRisk(RequestBody requestBody){
        CheckFact fact = process(requestBody);
        RiskResult result = flowtableProcessor.handle(fact);
        return  new ResponseBody();
    }
}
