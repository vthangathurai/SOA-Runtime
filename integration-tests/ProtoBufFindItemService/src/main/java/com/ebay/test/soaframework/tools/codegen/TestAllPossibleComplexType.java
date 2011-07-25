
package com.ebay.test.soaframework.tools.codegen;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * <p>Java class for TestAllPossibleComplexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TestAllPossibleComplexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="param1" type="{http://codegen.tools.soaframework.test.ebay.com}testtoken" minOccurs="0"/>
 *         &lt;element name="param2" type="{http://codegen.tools.soaframework.test.ebay.com}teststring" minOccurs="0"/>
 *         &lt;element name="param3" type="{http://codegen.tools.soaframework.test.ebay.com}testdecimal" minOccurs="0"/>
 *         &lt;element name="param4" type="{http://codegen.tools.soaframework.test.ebay.com}testinteger" minOccurs="0"/>
 *         &lt;element name="param5" type="{http://codegen.tools.soaframework.test.ebay.com}MySimpleTypeList" minOccurs="0"/>
 *         &lt;element name="param6" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="param7" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="param8" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="param9" type="{http://www.w3.org/2001/XMLSchema}ID" minOccurs="0"/>
 *         &lt;element name="param11" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" minOccurs="0"/>
 *         &lt;element name="param12" type="{http://www.w3.org/2001/XMLSchema}NMTOKENS" minOccurs="0"/>
 *         &lt;element name="param14" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="param15" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="param17" type="{http://www.w3.org/2001/XMLSchema}byte" minOccurs="0"/>
 *         &lt;element name="param19" type="{http://www.w3.org/2001/XMLSchema}duration" minOccurs="0"/>
 *         &lt;element name="param20" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="param21" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/>
 *         &lt;element name="param22" type="{http://www.w3.org/2001/XMLSchema}gYearMonth" minOccurs="0"/>
 *         &lt;element name="param23" type="{http://www.w3.org/2001/XMLSchema}gYear" minOccurs="0"/>
 *         &lt;element name="param24" type="{http://www.w3.org/2001/XMLSchema}gMonthDay" minOccurs="0"/>
 *         &lt;element name="param25" type="{http://www.w3.org/2001/XMLSchema}gDay" minOccurs="0"/>
 *         &lt;element name="param26" type="{http://www.w3.org/2001/XMLSchema}gMonth" minOccurs="0"/>
 *         &lt;element name="param27" type="{http://www.w3.org/2001/XMLSchema}hexBinary" minOccurs="0"/>
 *         &lt;element name="param28" type="{http://www.w3.org/2001/XMLSchema}QName" minOccurs="0"/>
 *         &lt;element name="param29" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" minOccurs="0"/>
 *         &lt;element name="param30" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="param31" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="param32" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" minOccurs="0"/>
 *         &lt;element name="param33" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TestAllPossibleComplexType", propOrder = {
    "param1",
    "param2",
    "param3",
    "param4",
    "param5",
    "param6",
    "param7",
    "param8",
    "param9",
    "param11",
    "param12",
    "param14",
    "param15",
    "param17",
    "param19",
    "param20",
    "param21",
    "param22",
    "param23",
    "param24",
    "param25",
    "param26",
    "param27",
    "param28",
    "param29",
    "param30",
    "param31",
    "param32",
    "param33"
})
public class TestAllPossibleComplexType {

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String param1;
    protected String param2;
    protected BigDecimal param3;
    protected Integer param4;
    @XmlList
    @XmlElement(type = Double.class)
    protected List<Double> param5;
    protected Integer param6;
    protected Long param7;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger param8;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String param9;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String param11;
    @XmlList
    @XmlSchemaType(name = "NMTOKENS")
    protected List<String> param12;
    @XmlSchemaType(name = "anyURI")
    protected String param14;
    protected Boolean param15;
    protected Byte param17;
    protected Duration param19;
    protected XMLGregorianCalendar param20;
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar param21;
    @XmlSchemaType(name = "gYearMonth")
    protected XMLGregorianCalendar param22;
    @XmlSchemaType(name = "gYear")
    protected XMLGregorianCalendar param23;
    @XmlSchemaType(name = "gMonthDay")
    protected XMLGregorianCalendar param24;
    @XmlSchemaType(name = "gDay")
    protected XMLGregorianCalendar param25;
    @XmlSchemaType(name = "gMonth")
    protected XMLGregorianCalendar param26;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlSchemaType(name = "hexBinary")
    protected byte[] param27;
    protected QName param28;
    @XmlSchemaType(name = "unsignedByte")
    protected Short param29;
    @XmlSchemaType(name = "unsignedInt")
    protected Long param30;
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger param31;
    @XmlSchemaType(name = "unsignedShort")
    protected Integer param32;
    protected Short param33;

    /**
     * Gets the value of the param1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParam1() {
        return param1;
    }

    /**
     * Sets the value of the param1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParam1(String value) {
        this.param1 = value;
    }

    /**
     * Gets the value of the param2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParam2() {
        return param2;
    }

    /**
     * Sets the value of the param2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParam2(String value) {
        this.param2 = value;
    }

    /**
     * Gets the value of the param3 property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getParam3() {
        return param3;
    }

    /**
     * Sets the value of the param3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setParam3(BigDecimal value) {
        this.param3 = value;
    }

    /**
     * Gets the value of the param4 property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getParam4() {
        return param4;
    }

    /**
     * Sets the value of the param4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setParam4(Integer value) {
        this.param4 = value;
    }

    /**
     * Gets the value of the param5 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the param5 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParam5().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * 
     * 
     */
    public List<Double> getParam5() {
        if (param5 == null) {
            param5 = new ArrayList<Double>();
        }
        return this.param5;
    }

