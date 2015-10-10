package com.ctrip.infosec.flowtable4j.master;

import com.ctrip.infosec.flowtable4j.Common.BaseNode;

import java.math.BigDecimal;

/**
 * Created by thyang on 2015-08-19.
 */
public class FraudOnlyTxn extends BaseNode {
     private MasterCard card;
     private BankResponse response;
     private String eci;
     private String security_code;
     private String amount;
     private String currency;
     private String tran_type="AVV";
     private String acquirer;
    @Override
    public String toXML()
    {
        StringBuilder sb= new StringBuilder();
        sb.append("<FraudOnlyTxn>\n");
        if(card!=null){
            sb.append(card.toXML());
        }
        if(response!=null){
            sb.append(response.toXML());
        }
        sb.append("<Secure>\n");
        createNode(sb,"eci",eci);
        createNode(sb,"security_code",security_code);
        sb.append("</Secure>\n");
        sb.append("<amount currency=\"" +currency +"\">" + amount +"</amount>\n");
        createNode(sb,"tran_type", tran_type);
        createNode(sb,"acquirer",acquirer);
        sb.append("</FraudOnlyTxn>\n");
        return sb.toString();
    }

    public void setCard(MasterCard card) {
        this.card = card;
    }

    public void setResponse(BankResponse response) {
        this.response = response;
    }

    public void setEci(String eci) {
        this.eci = eci;
    }

    public void setSecurity_code(String security_code) {
        this.security_code = security_code;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setTran_type(String tran_type) {
        this.tran_type = tran_type;
    }

    public void setAcquirer(String acquirer) {
        this.acquirer = acquirer;
    }
}
