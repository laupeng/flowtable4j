package com.ctrip.infosec.flowtable4j.accountsecurity;

/**
 * Created by thyang on 2015-05-12.
 */
public class RuleStore {
    private String s;

    private String v;

    private String e;

    /**
     * SceneType
     */
    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    /**
     * Result Level
     */
    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    /**
     * ExipiryDate
     */
    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }
}
