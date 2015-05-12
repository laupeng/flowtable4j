package com.ctrip.infosec.flowtable4j.accountsecurity;

/**
 * Created by thyang on 2015-05-12.
 */
public class RuleContent {

    private String checkType;
    private String sceneType;
    private String checkValue;
    private String expiryDate;
    private int resultLevel;

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public String getCheckValue() {
        return checkValue;
    }

    public void setCheckValue(String checkValue) {
        this.checkValue = checkValue;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getResultLevel() {
        return resultLevel;
    }

    public void setResultLevel(int resultLevel) {
        this.resultLevel = resultLevel;
    }
}
