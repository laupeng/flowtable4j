/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ctrip.infosec.flowtable4j.flowdispatch.rest;

import com.ctrip.infosec.flowtable4j.biz.CheckPaymentFacade;
import com.ctrip.infosec.flowtable4j.biz.PayAdaptFacade;
import com.ctrip.infosec.flowtable4j.model.PayAdaptFact;
import com.ctrip.infosec.flowtable4j.model.PayAdaptResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    PayAdaptResult checkPayAdapt(@RequestBody PayAdaptFact checkEntity) {
        return payAdaptProcessor.handle4PayAdapt(checkEntity);
    }

    @RequestMapping(value = "/checkPayment")
    public
    @ResponseBody
    com.ctrip.infosec.flowtable4j.model.ResponseBody checkPayAdapt(@RequestBody com.ctrip.infosec.flowtable4j.model.RequestBody checkEntity) {
        return checkPaymentService.checkRisk(checkEntity);
    }


}
