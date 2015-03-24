package com.ctrip.infosec.flowtable4j.accountsecurity;

/**
 * Created by zhangsx on 2015/3/17.
 */
public class KeyValue {
    private String sceneType ;
    private String ruleKey ;

    public String getRuleKey() {
        return ruleKey;
    }

    public void setRuleKey(String ruleKey) {
        this.ruleKey = ruleKey;
    }

    public String getSceneType() {

        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }
}
