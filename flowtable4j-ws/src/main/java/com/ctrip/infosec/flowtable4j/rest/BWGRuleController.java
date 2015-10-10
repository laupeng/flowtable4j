package com.ctrip.infosec.flowtable4j.rest;

import com.ctrip.infosec.flowtable4j.t3afs.biz.processor.AccountBWGProcessor;
import com.ctrip.infosec.flowtable4j.t3afs.model.AccountFact;
import com.ctrip.infosec.flowtable4j.t3afs.model.AccountResult;
import com.ctrip.infosec.flowtable4j.t3afs.model.BWRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by zhangsx on 2015/5/14.
 */
@Controller
public class BWGRuleController {

    @Autowired
    AccountBWGProcessor processor;

    @RequestMapping("/setBWGRule")
    public
    @ResponseBody
    String setBWGRule(@RequestBody BWRequest request) {
        if (request != null && request.getRules() != null) {
           return processor.setBWGRule(request.getRules());
        }
        return "OK";
    }

    @RequestMapping("/checkBWGRule")
    public
    @ResponseBody
    AccountResult checkBWGRule(@RequestBody AccountFact request) {
        AccountResult response = new AccountResult();
        response.setStatus("OK");
        if (request != null && request.getCheckItems() != null) {
            processor.checkBWGRule(request, response);
        }
        return response;
    }

    @RequestMapping("/removeBWGRule")
    public
    @ResponseBody
    String removeBWGRule(@RequestBody BWRequest request) {
        if (request != null && request.getRules() != null) {
          return processor.removeBWGRule(request.getRules());
        }
        return "OK";
    }

    @RequestMapping("/hello")
    public @ResponseBody String hello(){
        return "hello";
    }
}
