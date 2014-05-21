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

import org.eclipse.om2m.commons.resource.Base64Binary;
import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.Group;
import org.eclipse.om2m.commons.resource.MembersContentResponses;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.resource.MembersContentResponses.Status;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.commons.utils.DateConverter;
import org.eclipse.om2m.core.constants.Constants;
import org.eclipse.om2m.core.dao.DAOFactory;
import org.eclipse.om2m.core.router.Router;

/**
 * Implements Create, Retrieve, Update, Delete and Execute methods to handle
 * generic REST request for {@link Group} Members resources.
 *
 * @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>       
 *         </ul>
 */

public class MembersContentController extends Controller {

    /**
     * fan-out requests to group members and aggregate their responses.
     * @param requestIndication - The generic request to handle.
     * @return the aggregation of the requests responses
     */
    public ResponseConfirm fanOutRequestIndication (RequestIndication requestIndication) {

        String groupUri = requestIndication.getTargetID().split("/membersContent")[0];
        Group group = DAOFactory.getGroupDAO().find(groupUri);

        // Check Parent Existence
        if (group == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,groupUri+" does not exist")) ;
        }
        // Fan-out request to group Members
        final MembersContentResponses membersContentResponses = new MembersContentResponses();

        if (group.getMembers() != null && !group.getMembers().getReference().isEmpty()) {
            for (int i=0; i<group.getMembers().getReference().size(); i++) {
                // Get the members references
                String memberReference = group.getMembers().getReference().get(i);
                //Fan-out the request to the members
                requestIndication.setTargetID(memberReference);
                final RequestIndication memberRequestIndication = new RequestIndication();
                memberRequestIndication.setMethod(requestIndication.getMethod());
                memberRequestIndication.setTargetID(memberReference);
                memberRequestIndication.setRequestingEntity(requestIndication.getRequestingEntity());
                memberRequestIndication.setRepresentation(requestIndication.getRepresentation());

                Thread thread = new Thread(){
                    public void run() {
                    // Get the response of each request
                      ResponseConfirm responseConfirm = new Router().doRequest(memberRequestIndication);
                      // Construct the status of the response
                      Status status = new MembersContentResponses.Status();
                      status.setId(memberRequestIndication.getTargetID());
                      status.setStatusCode(responseConfirm.getStatusCode().value());
                      status.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
                       if (responseConfirm.getRepresentation() != null) {
                            Base64Binary responseBody = new Base64Binary();
                            responseBody.setValue(responseConfirm.getRepresentation().getBytes());
                            status.setResultBody(responseBody);
                        }
                      membersContentResponses.getStatus().add(status);
                    }
                };
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    LOGGER.error("Group request thread join error",e);
                }
            }


        }

        if(requestIndication.getMethod().equals(Constants.METHOD_CREATE)){
            return new ResponseConfirm(StatusCode.STATUS_CREATED, membersContentResponses);
        }
        return new ResponseConfirm(StatusCode.STATUS_OK, membersContentResponses);

    }

    /**
     * fan-out create requests to group members and aggregate their responses.
     * @param requestIndication - The generic request to handle.
     * @return the aggregation of the requests responses
     */
    public ResponseConfirm doCreate(RequestIndication requestIndication) {
        return fanOutRequestIndication(requestIndication);
    }

    /**
     * fan-out retrieve requests to group members and aggregate their responses.
     * @param requestIndication - The generic request to handle.
     * @return the aggregation of the requests responses
     */
    public ResponseConfirm doRetrieve(RequestIndication requestIndication) {
        return fanOutRequestIndication(requestIndication);
    }

    /**
     * fan-out update requests to group members and aggregate their responses.
     * @param requestIndication - The generic request to handle.
     * @return the aggregation of the requests responses
     */
    public ResponseConfirm doUpdate(RequestIndication requestIndication) {
        return fanOutRequestIndication(requestIndication);
    }

    /**
     * fan-out delete requests to group members and aggregate their responses.
     * @param requestIndication - The generic request to handle.
     * @return the aggregation of the requests responses
     */
    public ResponseConfirm doDelete(RequestIndication requestIndication) {
        return fanOutRequestIndication(requestIndication);
    }

    /**
     * fan-out execute requests to group members and aggregate their responses.
     * @param requestIndication - The generic request to handle.
     * @return the aggregation of the requests responses
     */
    public ResponseConfirm doExecute(RequestIndication requestIndication) {
        return fanOutRequestIndication(requestIndication);
    }

}
