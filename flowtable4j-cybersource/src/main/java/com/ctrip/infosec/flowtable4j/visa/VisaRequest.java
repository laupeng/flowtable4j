package com.ctrip.infosec.flowtable4j.visa;
import com.ctrip.infosec.flowtable4j.Common.BaseNode;

import java.util.List;

/**
 * Created by thyang on 2015-07-24.
 */
public class VisaRequest extends BaseNode {
    private Long orderID;
    private Integer orderType;
    private String eventID;
    private Long reqID;
    private String merchantID;
    private String merchantReferenceCode;
    private BillTo billTo;
    private VisaCard card;
    private PurchaseTotals purchaseTotals;
    private String deviceFingerprintID;
    private DecisionManager decisionManager;
    private List<Item> items;
    private MerchantDefinedData merchantDefinedData;

    public String toXml(){
        StringBuilder sb=new StringBuilder();
        createNode(sb,"merchantID",merchantID);
        createNode(sb,"merchantReferenceCode",merchantReferenceCode);
        if(billTo!=null) {
            sb.append(billTo.toXML());
        }
        if(card!=null) {
           sb.append(card.toXML());
        }
        createNode(sb,"deviceFingerprintID",deviceFingerprintID);
        if(purchaseTotals!=null) {
            sb.append(purchaseTotals.toXML());
        }
        if(items!=null && items.size()>0){
            for(int i=0;i< items.size();i++) {
                sb.append(items.get(i).toXML(i));
            }
        }
        if(merchantDefinedData!=null) {
            sb.append(merchantDefinedData.toXML());
        }
        return sb.toString();
    }

    public PurchaseTotals getPurchaseTotals() {
        return purchaseTotals;
    }

    public void setPurchaseTotals(PurchaseTotals purchaseTotals) {
        this.purchaseTotals = purchaseTotals;
    }

    public String getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }

    public String getMerchantReferenceCode() {
        return merchantReferenceCode;
    }

    public void setMerchantReferenceCode(String merchantReferenceCode) {
        this.merchantReferenceCode = merchantReferenceCode;
    }

    public BillTo getBillTo() {
        return billTo;
    }

    public void setBillTo(BillTo billTo) {
        this.billTo = billTo;
    }

    public VisaCard getCard() {
        return card;
    }

    public void setCard(VisaCard card) {
        this.card = card;
    }

    public String getDeviceFingerprintID() {
        return deviceFingerprintID;
    }

    public void setDeviceFingerprintID(String deviceFingerprintID) {
        this.deviceFingerprintID = deviceFingerprintID;
    }

    public DecisionManager getDecisionManager() {
        return decisionManager;
    }

    public void setDecisionManager(DecisionManager decisionManager) {
        this.decisionManager = decisionManager;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public MerchantDefinedData getMerchantDefinedData() {
        return merchantDefinedData;
    }

    public void setMerchantDefinedData(MerchantDefinedData merchantDefinedData) {
        this.merchantDefinedData = merchantDefinedData;
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

    public Long getReqID() {
        return reqID;
    }

    public void setReqID(Long reqID) {
        this.reqID = reqID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }
}
