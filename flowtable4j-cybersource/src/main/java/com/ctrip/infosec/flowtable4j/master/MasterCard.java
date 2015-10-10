package com.ctrip.infosec.flowtable4j.master;

import com.ctrip.infosec.flowtable4j.Common.BaseNode;

/**
 * Created by thyang on 2015-08-19.
 */
public class MasterCard extends BaseNode {
    private String pan;
    private String bin;
    private String last_4_digits;
    private String pan_sha1;
    private String expirydate;
    private String issuenumber_or_startdate;

    @Override
    public String toXML()
    {
        StringBuilder sb= new StringBuilder();
        sb.append("<Card>\n");
        createNode(sb,"pan",pan);
        createNode(sb,"bin",bin);
        createNode(sb,"last_4_digits",last_4_digits);
        createNode(sb,"pan_sha1",pan_sha1);
        createNode(sb,"expirydate",expirydate);
        createNode(sb,"issuenumber_or_startdate",issuenumber_or_startdate);
        sb.append("</Card>\n");
        return sb.toString();
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public void setLast_4_digits(String last_4_digits) {
        this.last_4_digits = last_4_digits;
    }

    public void setPan_sha1(String pan_sha1) {
        this.pan_sha1 = pan_sha1;
    }

    public void setExpirydate(String expirydate) {
        this.expirydate = expirydate;
    }

    public void setIssuenumber_or_startdate(String issuenumber_or_startdate) {
        this.issuenumber_or_startdate = issuenumber_or_startdate;
    }
}
