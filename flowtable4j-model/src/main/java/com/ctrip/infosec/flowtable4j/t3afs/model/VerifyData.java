package com.ctrip.infosec.flowtable4j.t3afs.model;

/**
 * Created by thyang on 2015-09-14.
 */
public class VerifyData {
    private Long startReq;
    private Long lastReq;
    private Integer orderType;

    public Long getStartReq() {
        return startReq;
    }

    public void setStartReq(Long startReq) {
        this.startReq = startReq;
    }

    public Long getLastReq() {
        return lastReq;
    }

    public void setLastReq(Long lastReq) {
        this.lastReq = lastReq;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }
}
