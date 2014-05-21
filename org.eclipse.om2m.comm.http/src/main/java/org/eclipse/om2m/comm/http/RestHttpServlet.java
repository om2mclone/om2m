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
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.core.service.SclService;

/**
 *  Provides mapping from a HTTP-specific request to a protocol-independent request.
 *
 *  @author <ul>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Marouane El kiasse < melkiasse@laas.fr > < kiasmarouane@gmail.com ></li>
 *         </ul>
 */
public class RestHttpServlet extends HttpServlet {
    /** Logger */
    private static Log LOGGER = LogFactory.getLog(RestHttpServlet.class);
    /** Serial Version UID */
    private static final long serialVersionUID = 1L;
    /** Discovered SCL service */
    private static SclService scl;

    /**
     * Converts a {@link HttpServletRequest} to a {@link RequestIndication} and uses it to invoke the SCL service.
     * Converts the received {@link ResponseConfirm} to a {@link HttpServletResponse} and returns it back.
     */
    @Override
    protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        //Construct a requestIndication Object from the http request
        RequestIndication requestIndication = new RequestIndication();
        //Get the targetID
        String targetID = httpServletRequest.getRequestURI().substring(System.getProperty("org.eclipse.om2m.sclBaseContext").length());
        requestIndication.setTargetID(targetID);
        //Get the representation
        String representation = null;
        try {
            representation = convertStreamToString(httpServletRequest.getInputStream());
        } catch (IOException e) {
            LOGGER.error("Error reading httpServletRequest InputStream",e);
        }
        requestIndication.setRepresentation(representation);
        //Get the method
        String httpMethod = httpServletRequest.getMethod();
        String restMethod = getRestMethod(httpMethod, representation.isEmpty());
        requestIndication.setMethod(restMethod);
        //Get the authorization
        String authorization = httpServletRequest.getHeader("Authorization");
        if (authorization != null) {
            if (authorization.startsWith("Basic")) {
                String requestingEntity64 = authorization.split(" ")[1];
                if (requestingEntity64 != null) {
                    requestIndication.setRequestingEntity(new String(Base64.decodeBase64(requestingEntity64.getBytes())));
                }
            }
        }
        //Get the request parameters
        String queryString=httpServletRequest.getQueryString();
        if(httpServletRequest.getQueryString()!=null){
            requestIndication.setParameters(getParamsFromQuery(queryString));
        }
        requestIndication.setProtocol("http");
        //Construct the response to return
        ResponseConfirm responseConfirm = new ResponseConfirm();
        LOGGER.info(httpRequestToString(httpMethod, targetID, representation, authorization, queryString));
        if(scl!=null){
            responseConfirm = scl.doRequest(requestIndication);
        }else{
            responseConfirm = new ResponseConfirm(StatusCode.STATUS_SERVICE_UNAVAILABLE, "SCL service not installed");
        }
        boolean isEmptyResponse=false;
        if(responseConfirm.getRepresentation()==null || responseConfirm.getRepresentation().isEmpty()){
            isEmptyResponse=true;
        }
        int statusCode = getHttpStatusCode(responseConfirm.getStatusCode(),isEmptyResponse);

        if (statusCode == 201) {
            if(responseConfirm.getResourceURI()!=null){
                httpServletResponse.addHeader("Location", responseConfirm.getResourceURI());
            }
        }
        if (statusCode != 204){
            httpServletResponse.setContentType("application/xml");
        }
        httpServletResponse.setStatus(statusCode);
        String body = responseConfirm.getRepresentation();
        PrintWriter out = null;
        try {
            out = httpServletResponse.getWriter();
        } catch (IOException e) {
            LOGGER.error("Error reading httpServletResponse Writer",e);
        }
        out.println(body);
        out.close();
        LOGGER.info(httpResponseToString(statusCode,body));
    }

    /**
    * Converts a standard HTTP query String into a protocol independent parameters map.
    * @param query - standard HTTP query String.
    * @return protocol independent parameters map.
    */
    public static Map<String, List<String>> getParamsFromQuery(String query){
        Map<String, List<String>> parameters = new HashMap<String, List<String>>();
        if (query != null) {
            String[] pairs = query.split("[&]");
            for (String pair : pairs) {
                String[] param = pair.split("[=]");

                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = param[0];
                }
                if (param.length > 1) {
                    value = param[1];
                }
                if (parameters.containsKey(key)) {
                    parameters.get(key).add(value);
                } else {
                    List<String> values = new ArrayList<String>();
                    values.add(value);
                    parameters.put(key,values);
                }
            }
        }
        return parameters;
    }
    /**
    * Converts a protocol-independent method to standard HTTP method
    * @param method - protocol-independent method
    * @param isEmptyBody - request body existence
    * @return standard HTTP method
    */
    public static String getRestMethod(String method, boolean isEmptyBody) {
        switch (method) {
        case "GET":
            return "RETRIEVE";
        case "POST":
            if (isEmptyBody) {
                return "EXECUTE";
            }
            return "CREATE";

        case "PUT": return "UPDATE";
        case "DELETE": return "DELETE";
        default: return null;
        }
    }

    /**
     * Converts a standard HTTP status code into a  {@link StatusCode} object.
     * @param statusCode - protocol-independent status code.
     * @param isEmptyBody - request body existence
     * @return standard HTTP status code.
     */
    public static int getHttpStatusCode(StatusCode statusCode, boolean isEmptyBody){
        switch(statusCode){
        case STATUS_OK :
            if (isEmptyBody) {
                return 204;
            }
            return 200;
        case STATUS_ACCEPTED: return 202;
        case STATUS_CREATED: return 201;
        case STATUS_BAD_REQUEST: return 400;
        case STATUS_PERMISSION_DENIED: return 401;
        case STATUS_AUTHORIZATION_NOT_ADDED: return 402;
        case STATUS_FORBIDDEN: return 403;
        case STATUS_NOT_FOUND: return 404;
        case STATUS_METHOD_NOT_ALLOWED: return 405;
        case STATUS_NOT_ACCEPTABLE: return 406;
        case STATUS_REQUEST_TIMEOUT: return 408;
        case STATUS_CONFLICT: return 409;
        case STATUS_UNSUPPORTED_MEDIA_TYPE: return 415;
        case STATUS_INTERNAL_SERVER_ERROR: return 500;
        case STATUS_NOT_IMPLEMENTED: return 501;
        case STATUS_BAD_GATEWAY: return 502;
        case STATUS_SERVICE_UNAVAILABLE: return 503;
        case STATUS_GATEWAY_TIMEOUT: return 504;
        case STATUS_EXPIRED: return 410;
        default : return 500;
        }
    }

    public static String httpRequestToString(String method, String uri, String rep, String auth, String query){
        return "HttpRequest [method=" + method + ", URI=" +uri+ ", representation=" + rep +
                ", Authorization=" + auth+", queryString=" + query + "]";
    }

    public static String httpResponseToString(int statusCode, String rep){
        return "HttpResponse [statusCode=" + statusCode /*+ ", representation=" + rep*/ + "]";
    }

    public static SclService getScl() {
        return scl;
    }

    public static void setScl(SclService scl) {
        RestHttpServlet.scl = scl;
    }

    static String convertStreamToString(InputStream is) {
        Scanner scanner = new Scanner(is);
        scanner.useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
