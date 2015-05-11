package com.ctrip.infosec.flowtable4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by zhangsx on 2015/5/6.
 */
public class PayAdaptFact {
    //订单类型
    @JsonProperty(value = "OrderType")
    private int orderType;
    //订单号
    @JsonProperty(value = "OrderID")
    public long OrderID;
    //商户号
    @JsonProperty(value = "MerchantID")
    public String merchantID;
    //用户名称
    @JsonProperty(value = "UID")
    public String uid;
    //IP地址
    @JsonProperty(value = "IPAddr")
    public String ipAddr;
    //DeviceID
    @JsonProperty(value = "DID")
    public String did;

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


}
