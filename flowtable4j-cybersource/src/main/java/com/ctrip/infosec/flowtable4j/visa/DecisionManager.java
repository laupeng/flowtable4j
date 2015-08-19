
package com.ctrip.infosec.flowtable4j.visa;

import com.ctrip.infosec.flowtable4j.Common.BaseNode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>DecisionManager complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="DecisionManager">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="enabled" type="{urn:schemas-cybersource-com:transaction-data-1.118}boolean" minOccurs="0"/>
 *         &lt;element name="profile" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="travelData" type="{urn:schemas-cybersource-com:transaction-data-1.118}DecisionManagerTravelData" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DecisionManager", propOrder = {
    "enabled",
    "profile",
    "travelData"
})
public class DecisionManager extends BaseNode {

    protected String enabled;
    protected String profile;
    protected DecisionManagerTravelData travelData;

    @Override
    public String toXML(){
        if(travelData!=null){
            StringBuilder sb=new StringBuilder();
            sb.append("<decisionManager>\n");
            createNode(sb,"enabled",enabled);
            sb.append(travelData.toXML());
            sb.append("</decisionManager>\n");
            return sb.toString();
        }
        return "";
    }

    /**
     * 获取enabled属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEnabled() {
        return enabled;
    }

    /**
     * 设置enabled属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEnabled(String value) {
        this.enabled = value;
    }

    /**
     * 获取profile属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProfile() {
        return profile;
    }

    /**
     * 设置profile属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProfile(String value) {
        this.profile = value;
    }

    /**
     * 获取travelData属性的值。
     *
     * @return
     *     possible object is
     *
     */
    public DecisionManagerTravelData getTravelData() {
        return travelData;
    }

    /**
     * 设置travelData属性的值。
     *
     * @param value
     *     allowed object is
     *
     */
    public void setTravelData(DecisionManagerTravelData value) {
        this.travelData = value;
    }

}
