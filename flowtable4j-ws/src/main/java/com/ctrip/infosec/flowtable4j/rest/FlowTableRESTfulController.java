/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ctrip.infosec.flowtable4j.rest;

import com.ctrip.flowtable4j.core.Processor;
import com.ctrip.infosec.flowtable4j.model.check.CheckEntity;
import com.ctrip.infosec.flowtable4j.model.check.RiskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 配置读取
 *
 * @author zhengby
 */
@Controller
public class FlowTableRESTfulController {

    @Autowired
    Processor processor;
    private static Logger logger = LoggerFactory.getLogger(FlowTableRESTfulController.class);
    @RequestMapping(value = "/checkRisk")
    public @ResponseBody
    List<RiskResult> checkRisk(@RequestBody CheckEntity checkEntity) {
        return processor.handle(checkEntity);
    }
//    @RequestMapping(value = "/check", method = RequestMethod.POST)
//    @ResponseBody
//    public ResponseEntity<?> check(@RequestBody String checkEntityTxt) {
//        logger.info("REST: checkEntity=" + checkEntityTxt);
//       // FlowTableCheckResult result = new FlowTableCheckResult();
//        try {
//            Map<String, ?> checkEntity = JSON.parseObject(checkEntityTxt, Map.class);
//
//            // TODO: 执行规则
//        } catch (Throwable ex) {
//            // TODO: 处理异常
//            logger.error("invoke check exception.", ex);
//        }
//       // logger.info("RESULT: " + JSON.toJSONString(result));
//       // return new ResponseEntity(result, HttpStatus.OK);
//    }
}
