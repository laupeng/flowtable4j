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
        if(risk!=null) {
            sb.append(risk.toXML());
        }
        createNode(sb,"merchantreference",merchantreference);
        sb.append("</TxnDetails>\n");
        if(fraudOnlyTxn!=null){
            sb.append(fraudOnlyTxn.toXML());
        }
        sb.append("</Transaction>\n");
        sb.append("<Authentication>\n");
        createNode(sb,"client",client);
        createNode(sb,"password",password);
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
}
