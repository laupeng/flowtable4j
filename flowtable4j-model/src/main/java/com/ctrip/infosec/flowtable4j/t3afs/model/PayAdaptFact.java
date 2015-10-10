package com.ctrip.infosec.flowtable4j.t3afs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by zhangsx on 2015/5/6.
 */
public class PayAdaptFact {
    private int orderType;

    public long OrderID;

    public String merchantID;

    public String uid;

    public String ipAddr;

    public String did;

    private int checkType;

    private Map<String,Object> blackList;

    private Map<String,Object> flowList;

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public long getOrderID() {
        return OrderID;
    }

    public void setOrderID(long orderID) {
        OrderID = orderID;
    }

    public String getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }


    public int getCheckType() {
        return checkType;
    }

    public void setCheckType(int checkType) {
        this.checkType = checkType;
    }

    public Map<String, Object> getBlackList() {
        return blackList;
    }

    public void setBlackList(Map<String, Object> blackList) {
        this.blackList = blackList;
    }

    public Map<String, Object> getFlowList() {
        return flowList;
    }

    public void setFlowList(Map<String, Object> flowList) {
        this.flowList = flowList;
    }
}
