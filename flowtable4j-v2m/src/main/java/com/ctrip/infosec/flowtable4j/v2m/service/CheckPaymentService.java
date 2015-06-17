package com.ctrip.infosec.flowtable4j.v2m.service;

import com.ctrip.infosec.flowtable4j.model.CheckFact;
import com.ctrip.infosec.flowtable4j.model.RequestBody;
import com.ctrip.infosec.flowtable4j.model.ResponseBody;
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
    public CheckFact process(RequestBody request){
        CheckFact fact =null;
        PO po = null;
        //poConverter
        //blackWhiteConverter
        //flowConverter
        //accountConverter
        return fact;
    }

    public ResponseBody checkRisk(RequestBody requestBody){
        return  new ResponseBody();
    }
}
