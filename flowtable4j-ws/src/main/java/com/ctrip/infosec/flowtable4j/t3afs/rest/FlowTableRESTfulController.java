/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ctrip.infosec.flowtable4j.t3afs.rest;

import com.ctrip.infosec.flowtable4j.t3afs.biz.CheckPaymentFacade;
import com.ctrip.infosec.flowtable4j.t3afs.biz.ResultCompare;
import com.ctrip.infosec.flowtable4j.t3afs.model.RiskResult;
import com.ctrip.infosec.flowtable4j.t3afs.model.VerifyData;
import com.ctrip.infosec.flowtable4j.t3afs.model.persist.PO;
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

    @Autowired
    ResultCompare compare;

    @RequestMapping(value = "/saveData4Offline")
    public
    @ResponseBody
    long saveData4Offline(@RequestBody PO po) {
        return checkPaymentService.saveData4Offline(po);
    }

    @RequestMapping(value = "/checkBWGList")
    public
    @ResponseBody
    RiskResult checkBWGList(@RequestBody com.ctrip.infosec.flowtable4j.t3afs.model.RequestBody checkEntity) {
        return checkPaymentService.checkBWGList(checkEntity);
    }

    @RequestMapping(value = "/verifyData")
    public
    @ResponseBody
    String verifyData(@RequestBody VerifyData checkEntity) {
        return compare.checkOrderData(checkEntity);
    }
}
