package com.ctrip.infosec.flowtable4j.flowlist;
import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by thyang on 2015/3/13 0013.
 */
public class FlowRuleStatement {
    final static Logger logger = LoggerFactory.getLogger(FlowRuleStatement.class);
    private Integer ruleID;
    private String ruleName;
    private String prepayType;
    private Integer riskLevel;
    private String remark;
    private Date effectDate;
    private Date expireDate;
    private List<FlowRuleTerm> flowRuleTerms;
    private Integer orderType;

    void setParentNode()
    {
        for(FlowRuleTerm term:flowRuleTerms ){
            term.prefix = ExtraFieldManager.getParentNode(term.fieldName,orderType);
        }
    }

    public Integer getRuleID() {
        return ruleID;
    }

    public void setRuleID(Integer ruleID) {
        this.ruleID = ruleID;
    }

    public Integer getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(Integer riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getEffectDate() {
        return effectDate;
    }

    public void setEffectDate(Date effectDate) {
        this.effectDate = effectDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public List<FlowRuleTerm> getFlowRuleTerms() {
        return flowRuleTerms;
    }

    public void setFlowRuleTerms(List<FlowRuleTerm> flowRuleTerms) {
        this.flowRuleTerms = flowRuleTerms;
    }

    public boolean check(FlowFact fact, List<RiskResult> results) {
        boolean match = false;
        try {
            if (flowRuleTerms != null && flowRuleTerms.size() > 0) {
                match = true;
                for (FlowRuleTerm term : flowRuleTerms) {
                    if (!term.check(fact)) {
                        match = false;
                        break;
                    }
                }
            }
            if (match) {
                RiskResult result = new RiskResult();
                result.setRuleID(ruleID);
                result.setRiskLevel(riskLevel);
                result.setRuleRemark(remark);
                results.add(result);
            }
        }
        catch (Throwable ex){
            logger.warn(ex.getMessage());
        }
        return match;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FlowRuleStatement) {
            FlowRuleStatement rs = (FlowRuleStatement) obj;
            return this.ruleID.equals(rs.getRuleID());
        }
        return super.equals(obj);
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getPrepayType() {
        return prepayType;
    }

    public void setPrepayType(String prepayType) {
        this.prepayType = prepayType;
    }
}

