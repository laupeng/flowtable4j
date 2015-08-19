
package com.ctrip.infosec.flowtable4j.visa;

import com.ctrip.infosec.flowtable4j.Common.BaseNode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>PurchaseTotals complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="PurchaseTotals">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="currency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="discountAmount" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="discountAmountSign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="discountManagementIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="taxAmount" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="dutyAmount" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="dutyAmountSign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="grandTotalAmount" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="freightAmount" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="freightAmountSign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="foreignAmount" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="foreignCurrency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="originalAmount" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="originalCurrency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="exchangeRate" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *         &lt;element name="exchangeRateTimeStamp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additionalAmountType0" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additionalAmount0" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additionalAmountType1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additionalAmount1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additionalAmountType2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additionalAmount2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additionalAmountType3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additionalAmount3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additionalAmountType4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="additionalAmount4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serviceFeeAmount" type="{urn:schemas-cybersource-com:transaction-data-1.118}amount" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PurchaseTotals", propOrder = {
    "currency",
    "discountAmount",
    "discountAmountSign",
    "discountManagementIndicator",
    "taxAmount",
    "dutyAmount",
    "dutyAmountSign",
    "grandTotalAmount",
    "freightAmount",
    "freightAmountSign",
    "foreignAmount",
    "foreignCurrency",
    "originalAmount",
    "originalCurrency",
    "exchangeRate",
    "exchangeRateTimeStamp",
    "additionalAmountType0",
    "additionalAmount0",
    "additionalAmountType1",
    "additionalAmount1",
    "additionalAmountType2",
    "additionalAmount2",
    "additionalAmountType3",
    "additionalAmount3",
    "additionalAmountType4",
    "additionalAmount4",
    "serviceFeeAmount"
})
public class PurchaseTotals extends BaseNode {

    private String currency;
    protected String discountAmount;
    protected String discountAmountSign;
    protected String discountManagementIndicator;
    protected String taxAmount;
    protected String dutyAmount;
    protected String dutyAmountSign;
    private String grandTotalAmount;
    protected String freightAmount;
    protected String freightAmountSign;
    protected String foreignAmount;
    protected String foreignCurrency;
    protected String originalAmount;
    protected String originalCurrency;
    protected String exchangeRate;
    protected String exchangeRateTimeStamp;
    protected String additionalAmountType0;
    protected String additionalAmount0;
    protected String additionalAmountType1;
    protected String additionalAmount1;
    protected String additionalAmountType2;
    protected String additionalAmount2;
    protected String additionalAmountType3;
    protected String additionalAmount3;
    protected String additionalAmountType4;
    protected String additionalAmount4;
    protected String serviceFeeAmount;

    @Override
    public String toXML(){
        StringBuilder sb=new StringBuilder();
        sb.append("<purchaseTotals>\n");
        createNode(sb,"currency",currency);
        createNode(sb,"grandTotalAmount",grandTotalAmount);
        sb.append("</purchaseTotals>\n");
        return sb.toString();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getGrandTotalAmount() {
        return grandTotalAmount;
    }

    public void setGrandTotalAmount(String grandTotalAmount) {
        this.grandTotalAmount = grandTotalAmount;
    }
}
