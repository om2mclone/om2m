//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2013.07.08 at 01:02:29 PM CEST
//


package org.eclipse.om2m.commons.resource;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java Class for RcatType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RcatType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RCAT_0"/>
 *     &lt;enumeration value="RCAT_1"/>
 *     &lt;enumeration value="RCAT_2"/>
 *     &lt;enumeration value="RCAT_3"/>
 *     &lt;enumeration value="RCAT_4"/>
 *     &lt;enumeration value="RCAT_5"/>
 *     &lt;enumeration value="RCAT_6"/>
 *     &lt;enumeration value="RCAT_7"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "RcatType")
@XmlEnum
public enum RcatType {

    RCAT_0,
    RCAT_1,
    RCAT_2,
    RCAT_3,
    RCAT_4,
    RCAT_5,
    RCAT_6,
    RCAT_7;

    public String value() {
        return name();
    }

    public static RcatType fromValue(String v) {
        return valueOf(v);
    }

}
