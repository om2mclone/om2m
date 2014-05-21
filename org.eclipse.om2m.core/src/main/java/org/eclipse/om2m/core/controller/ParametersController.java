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
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;

/**
 * Implements Create, Retrieve, Update, Delete and Execute methods to handle
 * generic REST request for {@link Parameters} collection resource.
 *
 * @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>       
 *         </ul>
 */

public class ParametersController extends Controller {

    /**
     * Creates {@link Parameters} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doCreate (RequestIndication requestIndication) {

        // parametersCollection:    (createReq NP) (response M)
        // subscriptionsReference:  (createReq NP) (response M)
        // accessRightID:           (createReq O)  (response O)
        // creationTime:            (createReq NP) (response M)
        // lastModifiedTime:        (createReq NP) (response M)
        // originalMO:              (createReq O)  (response O)
        // <moAttribute>:           (createReq O)  (response O)
        // id:                      (createReq O)  (response M*)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not implemented"));
    }

    /**
     * Retrieves {@link Parameters} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doRetrieve (RequestIndication requestIndication) {

        // parametersCollection:    (response M)
        // subscriptionsReference:  (response M)
        // accessRightID:           (response O)
        // creationTime:            (response M)
        // lastModifiedTime:        (response M)
        // originalMO:              (response O)
        // <moAttribute>:           (response O)
        // id:                      (response M*)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not implemented"));
    }

    /**
     * Updates {@link Parameters} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doUpdate (RequestIndication requestIndication) {

        // parametersCollection:    (updateReq NP) (response M)
        // subscriptionsReference:  (updateReq NP) (response M)
        // accessRightID:           (updateReq O)  (response O)
        // creationTime:            (updateReq NP) (response M)
        // lastModifiedTime:        (updateReq NP) (response M)
        // originalMO:              (updateReq NP) (response O)
        // <moAttribute>:           (updateReq O)  (response O)
        // id:                      (updateReq NP) (response M*)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not implemented"));
    }

    /**
     * Deletes {@link Parameters} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doDelete (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not implemented"));
    }

    /**
     * Executes {@link Parameters} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doExecute (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not implemented"));
    }
}
