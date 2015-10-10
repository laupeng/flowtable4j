package com.ctrip.infosec.flowtable4j.master;

import com.ctrip.infosec.flowtable4j.Common.BaseNode;

/**
 * Created by thyang on 2015-08-19.
 */
public class RiskDetails extends BaseNode {
    private String  account_number;
    private String  email_address;
    private String  ip_address;
    private String  user_id;
    private String  usermachine_id;

    @Override
    public String toXML()
    {
        StringBuilder sb= new StringBuilder();
        sb.append("<RiskDetails>\n");
        createNode(sb,"account_number",account_number);
        createNode(sb,"email_address",email_address);
        createNode(sb,"ip_address",ip_address);
        createNode(sb,"user_id",user_id);
        createNode(sb,"usermachine_id", usermachine_id);
        sb.append("</RiskDetails>\n");
        return sb.toString();
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUsermachine_id(String usermachine_id) {
        this.usermachine_id = usermachine_id;
    }
}
