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
import org.eclipse.om2m.commons.resource.Subscription;
import org.eclipse.om2m.commons.resource.Subscriptions;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.core.constants.Constants;
import org.eclipse.om2m.core.dao.DAOFactory;

/**
 * Implements Create, Retrieve, Update, Delete and Execute methods to handle
 * generic REST request for {@link Subscription} resource.
 *
 * @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>       
 *         </ul>
 */

public class SubscriptionsController extends Controller {

    /**
     * Creates {@link Subscriptions} resource. It is not allowed Through the API.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doCreate (RequestIndication requestIndication) {

        // subscriptionCollection:    (createReq NA) (response M)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed"));
    }

    /**
     * Retrieves {@link Subscriptions} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doRetrieve (RequestIndication requestIndication) {

        ResponseConfirm errorResponse = new ResponseConfirm();
        Subscriptions subscriptions = DAOFactory.getSubscriptionsDAO().find(requestIndication.getTargetID());

        // Check the resource existence
        if (subscriptions == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(getAccessRightID(subscriptions.getUri()), requestIndication.getRequestingEntity(), Constants.AR_READ);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, DAOFactory.getSubscriptionsDAO().find(requestIndication.getTargetID()));

    }

    /**
     * Updates {@link Subscriptions} resource. It is not allowed Through the API.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doUpdate (RequestIndication requestIndication) {

        // subscriptionsCollection:    (updateReq NP) (response M)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed")) ;
    }

    /**
     * Deletes {@link Subscriptions} resource. It is not allowed Through the API.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doDelete (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed"));
    }

    /**
     * Executes {@link Subscriptions} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doExecute (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not implmented"));
    }

    /**
     * Get the subscriptions collection parent AccessRightID
     * @param subscriptionsUri
     * @return accessRightID
     */
    public String getAccessRightID(String subscriptionsUri) {
        String parentSubsUri;
        // Return Container AccessRightID if ContentInstances resource is the parent
        if (subscriptionsUri.contains("contentInstances")) {
            parentSubsUri = subscriptionsUri.split("/contentInstances")[0];
            return DAOFactory.getResourceDAO().find(parentSubsUri).getAccessRightID();
        }
        parentSubsUri = subscriptionsUri.split("/subscriptions")[0];

        // Return AccessRightID if AccessRight resource is the parent
        if(parentSubsUri.split("/").length>1){
            if("accessRights".equals(parentSubsUri.split("/")[parentSubsUri.split("/").length-2])){
                return parentSubsUri;
            }
        }

        // Return parent AccessRightID for other resources
        return DAOFactory.getResourceDAO().find(parentSubsUri).getAccessRightID();
    }
}
