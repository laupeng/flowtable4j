package com.ctrip.infosec.flowtable4j.t3afs.model;

/**
 * Created by zhangsx on 2015/3/25.
 */
public class CheckResultLog {
    private String ruleType;
    private Integer ruleID ;
    private String ruleName;
    private Integer riskLevel;
    private String ruleRemark;
    public CheckResultLog(){
        ruleID = 0;
        riskLevel =0;
        ruleType ="";
        ruleName ="";
        ruleRemark ="";
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public Integer getRuleID() {
        return ruleID;
    }

    public void setRuleID(Integer ruleID) {
        this.ruleID = ruleID;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Integer getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(Integer riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRuleRemark() {
        return ruleRemark;
    }

    public void setRuleRemark(String ruleRemark) {
        this.ruleRemark = ruleRemark;
    }

    public String toString()
    {
        return String.format("RuleID:%d, RiskLevel:%d, Remark:%s",ruleID,riskLevel,ruleRemark);
    }
}
