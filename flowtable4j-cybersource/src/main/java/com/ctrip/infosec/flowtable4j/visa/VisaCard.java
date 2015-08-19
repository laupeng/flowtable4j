
package com.ctrip.infosec.flowtable4j.visa;

import com.ctrip.infosec.flowtable4j.Common.BaseNode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.math.BigInteger;


/**
 * <p>VisaCard complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="Card">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fullName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accountNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="expirationMonth" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="expirationYear" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="cvIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cvNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cardType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="issueNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="startMonth" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="startYear" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="pin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accountEncoderID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="encryptedData" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="suffix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="prefix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Card", propOrder = {
    "fullName",
    "accountNumber",
    "expirationMonth",
    "expirationYear",
    "cvIndicator",
    "cvNumber",
    "cardType",
    "issueNumber",
    "startMonth",
    "startYear",
    "pin",
    "accountEncoderID",
    "bin",
    "encryptedData",
    "suffix",
    "prefix"
})
public class VisaCard extends BaseNode {

    protected String fullName;
    private String accountNumber;
    private BigInteger expirationMonth;
    private BigInteger expirationYear;
    protected String cvIndicator;
    protected String cvNumber;
    private String cardType;
    protected String issueNumber;
    protected BigInteger startMonth;
    protected BigInteger startYear;
    protected String pin;
    protected String accountEncoderID;
    protected String bin;
    protected String encryptedData;
    protected String suffix;
    private String prefix;

    @Override
    public String toXML(){
        StringBuilder sb = new StringBuilder();
        sb.append("<card>\n");
        createNode(sb,"accountNumber",accountNumber);
        createNode(sb,"expirationMonth",expirationMonth.toString());
        createNode(sb,"expirationYear",expirationYear.toString());
        createNode(sb,"cardType",cardType);
        sb.append("</card>\n");
        return sb.toString();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigInteger getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(BigInteger expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public BigInteger getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(BigInteger expirationYear) {
        this.expirationYear = expirationYear;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}
