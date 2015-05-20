package com.ctrip.infosec.flowtable4j.rest;

import com.ctrip.infosec.flowtable4j.model.BWRequest;
import com.ctrip.infosec.flowtable4j.model.RuleContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by zhangsx on 2015/5/14.
 */
@Controller
public class BWGRuleController {

    @Autowired
    Processor processor;

    @RequestMapping("/setBWGRule")
    public @ResponseBody String setBWGRule(@RequestBody BWRequest request){
        if(request!=null && request.getRules()!=null){
            processor.setBWGRule(request.getRules());
        }
        return "ok";
    }

    @RequestMapping("/removeBWGRule")
    public @ResponseBody String removeBWGRule(@RequestBody BWRequest request){
        if(request!=null && request.getRules()!=null) {
            processor.removeBWGRule(request.getRules());
        }
        return "ok";
    }
}
