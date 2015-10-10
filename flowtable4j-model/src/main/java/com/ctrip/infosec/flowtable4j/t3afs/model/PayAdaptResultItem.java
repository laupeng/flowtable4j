package com.ctrip.infosec.flowtable4j.t3afs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by zhangsx on 2015/5/6.
 */
public class PayAdaptResultItem {

    @JsonProperty(value = "ResultLevel")
    private int resultLevel;

    @JsonProperty(value = "ResultList")
    private List<String> resultList;

    @JsonProperty(value = "ResultType")
    private String resultType;

    @JsonProperty(value = "SceneType")
    private String sceneType;

    @JsonProperty(value = "RuleRemark")
    private String ruleRemark;

    @JsonIgnore()
    private String paymentStatus;

    public int getResultLevel() {
        return resultLevel;
    }

    public void setResultLevel(int resultLevel) {
        this.resultLevel = resultLevel;
    }

    public List<String> getResultList() {
        return resultList;
    }

    public void setResultList(List<String> resultList) {
        this.resultList = resultList;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public String getRuleRemark() {
        return ruleRemark;
    }

    public void setRuleRemark(String ruleRemark) {
        this.ruleRemark = ruleRemark;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
