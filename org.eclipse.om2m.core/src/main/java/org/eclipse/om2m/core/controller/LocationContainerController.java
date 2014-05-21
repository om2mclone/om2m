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
import org.eclipse.om2m.commons.resource.LocationContainer;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;

/**
 * Implements Create, Retrieve, Update, Delete and Execute methods to handle
 * generic REST request for {@link LocationContainer} resource.
 *
 * @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>       
 *         </ul>
 */

public class LocationContainerController extends Controller {

    /**
     * Creates {@link LocationContainer} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doCreate (RequestIndication requestIndication) {

        // contentInstancesReference:   (createReq NP) (response M)
        // subscriptionsReference:      (createReq NP) (response M)
        // id:                          (createReq O)  (response M*)
        // expirationTime:              (createReq O)  (response M*)
        // accessRightID:               (createReq O)  (response O)
        // searchStrings:               (createReq O)  (response M)
        // creationTime:                (createReq NP) (response M)
        // lastModifiedTime:            (createReq NP) (response M)
        // announceTo:                  (createReq O)  (response M*)
        // maxNrOfInstances:            (createReq O)  (response M*)
        // maxBytesSize:                (createReq O)  (response M*)
        // maxInstanceAge:              (createReq O)  (response M*)
        // locationContainerType:       (createReq M)  (response M)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;

    }

    /**
     * Retrieves {@link LocationContainer} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doRetrieve (RequestIndication requestIndication) {

        // contentInstancesReference:   (response M)
        // subscriptionsReference:      (response M)
        // id:                          (response M*)
        // expirationTime:              (response M*)
        // accessRightID:               (response O)
        // searchStrings:               (response M)
        // creationTime:                (response M)
        // lastModifiedTime:            (response M)
        // announceTo:                  (response M*)
        // maxNrOfInstances:            (response M*)
        // maxBytesSize:                (response M*)
        // maxInstanceAge:              (response M*)
        // locationContainerType:       (response M)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }

    /**
     * Updates {@link LocationContainer} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doUpdate (RequestIndication requestIndication) {

        // contentInstancesReference:   (updateReq NP) (response M)
        // subscriptionsReference:      (updateReq NP) (response M)
        // id:                          (updateReq NP) (response M*)
        // expirationTime:              (updateReq O)  (response M*)
        // accessRightID:               (updateReq O)  (response O)
        // searchStrings:               (updateReq O)  (response M)
        // creationTime:                (updateReq NP) (response M)
        // lastModifiedTime:            (updateReq NP) (response M)
        // announceTo:                  (updateReq O)  (response M*)
        // maxNrOfInstances:            (updateReq O)  (response M*)
        // maxBytesSize:                (updateReq O)  (response M*)
        // maxInstanceAge:              (updateReq O)  (response M*)
        // locationContainerType:       (updateReq NP) (response M)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }

    /**
     * Deletes {@link LocationContainer} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doDelete (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }

    /**
     * Executes {@link LocationContainer} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doExecute (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }
}
