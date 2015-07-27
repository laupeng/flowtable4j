/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ctrip.infosec.flowtable4j.rest;

import com.ctrip.infosec.flowtable4j.biz.CheckPaymentFacade;
import com.ctrip.infosec.flowtable4j.biz.PayAdaptFacade;
import com.ctrip.infosec.flowtable4j.model.MapX;
import com.ctrip.infosec.flowtable4j.model.PayAdaptFact;
import com.ctrip.infosec.flowtable4j.model.PayAdaptResult;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 配置读取
 *
 * @author zhengby
 */
@Controller
public class FlowTableRESTfulController {

    @Autowired
    PayAdaptFacade payAdaptProcessor;

    @Autowired
    CheckPaymentFacade checkPaymentService;

    private static Logger logger = LoggerFactory.getLogger(FlowTableRESTfulController.class);

    @RequestMapping(value = "/checkPayAdapt")
    public
    @ResponseBody
    PayAdaptResult checkPayAdapt(@RequestBody com.ctrip.infosec.flowtable4j.model.RequestBody checkEntity) {
        long start= System.nanoTime();
        PayAdaptFact fact = new PayAdaptFact();
        Map<String,Object> eventBody = checkEntity.getEventBody();
        fact.setDid(MapX.getString(eventBody,"did"));
        fact.setIpAddr(MapX.getString(eventBody,"ipaddr"));
        fact.setOrderID(Long.parseLong(MapX.getString(eventBody,"orderid")));
        fact.setOrderType(Integer.parseInt(MapX.getString(eventBody,"ordertype")));
        fact.setMerchantID(MapX.getString(eventBody,"merchantid"));
        fact.setUid(MapX.getString(eventBody,"uid"));
        long finish = System.nanoTime();
        logger.debug("CheckPayAdpat total elapse " + (finish-start)/1000000L +" ms");
        return payAdaptProcessor.handle4PayAdapt(fact);
    }

    @RequestMapping(value = "/checkPayment")
    public
    @ResponseBody
    RiskResult checkPayment(@RequestBody com.ctrip.infosec.flowtable4j.model.RequestBody checkEntity) {
        long start= System.nanoTime();
        RiskResult result = checkPaymentService.checkRisk2(checkEntity);
        long finish = System.nanoTime();
        logger.debug("CheckPayment total elapse " + (finish-start)/1000000L +" ms");
        return  result;
    }

    @RequestMapping(value = "/saveData4Offline")
    public
    @ResponseBody
    long saveData4Offline(@RequestBody com.ctrip.infosec.flowtable4j.model.RequestBody checkEntity) {
        return checkPaymentService.saveData4Offline(checkEntity);
    }

}
