/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ctrip.infosec.flowtable4j.rest;

import com.ctrip.infosec.flowtable4j.model.CheckFact;
import com.ctrip.infosec.flowtable4j.model.PayAdaptFact;
import com.ctrip.infosec.flowtable4j.model.PayAdaptResult;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import com.ctrip.infosec.flowtable4j.translate.PreProcessor;
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
    Processor processor;

    @Autowired
    PreProcessor preProcessor;

    private static Logger logger = LoggerFactory.getLogger(FlowTableRESTfulController.class);

    @RequestMapping(value = "/checkRisk")
    public @ResponseBody
    RiskResult checkRisk(@RequestBody CheckFact checkEntity) {
        checkEntity.processOrderTypes();
        return processor.handle(checkEntity);
    }

    @RequestMapping(value = "/preProcess")
    public @ResponseBody
    CheckFact preProcess(@RequestBody Map data)
    {
        long now  = System.currentTimeMillis();
        CheckFact checkFact =preProcessor.execute(data);
        logger.info("总的执行时间是;"+(System.currentTimeMillis()-now));
        return checkFact;
    }

    @RequestMapping(value="/checkPayAdapt")
    public @ResponseBody
    PayAdaptResult checkPayAdapt(@RequestBody PayAdaptFact checkEntity){
        return processor.handle4PayAdapt(checkEntity);
    }

}
