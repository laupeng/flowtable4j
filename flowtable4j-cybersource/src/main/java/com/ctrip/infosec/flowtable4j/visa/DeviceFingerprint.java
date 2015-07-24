
package com.ctrip.infosec.flowtable4j.visa;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>DeviceFingerprint complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="DeviceFingerprint">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cookiesEnabled" type="{urn:schemas-cybersource-com:transaction-data-1.118}boolean" minOccurs="0"/>
 *         &lt;element name="flashEnabled" type="{urn:schemas-cybersource-com:transaction-data-1.118}boolean" minOccurs="0"/>
 *         &lt;element name="hash" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="imagesEnabled" type="{urn:schemas-cybersource-com:transaction-data-1.118}boolean" minOccurs="0"/>
 *         &lt;element name="javascriptEnabled" type="{urn:schemas-cybersource-com:transaction-data-1.118}boolean" minOccurs="0"/>
 *         &lt;element name="proxyIPAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="proxyIPAddressActivities" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="proxyIPAddressAttributes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="proxyServerType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="trueIPAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="trueIPAddressActivities" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="trueIPAddressAttributes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="trueIPAddressCity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="trueIPAddressCountry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smartID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smartIDConfidenceLevel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="screenResolution" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="browserLanguage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="agentType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateTime" type="{urn:schemas-cybersource-com:transaction-data-1.118}dateTime" minOccurs="0"/>
 *         &lt;element name="profileDuration" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="profiledURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="timeOnPage" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="deviceMatch" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstEncounter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="flashOS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="flashVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deviceLatitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deviceLongitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gpsAccuracy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="jbRoot" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="jbRootReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeviceFingerprint", propOrder = {
    "cookiesEnabled",
    "flashEnabled",
    "hash",
    "imagesEnabled",
    "javascriptEnabled",
    "proxyIPAddress",
    "proxyIPAddressActivities",
    "proxyIPAddressAttributes",
    "proxyServerType",
    "trueIPAddress",
    "trueIPAddressActivities",
    "trueIPAddressAttributes",
    "trueIPAddressCity",
    "trueIPAddressCountry",
    "smartID",
    "smartIDConfidenceLevel",
    "screenResolution",
    "browserLanguage",
    "agentType",
    "dateTime",
    "profileDuration",
    "profiledURL",
    "timeOnPage",
    "deviceMatch",
    "firstEncounter",
    "flashOS",
    "flashVersion",
    "deviceLatitude",
    "deviceLongitude",
    "gpsAccuracy",
    "jbRoot",
    "jbRootReason"
})
public class DeviceFingerprint {

    protected String cookiesEnabled;
    protected String flashEnabled;
    protected String hash;
    protected String imagesEnabled;
    protected String javascriptEnabled;
    protected String proxyIPAddress;
    protected String proxyIPAddressActivities;
    protected String proxyIPAddressAttributes;
    protected String proxyServerType;
    protected String trueIPAddress;
    protected String trueIPAddressActivities;
    protected String trueIPAddressAttributes;
    protected String trueIPAddressCity;
    protected String trueIPAddressCountry;
    protected String smartID;
    protected String smartIDConfidenceLevel;
    protected String screenResolution;
    protected String browserLanguage;
    protected String agentType;
    protected String dateTime;
    protected BigInteger profileDuration;
    protected String profiledURL;
    protected BigInteger timeOnPage;
    protected String deviceMatch;
    protected String firstEncounter;
    protected String flashOS;
    protected String flashVersion;
    protected String deviceLatitude;
    protected String deviceLongitude;
    protected String gpsAccuracy;
    protected BigInteger jbRoot;
    protected String jbRootReason;

