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

/**
 * Created by thyang on 2015-06-12.
 */
public class VerifyService {
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
}
