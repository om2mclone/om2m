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
package org.eclipse.om2m.comm.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.comm.service.RestClientService;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;

/**
 *  Provides mapping from a protocol-independent request to a HTTP-specific request.
 *  @author <ul>
 *         <li> Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li> Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li> Marouane El kiasse < melkiasse@laas.fr > < kiasmarouane@gmail.com ></li>
 *         </ul>
 */

public class RestHttpClient implements RestClientService {
    /** Logger */
    private static Log LOGGER = LogFactory.getLog(RestHttpClient.class);
    /** implemented specific protocol name */
    private static String protocol ="http";

    /**
    * gets the implemented specific protocol name
    * @return protocol name
    */
    public String getProtocol() {
        return protocol;
    }

    /**
    * Converts a protocol-independent {@link RequestIndication} object into a standard HTTP request and sends a standard HTTP request.
    * Converts the received standard HTTP request into {@link ResponseConfirm} object and returns it back.
    * @param requestIndication - protocol independent request.
    * @return protocol independent response.
    */
    public ResponseConfirm sendRequest(RequestIndication requestIndication) {
        LOGGER.debug("Http Client > "+requestIndication);
        HttpClient httpclient = new HttpClient();

        ResponseConfirm responseConfirm = new ResponseConfirm();
        HttpMethod httpMethod=null;
        String url = requestIndication.getUrl();
        if(!url.startsWith(protocol+"://")){
            url=protocol+"://"+url;
        }
        try {
            switch (requestIndication.getMethod()){
            case "RETRIEVE" :
                httpMethod =  new GetMethod(url);
                break;
            case "CREATE":
                httpMethod = new PostMethod(url);

                ((PostMethod)httpMethod).setRequestEntity(new StringRequestEntity(requestIndication.getRepresentation(),"application/xml", "UTF8"));
                break;
            case "UPDATE":
                httpMethod = new PutMethod(url);
                ((PutMethod)httpMethod).setRequestEntity(new StringRequestEntity(requestIndication.getRepresentation(),"application/xml", "UTF8"));
                break;
            case "DELETE":
                httpMethod =new DeleteMethod(url);
                break;
            case "EXECUTE":
                httpMethod = new PostMethod(url);
                break;
            default: return new ResponseConfirm();
            }
            httpMethod.addRequestHeader("Authorization", "Basic "+new String(Base64.encodeBase64(requestIndication.getRequestingEntity().getBytes())));
            httpMethod.setQueryString(getQueryFromParams(requestIndication.getParameters()));

            int statusCode = httpclient.executeMethod(httpMethod);
            responseConfirm.setStatusCode(getRestStatusCode(statusCode));

            if(statusCode!=204){
                if(httpMethod.getResponseBody()!=null){
                    responseConfirm.setRepresentation(new String(httpMethod.getResponseBody()));
                }
            }
            if(statusCode==201){
                if(httpMethod.getResponseHeader("Location").getValue()!=null){
                    responseConfirm.setResourceURI(httpMethod.getResponseHeader("Location").getValue());
                }
            }
            LOGGER.debug("Http Client > "+responseConfirm);

        }catch(IOException e){
            LOGGER.error(url+ " Not Found"+responseConfirm,e);
        } finally {
            httpMethod.releaseConnection();
        }

        return responseConfirm;
    }

    /**
    * Converts a standard HTTP status code into a protocol-independent {@link StatusCode} object.
    * @param statusCode - standard HTTP status code.
    * @return protocol independent status.
    */
    public static StatusCode getRestStatusCode(int statusCode){
        switch(statusCode){
        case 200: return StatusCode.STATUS_OK;
        case 204: return StatusCode.STATUS_OK;
        case 202: return StatusCode.STATUS_ACCEPTED;
        case 201: return StatusCode.STATUS_CREATED;
        case 400: return StatusCode.STATUS_BAD_REQUEST;
        case 401: return StatusCode.STATUS_PERMISSION_DENIED;
        case 402: return StatusCode.STATUS_AUTHORIZATION_NOT_ADDED;
        case 403: return StatusCode.STATUS_FORBIDDEN;
        case 404: return StatusCode.STATUS_NOT_FOUND;
        case 405: return StatusCode.STATUS_METHOD_NOT_ALLOWED;
        case 406: return StatusCode.STATUS_NOT_ACCEPTABLE;
        case 408: return StatusCode.STATUS_REQUEST_TIMEOUT;
        case 409: return StatusCode.STATUS_CONFLICT;
        case 415: return StatusCode.STATUS_UNSUPPORTED_MEDIA_TYPE;
        case 500: return StatusCode.STATUS_INTERNAL_SERVER_ERROR;
        case 501: return StatusCode.STATUS_NOT_IMPLEMENTED;
        case 502: return StatusCode.STATUS_BAD_GATEWAY;
        case 503: return StatusCode.STATUS_SERVICE_UNAVAILABLE;
        case 504: return StatusCode.STATUS_GATEWAY_TIMEOUT;
        case 410: return StatusCode.STATUS_EXPIRED;
        default : return StatusCode.STATUS_INTERNAL_SERVER_ERROR;
        }
    }

    /**
    * Converts a protocol independent parameters into a standard HTTP parameters.
    * @param params - protocol independent parameters map.
    * @return standard HTTP query string.
    */
    public static String getQueryFromParams(Map<String, List<String>> params){
        String query;
        List<String> values = new ArrayList<String>();
        String name;
        if (params != null) {
            query="?";
            Iterator<String> it = params.keySet().iterator();
            while(it.hasNext()){
                name = it.next().toString();
                values = params.get(name);
                for(int i=0;i<values.size();i++){
                    query = query+name+"="+values.get(i)+"&";
                }
            }
            return query;
        }
        return null;
    }
}
