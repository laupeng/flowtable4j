/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ctrip.infosec.flowtable4j.rest;

import com.ctrip.infosec.flowtable4j.biz.CheckPaymentFacade;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
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
    CheckPaymentFacade checkPaymentService;

    @RequestMapping(value = "/saveData4Offline")
    public
    @ResponseBody
    long saveData4Offline(@RequestBody com.ctrip.infosec.flowtable4j.model.persist.PO po) {
        return checkPaymentService.saveData4Offline(po);
    }

    @RequestMapping(value = "/checkBWGList")
    public
    @ResponseBody
    RiskResult checkBWGList(@RequestBody com.ctrip.infosec.flowtable4j.model.RequestBody checkEntity) {
        return checkPaymentService.checkBWGList(checkEntity);
    }
}
