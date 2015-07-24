
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
public class MerchantDefinedData {

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
    protected List<MDDField> mddField;

    /**
     * 获取field1属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField1() {
        return field1;
    }

    /**
     * 设置field1属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField1(String value) {
        this.field1 = value;
    }

    /**
     * 获取field2属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField2() {
        return field2;
    }

    /**
     * 设置field2属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField2(String value) {
        this.field2 = value;
    }

    /**
     * 获取field3属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField3() {
        return field3;
    }

    /**
     * 设置field3属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField3(String value) {
        this.field3 = value;
    }

    /**
     * 获取field4属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField4() {
        return field4;
    }

    /**
     * 设置field4属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField4(String value) {
        this.field4 = value;
    }

    /**
     * 获取field5属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField5() {
        return field5;
    }

    /**
     * 设置field5属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField5(String value) {
        this.field5 = value;
    }

    /**
     * 获取field6属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField6() {
        return field6;
    }

    /**
     * 设置field6属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField6(String value) {
        this.field6 = value;
    }

    /**
     * 获取field7属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField7() {
        return field7;
    }

    /**
     * 设置field7属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField7(String value) {
        this.field7 = value;
    }

    /**
     * 获取field8属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField8() {
        return field8;
    }

    /**
     * 设置field8属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField8(String value) {
        this.field8 = value;
    }

    /**
     * 获取field9属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField9() {
        return field9;
    }

    /**
     * 设置field9属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField9(String value) {
        this.field9 = value;
    }

    /**
     * 获取field10属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField10() {
        return field10;
    }

    /**
     * 设置field10属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField10(String value) {
        this.field10 = value;
    }

    /**
     * 获取field11属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField11() {
        return field11;
    }

    /**
     * 设置field11属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField11(String value) {
        this.field11 = value;
    }

    /**
     * 获取field12属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField12() {
        return field12;
    }

    /**
     * 设置field12属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField12(String value) {
        this.field12 = value;
    }

    /**
     * 获取field13属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField13() {
        return field13;
    }

    /**
     * 设置field13属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField13(String value) {
        this.field13 = value;
    }

    /**
     * 获取field14属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField14() {
        return field14;
    }

    /**
     * 设置field14属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField14(String value) {
        this.field14 = value;
    }

    /**
     * 获取field15属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField15() {
        return field15;
    }

    /**
     * 设置field15属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField15(String value) {
        this.field15 = value;
    }

    /**
     * 获取field16属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField16() {
        return field16;
    }

    /**
     * 设置field16属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField16(String value) {
        this.field16 = value;
    }

    /**
     * 获取field17属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField17() {
        return field17;
    }

    /**
     * 设置field17属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField17(String value) {
        this.field17 = value;
    }

    /**
     * 获取field18属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField18() {
        return field18;
    }

    /**
     * 设置field18属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField18(String value) {
        this.field18 = value;
    }

    /**
     * 获取field19属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField19() {
        return field19;
    }

    /**
     * 设置field19属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField19(String value) {
        this.field19 = value;
    }

    /**
     * 获取field20属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getField20() {
        return field20;
    }

    /**
     * 设置field20属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setField20(String value) {
        this.field20 = value;
    }

    /**
     * Gets the value of the mddField property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mddField property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMddField().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MDDField }
     * 
     * 
     */
    public List<MDDField> getMddField() {
        if (mddField == null) {
            mddField = new ArrayList<MDDField>();
        }
        return this.mddField;
    }

}
