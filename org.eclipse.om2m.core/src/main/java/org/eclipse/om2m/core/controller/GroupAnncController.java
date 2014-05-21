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

import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.GroupAnnc;
import org.eclipse.om2m.commons.resource.Groups;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.commons.utils.XmlMapper;
import org.eclipse.om2m.core.constants.Constants;
import org.eclipse.om2m.core.dao.DAOFactory;
import org.eclipse.om2m.core.notifier.Notifier;

/**
 * Implements Create, Retrieve, Update, Delete and Execute methods to handle
 * generic REST request for {@link GroupAnnc} resource.
 *
 * @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         </ul>
 */

public class GroupAnncController extends Controller {

    /**
     * Creates {@link GroupAnnc} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doCreate (RequestIndication requestIndication) {

        // Link:            (createReq M)  (response M)
        // accessRightID:   (createReq O)  (response O)
        // searchStrings:   (createReq M)  (response M)
        // expirationTime:  (createReq O)  (response M*)
        // Id:              (createReq O)  (response M*)

        ResponseConfirm errorResponse = new ResponseConfirm();
        Groups groups = DAOFactory.getGroupsDAO().lazyFind(requestIndication.getTargetID());

        // Check Resource Parent Existence
        if (groups == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(groups.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_CREATE);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Check Resource Representation
        if (requestIndication.getRepresentation() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Resource Representation is EMPTY")) ;
        }
        // Check XML Validity
        errorResponse = checkMessageSyntax(requestIndication.getRepresentation(),"groupAnnc.xsd");
        if (errorResponse != null) {
            return errorResponse;
        }
        // Checks on attributes
        GroupAnnc groupAnnc = (GroupAnnc) XmlMapper.getInstance().xmlToObject(requestIndication.getRepresentation());
        // Check the Id uniqueness
        if (groupAnnc.getId() != null && DAOFactory.getApplicationAnncDAO().find(requestIndication.getTargetID()+"/"+groupAnnc.getId()) != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_CONFLICT,"ApplicationAnncId Conflit")) ;
        }
        // Generate the id it it does not exist
        if (groupAnnc.getId() == null || groupAnnc.getId().isEmpty()) {
            groupAnnc.setId(generateId("GRP_","Annc"));
        }
        // SearchStrings Attribute is mandatory
        if (groupAnnc.getSearchStrings() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"searchStrings attribute CREATE is Mandatory")) ;
        }
        // Link is Mandatory
        if (groupAnnc.getLink() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Link attribute is Mandatory")) ;
        }
        // Check ExpirationTime
        if (groupAnnc.getExpirationTime() != null && !checkExpirationTime(groupAnnc.getExpirationTime())) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Expiration Time is Out of Date")) ;
        }
        // Storage
        // Set URI
        groupAnnc.setUri(requestIndication.getTargetID()+ "/" +groupAnnc.getId());
        // Set Expiration Time if it is null
        if (groupAnnc.getExpirationTime() == null) {
            //default Expiration Time
            groupAnnc.setExpirationTime(getNewExpirationTime(Constants.EXPIRATION_TIME));
        }
        // Set AccessRightID from the Parent if it's null or nonexistent
        if (DAOFactory.getAccessRightDAO().find(groupAnnc.getAccessRightID()) == null) {
            groupAnnc.setAccessRightID(groups.getAccessRightID());
        }
        // Notify the subscribers
        Notifier.notify(StatusCode.STATUS_CREATED, groupAnnc);

        // Store
        DAOFactory.getGroupAnncDAO().create(groupAnnc);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_CREATED, groupAnnc);
    }

    /**
     * Retrieves {@link GroupAnnc} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doRetrieve (RequestIndication requestIndication) {

        // Link:            (response M)
        // accessRightID:   (response O)
        // searchStrings:   (response M)
        // expirationTime:  (response M*)
        // Id:              (response M*)

        ResponseConfirm errorResponse = new ResponseConfirm();
        GroupAnnc groupAnnc = DAOFactory.getGroupAnncDAO().find(requestIndication.getTargetID());

        // Check if the resource exists in DataBase or Not
        if (groupAnnc == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist in DataBase")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(groupAnnc.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_READ);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, groupAnnc);
    }

    /**
     * Updates {@link GroupAnnc} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doUpdate (RequestIndication requestIndication) {

        // Link:            (updateReq NP)  (response M)
        // accessRightID:   (updateReq O)  (response O)
        // searchStrings:   (updateReq M)  (response M)
        // expirationTime:  (updateReq O)  (response M*)
        // Id:              (updateReq NP)  (response M*)

        ResponseConfirm errorResponse = new ResponseConfirm();
        GroupAnnc groupAnnc = DAOFactory.getGroupAnncDAO().find(requestIndication.getTargetID());

        // Check resource Existence
        if (groupAnnc == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist in DataBase")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(groupAnnc.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_WRITE);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Check Resource Representation
        if (requestIndication.getRepresentation() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Resource Representation is EMPTY")) ;
        }
        // Check XML Validity
        errorResponse = checkMessageSyntax(requestIndication.getRepresentation(),"groupAnnc.xsd");
        if (errorResponse != null) {
            return errorResponse;
        }
        // Checks on attributes
        GroupAnnc groupAnncNew = (GroupAnnc) XmlMapper.getInstance().xmlToObject(requestIndication.getRepresentation());
        // The Update of the Id is NP
        if (groupAnncNew.getId() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"GroupAnncId UPDATE is Not Permitted")) ;
        }
        // Check ExpirationTime
        if (groupAnncNew.getExpirationTime() != null && !checkExpirationTime(groupAnncNew.getExpirationTime())) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Expiration Time UPDATE is Out of Date")) ;
        }
        // Link Must be NP
        if (groupAnncNew.getLink() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Link attribute UPDATE is Mandatory")) ;
        }
        // SearchStrings Attribute is mandatory
        if (groupAnncNew.getSearchStrings() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"searchStrings attribute UPDATE is Mandatory")) ;
        }
        // Storage
        // Set Expiration Time
        if (groupAnncNew.getExpirationTime() != null) {
            groupAnnc.setExpirationTime(groupAnncNew.getExpirationTime());
        }
        // Set accessRightID if it exists
        if (DAOFactory.getAccessRightDAO().find(groupAnncNew.getAccessRightID()) != null) {
            groupAnnc.setAccessRightID(groupAnncNew.getAccessRightID());
        }
        // Set searchStrings
        groupAnnc.setSearchStrings(groupAnncNew.getSearchStrings());

        // Notify the subscribers
        Notifier.notify(StatusCode.STATUS_OK, groupAnnc);

        // Store
        DAOFactory.getGroupAnncDAO().update(groupAnnc);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, groupAnnc);
    }

    /**
     * Deletes {@link GroupAnnc} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doDelete (RequestIndication requestIndication) {

        ResponseConfirm errorResponse = new ResponseConfirm();
        GroupAnnc groupAnnc = DAOFactory.getGroupAnncDAO().find(requestIndication.getTargetID());

        // Check Resource Existence
        if (groupAnnc == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(groupAnnc.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_DELETE);
        if (errorResponse != null) {
            return errorResponse;
        }

        // Notify the subscribers
        Notifier.notify(StatusCode.STATUS_DELETED, groupAnnc);

        // Delete
        DAOFactory.getGroupAnncDAO().delete(groupAnnc);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK);

    }

    /**
     * Executes {@link GroupAnnc} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doExecute (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }
}
