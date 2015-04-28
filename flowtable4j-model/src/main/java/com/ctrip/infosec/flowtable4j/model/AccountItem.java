package com.ctrip.infosec.flowtable4j.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by zhangsx on 2015/3/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountItem {
    private String checkType;

    private String sceneType;

    private String checkValue;

    public AccountItem(){

    }

    public AccountItem(String checkType, String sceneType, String checkValue){
        this.checkType=checkType;
        this.checkValue=checkValue;
        this.sceneType=sceneType;
    }
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
}
