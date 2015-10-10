package com.ctrip.infosec.flowtable4j.t3afs.visa;
import com.ctrip.infosec.flowtable4j.t3afs.Common.BaseNode;
public class Item extends BaseNode {
    private String passengerFirstName;
    private String passengerLastName;
    private String passengerID;
    private String passengerStatus;
    private String passengerType;
    private String passengerEmail;
    private String passengerPhone;
    private Double unitPrice=0.0d;

    @Override
    public String toXML(int sequence){
        StringBuilder sb= new StringBuilder();
        sb.append("<item id=\"").append(sequence).append("\">");
        createNode(sb,"passengerFirstName",passengerFirstName);
        createNode(sb,"passengerLastName",passengerLastName);
        createNode(sb,"passengerID",passengerID);
        createNode(sb,"passengerStatus",passengerStatus);
        createNode(sb,"passengerType",passengerType);
        createNode(sb,"passengerEmail",passengerEmail);
        createNode(sb,"passengerPhone",passengerPhone);
        createNode(sb,"unitPrice",passengerPhone);
        sb.append("</item>");
        return sb.toString();
    }

    public String getPassengerFirstName() {
        return passengerFirstName;
    }

    public void setPassengerFirstName(String passengerFirstName) {
        this.passengerFirstName = passengerFirstName;
    }

    public String getPassengerLastName() {
        return passengerLastName;
    }

    public void setPassengerLastName(String passengerLastName) {
        this.passengerLastName = passengerLastName;
    }

    public String getPassengerID() {
        return passengerID;
    }

    public void setPassengerID(String passengerID) {
        this.passengerID = passengerID;
    }

    public String getPassengerStatus() {
        return passengerStatus;
    }

    public void setPassengerStatus(String passengerStatus) {
        this.passengerStatus = passengerStatus;
    }

    public String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }
}
