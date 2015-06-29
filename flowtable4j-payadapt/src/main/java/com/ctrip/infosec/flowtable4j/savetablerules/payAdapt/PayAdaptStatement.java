package com.ctrip.infosec.flowtable4j.savetablerules.payAdapt;
import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.PayAdaptResultItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thyang on 2015/3/13 0013.
 */
public class PayAdaptStatement {
    final static Logger logger = LoggerFactory.getLogger(PayAdaptStatement.class);
    private Integer ruleID;
    private String ruleName;
    private Integer riskLevel;
    private String sceneType;
    private String paymentStatus;
    private int isCheckAccount;
    private String ruleDesc;
    private Integer orderType;
    private List<PayAdaptRuleTerm> flowRuleTerms;

    /**
     * 有些流量规则需要遍历List，比如用passengerList的每位乘客姓名进行比较
     */
    void setParentNode() {
        for (PayAdaptRuleTerm term : flowRuleTerms) {
            term.prefix = ExtraFieldManager.getParentNode(term.fieldName, orderType);
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

    public List<PayAdaptRuleTerm> getFlowRuleTerms() {
        return flowRuleTerms;
    }

    public void setFlowRuleTerms(List<PayAdaptRuleTerm> flowRuleTerms) {
        this.flowRuleTerms = flowRuleTerms;
    }

    public boolean check(FlowFact fact, List<PayAdaptResultItem> results) {
        boolean match = true;
        try {
            if (flowRuleTerms != null && flowRuleTerms.size() > 0) {
                match = true;
                for (PayAdaptRuleTerm term : flowRuleTerms) {
                    match = term.check(fact);
                    if (!match) {
                        break;
                    }
                }
            }
            if (match) {
                PayAdaptResultItem result = new PayAdaptResultItem();
                result.setPaymentStatus(this.paymentStatus);
                result.setResultLevel(this.riskLevel);
                result.setRuleRemark(this.ruleDesc);
                result.setSceneType(this.sceneType);
                result.setResultList(new ArrayList<String>());
                result.getResultList().add(this.paymentStatus);
                result.setResultType("F");
                results.add(result);
            }
        } catch (Throwable ex) {
            logger.error("PayAdapt check error", ex);
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
        if (obj instanceof PayAdaptStatement) {
            PayAdaptStatement rs = (PayAdaptStatement) obj;
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

    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int getIsCheckAccount() {
        return isCheckAccount;
    }

    public void setIsCheckAccount(int isCheckAccount) {
        this.isCheckAccount = isCheckAccount;
    }

    public String getRuleDesc() {
        return ruleDesc;
    }

    public void setRuleDesc(String ruleDesc) {
        this.ruleDesc = ruleDesc;
    }
}

