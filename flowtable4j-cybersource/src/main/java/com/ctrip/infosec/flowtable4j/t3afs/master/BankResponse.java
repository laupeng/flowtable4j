package com.ctrip.infosec.flowtable4j.t3afs.master;

/**
 * Created by thyang on 2015-08-19.
 */

import com.ctrip.infosec.flowtable4j.t3afs.Common.BaseNode;

/**
 * Post Payment, Response of Bank
 */
public class BankResponse extends BaseNode {
    private String auth_code;
    private String avs_address_response;
    private String avs_postcode_response;
    private String bank_response_code;
    private String bank_response_message;
    private String cv2_response;

    @Override
    public String toXML()
    {
        StringBuilder sb= new StringBuilder();
        sb.append("<Response>\n");
        createNode(sb,"auth_code",auth_code);
        createNode(sb,"avs_address_response",avs_address_response);
        createNode(sb,"avs_postcode_response",avs_postcode_response);
        createNode(sb,"bank_response_code",bank_response_code);
        createNode(sb,"bank_response_message",bank_response_message);
        createNode(sb,"cv2_response",cv2_response);
        sb.append("</Response>\n");
        return sb.toString();
    }

    public void setAuth_code(String auth_code) {
        this.auth_code = auth_code;
    }

    public void setAvs_address_response(String avs_address_response) {
        this.avs_address_response = avs_address_response;
    }

    public void setAvs_postcode_response(String avs_postcode_response) {
        this.avs_postcode_response = avs_postcode_response;
    }

    public void setBank_response_code(String bank_response_code) {
        this.bank_response_code = bank_response_code;
    }

    public void setBank_response_message(String bank_response_message) {
        this.bank_response_message = bank_response_message;
    }

    public void setCv2_response(String cv2_response) {
        this.cv2_response = cv2_response;
    }
}
