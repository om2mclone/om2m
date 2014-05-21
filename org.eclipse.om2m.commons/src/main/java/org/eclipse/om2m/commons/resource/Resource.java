package org.eclipse.om2m.commons.resource;

/**
 * Resource represents a generic resource representation
 */

import javax.xml.bind.annotation.XmlTransient;

public abstract class Resource {

    protected String uri;
    protected SearchStrings searchStrings;
    protected String accessRightID;

    /**
     * Gets the value of the property uri.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @XmlTransient
    public String getUri() {
        return uri;
    }

    /**
     * Sets the value of the property uri.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Gets the value of the property accessRightID.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAccessRightID() {
        return accessRightID;
    }

    /**
     * Sets the value of the property accessRightID.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAccessRightID(String value) {
        this.accessRightID = value;
    }

    /**
     * Gets the value of the searchStrings property.
     *
     * @return possible object is {@link SearchStrings }
     *
     */
    public SearchStrings getSearchStrings() {
        return searchStrings;
    }

    /**
     * Sets the value of the searchStrings property.
     *
     * @param value
     *            allowed object is {@link SearchStrings }
     *
     */
    public void setSearchStrings(SearchStrings value) {
        this.searchStrings = value;
    }
}
