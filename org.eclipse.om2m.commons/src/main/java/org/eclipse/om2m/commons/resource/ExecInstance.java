//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2014.01.25 at 05:56:27 PM CET
//


package org.eclipse.om2m.commons.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;



/**
 * <p>Java class for ExecInstance complex type.
 *
 * <p>ExecInstance resource represents an ongoing execution request instance, triggered by an M2M
 * network application using UPDATE method to the attribute "execute" of {@link MgmtCmd}.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ExecInstance">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://uri.etsi.org/m2m}expirationTime" minOccurs="0"/>
 *         &lt;element ref="{http://uri.etsi.org/m2m}accessRightID" minOccurs="0"/>
 *         &lt;element ref="{http://uri.etsi.org/m2m}creationTime" minOccurs="0"/>
 *         &lt;element ref="{http://uri.etsi.org/m2m}lastModifiedTime" minOccurs="0"/>
 *         &lt;element ref="{http://uri.etsi.org/m2m}execStatus" minOccurs="0"/>
 *         &lt;element ref="{http://uri.etsi.org/m2m}execResult" minOccurs="0"/>
 *         &lt;element ref="{http://uri.etsi.org/m2m}execDisable" minOccurs="0"/>
 *         &lt;element ref="{http://uri.etsi.org/m2m}subscriptionsReference" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://uri.etsi.org/m2m}id"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class ExecInstance extends Resource {

    @XmlSchemaType(name = "dateTime")
    protected String expirationTime;
    @XmlSchemaType(name = "dateTime")
    protected String creationTime;
    @XmlSchemaType(name = "dateTime")
    protected String lastModifiedTime;
    protected ExecStatus execStatus;
    protected ExecResultList execResult;
    @XmlSchemaType(name = "anyURI")
    protected String execDisable;
    @XmlSchemaType(name = "anyURI")
    protected String subscriptionsReference;
    @XmlAttribute(name = "id", namespace = "http://uri.etsi.org/m2m")
    @XmlSchemaType(name = "anyURI")
    protected String id;

    /**
     * Gets the value of the expirationTime property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExpirationTime() {
        return expirationTime;
    }

    /**
     * Sets the value of the expirationTime property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExpirationTime(String value) {
        this.expirationTime = value;
    }

    /**
     * Gets the value of the creationTime property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCreationTime() {
        return creationTime;
    }

    /**
     * Sets the value of the creationTime property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCreationTime(String value) {
        this.creationTime = value;
    }

    /**
     * Gets the value of the lastModifiedTime property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    /**
     * Sets the value of the lastModifiedTime property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLastModifiedTime(String value) {
        this.lastModifiedTime = value;
    }

    /**
     * Gets the value of the execStatus property.
     *
     * @return
     *     possible object is
     *     {@link ExecStatus }
     *
     */
    public ExecStatus getExecStatus() {
        return execStatus;
    }

    /**
     * Sets the value of the execStatus property.
     *
     * @param value
     *     allowed object is
     *     {@link ExecStatus }
     *
     */
    public void setExecStatus(ExecStatus value) {
        this.execStatus = value;
    }

    /**
     * Gets the value of the execResult property.
     *
     * @return
     *     possible object is
     *     {@link ExecResultList }
     *
     */
    public ExecResultList getExecResult() {
        return execResult;
    }

    /**
     * Sets the value of the execResult property.
     *
     * @param value
     *     allowed object is
     *     {@link ExecResultList }
     *
     */
    public void setExecResult(ExecResultList value) {
        this.execResult = value;
    }

    /**
     * Gets the value of the execDisable property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExecDisable() {
        return execDisable;
    }

    /**
     * Sets the value of the execDisable property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExecDisable(String value) {
        this.execDisable = value;
    }

    /**
     * Gets the value of the subscriptionsReference property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSubscriptionsReference() {
        return subscriptionsReference;
    }

    /**
     * Sets the value of the subscriptionsReference property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSubscriptionsReference(String value) {
        this.subscriptionsReference = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

    public String toString() {
        return "ExecInstance [expirationTime=" + expirationTime
                + ", accessRightID=" + accessRightID + ", creationTime="
                + creationTime + ", lastModifiedTime=" + lastModifiedTime
                + ", execStatus=" + execStatus + ", execResult=" + execResult
                + ", execDisable=" + execDisable + ", subscriptionsReference="
                + subscriptionsReference + ", id=" + id + "]";
    }

}
