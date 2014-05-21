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
 * 		conception and documentation.
 *     Mahdi Ben Alaya (Project co-founder) - Management and initial specification, 
 * 		conception, implementation, test and documentation.
 *     Christophe Chassot - Management and initial specification.
 *     Khalil Drira - Management and initial specification.
 *     Yassine Banouar - Initial specification, conception, implementation, test 
 * 		and documentation.
 ******************************************************************************/
package org.eclipse.om2m.core.controller;

import org.eclipse.om2m.commons.resource.AccessRightAnnc;
import org.eclipse.om2m.commons.resource.AccessRights;
import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.commons.utils.XmlMapper;
import org.eclipse.om2m.core.constants.Constants;
import org.eclipse.om2m.core.dao.DAOFactory;
import org.eclipse.om2m.core.notifier.Notifier;

/**
 * Implements Create, Retrieve, Update, Delete and Execute methods to handle
 * generic REST request for {@link AccessRightAnnc} resource.
 *
 * @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         </ul>
 */

public class AccessRightAnncController extends Controller {

    /**
     * Creates {@link AccessRightAnnc} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doCreate(RequestIndication requestIndication) {

        // Link:            (createReq M) (response M)
        // accessRightID:   (createReq O) (response O)
        // searchStrings:   (createReq M) (response M)
        // expirationTime:  (createReq O) (response M*)
        // Id:              (createReq O) (response M*)

        ResponseConfirm errorResponse = new ResponseConfirm();
        AccessRights accessRights = DAOFactory.getAccessRightsDAO().lazyFind(requestIndication.getTargetID());

        // Check Resource Parent Existence
        if (accessRights == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(accessRights.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_CREATE);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Check Resource Representation
        if (requestIndication.getRepresentation() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Resource Representation is EMPTY")) ;
        }
        // Check XML Validity
        errorResponse = checkMessageSyntax(requestIndication.getRepresentation(),"accessRightAnnc.xsd");
        if (errorResponse != null) {
            return errorResponse;
        }
        // Checks on attributes
        AccessRightAnnc accessRightAnnc = (AccessRightAnnc) XmlMapper.getInstance().xmlToObject(requestIndication.getRepresentation());
        // Check the Id uniqueness
        if (accessRightAnnc.getId() != null && DAOFactory.getApplicationAnncDAO().find(requestIndication.getTargetID()+"/"+accessRightAnnc.getId()) != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_CONFLICT,"ApplicationAnncId Conflit")) ;
        }
        // Generate the id it it does not exist
        if (accessRightAnnc.getId() == null || accessRightAnnc.getId().isEmpty()) {
            accessRightAnnc.setId(generateId("AR_","Annc"));
        }
        // SearchStrings Attribute is mandatory
        if (accessRightAnnc.getSearchStrings() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"searchStrings attribute CREATE is Mandatory")) ;
        }
        // Link is Mandatory
        if (accessRightAnnc.getLink() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Link attribute is Mandatory")) ;
        }
        // Check ExpirationTime
        if (accessRightAnnc.getExpirationTime() != null && !checkExpirationTime(accessRightAnnc.getExpirationTime())) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Expiration Time is Out of Date")) ;
        }
        // Storage
        // Set URI
        accessRightAnnc.setUri(requestIndication.getTargetID()+ "/" +accessRightAnnc.getId());
        // Set Expiration Time if it is null
        if (accessRightAnnc.getExpirationTime() == null) {
            //default Expiration Time
            accessRightAnnc.setExpirationTime(getNewExpirationTime(Constants.EXPIRATION_TIME));
        }
        // Set AccessRightID from the Parent if it's null or nonexistent
        if (DAOFactory.getAccessRightDAO().find(accessRightAnnc.getAccessRightID()) == null) {
            accessRightAnnc.setAccessRightID(accessRights.getAccessRightID());
        }
        // Notify the subscribers
        Notifier.notify(StatusCode.STATUS_CREATED, accessRightAnnc);

        // Store
        DAOFactory.getAccessRightAnncDAO().create(accessRightAnnc);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_CREATED, accessRightAnnc);
    }

    /**
     * Retrieves {@link AccessRightAnnc} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doRetrieve(RequestIndication requestIndication) {

        // Link:            (response M)
        // accessRightID:   (response O)
        // searchStrings:   (response M)
        // expirationTime:  (response M*)
        // Id:              (response M*)

        ResponseConfirm errorResponse = new ResponseConfirm();
        AccessRightAnnc accessRightAnnc = DAOFactory.getAccessRightAnncDAO().find(requestIndication.getTargetID());

        // Check if the resource exists in DataBase or Not
        if (accessRightAnnc == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist in DataBase")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(accessRightAnnc.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_READ);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, accessRightAnnc);
    }

    /**
     * Updates {@link AccessRightAnnc} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doUpdate(RequestIndication requestIndication) {

        // Link:            (updateReq NP) (response M)
        // accessRightID:   (updateReq O)  (response O)
        // searchStrings:   (updateReq M)  (response M)
        // expirationTime:  (updateReq O)  (response M*)
        // Id:              (updateReq NP) (response M*)

        ResponseConfirm errorResponse = new ResponseConfirm();
        AccessRightAnnc accessRightAnnc = DAOFactory.getAccessRightAnncDAO().find(requestIndication.getTargetID());

        // Check resource Existence
        if (accessRightAnnc == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist in DataBase")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(accessRightAnnc.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_WRITE);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Check Resource Representation
        if (requestIndication.getRepresentation() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Resource Representation is EMPTY")) ;
        }
        // Check XML Validity
        errorResponse = checkMessageSyntax(requestIndication.getRepresentation(),"accessRightAnnc.xsd");
        if (errorResponse != null) {
            return errorResponse;
        }
        // Checks on attributes
        AccessRightAnnc accessRightAnncNew = (AccessRightAnnc) XmlMapper.getInstance().xmlToObject(requestIndication.getRepresentation());
        // The Update of the Id is NP
        if (accessRightAnncNew.getId() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"ContainerAnncId UPDATE is Not Permitted")) ;
        }
        // Check ExpirationTime
        if (accessRightAnncNew.getExpirationTime() != null && !checkExpirationTime(accessRightAnncNew.getExpirationTime())) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Expiration Time UPDATE is Out of Date")) ;
        }
        // Link Must be NP
        if (accessRightAnncNew.getLink() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Link attribute UPDATE is Mandatory")) ;
        }
        // SearchStrings Attribute is mandatory
        if (accessRightAnncNew.getSearchStrings() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"searchStrings attribute UPDATE is Mandatory")) ;
        }
        // Storage
        // Set Expiration Time
        if (accessRightAnncNew.getExpirationTime() != null) {
            accessRightAnnc.setExpirationTime(accessRightAnncNew.getExpirationTime());
        }
        // Set accessRightID if it exists
        if (DAOFactory.getAccessRightDAO().find(accessRightAnncNew.getAccessRightID()) != null) {
            accessRightAnnc.setAccessRightID(accessRightAnncNew.getAccessRightID());
        }
        // Set searchStrings
        accessRightAnnc.setSearchStrings(accessRightAnncNew.getSearchStrings());

        // Notify the subscribers
        Notifier.notify(StatusCode.STATUS_OK, accessRightAnnc);

        // Store
        DAOFactory.getAccessRightAnncDAO().update(accessRightAnnc);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, accessRightAnnc);
    }

    /**
     * Deletes {@link AccessRightAnnc} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doDelete(RequestIndication requestIndication) {

        ResponseConfirm errorResponse = new ResponseConfirm();
        AccessRightAnnc accessRightAnnc = DAOFactory.getAccessRightAnncDAO().find(requestIndication.getTargetID());

        // Check Resource Existence
        if (accessRightAnnc == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(accessRightAnnc.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_DELETE);
        if (errorResponse != null) {
            return errorResponse;
        }

        // Notify the subscribers
        Notifier.notify(StatusCode.STATUS_DELETED, accessRightAnnc);

        // Delete
        DAOFactory.getAccessRightAnncDAO().delete(accessRightAnnc);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK);
    }

    /**
     * Executes {@link AccessRightAnnc} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doExecute(RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }
}
