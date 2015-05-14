package com.ctrip.infosec.flowtable4j.rest;

import com.ctrip.infosec.flowtable4j.accountsecurity.RuleContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangsx on 2015/5/14.
 */
@Controller
public class BWGRuleController {

    @Autowired
    Processor processor;

    @RequestMapping("/setBWGRule")
    public void setBWGRule(@RequestBody List<RuleContent> rules){
        processor.setBWGRule(rules);
    }

    @RequestMapping("/removeBWGRule")
    public void removeBWGRule(@RequestBody List<RuleContent> rules){
        processor.removeBWGRule(rules);
    }
}
