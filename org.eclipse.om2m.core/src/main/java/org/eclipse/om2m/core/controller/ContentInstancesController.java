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

import org.eclipse.om2m.commons.resource.Container;
import org.eclipse.om2m.commons.resource.ContentInstances;
import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.commons.utils.XmlMapper;
import org.eclipse.om2m.core.constants.Constants;
import org.eclipse.om2m.core.dao.DAOFactory;

/**
 * Implements Create, Retrieve, Update, Delete and Execute methods to handle
 * generic REST request for {@link ContentInstances} collection resource.
 *
 * @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         </ul>
 */

public class ContentInstancesController extends Controller {

    /**
     * Creates {@link ContentInstances} resource. It is not allowed Through the API.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doCreate (RequestIndication requestIndication) {

        // contentInstanceCollection:   (createReq NA) (response M*)
        // latest:                      (createReq NA) (response O)
        // oldest:                      (createReq NA) (response O)
        // subscriptionsReference:      (createReq NA) (response M*)
        // creationTime:                (createReq NA) (response M*)
        // lastModifiedTime:            (createReq NA) (response M*)
        // currentNrOfInstances:        (createReq NA) (response M*)
        // currentByteSize:             (createReq NA) (response M*)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed"));

    }

    /**
     * Retrieves {@link ContentInstances} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doRetrieve (RequestIndication requestIndication) {

        // contentInstanceCollection:   (response M*)
        // latest:                      (response O)
        // oldest:                      (response O)
        // subscriptionsReference:      (response M*)
        // creationTime:                (response M*)
        // lastModifiedTime:            (response M*)
        // currentNrOfInstances:        (response M*)
        // currentByteSize:             (response M*)

        ResponseConfirm errorResponse = new ResponseConfirm();
        ContentInstances contentInstances =  DAOFactory.getContentInstancesDAO().find(requestIndication.getTargetID());

        // Check the resource existence
        if (contentInstances == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight of the Container Parent
        Container container = DAOFactory.getContainerDAO().find(requestIndication.getTargetID().split("/contentInstances")[0]);
        errorResponse = checkAccessRight(container.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_READ);
        if (errorResponse != null) {
            return errorResponse;
        }
        String xmlContentInstances = XmlMapper.getInstance().objectToXml(contentInstances);
        // Response
        contentInstances.getContentInstanceCollection().getContentInstance().clear();
        return new ResponseConfirm(StatusCode.STATUS_OK, xmlContentInstances);

    }

    /**
     * Updates {@link ContentInstances} resource. It is not allowed Through the API.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doUpdate (RequestIndication requestIndication)  {

        // contentInstanceCollection:    (updateReq NA) (response M*)
        // latest:                       (updateReq NA) (response O)
        // oldest:                       (updateReq NA) (response O)
        // subscriptionsReference:       (updateReq NA) (response M*)
        // creationTime:                 (updateReq NA) (response M*)
        // lastModifiedTime:             (updateReq NA) (response M*)
        // currentNrOfInstances:         (updateReq NA) (response M*)
        // currentByteSize:              (updateReq NA) (response M*)

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed"));

    }

    /**
     * Deletes {@link ContentInstances} resource. It is not allowed Through the API.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doDelete (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed"));
    }

    /**
     * Executes {@link ContentInstances} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doExecute (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not implmented"));
    }
}
