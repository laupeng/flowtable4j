package com.ctrip.infosec.flowtable4j.visa;

import com.ctrip.infosec.flowtable4j.Common.BaseNode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>AFSService complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="AFSService">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="avsCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cvCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="disableAVSScoring" type="{urn:schemas-cybersource-com:transaction-data-1.118}boolean" minOccurs="0"/>
 *         &lt;element name="customRiskModel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="run" use="required" type="{urn:schemas-cybersource-com:transaction-data-1.118}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AFSService", propOrder = {
    "avsCode",
    "cvCode",
    "disableAVSScoring",
    "customRiskModel"
})
public class AFSService extends BaseNode {

    protected String avsCode;
    protected String cvCode;
    protected String disableAVSScoring;
    protected String customRiskModel;
    @XmlAttribute(name = "run", required = true)
    protected String run;

    public String toXML(){
        StringBuilder sb=new StringBuilder("<afsService run=\"true\">");
            createNode(sb,"avsCode",avsCode);
            createNode(sb,"cvCode",cvCode);
            createNode(sb,"disableAVSScoring",disableAVSScoring);
            createNode(sb,"customRiskModel",customRiskModel);
            sb.append("</afsService>");
        return sb.toString();
    }

    /**
     * 获取avsCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvsCode() {
        return avsCode;
    }

    /**
     * 设置avsCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvsCode(String value) {
        this.avsCode = value;
    }

    /**
     * 获取cvCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCvCode() {
        return cvCode;
    }

    /**
     * 设置cvCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCvCode(String value) {
        this.cvCode = value;
    }

    /**
     * 获取disableAVSScoring属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisableAVSScoring() {
        return disableAVSScoring;
    }

    /**
     * 设置disableAVSScoring属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisableAVSScoring(String value) {
        this.disableAVSScoring = value;
    }

    /**
     * 获取customRiskModel属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomRiskModel() {
        return customRiskModel;
    }

    /**
     * 设置customRiskModel属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomRiskModel(String value) {
        this.customRiskModel = value;
    }

    /**
     * 获取run属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRun() {
        return run;
    }

    /**
     * 设置run属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRun(String value) {
        this.run = value;
    }

}
