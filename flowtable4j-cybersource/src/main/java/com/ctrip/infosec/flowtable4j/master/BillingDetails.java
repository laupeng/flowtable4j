package com.ctrip.infosec.flowtable4j.master;

import com.ctrip.infosec.flowtable4j.Common.BaseNode;

/**
 * Created by thyang on 2015-08-19.
 */
public class BillingDetails extends BaseNode {
    private String name;
    private String city;
    private String country;
    private String address_line1;
    private String address_line2;
    private String zip_code;
    private String state_province;

    @Override
    public String toXML()
    {
        StringBuilder sb= new StringBuilder();
        sb.append("<BillingDetails>\n");
        createNode(sb,"name",name);
        createNode(sb,"city",city);
        createNode(sb,"country",country);
        createNode(sb,"address_line1",address_line1);
        createNode(sb,"address_line2",address_line2);
        createNode(sb,"zip_code",zip_code);
        createNode(sb,"state_province",state_province);
        sb.append("</BillingDetails>\n");
        return sb.toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setAddress_line1(String address_line1) {
        this.address_line1 = address_line1;
    }

    public void setAddress_line2(String address_line2) {
        this.address_line2 = address_line2;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public void setState_province(String state_province) {
        this.state_province = state_province;
    }
}
