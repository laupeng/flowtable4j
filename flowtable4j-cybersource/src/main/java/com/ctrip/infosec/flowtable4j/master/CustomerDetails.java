package com.ctrip.infosec.flowtable4j.master;

import com.ctrip.infosec.flowtable4j.Common.BaseNode;

/**
 * Created by thyang on 2015-08-19.
 */
public class CustomerDetails extends BaseNode {
    private String transaction_type;
    private String payment_method;
    private RiskDetails riskDetails;
    private PersonalDetails personalDetails;
    private BillingDetails billingDetails;
    private Journey journey;

    @Override
    public String toXML()
    {
        StringBuilder sb= new StringBuilder();
        sb.append("<CustomerDetails>\n");
        if(riskDetails!=null){
            sb.append(riskDetails.toXML());
        }
        if(personalDetails!=null){
            sb.append(personalDetails.toXML());
        }
        sb.append("<PaymentDetails>\n");
        createNode(sb,"transaction_type",transaction_type);
        createNode(sb,"payment_method",payment_method);
        sb.append("<WalletDetails/>\n");
        sb.append("</PaymentDetails>");
        if(billingDetails!=null){
            sb.append("<OrderDetails>\n");
            sb.append(billingDetails.toXML());
            sb.append("</OrderDetails>\n");
        }
        sb.append("<AirlineDetails>\n");
        if(journey!=null){
            sb.append(journey.toXML());
        }
        sb.append("</AirlineDetails>\n");
        sb.append("</CustomerDetails>\n");
        return sb.toString();
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public void setRiskDetails(RiskDetails riskDetails) {
        this.riskDetails = riskDetails;
    }

    public void setPersonalDetails(PersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
    }

    public void setBillingDetails(BillingDetails billingDetails) {
        this.billingDetails = billingDetails;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }
}
