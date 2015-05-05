package com.ctrip.infosec.flowtable4j.accountsecurity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by zhangsx on 2015/3/17.
 */
public class RedisStoreItem {
    @JsonProperty("D")
    private String SourceID;
    @JsonProperty("F")
    private int ResultLevel;
    @JsonProperty("G")
    private String EffectDate;
    @JsonProperty("H")
    private String ExpiryDate;
    private String RuleRemark;
    private String CreateDate;
    private String LastDate;
    private String SceneType;

    public String getSourceID() {
        return SourceID;
    }

    public void setSourceID(String sourceID) {
        SourceID = sourceID;
    }

    public int getResultLevel() {
        return ResultLevel;
    }

    public void setResultLevel(int resultLevel) {
        ResultLevel = resultLevel;
    }

    public String getEffectDate() {
        return EffectDate;
    }

    public void setEffectDate(String effectDate) {
        EffectDate = effectDate;
    }

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        ExpiryDate = expiryDate;
    }

    public String getRuleRemark() {
        return RuleRemark;
    }

    public void setRuleRemark(String ruleRemark) {
        RuleRemark = ruleRemark;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public String getLastDate() {
        return LastDate;
    }

    public void setLastDate(String lastDate) {
        LastDate = lastDate;
    }

    public String getSceneType() {
        return SceneType;
    }

    public void setSceneType(String sceneType) {
        SceneType = sceneType;
    }


}