    /**
     * Gets the value of the param6 property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getParam6() {
        return param6;
    }

    /**
     * Sets the value of the param6 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setParam6(Integer value) {
        this.param6 = value;
    }

    /**
     * Gets the value of the param7 property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getParam7() {
        return param7;
    }

    /**
     * Sets the value of the param7 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setParam7(Long value) {
        this.param7 = value;
    }

    /**
     * Gets the value of the param8 property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getParam8() {
        return param8;
    }

    /**
     * Sets the value of the param8 property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setParam8(BigInteger value) {
        this.param8 = value;
    }

    /**
     * Gets the value of the param9 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParam9() {
        return param9;
    }

    /**
     * Sets the value of the param9 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParam9(String value) {
        this.param9 = value;
    }

    /**
     * Gets the value of the param11 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParam11() {
        return param11;
    }

    /**
     * Sets the value of the param11 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParam11(String value) {
        this.param11 = value;
    }

    /**
     * Gets the value of the param12 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the param12 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParam12().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getParam12() {
        if (param12 == null) {
            param12 = new ArrayList<String>();
        }
        return this.param12;
    }

    /**
     * Gets the value of the param14 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParam14() {
        return param14;
    }

    /**
     * Sets the value of the param14 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParam14(String value) {
        this.param14 = value;
    }

    /**
     * Gets the value of the param15 property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isParam15() {
        return param15;
    }

    /**
     * Sets the value of the param15 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setParam15(Boolean value) {
        this.param15 = value;
    }

    /**
     * Gets the value of the param17 property.
     * 
     * @return
     *     possible object is
     *     {@link Byte }
     *     
     */
    public Byte getParam17() {
        return param17;
    }

    /**
     * Sets the value of the param17 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Byte }
     *     
     */
    public void setParam17(Byte value) {
        this.param17 = value;
    }

    /**
     * Gets the value of the param19 property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getParam19() {
        return param19;
    }

    /**
     * Sets the value of the param19 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setParam19(Duration value) {
        this.param19 = value;
    }

    /**
     * Gets the value of the param20 property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getParam20() {
        return param20;
    }

    /**
     * Sets the value of the param20 property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setParam20(XMLGregorianCalendar value) {
        this.param20 = value;
    }

    /**
     * Gets the value of the param21 property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getParam21() {
        return param21;
    }

    /**
     * Sets the value of the param21 property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setParam21(XMLGregorianCalendar value) {
        this.param21 = value;
    }

    /**
     * Gets the value of the param22 property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getParam22() {
        return param22;
    }

    /**
     * Sets the value of the param22 property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setParam22(XMLGregorianCalendar value) {
        this.param22 = value;
    }

    /**
     * Gets the value of the param23 property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getParam23() {
        return param23;
    }

    /**
     * Sets the value of the param23 property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setParam23(XMLGregorianCalendar value) {
        this.param23 = value;
    }

    /**
     * Gets the value of the param24 property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getParam24() {
        return param24;
    }

    /**
     * Sets the value of the param24 property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setParam24(XMLGregorianCalendar value) {
        this.param24 = value;
    }

    /**
     * Gets the value of the param25 property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getParam25() {
        return param25;
    }

    /**
     * Sets the value of the param25 property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setParam25(XMLGregorianCalendar value) {
        this.param25 = value;
    }

    /**
     * Gets the value of the param26 property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getParam26() {
        return param26;
    }

    /**
     * Sets the value of the param26 property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setParam26(XMLGregorianCalendar value) {
        this.param26 = value;
    }

    /**
     * Gets the value of the param27 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public byte[] getParam27() {
        return param27;
    }

    /**
     * Sets the value of the param27 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParam27(byte[] value) {
        this.param27 = ((byte[]) value);
    }

    /**
     * Gets the value of the param28 property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getParam28() {
        return param28;
    }

    /**
     * Sets the value of the param28 property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setParam28(QName value) {
        this.param28 = value;
    }

    /**
     * Gets the value of the param29 property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getParam29() {
        return param29;
    }

    /**
     * Sets the value of the param29 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setParam29(Short value) {
        this.param29 = value;
    }

    /**
     * Gets the value of the param30 property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getParam30() {
        return param30;
    }

    /**
     * Sets the value of the param30 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setParam30(Long value) {
        this.param30 = value;
    }

    /**
     * Gets the value of the param31 property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getParam31() {
        return param31;
    }

    /**
     * Sets the value of the param31 property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setParam31(BigInteger value) {
        this.param31 = value;
    }

    /**
     * Gets the value of the param32 property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getParam32() {
        return param32;
    }

    /**
     * Sets the value of the param32 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setParam32(Integer value) {
        this.param32 = value;
    }

    /**
     * Gets the value of the param33 property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getParam33() {
        return param33;
    }

    /**
     * Sets the value of the param33 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setParam33(Short value) {
        this.param33 = value;
    }

}
