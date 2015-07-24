
package com.ctrip.infosec.flowtable4j.visa;

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
public class PurchaseTotals {

    protected String currency;
    protected String discountAmount;
    protected String discountAmountSign;
    protected String discountManagementIndicator;
    protected String taxAmount;
    protected String dutyAmount;
    protected String dutyAmountSign;
    protected String grandTotalAmount;
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

    /**
     * 获取currency属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * 设置currency属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrency(String value) {
        this.currency = value;
    }

    /**
     * 获取discountAmount属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiscountAmount() {
        return discountAmount;
    }

    /**
     * 设置discountAmount属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiscountAmount(String value) {
        this.discountAmount = value;
    }

    /**
     * 获取discountAmountSign属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiscountAmountSign() {
        return discountAmountSign;
    }

    /**
     * 设置discountAmountSign属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiscountAmountSign(String value) {
        this.discountAmountSign = value;
    }

    /**
     * 获取discountManagementIndicator属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiscountManagementIndicator() {
        return discountManagementIndicator;
    }

    /**
     * 设置discountManagementIndicator属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiscountManagementIndicator(String value) {
        this.discountManagementIndicator = value;
    }

    /**
     * 获取taxAmount属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxAmount() {
        return taxAmount;
    }

    /**
     * 设置taxAmount属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxAmount(String value) {
        this.taxAmount = value;
    }

    /**
     * 获取dutyAmount属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDutyAmount() {
        return dutyAmount;
    }

    /**
     * 设置dutyAmount属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDutyAmount(String value) {
        this.dutyAmount = value;
    }

    /**
     * 获取dutyAmountSign属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDutyAmountSign() {
        return dutyAmountSign;
    }

    /**
     * 设置dutyAmountSign属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDutyAmountSign(String value) {
        this.dutyAmountSign = value;
    }

    /**
     * 获取grandTotalAmount属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGrandTotalAmount() {
        return grandTotalAmount;
    }

    /**
     * 设置grandTotalAmount属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGrandTotalAmount(String value) {
        this.grandTotalAmount = value;
    }

    /**
     * 获取freightAmount属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFreightAmount() {
        return freightAmount;
    }

    /**
     * 设置freightAmount属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFreightAmount(String value) {
        this.freightAmount = value;
    }

    /**
     * 获取freightAmountSign属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFreightAmountSign() {
        return freightAmountSign;
    }

    /**
     * 设置freightAmountSign属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFreightAmountSign(String value) {
        this.freightAmountSign = value;
    }

    /**
     * 获取foreignAmount属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForeignAmount() {
        return foreignAmount;
    }

    /**
     * 设置foreignAmount属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForeignAmount(String value) {
        this.foreignAmount = value;
    }

    /**
     * 获取foreignCurrency属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForeignCurrency() {
        return foreignCurrency;
    }

    /**
     * 设置foreignCurrency属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForeignCurrency(String value) {
        this.foreignCurrency = value;
    }

    /**
     * 获取originalAmount属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginalAmount() {
        return originalAmount;
    }

    /**
     * 设置originalAmount属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginalAmount(String value) {
        this.originalAmount = value;
    }

    /**
     * 获取originalCurrency属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginalCurrency() {
        return originalCurrency;
    }

    /**
     * 设置originalCurrency属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginalCurrency(String value) {
        this.originalCurrency = value;
    }

    /**
     * 获取exchangeRate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExchangeRate() {
        return exchangeRate;
    }

    /**
     * 设置exchangeRate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExchangeRate(String value) {
        this.exchangeRate = value;
    }

    /**
     * 获取exchangeRateTimeStamp属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExchangeRateTimeStamp() {
        return exchangeRateTimeStamp;
    }

    /**
     * 设置exchangeRateTimeStamp属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExchangeRateTimeStamp(String value) {
        this.exchangeRateTimeStamp = value;
    }

    /**
     * 获取additionalAmountType0属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalAmountType0() {
        return additionalAmountType0;
    }

    /**
     * 设置additionalAmountType0属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalAmountType0(String value) {
        this.additionalAmountType0 = value;
    }

    /**
     * 获取additionalAmount0属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalAmount0() {
        return additionalAmount0;
    }

    /**
     * 设置additionalAmount0属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalAmount0(String value) {
        this.additionalAmount0 = value;
    }

    /**
     * 获取additionalAmountType1属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalAmountType1() {
        return additionalAmountType1;
    }

    /**
     * 设置additionalAmountType1属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalAmountType1(String value) {
        this.additionalAmountType1 = value;
    }

    /**
     * 获取additionalAmount1属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalAmount1() {
        return additionalAmount1;
    }

    /**
     * 设置additionalAmount1属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalAmount1(String value) {
        this.additionalAmount1 = value;
    }

    /**
     * 获取additionalAmountType2属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalAmountType2() {
        return additionalAmountType2;
    }

    /**
     * 设置additionalAmountType2属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalAmountType2(String value) {
        this.additionalAmountType2 = value;
    }

    /**
     * 获取additionalAmount2属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalAmount2() {
        return additionalAmount2;
    }

    /**
     * 设置additionalAmount2属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalAmount2(String value) {
        this.additionalAmount2 = value;
    }

    /**
     * 获取additionalAmountType3属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalAmountType3() {
        return additionalAmountType3;
    }

    /**
     * 设置additionalAmountType3属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalAmountType3(String value) {
        this.additionalAmountType3 = value;
    }

    /**
     * 获取additionalAmount3属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalAmount3() {
        return additionalAmount3;
    }

    /**
     * 设置additionalAmount3属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalAmount3(String value) {
        this.additionalAmount3 = value;
    }

    /**
     * 获取additionalAmountType4属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalAmountType4() {
        return additionalAmountType4;
    }

    /**
     * 设置additionalAmountType4属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalAmountType4(String value) {
        this.additionalAmountType4 = value;
    }

    /**
     * 获取additionalAmount4属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalAmount4() {
        return additionalAmount4;
    }

    /**
     * 设置additionalAmount4属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalAmount4(String value) {
        this.additionalAmount4 = value;
    }

    /**
     * 获取serviceFeeAmount属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceFeeAmount() {
        return serviceFeeAmount;
    }

    /**
     * 设置serviceFeeAmount属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceFeeAmount(String value) {
        this.serviceFeeAmount = value;
    }

}
