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

import java.util.Date;

import org.eclipse.om2m.commons.resource.Applications;
import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.commons.utils.DateConverter;
import org.eclipse.om2m.commons.utils.XmlMapper;
import org.eclipse.om2m.core.constants.Constants;
import org.eclipse.om2m.core.dao.DAOFactory;
import org.eclipse.om2m.core.notifier.Notifier;

/**
 * Implements Create, Retrieve, Update, Delete and Execute methods to handle
 * generic REST request for {@link Applications} collection resource.
 *
 * @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         </ul>
 */

public class ApplicationsController extends Controller {

    /**
     * Creates {@link Applications} resource. It is not allowed Through the API.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doCreate (RequestIndication requestIndication) {

        // applicationCollection:       (createReq NA) (response M)
        // applicationAnncCollection:   (createReq NA) (response M)
        // subscriptionsReference:      (createReq NA) (response M)
        // accessRightID:               (createReq NA) (response O)
        // creationTime:                (createReq NA) (response M)
        // lastModifiedTime:            (createReq NA) (response M)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed"));
    }

    /**
     * Retrieves {@link Applications} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doRetrieve (RequestIndication requestIndication) {

        // applicationCollection:       (response M)
        // applicationAnncCollection:   (response M)
        // subscriptionsReference:      (response M)
        // accessRightID:               (response O)
        // creationTime:                (response M)
        // lastModifiedTime:            (response M)

        ResponseConfirm errorResponse = new ResponseConfirm();
        Applications applications = DAOFactory.getApplicationsDAO().find(requestIndication.getTargetID());

        // Check resource existence
        if (applications == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(applications.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_READ);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, applications);

    }

    /**
     * Updates {@link Applications} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doUpdate (RequestIndication requestIndication)  {

        // applicationCollection:       (updateReq NP) (response M)
        // applicationAnncCollection:   (updateReq NP) (response M)
        // subscriptionsReference:      (updateReq NP) (response M)
        // accessRightID:               (updateReq O)  (response O)
        // creationTime:                (updateReq NP) (response M)
        // lastModifiedTime:            (updateReq NP) (response M)

        ResponseConfirm errorResponse = new ResponseConfirm();
        Applications applications = DAOFactory.getApplicationsDAO().lazyFind(requestIndication.getTargetID());

        // Check resource existence
        if (applications == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(applications.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_WRITE);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Check Resource Representation
        if (requestIndication.getRepresentation() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Resource Representation is EMPTY")) ;
        }
        // Check XML Validity
        errorResponse = checkMessageSyntax(requestIndication.getRepresentation(),"applications.xsd");
        if (errorResponse != null) {
            return errorResponse;
        }
        // Checks on attributes
        Applications applicationsNew = (Applications) XmlMapper.getInstance().xmlToObject(requestIndication.getRepresentation());
        // ApplicationCollection Must be NP
        if (applicationsNew.getApplicationCollection().getNamedReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Application Collection UPDATE is Not Permitted")) ;
        }
        // ApplicationAnncCollection Must be NP
        if (applicationsNew.getApplicationAnncCollection().getNamedReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"ApplicationAnnc Collection UPDATE is Not Permitted")) ;
        }
        // References Must be NP
        if (applicationsNew.getMgmtObjsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"MgmtObjs Reference UPDATE is Not Permitted")) ;
        }
        // Subscriptions Must be NP
        if (applicationsNew.getSubscriptionsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Subscriptions Reference UPDATE is Not Permitted")) ;
        }
        // CreationTime Must be NP
        if (applicationsNew.getCreationTime() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Creation Time UPDATE is Not Permitted")) ;
        }
        // LastModifiedTime Must be NP
        if (applicationsNew.getLastModifiedTime() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Last Modified Time UPDATE is Not Permitted")) ;
        }
        // Storage
        // Set accessRightID if it exists
        if (DAOFactory.getAccessRightDAO().find(applicationsNew.getAccessRightID()) != null) {
            applications.setAccessRightID(applicationsNew.getAccessRightID());
        }
        // Set LastModifiedTime
        applications.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());

        // Notify the subscribers
        Notifier.notify(StatusCode.STATUS_OK, applications);

        // Store applications
        DAOFactory.getApplicationsDAO().update(applications);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, applications);
    }

    /**
     * Deletes {@link Applications} resource. It is not allowed Through the API.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doDelete (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed"));
    }

    /**
     * Executes {@link Applications} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doExecute (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not implmented"));
    }
}
