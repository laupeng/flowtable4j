package com.ctrip.infosec.flowtable4j.t3afs.visa;
import com.ctrip.infosec.flowtable4j.t3afs.Common.BaseNode;
public class BillTo extends BaseNode {
    private String firstName="noreal";
    private String lastName="name";
    private String street1="1295 Charleston Rd";
    private String city="Mountain View";
    private String state="CA";
    private String postalCode="94043";
    private String country="US";
    private String phoneNumber;
    private String email="null@cybersource.com";
    private String ipAddress;
    private String customerID;

    @Override
    public String toXML()
    {
        StringBuilder sb= new StringBuilder();
        sb.append("<billTo>\n");
        createNode(sb,"firstName",firstName);
        createNode(sb,"lastName",lastName);
        createNode(sb,"street1",street1);
        createNode(sb,"city",city);
        createNode(sb,"state",state);
        createNode(sb,"postalCode",postalCode);
        createNode(sb,"country",country);
        createNode(sb,"phoneNumber",phoneNumber);
        createNode(sb,"email",email);
        createNode(sb,"ipAddress",ipAddress);
        createNode(sb,"customerID",customerID);
        sb.append("</billTo>\n");
        return sb.toString();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }
}
