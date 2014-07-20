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

import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.MgmtObjs;
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
 * generic REST request for {@link MgmtObjs} collection resource.
 *
 * @author <ul>
 *         <li>Maroua Meddeb < marouameddeb@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>       
 *         </ul>
 */

public class MgmtObjsController extends Controller {

    /**
     * Creates {@link MgmtObjs} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doCreate (RequestIndication requestIndication) {

        // mgmtObjCollection:       (createReq NA) (response M)
        // mgmtCmdCollection:       (createReq NA) (response M)
        // subscriptionsReference:  (createReq NA) (response M)
        // accessRightID:           (createReq NA) (response O)
        // creationTime:            (createReq NA) (response M)
        // lastModifiedTime:        (createReq NA) (response M)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed"));
    }

    /**
     * Retrieves {@link MgmtObjs} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doRetrieve (RequestIndication requestIndication) {

        // mgmtObjCollection:       (response M)
        // mgmtCmdCollection:       (response M)
        // subscriptionsReference:  (response M)
        // accessRightID:           (response O)
        // creationTime:            (response M)
        // lastModifiedTime:        (response M)

        ResponseConfirm errorResponse = new ResponseConfirm();
        MgmtObjs mgmtObjs = DAOFactory.getMgmtObjsDAO().find(requestIndication.getTargetID());

        // Check the resource existence
        if (mgmtObjs == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(mgmtObjs.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_READ);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, mgmtObjs);

    }

    /**
     * Updates {@link MgmtObjs} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doUpdate (RequestIndication requestIndication)  {

        // mgmtObjCollection:       (updateReq NP) (response M)
        // mgmtCmdCollection:       (updateReq NP) (response M)
        // subscriptionsReference:  (updateReq NP) (response M)
        // accessRightID:           (updateReq O)  (response O)
        // creationTime:            (updateReq NP) (response M)
        // lastModifiedTime:        (updateReq NP) (response M)

        ResponseConfirm errorResponse = new ResponseConfirm();
        MgmtObjs mgmtObjs = DAOFactory.getMgmtObjsDAO().lazyFind(requestIndication.getTargetID());

        // Check the resource existence
        if (mgmtObjs == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(mgmtObjs.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_WRITE);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Check Resource Representation
        if (requestIndication.getRepresentation() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Resource Representation is EMPTY")) ;
        }
        // Check XML Validity
        errorResponse = checkMessageSyntax(requestIndication.getRepresentation(),"mgmtObjs.xsd");
        if (errorResponse != null) {
            return errorResponse;
        }
        // Checks attributes
        MgmtObjs mgmtObjsNew = (MgmtObjs) XmlMapper.getInstance().xmlToObject(requestIndication.getRepresentation());
        //mgmtObjCollection Must be NP
        if (mgmtObjsNew.getMgmtObjCollection() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"MgmtObj Collection UPDATE is Not Permitted")) ;
        }
        //mgmtCmdCollection Must be NP
        if (mgmtObjsNew.getMgmtCmdCollection() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"MgmtCmd Collection UPDATE is Not Permitted")) ;
        }
        //SubscriptionsReference Must be NP
        if (mgmtObjsNew.getSubscriptionsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Subscriptions Reference UPDATE is Not Permitted")) ;
        }
        // CreationTime Must be NP
        if (mgmtObjsNew.getCreationTime() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Creation Time UPDATE is Not Permitted")) ;
        }
        //LastModifiedTime Must be NP
        if (mgmtObjsNew.getLastModifiedTime() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Last Modified Time UPDATE is Not Permitted")) ;
        }
        //Storage
        // Set accessRightID if it exists
        if (DAOFactory.getAccessRightDAO().find(mgmtObjsNew.getAccessRightID()) != null) {
            mgmtObjs.setAccessRightID(mgmtObjsNew.getAccessRightID());
        }
        // Set LastModifiedTime
        mgmtObjs.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());

        // Notify the subscribers
        Notifier.notify(StatusCode.STATUS_OK, mgmtObjs);

        // Store mgmtObjs
        DAOFactory.getMgmtObjsDAO().update(mgmtObjs);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, mgmtObjs);

    }

    /**
     * Deletes {@link MgmtObjs} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doDelete (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed"));
    }

    /**
     * Executes {@link MgmtObjs} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doExecute (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not implmented"));
    }
}
