package com.ctrip.infosec.flowtable4j.t3afs.visa;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by thyang on 2015-07-27.
 */
public class VisaResponse {
   private String requestToken;
   private List<String> missingFields = new ArrayList<String>();
   private AFSReply afsReply = new AFSReply();
   private String reasonCode;
   private String requestid;
   private String decision;
   private String responseStr;
   private String serviceStatus="FAIL";
   private String serviceError;

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public List<String> getMissingFields() {
        return missingFields;
    }

    public void setMissingFields(List<String> missingFields) {
        this.missingFields = missingFields;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getRequestid() {
        return requestid;
    }

    public void setRequestid(String requestid) {
        this.requestid = requestid;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public AFSReply getAfsReply() {
        return afsReply;
    }

    public void setAfsReply(AFSReply afsReply) {
        this.afsReply = afsReply;
    }

    public String getResponseStr() {
        return responseStr;
    }

    public void setResponseStr(String responseStr) {
        this.responseStr = responseStr;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getServiceError() {
        return serviceError;
    }

    public void setServiceError(String serviceError) {
        this.serviceError = serviceError;
    }
}
