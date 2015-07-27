
package com.ctrip.infosec.flowtable4j.visa;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Item complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="Item">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="unitPrice" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="quantity" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="productCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="productName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="productSKU" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="productRisk" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="taxAmount" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="cityOverrideAmount" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="cityOverrideRate" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="countyOverrideAmount" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="countyOverrideRate" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="districtOverrideAmount" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="districtOverrideRate" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="stateOverrideAmount" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="stateOverrideRate" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="countryOverrideAmount" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="countryOverrideRate" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="orderAcceptanceCity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderAcceptanceCounty" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderAcceptanceCountry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderAcceptanceState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderAcceptancePostalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderOriginCity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderOriginCounty" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderOriginCountry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderOriginState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderOriginPostalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipFromCity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipFromCounty" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipFromCountry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipFromState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shipFromPostalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="export" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="noExport" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nationalTax" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="vatRate" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="sellerRegistration" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sellerRegistration0" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sellerRegistration1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sellerRegistration2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sellerRegistration3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sellerRegistration4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sellerRegistration5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sellerRegistration6" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sellerRegistration7" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sellerRegistration8" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sellerRegistration9" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="buyerRegistration" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="middlemanRegistration" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pointOfTitleTransfer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="giftCategory" type="{urn:schemas-cybersource-com:transaction-data-1.118}boolean" minOccurs="0"/>
 *         &lt;element name="timeCategory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hostHedge" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="timeHedge" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="velocityHedge" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nonsensicalHedge" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phoneHedge" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="obscenitiesHedge" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="unitOfMeasure" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="taxRate" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="totalAmount" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="discountAmount" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="discountRate" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="commodityCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="grossNetIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="taxTypeApplied" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="discountIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="alternateTaxID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="alternateTaxAmount" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="alternateTaxTypeApplied" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="alternateTaxRate" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="alternateTaxType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="localTax" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="zeroCostToCustomerIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="passengerFirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="passengerLastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="passengerID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="passengerStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="passengerType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="passengerEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="passengerPhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="invoiceNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="productDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="taxStatusIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="discountManagementIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="typeOfSupply" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Item", propOrder = {
    "unitPrice",
    "quantity",
    "productCode",
    "productName",
    "productSKU",
    "productRisk",
    "taxAmount",
    "cityOverrideAmount",
    "cityOverrideRate",
    "countyOverrideAmount",
    "countyOverrideRate",
    "districtOverrideAmount",
    "districtOverrideRate",
    "stateOverrideAmount",
    "stateOverrideRate",
    "countryOverrideAmount",
    "countryOverrideRate",
    "orderAcceptanceCity",
    "orderAcceptanceCounty",
    "orderAcceptanceCountry",
    "orderAcceptanceState",
    "orderAcceptancePostalCode",
    "orderOriginCity",
    "orderOriginCounty",
    "orderOriginCountry",
    "orderOriginState",
    "orderOriginPostalCode",
    "shipFromCity",
    "shipFromCounty",
    "shipFromCountry",
    "shipFromState",
    "shipFromPostalCode",
    "export",
    "noExport",
    "nationalTax",
    "vatRate",
    "sellerRegistration",
    "sellerRegistration0",
    "sellerRegistration1",
    "sellerRegistration2",
    "sellerRegistration3",
    "sellerRegistration4",
    "sellerRegistration5",
    "sellerRegistration6",
    "sellerRegistration7",
    "sellerRegistration8",
    "sellerRegistration9",
    "buyerRegistration",
    "middlemanRegistration",
    "pointOfTitleTransfer",
    "giftCategory",
    "timeCategory",
    "hostHedge",
    "timeHedge",
    "velocityHedge",
    "nonsensicalHedge",
    "phoneHedge",
    "obscenitiesHedge",
    "unitOfMeasure",
    "taxRate",
    "totalAmount",
    "discountAmount",
    "discountRate",
    "commodityCode",
    "grossNetIndicator",
    "taxTypeApplied",
    "discountIndicator",
    "alternateTaxID",
    "alternateTaxAmount",
    "alternateTaxTypeApplied",
    "alternateTaxRate",
    "alternateTaxType",
    "localTax",
    "zeroCostToCustomerIndicator",
    "passengerFirstName",
    "passengerLastName",
    "passengerID",
    "passengerStatus",
    "passengerType",
    "passengerEmail",
    "passengerPhone",
    "invoiceNumber",
    "productDescription",
    "taxStatusIndicator",
    "discountManagementIndicator",
    "typeOfSupply",
    "sign"
})
public class Item extends BaseNode {

    protected String unitPrice;
    protected String quantity;
    protected String productCode;
    protected String productName;
    protected String productSKU;
    protected String productRisk;
    protected String taxAmount;
    protected String cityOverrideAmount;
    protected String cityOverrideRate;
    protected String countyOverrideAmount;
    protected String countyOverrideRate;
    protected String districtOverrideAmount;
    protected String districtOverrideRate;
    protected String stateOverrideAmount;
    protected String stateOverrideRate;
    protected String countryOverrideAmount;
    protected String countryOverrideRate;
    protected String orderAcceptanceCity;
    protected String orderAcceptanceCounty;
    protected String orderAcceptanceCountry;
    protected String orderAcceptanceState;
    protected String orderAcceptancePostalCode;
    protected String orderOriginCity;
    protected String orderOriginCounty;
    protected String orderOriginCountry;
    protected String orderOriginState;
    protected String orderOriginPostalCode;
    protected String shipFromCity;
    protected String shipFromCounty;
    protected String shipFromCountry;
    protected String shipFromState;
    protected String shipFromPostalCode;
    protected String export;
    protected String noExport;
    protected String nationalTax;
    protected String vatRate;
    protected String sellerRegistration;
    protected String sellerRegistration0;
    protected String sellerRegistration1;
    protected String sellerRegistration2;
    protected String sellerRegistration3;
    protected String sellerRegistration4;
    protected String sellerRegistration5;
    protected String sellerRegistration6;
    protected String sellerRegistration7;
    protected String sellerRegistration8;
    protected String sellerRegistration9;
    protected String buyerRegistration;
    protected String middlemanRegistration;
    protected String pointOfTitleTransfer;
    protected String giftCategory;
    protected String timeCategory;
    protected String hostHedge;
    protected String timeHedge;
    protected String velocityHedge;
    protected String nonsensicalHedge;
    protected String phoneHedge;
    protected String obscenitiesHedge;
    protected String unitOfMeasure;
    protected String taxRate;
    protected String totalAmount;
    protected String discountAmount;
    protected String discountRate;
    protected String commodityCode;
    protected String grossNetIndicator;
    protected String taxTypeApplied;
    protected String discountIndicator;
    protected String alternateTaxID;
    protected String alternateTaxAmount;
    protected String alternateTaxTypeApplied;
    protected String alternateTaxRate;
    protected String alternateTaxType;
    protected String localTax;
    protected String zeroCostToCustomerIndicator;
    private String passengerFirstName;
    private String passengerLastName;
    private String passengerID;
    private String passengerStatus;
    private String passengerType;
    private String passengerEmail;
    private String passengerPhone;
    protected String invoiceNumber;
    protected String productDescription;
    protected String taxStatusIndicator;
    protected String discountManagementIndicator;
    protected String typeOfSupply;
    protected String sign;

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
