package com.ctrip.infosec.flowtable4j.t3afs.visa;
import com.ctrip.infosec.flowtable4j.t3afs.Common.BaseNode;
public class PurchaseTotals extends BaseNode {
    private String currency;
    private String grandTotalAmount;

    @Override
    public String toXML(){
        StringBuilder sb=new StringBuilder();
        sb.append("<purchaseTotals>\n");
        createNode(sb,"currency",currency);
        createNode(sb,"grandTotalAmount",grandTotalAmount);
        sb.append("</purchaseTotals>\n");
        return sb.toString();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getGrandTotalAmount() {
        return grandTotalAmount;
    }

    public void setGrandTotalAmount(String grandTotalAmount) {
        this.grandTotalAmount = grandTotalAmount;
    }
}
