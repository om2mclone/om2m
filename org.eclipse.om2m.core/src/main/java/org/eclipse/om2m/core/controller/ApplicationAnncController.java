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

import org.eclipse.om2m.commons.resource.ApplicationAnnc;
import org.eclipse.om2m.commons.resource.Applications;
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
 * generic REST request for {@link ApplicationAnnc} resource.
 *
 * @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         </ul>
 */

public class ApplicationAnncController extends Controller {

    /**
     * Creates {@link ApplicationAnnc} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doCreate(RequestIndication requestIndication)  {

        // containersReference:     (createReq NP) (response M)
        // groupsReference:         (createReq NP) (response M)
        // accessRightsReference:   (createReq NP) (response M)
        // Link:                    (createReq M)  (response M)
        // accessRightID:           (createReq O)  (response O)
        // searchStrings:           (createReq M)  (response M)
        // expirationTime:          (createReq O)  (response M*)
        // Id:                      (createReq O)  (response M*)

        ResponseConfirm errorResponse = new ResponseConfirm();
        Applications applications = DAOFactory.getApplicationsDAO().lazyFind(requestIndication.getTargetID());

        // Check Resource Parent Existence
        if (applications == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(applications.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_CREATE);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Check Resource Representation
        if (requestIndication.getRepresentation() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Resource Representation is EMPTY")) ;
        }
        // Check XML Validity
        errorResponse = checkMessageSyntax(requestIndication.getRepresentation(),"applicationAnnc.xsd");
        if (errorResponse != null) {
            return errorResponse;
        }
        // Checks on attributes
        ApplicationAnnc applicationAnnc = (ApplicationAnnc) XmlMapper.getInstance().xmlToObject(requestIndication.getRepresentation());
        // Check the Id uniqueness
        if (applicationAnnc.getId() != null && DAOFactory.getApplicationAnncDAO().find(requestIndication.getTargetID()+"/"+applicationAnnc.getId()) != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_CONFLICT,"ApplicationAnncId Conflit")) ;
        }
        // Generate the id it it does not exist
        if (applicationAnnc.getId() == null || applicationAnnc.getId().isEmpty()) {
            applicationAnnc.setId(generateId("APP_","Annc"));
        }
        // SearchStrings Attribute is mandatory
        if (applicationAnnc.getSearchStrings() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"searchStrings attribute CREATE is Mandatory")) ;
        }
        // Link is Mandatory
        if (applicationAnnc.getLink() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Link attribute is Mandatory")) ;
        }
        // Check ExpirationTime
        if (applicationAnnc.getExpirationTime() != null && !checkExpirationTime(applicationAnnc.getExpirationTime())) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Expiration Time is Out of Date")) ;
        }
        // Containers Reference Must be NP
        if (applicationAnnc.getContainersReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST," Containers Reference is Not Permitted")) ;
        }
        // Groups Reference Must be NP
        if (applicationAnnc.getGroupsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Groups Reference is Not Permitted")) ;
        }
        // AccessRightsReference Must be NP
        if (applicationAnnc.getAccessRightsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"AccessRights Reference is Not Permitted")) ;
        }
        // Storage
        // Set URI
        applicationAnnc.setUri(requestIndication.getTargetID()+ "/" +applicationAnnc.getId());
        // Set Expiration Time if it is null
        if (applicationAnnc.getExpirationTime() == null) {
            //default Expiration Time
            applicationAnnc.setExpirationTime(getNewExpirationTime(Constants.EXPIRATION_TIME));
        }
        // Set AccessRightID from the Parent if it's null or nonexistent
        if (DAOFactory.getAccessRightDAO().find(applicationAnnc.getAccessRightID()) == null) {
            applicationAnnc.setAccessRightID(applications.getAccessRightID());
        }
        // Set References
        applicationAnnc.setContainersReference(applicationAnnc.getUri()+"/containers");
        applicationAnnc.setGroupsReference(applicationAnnc.getUri()+"/groups");
        applicationAnnc.setAccessRightsReference(applicationAnnc.getUri()+"/accessRights");

        // Notify the subscribers
        Notifier.notify(StatusCode.STATUS_CREATED, applicationAnnc);

        // Store
        DAOFactory.getApplicationAnncDAO().create(applicationAnnc);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_CREATED, applicationAnnc);
    }

    /**
     * Retrieves {@link ApplicationAnnc} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doRetrieve (RequestIndication requestIndication) {

        // containersReference:     (response M)
        // groupsReference:         (response M)
        // accessRightsReference:   (response M)
        // Link:                    (response M)
        // accessRightID:           (response O)
        // searchStrings:           (response M)
        // expirationTime:          (response M*)
        // Id:                      (response M*)

        ResponseConfirm errorResponse = new ResponseConfirm();
        ApplicationAnnc applicationAnnc = DAOFactory.getApplicationAnncDAO().find(requestIndication.getTargetID());

        // Check if the resource exists in DataBase or Not
        if (applicationAnnc == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist in DataBase")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(applicationAnnc.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_READ);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, applicationAnnc);
    }

    /**
     * Updates {@link ApplicationAnnc} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doUpdate (RequestIndication requestIndication) {

        // containersReference:     (updateReq NP) (response M)
        // groupsReference:         (updateReq NP) (response M)
        // accessRightsReference:   (updateReq NP) (response M)
        // Link:                    (updateReq NP) (response M)
        // accessRightID:           (updateReq O)  (response O)
        // searchStrings:           (updateReq M)  (response M)
        // expirationTime:          (updateReq O)  (response M*)
        // Id:                      (updateReq NP) (response M*)

        ResponseConfirm errorResponse = new ResponseConfirm();
        ApplicationAnnc applicationAnnc = DAOFactory.getApplicationAnncDAO().find(requestIndication.getTargetID());

        // Check resource Existence
        if (applicationAnnc == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist in DataBase")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(applicationAnnc.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_WRITE);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Check Resource Representation
        if (requestIndication.getRepresentation() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Resource Representation is EMPTY")) ;
        }
        // Check XML Validity
        errorResponse = checkMessageSyntax(requestIndication.getRepresentation(),"applicationAnnc.xsd");
        if (errorResponse != null) {
            return errorResponse;
        }
        // Checks on attributes
        ApplicationAnnc applicationAnncNew = (ApplicationAnnc) XmlMapper.getInstance().xmlToObject(requestIndication.getRepresentation());
        // AppAnncId update is NP
        if (applicationAnncNew.getId() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"ApplicationAnncId UPDATE is Not Permitted")) ;
        }
        // Link Must be NP
        if (applicationAnncNew.getLink() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Link attribute UPDATE is Mandatory")) ;
        }
        // SearchStrings Attribute is mandatory
        if (applicationAnncNew.getSearchStrings() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"searchStrings attribute UPDATE is Mandatory")) ;
        }
        // Check ExpirationTime
        if (applicationAnncNew.getExpirationTime() != null && !checkExpirationTime(applicationAnncNew.getExpirationTime())) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Expiration Time UPDATE is Out of Date")) ;
        }
        // Containers Reference Must be NP
        if (applicationAnncNew.getContainersReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Containers Reference UPDATE is Not Permitted")) ;
        }
        // Groups Reference Must be NP
        if (applicationAnncNew.getGroupsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Groups Reference UPDATE is Not Permitted")) ;
        }
        // AccessRightsReference Must be NP
        if (applicationAnncNew.getAccessRightsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"AccessRights Reference UPDATE is Not Permitted")) ;
        }
        // Storage
        // Set Expiration Time
        if (applicationAnncNew.getExpirationTime() != null) {
            applicationAnnc.setExpirationTime(applicationAnncNew.getExpirationTime());
        }
        // Set accessRightID if it exists
        if (DAOFactory.getAccessRightDAO().find(applicationAnncNew.getAccessRightID()) != null) {
            applicationAnnc.setAccessRightID(applicationAnncNew.getAccessRightID());
        }
        // Set searchStrings
        applicationAnnc.setSearchStrings(applicationAnncNew.getSearchStrings());

        // Notify the subscribers
        Notifier.notify(StatusCode.STATUS_OK, applicationAnnc);

        // Store applicationAnnc
        DAOFactory.getApplicationAnncDAO().update(applicationAnnc);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, applicationAnnc);
    }

    /**
     * Deletes {@link ApplicationAnnc} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doDelete (RequestIndication requestIndication) {

        ResponseConfirm errorResponse = new ResponseConfirm();
        ApplicationAnnc applicationAnnc = DAOFactory.getApplicationAnncDAO().find(requestIndication.getTargetID());

        // Check Resource Existence
        if (applicationAnnc == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(applicationAnnc.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_DELETE);
        if (errorResponse != null) {
            return errorResponse;
        }

        // Notify the subscribers
        Notifier.notify(StatusCode.STATUS_DELETED, applicationAnnc);

        // Delete
        DAOFactory.getApplicationAnncDAO().delete(applicationAnnc);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK);

    }

    /**
     * Executes {@link ApplicationAnnc} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doExecute (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not implmented"));
    }
}
