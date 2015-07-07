package com.ctrip.infosec.flowtable4j.flowrule;
import com.ctrip.infosec.flowtable4j.model.CheckType;
import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.CheckResultLog;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private List<FlowRuleTerm> flowRuleTerms;
    private Integer orderType;

    /**
     * 有些流量规则需要遍历List，比如用passengerList的每位乘客姓名进行比较
     */
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

    public List<FlowRuleTerm> getFlowRuleTerms() {
        return flowRuleTerms;
    }

    public void setFlowRuleTerms(List<FlowRuleTerm> flowRuleTerms) {
        this.flowRuleTerms = flowRuleTerms;
    }

    public boolean check(FlowFact fact, RiskResult results) {
        boolean match = false;
        try {
            if (flowRuleTerms != null && flowRuleTerms.size() > 0) {
                match = true;
                for (FlowRuleTerm term : flowRuleTerms) {
                    if(term instanceof CounterMatchRuleTerm){
                        long start  = System.currentTimeMillis();
                        match = term.check(fact);
                        long elapse = 0;// System.currentTimeMillis() - start;
                        if(elapse > 100){
                            // 取数超过100ms
                            String info = term.toString()+" ReqID:" + fact.getReqId();
                            CheckResultLog result = new CheckResultLog();
                            result.setRuleID(0);
                            result.setRiskLevel(0);
                            result.setRuleRemark(info);
                            result.setRuleName(String.valueOf(elapse));
                            result.setRuleType(CheckType.COUNTER.toString());

                            results.add(result);
                            logger.debug(info);
                        }
                    }
                    else {
                          match =term.check(fact);
                    }
                    if(!match){
                        break;
                    }
                }
            }
            if (match) {
                CheckResultLog result = new CheckResultLog();
                result.setRuleID(ruleID);
                result.setRiskLevel(riskLevel);
                result.setRuleRemark(remark);
                result.setRuleName(ruleName);
                result.setRuleType(CheckType.FLOWRULE.toString());
                results.add(result);
            }
        }
        catch (Throwable ex){
            logger.error("error",ex);
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
        return ruleID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FlowRuleStatement) {
            FlowRuleStatement rs = (FlowRuleStatement) obj;
            return this.ruleID.equals(rs.ruleID);
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

    /**
     * 不区分大小写
     * @param prepayType
     */
    public void setPrepayType(String prepayType) {
        this.prepayType = Strings.nullToEmpty(prepayType).toLowerCase();
    }
}

