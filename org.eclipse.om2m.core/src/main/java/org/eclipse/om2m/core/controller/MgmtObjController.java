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
import org.eclipse.om2m.commons.resource.MgmtObj;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;

/**
 * Implements Create, Retrieve, Update, Delete and Execute methods to handle
 * generic REST request for {@link MgmtObj} resource.
 *
 * @author <ul>
 *         <li> Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li> Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li> Maroua Meddeb < marouameddeb@gmail.com ></li>
 *         </ul>
 */

public class MgmtObjController extends Controller {

    /**
     * Creates {@link MgmtObj} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doCreate (RequestIndication requestIndication) {

        // parametersCollection:    (createReq NP) (response M)
        // subscriptionsReference:  (createReq NP) (response M)
        // expirationTime:          (createReq O)  (response M*)
        // accessRightID:           (createReq O)  (response O)
        // searchStrings:           (createReq O)  (response M)
        // creationTime:            (createReq NP) (response M)
        // lastModifiedTime:        (createReq NP) (response M)
        // description:             (createReq O)  (response O)
        // moID:                    (createReq M)  (response M)
        // originalMO:              (createReq O)  (response M)
        // <moAttribute>:           (createReq O)  (response O)
        // id:                      (createReq O)  (response M*)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }

    /**
     * Retrieves {@link MgmtObj} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doRetrieve (RequestIndication requestIndication) {

        // parametersCollection:    (response M)
        // subscriptionsReference:  (response M)
        // expirationTime:          (response M*)
        // accessRightID:           (response O)
        // searchStrings:           (response M)
        // creationTime:            (response M)
        // lastModifiedTime:        (response M)
        // description:             (response O)
        // moID:                    (response M)
        // originalMO:              (response M)
        // <moAttribute>:           (response O)
        // id:                      (response M*)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }

    /**
     * Updates {@link MgmtObj} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doUpdate (RequestIndication requestIndication) {

        // parametersCollection:    (updateReq NP) (response M)
        // subscriptionsReference:  (updateReq NP) (response M)
        // expirationTime:          (updateReq O)  (response M*)
        // accessRightID:           (updateReq O)  (response O)
        // searchStrings:           (updateReq O)  (response M)
        // creationTime:            (updateReq NP) (response M)
        // lastModifiedTime:        (updateReq NP) (response M)
        // description:             (updateReq O)  (response O)
        // moID:                    (updateReq NP) (response M)
        // originalMO:              (updateReq NP) (response M)
        // <moAttribute>:           (updateReq O)  (response O)
        // id:                      (updateReq NP) (response M*)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }

    /**
     * Deletes {@link MgmtObj} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doDelete (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }

    /**
     * Executes {@link MgmtObj} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doExecute (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }
}
