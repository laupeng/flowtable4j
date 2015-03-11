/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ctrip.infosec.flowtable4j.rest;

import static com.ctrip.infosec.configs.utils.Utils.JSON;
import com.ctrip.infosec.flowtable4j.model.FlowTableCheckResult;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 配置读取
 *
 * @author zhengby
 */
@Controller
@RequestMapping(value = "/flowtable")
public class FlowTableRESTfulController {

    private static Logger logger = LoggerFactory.getLogger(FlowTableRESTfulController.class);

    @RequestMapping(value = "/check", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> check(@RequestBody String checkEntityTxt) {
        logger.info("REST: checkEntity=" + checkEntityTxt);
        FlowTableCheckResult result = new FlowTableCheckResult();
        try {
            Map<String, ?> checkEntity = JSON.parseObject(checkEntityTxt, Map.class);

            // TODO: 执行规则
        } catch (Throwable ex) {
            // TODO: 处理异常
            logger.error("invoke check exception.", ex);
        }
        logger.info("RESULT: " + JSON.toJSONString(result));
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
