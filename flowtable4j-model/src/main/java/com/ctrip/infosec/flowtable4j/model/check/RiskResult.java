package com.ctrip.infosec.flowtable4j.model.check;

/**
 * Created by zhangsx on 2015/3/25.
 */
public class RiskResult {
    private String ruleType;
    private int ruleID ;
    private String ruleName;
    private int riskLevel;
    private String ruleRemark;

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public int getRuleID() {
        return ruleID;
    }

    public void setRuleID(int ruleID) {
        this.ruleID = ruleID;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public int getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(int riskLevel) {
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
