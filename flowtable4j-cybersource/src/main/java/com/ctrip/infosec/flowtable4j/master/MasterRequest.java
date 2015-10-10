package com.ctrip.infosec.flowtable4j.master;

import com.ctrip.infosec.flowtable4j.Common.BaseNode;

/**
 * Created by thyang on 2015-08-19.
 */
public class MasterRequest extends BaseNode {
    private String merchantreference;
    private String client;
    private String password;
    private FraudOnlyTxn fraudOnlyTxn;
    private Risk risk;
    private Long orderID;
    private Integer orderType;
    private String eventID;
    private Long reqID;
    public MasterRequest(){

    }

    public MasterRequest(String userName,String password){
        this.setClient(userName);
        this.setPassword(password);
    }

    @Override
    public String toXML()
    {
        StringBuilder sb= new StringBuilder();
        sb.append("<?xml version=\"1.0\"?>\n");
        sb.append("<Request>\n");
        sb.append("<Transaction>\n");
        sb.append("<TxnDetails>\n");
        if(getRisk() !=null) {
            sb.append(getRisk().toXML());
        }
        createNode(sb,"merchantreference", getMerchantreference());
        sb.append("</TxnDetails>\n");
        if(getFraudOnlyTxn() !=null){
            sb.append(getFraudOnlyTxn().toXML());
        }
        sb.append("</Transaction>\n");
        sb.append("<Authentication>\n");
        createNode(sb,"client", getClient());
        createNode(sb,"password", getPassword());
        sb.append("</Authentication>\n");
        sb.append("</Request>\n");
        return sb.toString();
    }

    public void setMerchantreference(String merchantreference) {
        this.merchantreference = merchantreference;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFraudOnlyTxn(FraudOnlyTxn fraudOnlyTxn) {
        this.fraudOnlyTxn = fraudOnlyTxn;
    }

    public void setRisk(Risk risk) {
        this.risk = risk;
    }

    public String getMerchantreference() {
        return merchantreference;
    }

    public String getClient() {
        return client;
    }

    public String getPassword() {
        return password;
    }

    public FraudOnlyTxn getFraudOnlyTxn() {
        return fraudOnlyTxn;
    }

    public Risk getRisk() {
        return risk;
    }

    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public Long getReqID() {
        return reqID;
    }

    public void setReqID(Long reqID) {
        this.reqID = reqID;
    }
}
