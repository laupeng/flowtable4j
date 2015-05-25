package com.ctrip.infosec.flowtable4j.rest;

import com.ctrip.infosec.flowtable4j.biz.BWGProcessor;
import com.ctrip.infosec.flowtable4j.model.BWRequest;
import com.ctrip.infosec.flowtable4j.model.RuleContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangsx on 2015/5/14.
 */
@Controller
public class BWGRuleController {

    @Autowired
    BWGProcessor processor;

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
    @RequestMapping("/loadBWGRule")
    public @ResponseBody String loadBWGRule(long reqId){
          processor.loadExistBWGRule(reqId);
          return "ok";
    }

}
