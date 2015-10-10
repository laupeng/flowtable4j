package com.ctrip.infosec.flowtable4j.rest;

import com.ctrip.infosec.flowtable4j.dal.CardRiskDbService;
import com.ctrip.infosec.flowtable4j.master.MasterClient;
import com.ctrip.infosec.flowtable4j.master.MasterRequest;
import com.ctrip.infosec.flowtable4j.master.MasterResponse;
import com.ctrip.infosec.flowtable4j.visa.VisaClient;
import com.ctrip.infosec.flowtable4j.visa.VisaRequest;
import com.ctrip.infosec.flowtable4j.visa.VisaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangsx on 2015/5/14.
 */
@Controller
public class ThirdAFSController {

    @Autowired
    VisaClient visaClient;

    @Autowired
    MasterClient masterClient;

    @Autowired
    CardRiskDbService cardRiskDbService;

    @RequestMapping("/checkVisa")
    public
    @ResponseBody
    VisaResponse checkVisa(@RequestBody VisaRequest request) {
        VisaResponse response = visaClient.requestVisa(request);
        Map<String,Object> values = new HashMap<String, Object>();
        values.put("orderid", request.getOrderID());
        values.put("ordertype",request.getOrderType());
        values.put("servicetype","VISA");
        values.put("servicestatus",response.getServiceStatus());
        values.put("serviceerror",response.getServiceError());
        values.put("decision",response.getDecision());
        values.put("decisionmessage","");
        values.put("afsresult",response.getAfsReply().getAfsResult());
        values.put("reasoncode",response.getReasonCode());
        values.put("additionalmessage","");
        values.put("responsestr",response.getResponseStr());
        values.put("eventid",request.getEventID());
        cardRiskDbService.saveThirdAFSResult(values);
        return response;
    }

    @RequestMapping("/checkMaster")
    public
    @ResponseBody
    MasterResponse checkMaster(@RequestBody MasterRequest request) {
        MasterResponse response = masterClient.requestMaster(request);
        Map<String,Object> values = new HashMap<String, Object>();
        values.put("orderid", request.getOrderID());
        values.put("ordertype",request.getOrderType());
        values.put("servicetype","MASTER");
        values.put("servicestatus",response.getServiceStatus());
        values.put("serviceerror",response.getServiceError());
        values.put("decision",response.getResponse_code());
        values.put("decisionmessage",response.getResponse_message());
        values.put("afsresult",response.getCpi_value());
        values.put("reasoncode",response.getReason());
        values.put("additionalmessage", "");
        values.put("responsestr",response.getResponseStr());
        values.put("eventid",request.getEventID());
        cardRiskDbService.saveThirdAFSResult(values);
        return  response;
    }
}
