
package com.ctrip.infosec.flowtable4j.visa;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>DecisionManagerTravelData complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="DecisionManagerTravelData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="leg" type="{urn:schemas-cybersource-com:transaction-data-1.118}DecisionManagerTravelLeg" maxOccurs="100" minOccurs="0"/>
 *         &lt;element name="departureDateTime" type="{urn:schemas-cybersource-com:transaction-data-1.118}dateTime" minOccurs="0"/>
 *         &lt;element name="completeRoute" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="journeyType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DecisionManagerTravelData", propOrder = {
    "leg",
    "departureDateTime",
    "completeRoute",
    "journeyType"
})
public class DecisionManagerTravelData extends BaseNode {

    protected List<DecisionManagerTravelLeg> leg;
    protected String departureDateTime;
    protected String completeRoute;
    protected String journeyType;

    @Override
    public String toXML(){
        StringBuilder sb=new StringBuilder();
        sb.append("<travelData>\n");
        createNode(sb,"departureDateTime",departureDateTime);
        createNode(sb,"completeRoute",completeRoute);
        createNode(sb,"journeyType",journeyType);
        sb.append("</travelData>\n");
        return sb.toString();
    }

    /**
     * Gets the value of the leg property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the leg property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLeg().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DecisionManagerTravelLeg }
     * 
     * 
     */
    public List<DecisionManagerTravelLeg> getLeg() {
        if (leg == null) {
            leg = new ArrayList<DecisionManagerTravelLeg>();
        }
        return this.leg;
    }

    /**
     * 获取departureDateTime属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartureDateTime() {
        return departureDateTime;
    }

    /**
     * 设置departureDateTime属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartureDateTime(String value) {
        this.departureDateTime = value;
    }

    /**
     * 获取completeRoute属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompleteRoute() {
        return completeRoute;
    }

    /**
     * 设置completeRoute属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompleteRoute(String value) {
        this.completeRoute = value;
    }

    /**
     * 获取journeyType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJourneyType() {
        return journeyType;
    }

    /**
     * 设置journeyType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJourneyType(String value) {
        this.journeyType = value;
    }

}
