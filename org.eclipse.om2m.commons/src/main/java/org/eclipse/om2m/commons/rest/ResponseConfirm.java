/*******************************************************************************
 * Copyright (c) 2013-2014 LAAS-CNRS (www.laas.fr)
 * 7 Colonel Roche 31077 Toulouse - France
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Thierry Monteil (Project co-founder) - Management and initial specification,
 *         conception and documentation.
 *     Mahdi Ben Alaya (Project co-founder) - Management and initial specification,
 *         conception, implementation, test and documentation.
 *     Christophe Chassot - Management and initial specification.
 *     Khalil Drira - Management and initial specification.
 *     Yassine Banouar - Initial specification, conception, implementation, test
 *         and documentation.
 ******************************************************************************/
package org.eclipse.om2m.commons.rest;

import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.Resource;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.utils.XmlMapper;

/**
 *  Defines a generic, protocol-independent object to assist the SCL in sending a response to the client.
 *  @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         </ul>
 */
public class ResponseConfirm {
    /** Response status code */
    private StatusCode statusCode;
    /** Resource representation */
    private String representation;
    /** Created resource uri */
    private String resourceURI;

    /** Default ResponseConfirm Constructor */
    public ResponseConfirm() {
    }

    /**
     * ResponseConfirm Constructor.
     * @param statusCode
     */
    public ResponseConfirm(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * ResponseConfirm Constructor.
     * @param statusCode
     * @param representation
     */
    public ResponseConfirm(StatusCode statusCode, String representation) {
        this.statusCode = statusCode;
        this.representation = representation;
    }

    /**
     * ResponseConfirm Constructor.
     * @param statusCode
     * @param resource
     */
    public ResponseConfirm(StatusCode statusCode, Resource resource) {
        super();
        this.statusCode = statusCode;
        this.representation = XmlMapper.getInstance().objectToXml(resource);
        this.resourceURI = resource.getUri();
    }

    /**
     * ResponseConfirm Constructor.
     * @param errorInfo
     */
    public ResponseConfirm(ErrorInfo errorInfo) {
        super();
        this.statusCode = errorInfo.getStatusCode();
        this.representation = XmlMapper.getInstance().objectToXml(errorInfo);
    }

    /**
     * Gets the current statusCode
     * @return
     */
    public StatusCode getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the statusCode
     * @return statusCode
     */
    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Gets the current resourceURI
     * @return resourceURI
     */
    public String getResourceURI() {
        return resourceURI;
    }

    /**
     * Sets the resourceURI
     * @return resourceURI
     */
    public void setResourceURI(String resourceURI) {
        this.resourceURI = resourceURI;
    }

    /**
     * Gets the current representation
     * @return representation
     */
    public String getRepresentation() {
        return representation;
    }

    /**
     * Sets the representation
     * @return representation
     */
    public void setRepresentation(String representation) {
        this.representation = representation;
    }

    @Override
    public String toString() {
        return "ResponseConfirm [statusCode=" + statusCode
                + ", representation=" + representation + ", resourceURI="
                + resourceURI + "]";
    }
}
