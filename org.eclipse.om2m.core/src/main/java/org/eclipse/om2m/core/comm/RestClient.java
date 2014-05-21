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
 *         conception and documentation.
 *     Mahdi Ben Alaya (Project co-founder) - Management and initial specification,
 *         conception, implementation, test and documentation.
 *     Christophe Chassot - Management and initial specification.
 *     Khalil Drira - Management and initial specification.
 *     Yassine Banouar - Initial specification, conception, implementation, test
 *         and documentation.
 ******************************************************************************/
package org.eclipse.om2m.core.comm;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.comm.service.RestClientService;
import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.core.constants.Constants;

/**
 *
 * A generic client that acts as a proxy to forward requests to specific rest clients based on their
 * communication protocol such as HTTP, COAP, etc.
 *
 * @author <ul>
 *         <li> Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li> Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.fr ></li>
 *         <li> Marouane El kiasse < melkiasse@laas.fr > < kiasmarouane@gmail.com ></li>
 *         </ul>
 */
public class RestClient{
    /** Logger  */
    private static Log LOGGER = LogFactory.getLog(RestClient.class);
    /** Contains all discovered specific rest clients that will considered for sending requests */
    public static Map<String,RestClientService> restClients = new HashMap<String,RestClientService>();

    /**
     * Selects a specific client (HTTP by default) id available and uses it to send the request.
     * @param requestIndication - The generic request to handle
     * @return The generic returned response
     */
    public ResponseConfirm sendRequest(RequestIndication requestIndication){
        LOGGER.info(requestIndication);
        // Set http protocol if null
        if(requestIndication.getProtocol()==null){
            requestIndication.setProtocol(Constants.SCL_DEFAULT_PROTOCOL);
        }

        ResponseConfirm responseConfirm = new ResponseConfirm();
        // Find the appropriate client from the map and send the request
        if(restClients.containsKey(requestIndication.getProtocol())){
            try{
                responseConfirm = restClients.get(requestIndication.getProtocol()).sendRequest(requestIndication);
                if(responseConfirm.getStatusCode()==null){
                    throw new Exception();
                }
            }catch(Exception e){
                LOGGER.error("RestClient error",e);
                responseConfirm = new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_INTERNAL_SERVER_ERROR,"RestClient error"));
            }
        }else{
            responseConfirm = new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,"No RestClient service Found"));
        }
        LOGGER.info(responseConfirm);
        return responseConfirm;
    }

    /**
     * Gets RestClients
     * @return restClients
     */
    public static Map<String, RestClientService> getRestClients() {
        return restClients;
    }

    /**
     * Sets RestClient
     * @param sclClients
     */
    public static void setRestClients(Map<String, RestClientService> sclClients) {
        RestClient.restClients = sclClients;
    }
}
