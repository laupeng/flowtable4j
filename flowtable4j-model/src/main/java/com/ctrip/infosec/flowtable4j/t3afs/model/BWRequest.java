package com.ctrip.infosec.flowtable4j.t3afs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by zhangsx on 2015/5/15.
 */
public class BWRequest {

    @JsonProperty("Rules")
    private List<RuleContent> rules;

    public List<RuleContent> getRules() {
        return rules;
    }

    public void setRules(List<RuleContent> rules) {
        this.rules = rules;
    }
}
