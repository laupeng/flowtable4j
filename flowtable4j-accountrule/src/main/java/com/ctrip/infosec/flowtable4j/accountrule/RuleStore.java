package com.ctrip.infosec.flowtable4j.accountrule;

/**
 * Created by thyang on 2015-05-12.
 */
public class RuleStore {
    private String s;

    private int r;

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
    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
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
