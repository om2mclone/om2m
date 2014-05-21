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
import org.eclipse.om2m.commons.resource.ExecInstance;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;

/**
 * Implements Create, Retrieve, Update, Delete and Execute methods to handle
 * generic REST request for {@link ExecInstance} resource.
 *
 * @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         </ul>
 */

public class ExecInstanceController extends Controller {

    /**
     * Creates {@link ExecInstance} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doCreate (RequestIndication requestIndication) {

        // subscriptionsReference:  (createReq NA) (response M)
        // expirationTime:          (createReq NA) (response M*)
        // accessRightID:           (createReq NA) (response O)
        // creationTime:            (createReq NA) (response M)
        // lastModifiedTime:        (createReq NA) (response M)
        // execStatus:              (createReq NA) (response M)
        // execResult:              (createReq NA) (response M)
        // execDisable:             (createReq NA) (response O)
        // id:                      (createReq NA) (response M)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }

    /**
     * Retrieves {@link ExecInstance} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doRetrieve (RequestIndication requestIndication) {

        // subscriptionsReference:  (response M)
        // expirationTime:          (response M*)
        // accessRightID:           (response O)
        // creationTime:            (response M)
        // lastModifiedTime:        (response M)
        // execStatus:              (response M)
        // execResult:              (response M)
        // execDisable:             (response O)
        // id:                      (response M)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }

    /**
     * Updates {@link ExecInstance} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doUpdate (RequestIndication requestIndication) {

        // subscriptionsReference:  (updateReq NA) (response M)
        // expirationTime:          (updateReq NA) (response M*)
        // accessRightID:           (updateReq NA) (response O)
        // creationTime:            (updateReq NA) (response M)
        // lastModifiedTime:        (updateReq NA) (response M)
        // execStatus:              (updateReq NA) (response M)
        // execResult:              (updateReq NA) (response M)
        // execDisable:             (updateReq NA) (response O)
        // id:                      (updateReq NA) (response M)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }

    /**
     * Deletes {@link ExecInstance} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doDelete (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }

    /**
     * Executes {@link ExecInstance} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doExecute (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }
}