    /**
     * 获取cookiesEnabled属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCookiesEnabled() {
        return cookiesEnabled;
    }

    /**
     * 设置cookiesEnabled属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCookiesEnabled(String value) {
        this.cookiesEnabled = value;
    }

    /**
     * 获取flashEnabled属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlashEnabled() {
        return flashEnabled;
    }

    /**
     * 设置flashEnabled属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlashEnabled(String value) {
        this.flashEnabled = value;
    }

    /**
     * 获取hash属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHash() {
        return hash;
    }

    /**
     * 设置hash属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHash(String value) {
        this.hash = value;
    }

    /**
     * 获取imagesEnabled属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImagesEnabled() {
        return imagesEnabled;
    }

    /**
     * 设置imagesEnabled属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImagesEnabled(String value) {
        this.imagesEnabled = value;
    }

    /**
     * 获取javascriptEnabled属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJavascriptEnabled() {
        return javascriptEnabled;
    }

    /**
     * 设置javascriptEnabled属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJavascriptEnabled(String value) {
        this.javascriptEnabled = value;
    }

    /**
     * 获取proxyIPAddress属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProxyIPAddress() {
        return proxyIPAddress;
    }

    /**
     * 设置proxyIPAddress属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProxyIPAddress(String value) {
        this.proxyIPAddress = value;
    }

    /**
     * 获取proxyIPAddressActivities属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProxyIPAddressActivities() {
        return proxyIPAddressActivities;
    }

    /**
     * 设置proxyIPAddressActivities属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProxyIPAddressActivities(String value) {
        this.proxyIPAddressActivities = value;
    }

    /**
     * 获取proxyIPAddressAttributes属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProxyIPAddressAttributes() {
        return proxyIPAddressAttributes;
    }

    /**
     * 设置proxyIPAddressAttributes属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProxyIPAddressAttributes(String value) {
        this.proxyIPAddressAttributes = value;
    }

    /**
     * 获取proxyServerType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProxyServerType() {
        return proxyServerType;
    }

    /**
     * 设置proxyServerType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProxyServerType(String value) {
        this.proxyServerType = value;
    }

    /**
     * 获取trueIPAddress属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrueIPAddress() {
        return trueIPAddress;
    }

    /**
     * 设置trueIPAddress属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrueIPAddress(String value) {
        this.trueIPAddress = value;
    }

    /**
     * 获取trueIPAddressActivities属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrueIPAddressActivities() {
        return trueIPAddressActivities;
    }

    /**
     * 设置trueIPAddressActivities属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrueIPAddressActivities(String value) {
        this.trueIPAddressActivities = value;
    }

    /**
     * 获取trueIPAddressAttributes属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrueIPAddressAttributes() {
        return trueIPAddressAttributes;
    }

    /**
     * 设置trueIPAddressAttributes属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrueIPAddressAttributes(String value) {
        this.trueIPAddressAttributes = value;
    }

    /**
     * 获取trueIPAddressCity属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrueIPAddressCity() {
        return trueIPAddressCity;
    }

    /**
     * 设置trueIPAddressCity属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrueIPAddressCity(String value) {
        this.trueIPAddressCity = value;
    }

    /**
     * 获取trueIPAddressCountry属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrueIPAddressCountry() {
        return trueIPAddressCountry;
    }

    /**
     * 设置trueIPAddressCountry属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrueIPAddressCountry(String value) {
        this.trueIPAddressCountry = value;
    }

    /**
     * 获取smartID属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmartID() {
        return smartID;
    }

    /**
     * 设置smartID属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmartID(String value) {
        this.smartID = value;
    }

    /**
     * 获取smartIDConfidenceLevel属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmartIDConfidenceLevel() {
        return smartIDConfidenceLevel;
    }

    /**
     * 设置smartIDConfidenceLevel属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmartIDConfidenceLevel(String value) {
        this.smartIDConfidenceLevel = value;
    }

    /**
     * 获取screenResolution属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScreenResolution() {
        return screenResolution;
    }

    /**
     * 设置screenResolution属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScreenResolution(String value) {
        this.screenResolution = value;
    }

    /**
     * 获取browserLanguage属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrowserLanguage() {
        return browserLanguage;
    }

    /**
     * 设置browserLanguage属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrowserLanguage(String value) {
        this.browserLanguage = value;
    }

    /**
     * 获取agentType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgentType() {
        return agentType;
    }

    /**
     * 设置agentType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgentType(String value) {
        this.agentType = value;
    }

    /**
     * 获取dateTime属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * 设置dateTime属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateTime(String value) {
        this.dateTime = value;
    }

    /**
     * 获取profileDuration属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getProfileDuration() {
        return profileDuration;
    }

    /**
     * 设置profileDuration属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setProfileDuration(BigInteger value) {
        this.profileDuration = value;
    }

    /**
     * 获取profiledURL属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfiledURL() {
        return profiledURL;
    }

    /**
     * 设置profiledURL属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfiledURL(String value) {
        this.profiledURL = value;
    }

    /**
     * 获取timeOnPage属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTimeOnPage() {
        return timeOnPage;
    }

    /**
     * 设置timeOnPage属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTimeOnPage(BigInteger value) {
        this.timeOnPage = value;
    }

    /**
     * 获取deviceMatch属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeviceMatch() {
        return deviceMatch;
    }

    /**
     * 设置deviceMatch属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeviceMatch(String value) {
        this.deviceMatch = value;
    }

    /**
     * 获取firstEncounter属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstEncounter() {
        return firstEncounter;
    }

    /**
     * 设置firstEncounter属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstEncounter(String value) {
        this.firstEncounter = value;
    }

    /**
     * 获取flashOS属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlashOS() {
        return flashOS;
    }

    /**
     * 设置flashOS属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlashOS(String value) {
        this.flashOS = value;
    }

    /**
     * 获取flashVersion属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlashVersion() {
        return flashVersion;
    }

    /**
     * 设置flashVersion属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlashVersion(String value) {
        this.flashVersion = value;
    }

    /**
     * 获取deviceLatitude属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeviceLatitude() {
        return deviceLatitude;
    }

    /**
     * 设置deviceLatitude属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeviceLatitude(String value) {
        this.deviceLatitude = value;
    }

    /**
     * 获取deviceLongitude属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeviceLongitude() {
        return deviceLongitude;
    }

    /**
     * 设置deviceLongitude属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeviceLongitude(String value) {
        this.deviceLongitude = value;
    }

    /**
     * 获取gpsAccuracy属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGpsAccuracy() {
        return gpsAccuracy;
    }

    /**
     * 设置gpsAccuracy属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGpsAccuracy(String value) {
        this.gpsAccuracy = value;
    }

    /**
     * 获取jbRoot属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getJbRoot() {
        return jbRoot;
    }

    /**
     * 设置jbRoot属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setJbRoot(BigInteger value) {
        this.jbRoot = value;
    }

    /**
     * 获取jbRootReason属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJbRootReason() {
        return jbRootReason;
    }

    /**
     * 设置jbRootReason属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJbRootReason(String value) {
        this.jbRootReason = value;
    }

}
