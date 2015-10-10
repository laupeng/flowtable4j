package com.ctrip.infosec.flowtable4j.t3afs.model;

import java.util.List;

/**
 * Created by zhangsx on 2015/5/6.
 */
public class PayAdaptResult {
    private int retCode;
    private List<PayAdaptResultItem> payAdaptResultItems;

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public List<PayAdaptResultItem> getPayAdaptResultItems() {
        return payAdaptResultItems;
    }

    public void setPayAdaptResultItems(List<PayAdaptResultItem> payAdaptResultItems) {
        this.payAdaptResultItems = payAdaptResultItems;
    }
}
