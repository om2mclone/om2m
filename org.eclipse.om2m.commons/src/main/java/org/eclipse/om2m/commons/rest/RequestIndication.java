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

import java.util.List;
import java.util.Map;

import org.eclipse.om2m.commons.resource.Notify;
import org.eclipse.om2m.commons.resource.Resource;
import org.eclipse.om2m.commons.utils.XmlMapper;

/**
 *
 * Defines a generic, protocol-independent object to provide request information to the SCL.
 *  @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         </ul>
 */
public class RequestIndication {
    /** Request Rest method */
    private String method;
    /** Request communication protocol */
    private String protocol;
    /** Request base uri */
    private String base;
    /** resource targetID uri */
    private String targetID;
    /** resource representation */
    private String representation;
    /** Issuer Requesting Entity */
    private String requestingEntity;
    /** Request parameters */
    private Map<String, List<String>> parameters;

    /**
     * RequestIndication default Constructor.
     */
    public RequestIndication(){

    }

    /**
     * RequestIndication Constructor using resource XML representation as argument.
     * @param method - request rest method
     * @param targetID - request targetID uri
     * @param requestingEntity - issuer Requesting Entity
     * @param representation - resource representation
     */
    public RequestIndication(String method, String targetID, String requestingEntity, String representation) {
        this.method = method;
        this.targetID = targetID;
        this.representation = representation;
        this.requestingEntity = requestingEntity;
    }

    /**
     * RequestIndication Constructor using resource Java object as argument.
     *
     * @param method - request rest method
     * @param targetID - request targetID uri
     * @param requestingEntity - issuer Requesting Entity
     * @param representation - resource Java object
     */
    public RequestIndication(String method, String targetID, String requestingEntity, Resource resource) {
        this.method = method;
        this.targetID = targetID;
        this.representation = XmlMapper.getInstance().objectToXml(resource);
        this.requestingEntity = requestingEntity;
    }

    /**
     * Gets the current method.
     * @return method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the method
     * @param method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Gets the current requestingEntity.
     * @return requestingEntity
     */
    public String getRequestingEntity() {
        return requestingEntity;
    }

    /**
     * Gets the current targetID.
     * @return targetID
     */
    public String getTargetID() {
        return targetID;
    }

    /**
     * Sets the targetID
     * @param targetID
     */
    public void setTargetID(String targetID) {
        this.targetID = targetID;
    }

    /**
     * Gets the current representation.
     * @return representation
     */
    public String getRepresentation() {
        return representation;
    }

    /**
     * Sets the representation
     * @param representation
     */
    public void setRepresentation(String representation) {
        this.representation = representation;
    }

    /**
     * Sets the resource representation using a Resource object
     * @param resource
     */
    public void setRepresentation(Resource resource) {
        this.representation = XmlMapper.getInstance().objectToXml(resource);
    }

    /**
     * Sets the resource representation using a Notify object
     * @param notify
     */
    public void setRepresentation(Notify notify) {
        this.representation = XmlMapper.getInstance().objectToXml(notify);
    }

    /**
     * Gets the base uri concatenated with the targetID.
     * @return base concatenated with targetUD
     */
    public String getUrl(){
        return base+targetID;
    }

    /**
     * Sets the requestingEntity
     * @param requestingEntity
     */
    public void setRequestingEntity(String requestingEntity) {
        this.requestingEntity = requestingEntity;
    }
    /**
     * Gets the current base.
     * @return base
     */
    public String getBase() {
        return base;
    }

    /**
     * Sets the base uti
     * @param base
     */
    public void setBase(String base) {
        this.base = base;
    }
    /**
     * Gets the current parameters.
     * @return parameters
     */
    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    /**
     * Sets the base parameters
     * @param parameters
     */
    public void setParameters(Map<String, List<String>> parameters) {
        this.parameters = parameters;
    }
    /**
     * Gets the current protocol.
     * @return protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Sets the request protocol
     * @param protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        return "RequestIndication [method=" + method + ", base=" + base
                + ", targetID=" + targetID + ", representation="
                + representation + ", requestingEntity=" + requestingEntity
                + ", protocol=" + protocol +"]";
    }
}
