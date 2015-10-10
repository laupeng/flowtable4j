package com.ctrip.infosec.flowtable4j.t3afs.master;

/**
 * Created by thyang on 2015-08-19.
 */
public class MasterResponse {
    private String responseStr;
    private String status;
    private String reason;
    private String mode;
    private String additionalMessage;
    private String cpi_value;
    private String response_code;
    private String response_message;
    private String serviceStatus="FAIL";
    private String serviceError;
    public String getResponseStr() {
        return responseStr;
    }

    public void setResponseStr(String responseStr) {
        this.responseStr = responseStr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getAdditionalMessage() {
        return additionalMessage;
    }

    public void setAdditionalMessage(String additionalMessage) {
        this.additionalMessage = additionalMessage;
    }

    public String getCpi_value() {
        return cpi_value;
    }

    public void setCpi_value(String cpi_value) {
        this.cpi_value = cpi_value;
    }

    public String getResponse_code() {
        return response_code;
    }

    public void setResponse_code(String response_code) {
        this.response_code = response_code;
    }

    public String getResponse_message() {
        return response_message;
    }

    public void setResponse_message(String response_message) {
        this.response_message = response_message;
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
