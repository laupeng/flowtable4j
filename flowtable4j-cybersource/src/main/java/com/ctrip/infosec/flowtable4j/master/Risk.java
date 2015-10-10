package com.ctrip.infosec.flowtable4j.master;

import com.ctrip.infosec.flowtable4j.Common.BaseNode;

/**
 * Created by thyang on 2015-08-19.
 */
public class Risk extends BaseNode {
    private String merchant_location;
    private String channel="M";
    private CustomerDetails customerDetails;

    @Override
    public String toXML()
    {
        StringBuilder sb= new StringBuilder();
        sb.append("<Risk>\n");
        sb.append("<Action service=\"2\">\n");
        sb.append("<MerchantConfiguration>\n");
        createNode(sb,"merchant_location",merchant_location);
        createNode(sb,"channel",channel);
        sb.append("</MerchantConfiguration>\n");
        if(customerDetails!=null){
            sb.append(customerDetails.toXML());
        }
        sb.append("</Action>\n");
        sb.append("</Risk>\n");
        return sb.toString();
    }

    public void setMerchant_location(String merchant_location) {
        this.merchant_location = merchant_location;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setCustomerDetails(CustomerDetails customerDetails) {
        this.customerDetails = customerDetails;
    }
}
