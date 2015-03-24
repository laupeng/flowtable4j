package com.ctrip.infosec.flowtable4j.flowRule;

/**
 * Created by thyang on 2015/3/13 0013.
 */
public class BWResult {
    private Integer ruleID;
    private Integer riskLevel;
    private String remark;

    public Integer getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(Integer riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Integer getRuleID() {
        return ruleID;
    }

    public void setRuleID(Integer ruleID) {
        this.ruleID = ruleID;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String toString()
    {
        return String.format("RuleID:%d, RiskLevel:%d, Remark:%s",ruleID,riskLevel,remark);
    }
}
