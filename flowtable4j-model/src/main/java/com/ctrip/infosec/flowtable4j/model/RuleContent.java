package com.ctrip.infosec.flowtable4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thyang on 2015-05-12.
 */
public class RuleContent {

    @JsonProperty("CheckType")
    private String checkType;

    @JsonProperty("SceneType")
    private String sceneType;

    @JsonProperty("CheckValue")
    private String checkValue;

    @JsonProperty("ExpiryDate")
    private String expiryDate;

    @JsonProperty("ResultLevel")
    private Integer resultLevel;

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

    public Integer getResultLevel() {
        return resultLevel;
    }

    public void setResultLevel(Integer resultLevel) {
        this.resultLevel = resultLevel;
    }
}
