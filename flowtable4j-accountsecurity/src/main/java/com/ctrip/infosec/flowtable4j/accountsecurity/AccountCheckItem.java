package com.ctrip.infosec.flowtable4j.accountsecurity;

/**
 * Created by zhangsx on 2015/3/17.
 */
public class AccountCheckItem {
    private String checkType;

    private String sceneType;

    private String checkValue;

    public AccountCheckItem(String checkType,String sceneType,String checkValue){

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
