package com.ctrip.infosec.flowtable4j.accountsecurity;

/**
 * Created by zhangsx on 2015/3/17.
 */
public class RedisStoreItem {
    private String SourceID;

    private int ResultLevel;

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

    private String EffectDate;

    private String ExpiryDate;

    private String RuleRemark;

    private String CreateDate;

    private String LastDate;

    private String SceneType;
}
