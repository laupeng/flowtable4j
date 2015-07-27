
package com.ctrip.infosec.flowtable4j.visa;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>MerchantDefinedData complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="MerchantDefinedData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="field1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field6" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field7" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field8" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field9" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field10" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field11" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field12" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field13" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field14" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field15" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field16" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field17" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field18" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field19" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="field20" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mddField" type="{urn:schemas-cybersource-com:transaction-data-1.118}MDDField" maxOccurs="100" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MerchantDefinedData", propOrder = {
    "field1",
    "field2",
    "field3",
    "field4",
    "field5",
    "field6",
    "field7",
    "field8",
    "field9",
    "field10",
    "field11",
    "field12",
    "field13",
    "field14",
    "field15",
    "field16",
    "field17",
    "field18",
    "field19",
    "field20",
    "mddField"
})
public class MerchantDefinedData extends BaseNode {
    protected String field1;
    protected String field2;
    protected String field3;
    protected String field4;
    protected String field5;
    protected String field6;
    protected String field7;
    protected String field8;
    protected String field9;
    protected String field10;
    protected String field11;
    protected String field12;
    protected String field13;
    protected String field14;
    protected String field15;
    protected String field16;
    protected String field17;
    protected String field18;
    protected String field19;
    protected String field20;
    protected String field21;
    protected String field22;
    protected String field23;
    protected String field24;
    protected String field25;
    protected String field26;
    protected String field27;
    protected String field28;
    protected String field29;
    protected List<MDDField> mddField;

    @Override
    public String toXML(){
        StringBuilder sb= new StringBuilder();
        sb.append("<merchantDefinedData>\n");
        createNode(sb,"field1",field1);
        createNode(sb,"field2",field2);
        createNode(sb,"field3",field3);
        createNode(sb,"field4",field4);
        createNode(sb,"field5",field5);
        createNode(sb,"field6",field6);
        createNode(sb,"field7",field7);
        createNode(sb,"field8",field8);
        createNode(sb,"field9",field9);
        createNode(sb,"field10",field10);
        createNode(sb,"field11",field11);
        createNode(sb,"field12",field12);
        createNode(sb,"field13",field13);
        createNode(sb,"field14",field14);
        createNode(sb,"field15",field15);
        createNode(sb,"field16",field16);
        createNode(sb,"field17",field17);
        createNode(sb,"field18",field18);
        createNode(sb,"field19",field19);
        createNode(sb,"field20",field20);
        createNode(sb,"field21",field21);
        createNode(sb,"field22",field22);
        createNode(sb,"field23",field23);
        createNode(sb,"field24",field24);
        createNode(sb,"field25",field25);
        createNode(sb,"field26",field26);
        createNode(sb,"field27",field27);
        createNode(sb,"field28",field28);
        createNode(sb,"field29",field29);
        sb.append("</merchantDefinedData>\n");
        return sb.toString();
    }


    public void setIsCheckout(String isCheckout){
        field1 = isCheckout;
    }

    public void setSignupToDate(Integer ndays){
        if(ndays!=null){
            field2 = ndays.toString();
        }
    }

    public void setCardBin(String cardBin){
        field3 = cardBin;
    }

    public void setProductType(String productType){
        field4 = productType;
    }

    public void setHourToDepature(Integer hourToDepature){
        if(hourToDepature!=null){
            field5 = hourToDepature.toString();
        }
    }

    public void setDepartureCountry(String departureCountry){
        field6 = departureCountry;
    }

    public void setArrivalCountry(String arrivalCountry){
        field7 = arrivalCountry;
    }

    public void setDepartureAirportCode(String departureAirportCode){
        field8 = departureAirportCode;
    }

    public void setArrivalAirportCode(String arrivalAirportCode){
        field9 = arrivalAirportCode;
    }

    public void setServerFrom(String serverFrom){
        field10 = serverFrom;
    }

    public void setSalesChannel(String salesChannel){
        field11 = salesChannel;
    }

    public void setFlightClass(String flightClass){
        field12 = flightClass;
    }

    public void setAdultNums(int adultNums){
         field13 = String.valueOf(adultNums);
    }

    public void setChildNums(int childNums){
         field14 =String.valueOf(childNums);
    }

    public void setHotelCity(String hotelCity){
        field15 = hotelCity;
    }

    public void setHotelCountry(String hotelCountry){
        field16 = hotelCountry;
    }

    public void setHotelName(String hotelName){
        field17 = hotelName;
    }

    public void setRoomNums(int roomNums){
        field18 = String.valueOf(roomNums);
    }

    public void setNightNums(int nightNums){
        field19 =String.valueOf(nightNums);
    }

    public void setAgency(String agency){
        field20 = agency;
    }

    public void setIsCouponUsed(String couponUsed){
        field21 = couponUsed;
    }

    public void setDaysToCheckIn(Integer daysToCheckIn){
        if(daysToCheckIn!=null){
            field22 = daysToCheckIn.toString();
        }
    }

    public void setBillFullName(String billName){
        field23 = billName;
    }

    public void setPassengerName1(String passengerName1){
        field24 = passengerName1;
    }
    public void setPassengerName2(String passengerName1){
        field25 = passengerName1;
    }
    public void setPassengerName3(String passengerName1){
        field26 = passengerName1;
    }
    public void setPassengerName4(String passengerName1){
        field27 = passengerName1;
    }
    public void setPassengerName5(String passengerName1){
        field28 = passengerName1;
    }

    public void setDeviceID(String deviceID){
        field29 = deviceID;
    }


}
