
package com.ctrip.infosec.flowtable4j.visa;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigInteger;


/**
 * <p>AFSReply complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="AFSReply">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reasonCode" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="afsResult" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="hostSeverity" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="consumerLocalTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="afsFactorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="addressInfoCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hotlistInfoCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="internetInfoCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phoneInfoCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="suspiciousInfoCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="velocityInfoCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="identityInfoCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ipCountry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ipState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ipCity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ipRoutingMethod" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ipAnonymizerStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="scoreModelUsed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cardBin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="binCountry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cardAccountType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cardScheme" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cardIssuer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deviceFingerprint" type="{urn:schemas-cybersource-com:transaction-data-1.118}DeviceFingerprint" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AFSReply", propOrder = {
    "reasonCode",
    "afsResult",
    "hostSeverity",
    "consumerLocalTime",
    "afsFactorCode",
    "addressInfoCode",
    "hotlistInfoCode",
    "internetInfoCode",
    "phoneInfoCode",
    "suspiciousInfoCode",
    "velocityInfoCode",
    "identityInfoCode",
    "ipCountry",
    "ipState",
    "ipCity",
    "ipRoutingMethod",
    "ipAnonymizerStatus",
    "scoreModelUsed",
    "cardBin",
    "binCountry",
    "cardAccountType",
    "cardScheme",
    "cardIssuer",
    "deviceFingerprint"
})
public class AFSReply {

    @XmlElement(required = true)
    protected BigInteger reasonCode;
    protected BigInteger afsResult;
    protected BigInteger hostSeverity;
    protected String consumerLocalTime;
    protected String afsFactorCode;
    protected String addressInfoCode;
    protected String hotlistInfoCode;
    protected String internetInfoCode;
    protected String phoneInfoCode;
    protected String suspiciousInfoCode;
    protected String velocityInfoCode;
    protected String identityInfoCode;
    protected String ipCountry;
    protected String ipState;
    protected String ipCity;
    protected String ipRoutingMethod;
    protected String ipAnonymizerStatus;
    protected String scoreModelUsed;
    protected String cardBin;
    protected String binCountry;
    protected String cardAccountType;
    protected String cardScheme;
    protected String cardIssuer;
    protected DeviceFingerprint deviceFingerprint;

    /**
     * 获取reasonCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getReasonCode() {
        return reasonCode;
    }

    /**
     * 设置reasonCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setReasonCode(BigInteger value) {
        this.reasonCode = value;
    }

    /**
     * 获取afsResult属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAfsResult() {
        return afsResult;
    }

    /**
     * 设置afsResult属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAfsResult(BigInteger value) {
        this.afsResult = value;
    }

    /**
     * 获取hostSeverity属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getHostSeverity() {
        return hostSeverity;
    }

    /**
     * 设置hostSeverity属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setHostSeverity(BigInteger value) {
        this.hostSeverity = value;
    }

    /**
     * 获取consumerLocalTime属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConsumerLocalTime() {
        return consumerLocalTime;
    }

    /**
     * 设置consumerLocalTime属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConsumerLocalTime(String value) {
        this.consumerLocalTime = value;
    }

    /**
     * 获取afsFactorCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAfsFactorCode() {
        return afsFactorCode;
    }

    /**
     * 设置afsFactorCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAfsFactorCode(String value) {
        this.afsFactorCode = value;
    }

    /**
     * 获取addressInfoCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressInfoCode() {
        return addressInfoCode;
    }

    /**
     * 设置addressInfoCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressInfoCode(String value) {
        this.addressInfoCode = value;
    }

    /**
     * 获取hotlistInfoCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotlistInfoCode() {
        return hotlistInfoCode;
    }

    /**
     * 设置hotlistInfoCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotlistInfoCode(String value) {
        this.hotlistInfoCode = value;
    }

    /**
     * 获取internetInfoCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInternetInfoCode() {
        return internetInfoCode;
    }

    /**
     * 设置internetInfoCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInternetInfoCode(String value) {
        this.internetInfoCode = value;
    }

    /**
     * 获取phoneInfoCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhoneInfoCode() {
        return phoneInfoCode;
    }

    /**
     * 设置phoneInfoCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhoneInfoCode(String value) {
        this.phoneInfoCode = value;
    }

    /**
     * 获取suspiciousInfoCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuspiciousInfoCode() {
        return suspiciousInfoCode;
    }

    /**
     * 设置suspiciousInfoCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuspiciousInfoCode(String value) {
        this.suspiciousInfoCode = value;
    }

    /**
     * 获取velocityInfoCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVelocityInfoCode() {
        return velocityInfoCode;
    }

    /**
     * 设置velocityInfoCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVelocityInfoCode(String value) {
        this.velocityInfoCode = value;
    }

    /**
     * 获取identityInfoCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentityInfoCode() {
        return identityInfoCode;
    }

    /**
     * 设置identityInfoCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentityInfoCode(String value) {
        this.identityInfoCode = value;
    }

    /**
     * 获取ipCountry属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpCountry() {
        return ipCountry;
    }

    /**
     * 设置ipCountry属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpCountry(String value) {
        this.ipCountry = value;
    }

    /**
     * 获取ipState属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpState() {
        return ipState;
    }

    /**
     * 设置ipState属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpState(String value) {
        this.ipState = value;
    }

    /**
     * 获取ipCity属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpCity() {
        return ipCity;
    }

    /**
     * 设置ipCity属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpCity(String value) {
        this.ipCity = value;
    }

    /**
     * 获取ipRoutingMethod属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpRoutingMethod() {
        return ipRoutingMethod;
    }

    /**
     * 设置ipRoutingMethod属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpRoutingMethod(String value) {
        this.ipRoutingMethod = value;
    }

    /**
     * 获取ipAnonymizerStatus属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpAnonymizerStatus() {
        return ipAnonymizerStatus;
    }

    /**
     * 设置ipAnonymizerStatus属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpAnonymizerStatus(String value) {
        this.ipAnonymizerStatus = value;
    }

    /**
     * 获取scoreModelUsed属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScoreModelUsed() {
        return scoreModelUsed;
    }

    /**
     * 设置scoreModelUsed属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScoreModelUsed(String value) {
        this.scoreModelUsed = value;
    }

    /**
     * 获取cardBin属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardBin() {
        return cardBin;
    }

    /**
     * 设置cardBin属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardBin(String value) {
        this.cardBin = value;
    }

    /**
     * 获取binCountry属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBinCountry() {
        return binCountry;
    }

    /**
     * 设置binCountry属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBinCountry(String value) {
        this.binCountry = value;
    }

    /**
     * 获取cardAccountType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardAccountType() {
        return cardAccountType;
    }

    /**
     * 设置cardAccountType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardAccountType(String value) {
        this.cardAccountType = value;
    }

    /**
     * 获取cardScheme属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardScheme() {
        return cardScheme;
    }

    /**
     * 设置cardScheme属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardScheme(String value) {
        this.cardScheme = value;
    }

    /**
     * 获取cardIssuer属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardIssuer() {
        return cardIssuer;
    }

    /**
     * 设置cardIssuer属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardIssuer(String value) {
        this.cardIssuer = value;
    }

    /**
     * 获取deviceFingerprint属性的值。
     * 
     * @return
     *     possible object is
     *     {@link DeviceFingerprint }
     *     
     */
    public DeviceFingerprint getDeviceFingerprint() {
        return deviceFingerprint;
    }

    /**
     * 设置deviceFingerprint属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link DeviceFingerprint }
     *     
     */
    public void setDeviceFingerprint(DeviceFingerprint value) {
        this.deviceFingerprint = value;
    }

}
