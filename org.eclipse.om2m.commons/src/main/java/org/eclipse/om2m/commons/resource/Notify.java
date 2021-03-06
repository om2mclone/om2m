//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2013.07.17 at 02:24:48 PM CEST
//


package org.eclipse.om2m.commons.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java Class for Notify complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Notify">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://uri.etsi.org/m2m}statusCode"/>
 *         &lt;element name="representation" type="{http://www.w3.org/2005/05/xmlmime}base64Binary" minOccurs="0"/>
 *         &lt;element name="subscriptionReference" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Notify", propOrder = {
        "statusCode",
        "representation",
        "subscriptionReference"
})
public class Notify {

    @XmlElement(namespace = "http://uri.etsi.org/m2m", required = true)
    protected StatusCode statusCode;
    protected Base64Binary representation;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String subscriptionReference;


    public Notify(){
        super();
        this.representation = new Base64Binary();
    }

    /**
     * Gets the value of the property statusCode.
     *
     * @return
     *     possible object is
     *     {@link StatusCode }
     *
     */
    public StatusCode getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the value of the property statusCode.
     *
     * @param value
     *     allowed object is
     *     {@link StatusCode }
     *
     */
    public void setStatusCode(StatusCode value) {
        this.statusCode = value;
    }

    /**
     * Gets the value of the property representation.
     *
     * @return
     *     possible object is
     *     {@link Base64Binary }
     *
     */
    public Base64Binary getRepresentation() {
        return representation;
    }

    /**
     * Sets the value of the property representation.
     *
     * @param value
     *     allowed object is
     *     {@link Base64Binary }
     *
     */
    public void setRepresentation(Base64Binary value) {
        this.representation = value;
    }

    /**
     * Gets the value of the property subscriptionReference.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSubscriptionReference() {
        return subscriptionReference;
    }

    /**
     * Sets the value of the property subscriptionReference.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSubscriptionReference(String value) {
        this.subscriptionReference = value;
    }

}
