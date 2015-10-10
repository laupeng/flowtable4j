package com.ctrip.infosec.flowtable4j.t3afs.visa;

import com.ctrip.infosec.flowtable4j.t3afs.Common.BaseNode;
public class MerchantDefinedData extends BaseNode {
    private String isRegisterUser;
    private String days2Signup;
    private String cardBin;
    private String productType;
    private String hours2Departure;
    private String departureCountry;
    private String arrivalCountry;
    private String departureCode;
    private String arrivalCode;
    private String serverFrom;
    private String saleChannel;
    private String seatClass;
    private String adults;
    private String children;
    private String hotelCity;
    private String hotelCountry;
    private String hotelName;
    private String rooms;
    private String nights;
    private String carrier;
    private String isCouponUsed;
    private String days2CheckIn;
    private String billingFullname;
    private String passengerFullname1;
    private String passengerFullname2;
    private String passengerFullname3;
    private String passengerFullname4;
    private String passengerFullname5;
    private String deviceId;

    @Override
    public String toXML(){
        StringBuilder sb= new StringBuilder();
        sb.append("<merchantDefinedData>\n");
        createNode(sb,"field1", isRegisterUser);
        createNode(sb,"field2",days2Signup);
        createNode(sb,"field3",cardBin);
        createNode(sb,"field4",productType);
        createNode(sb,"field5", hours2Departure);
        createNode(sb,"field6",departureCountry);
        createNode(sb,"field7",arrivalCountry);
        createNode(sb,"field8",departureCode);
        createNode(sb,"field9",arrivalCode);
        createNode(sb,"field10",serverFrom);
        createNode(sb,"field11",saleChannel);
        createNode(sb,"field12",seatClass);
        createNode(sb,"field13",adults);
        createNode(sb,"field14",children);
        createNode(sb,"field15",hotelCity);
        createNode(sb,"field16",hotelCountry);
        createNode(sb,"field17",hotelName);
        createNode(sb,"field18",rooms);
        createNode(sb,"field19",nights);
        createNode(sb,"field20",carrier);
        createNode(sb,"field21",isCouponUsed);
        createNode(sb,"field22",days2CheckIn);
        createNode(sb,"field23",billingFullname);
        createNode(sb,"field24",passengerFullname1);
        createNode(sb,"field25",passengerFullname2);
        createNode(sb,"field26",passengerFullname3);
        createNode(sb,"field27",passengerFullname4);
        createNode(sb,"field28",passengerFullname5);
        createNode(sb,"field29",deviceId);
        sb.append("</merchantDefinedData>\n");
        return sb.toString();
    }

    public String getIsRegisterUser() {
        return isRegisterUser;
    }

    public void setIsRegisterUser(String isRegisterUser) {
        this.isRegisterUser = isRegisterUser;
    }

    public String getDays2Signup() {
        return days2Signup;
    }

    public void setDays2Signup(String days2Signup) {
        this.days2Signup = days2Signup;
    }

    public String getCardBin() {
        return cardBin;
    }

    public void setCardBin(String cardBin) {
        this.cardBin = cardBin;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getHours2Departure() {
        return hours2Departure;
    }

    public void setHours2Departure(String hours2Departure) {
        this.hours2Departure = hours2Departure;
    }

    public String getDepartureCountry() {
        return departureCountry;
    }

    public void setDepartureCountry(String departureCountry) {
        this.departureCountry = departureCountry;
    }

    public String getArrivalCountry() {
        return arrivalCountry;
    }

    public void setArrivalCountry(String arrivalCountry) {
        this.arrivalCountry = arrivalCountry;
    }

    public String getDepartureCode() {
        return departureCode;
    }

    public void setDepartureCode(String departureCode) {
        this.departureCode = departureCode;
    }

    public String getArrivalCode() {
        return arrivalCode;
    }

    public void setArrivalCode(String arrivalCode) {
        this.arrivalCode = arrivalCode;
    }

    public String getServerFrom() {
        return serverFrom;
    }

    public void setServerFrom(String serverFrom) {
        this.serverFrom = serverFrom;
    }

    public String getSaleChannel() {
        return saleChannel;
    }

    public void setSaleChannel(String saleChannel) {
        this.saleChannel = saleChannel;
    }

    public String getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }

    public String getAdults() {
        return adults;
    }

    public void setAdults(String adults) {
        this.adults = adults;
    }

    public String getChildren() {
        return children;
    }

    public void setChildren(String children) {
        this.children = children;
    }

    public String getHotelCity() {
        return hotelCity;
    }

    public void setHotelCity(String hotelCity) {
        this.hotelCity = hotelCity;
    }

    public String getHotelCountry() {
        return hotelCountry;
    }

    public void setHotelCountry(String hotelCountry) {
        this.hotelCountry = hotelCountry;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public String getNights() {
        return nights;
    }

    public void setNights(String nights) {
        this.nights = nights;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getIsCouponUsed() {
        return isCouponUsed;
    }

    public void setIsCouponUsed(String isCouponUsed) {
        this.isCouponUsed = isCouponUsed;
    }

    public String getDays2CheckIn() {
        return days2CheckIn;
    }

    public void setDays2CheckIn(String days2CheckIn) {
        this.days2CheckIn = days2CheckIn;
    }

    public String getBillingFullname() {
        return billingFullname;
    }

    public void setBillingFullname(String billingFullname) {
        this.billingFullname = billingFullname;
    }

    public String getPassengerFullname1() {
        return passengerFullname1;
    }

    public void setPassengerFullname1(String passengerFullname1) {
        this.passengerFullname1 = passengerFullname1;
    }

    public String getPassengerFullname2() {
        return passengerFullname2;
    }

    public void setPassengerFullname2(String passengerFullname2) {
        this.passengerFullname2 = passengerFullname2;
    }

    public String getPassengerFullname3() {
        return passengerFullname3;
    }

    public void setPassengerFullname3(String passengerFullname3) {
        this.passengerFullname3 = passengerFullname3;
    }

    public String getPassengerFullname4() {
        return passengerFullname4;
    }

    public void setPassengerFullname4(String passengerFullname4) {
        this.passengerFullname4 = passengerFullname4;
    }

    public String getPassengerFullname5() {
        return passengerFullname5;
    }

    public void setPassengerFullname5(String passengerFullname5) {
        this.passengerFullname5 = passengerFullname5;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
