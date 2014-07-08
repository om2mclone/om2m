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
package org.eclipse.om2m.core.controller;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.om2m.commons.resource.APoCPath;
import org.eclipse.om2m.commons.resource.Application;
import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.core.constants.Constants;
import org.eclipse.om2m.core.dao.DAOFactory;
import org.eclipse.om2m.ipu.service.IpuService;

/**
 * Forwards REST requests to the corresponding InterworkingProxy Unit depending on the aPoCPath.
 *
 * @author <ul>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */

public class InterworkingProxyController extends Controller {

    private static Map<String , IpuService> ipUnits = new HashMap<String , IpuService>();

    public static Map<String, IpuService> getIpUnits() {
        return ipUnits;
    }

    public static void setIpUnits(Map<String, IpuService> ipUnits) {
        InterworkingProxyController.ipUnits = ipUnits;
    }

    /**
     * Forwards Create request to the corresponding InterworkingProxy Unit depending on the aPoCPath.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doCreate (RequestIndication requestIndication) {

        ResponseConfirm errorResponse = new ResponseConfirm();
        String sclId = requestIndication.getTargetID().split("/")[0];
        String applicationId = requestIndication.getTargetID().split("/")[2];
        String path = requestIndication.getTargetID().split("/")[3];
        String applicationUri = sclId+"/applications/"+applicationId;

        // Check ApplicationResource Existence
        Application application = DAOFactory.getApplicationDAO().find(applicationUri);
        if (application == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,applicationUri+" does not exist")) ;
        }
        APoCPath aPoCPath = checkAPoCPathExistence(application, path);

        // Check aPoCPath Existence
        if (aPoCPath == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,path+" does not exist")) ;
        }
        // Check accessRight
        errorResponse = checkAccessRight(aPoCPath.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_CREATE);
        if (errorResponse != null) {
            return errorResponse;
        }

        try{
            // Forward the request
            if (ipUnits.containsKey(aPoCPath.getPath())) {
                return ipUnits.get(aPoCPath.getPath()).doCreate(requestIndication);
            }else {
                return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,"No IPU found for path "+aPoCPath.getPath())) ;
            }
        }catch (Exception e) {
            LOGGER.error("IPU Internal Exception", e);
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_INTERNAL_SERVER_ERROR,"IPU Internal Exception")) ;
        }
    }

    /**
     * Forwards Retrieve request to the corresponding InterworkingProxy Unit depending on the aPoCPath.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doRetrieve (RequestIndication requestIndication) {

        ResponseConfirm errorResponse = new ResponseConfirm();
        String sclId = requestIndication.getTargetID().split("/")[0];
        String applicationId = requestIndication.getTargetID().split("/")[2];
        String path = requestIndication.getTargetID().split("/")[3];
        String applicationUri = sclId+"/applications/"+applicationId;

        // Check ApplicationResource Existence
        Application application= DAOFactory.getApplicationDAO().find(applicationUri);
        if (application == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,applicationUri+" does not exist")) ;
        }
        APoCPath aPoCPath = checkAPoCPathExistence(application, path);

        // Check aPoCPath Existence
        if (aPoCPath == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,path+" does not exist")) ;
        }
        // Check accessRight
        errorResponse = checkAccessRight(aPoCPath.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_READ);
        if (errorResponse != null) {
            return errorResponse;
        }

        try{
            // Forward the request
            if (ipUnits.containsKey(aPoCPath.getPath())) {
                return ipUnits.get(aPoCPath.getPath()).doRetrieve (requestIndication);
            }else {
                return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,"No IPU found for path "+aPoCPath.getPath())) ;
            }
        }catch (Exception e) {
            LOGGER.error("IPU Internal Exception", e);
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_INTERNAL_SERVER_ERROR,"IPU Internal Exception")) ;
        }
    }

    /**
     * Forwards Update request to the corresponding InterworkingProxy Unit depending on the aPoCPath.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doUpdate (RequestIndication requestIndication) {

        ResponseConfirm errorResponse = new ResponseConfirm();
        String sclId = requestIndication.getTargetID().split("/")[0];
        String applicationId = requestIndication.getTargetID().split("/")[2];
        String path = requestIndication.getTargetID().split("/")[3];
        String applicationUri = sclId+"/applications/"+applicationId;

        // Check ApplicationResource Existence
        Application application= DAOFactory.getApplicationDAO().find(applicationUri);
        if (application == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,applicationUri+" does not exist")) ;
        }
        APoCPath aPoCPath = checkAPoCPathExistence(application, path);

        // Check aPoCPath Existence
        if (aPoCPath == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,path+" does not exist")) ;
        }
        // Check accessRight
        errorResponse = checkAccessRight(aPoCPath.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_WRITE);
        if (errorResponse != null) {
            return errorResponse;
        }

        try{
            // Forward the request
            if (ipUnits.containsKey(aPoCPath.getPath())) {
                return ipUnits.get(aPoCPath.getPath()).doUpdate (requestIndication);
            }else {
                return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,"No IPU found for path "+aPoCPath.getPath())) ;
            }
        }catch (Exception e) {
            LOGGER.error("IPU Internal Exception", e);
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_INTERNAL_SERVER_ERROR,"IPU Internal Exception")) ;
        }
    }

    /**
     * Forwards Delete request to the corresponding InterworkingProxy Unit depending on the aPoCPath.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doDelete (RequestIndication requestIndication) {

        ResponseConfirm errorResponse = new ResponseConfirm();
        String sclId = requestIndication.getTargetID().split("/")[0];
        String applicationId = requestIndication.getTargetID().split("/")[2];
        String path = requestIndication.getTargetID().split("/")[3];
        String applicationUri = sclId+"/applications/"+applicationId;

        // Check ApplicationResource Existence
        Application application= DAOFactory.getApplicationDAO().find(applicationUri);
        if (application == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,applicationUri+" does not exist")) ;
        }
        APoCPath aPoCPath = checkAPoCPathExistence(application, path);

        // Check aPoCPath Existence
        if (aPoCPath == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,path+" does not exist")) ;
        }
        // Check accessRight
        errorResponse = checkAccessRight(aPoCPath.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_DELETE);
        if (errorResponse != null) {
            return errorResponse;
        }

        try{
            // Forward the request
            if (ipUnits.containsKey(aPoCPath.getPath())) {
                return ipUnits.get(aPoCPath.getPath()).doDelete (requestIndication);
            }else{
                return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,"No IPU found for path "+aPoCPath.getPath())) ;
            }
        }catch (Exception e) {
            LOGGER.error("IPU Internal Exception", e);
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_INTERNAL_SERVER_ERROR,"IPU Internal Exception")) ;
        }
    }

    /**
     * Forwards Execute request to the corresponding InterworkingProxy Unit depending on the aPoCPath.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doExecute (RequestIndication requestIndication) {

        ResponseConfirm errorResponse = new ResponseConfirm();
        String sclId = requestIndication.getTargetID().split("/")[0];
        String applicationId = requestIndication.getTargetID().split("/")[2];
        String path = requestIndication.getTargetID().split("/")[3];
        String applicationUri = sclId+"/applications/"+applicationId;

        // Check ApplicationResource Existence
        Application application= DAOFactory.getApplicationDAO().find(applicationUri);
        if (application == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,applicationUri+" does not exist")) ;
        }

        APoCPath aPoCPath = checkAPoCPathExistence(application, path);
        // Check aPoCPath Existence
        if (aPoCPath == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,path+" does not exist")) ;
        }
        // Check accessRight
        errorResponse = checkAccessRight(aPoCPath.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_CREATE);
        if (errorResponse != null) {
            return errorResponse;
        }

        try{
            // Forward the request
            if (ipUnits.containsKey(aPoCPath.getPath())) {
                return ipUnits.get(aPoCPath.getPath()).doExecute (requestIndication);
            }else {
                return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,"No IPU found for path "+aPoCPath.getPath())) ;
            }
        }catch (Exception e) {
            LOGGER.error("IPU Internal Exception", e);
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_INTERNAL_SERVER_ERROR,"IPU Internal Exception")) ;
        }
    }

    /**
     * Checks aPocPath existence in application resource.
     * @param {@link Application} resource
     * @param path - The path of the researched aPocPath
     * @return {@link APoCPath}
     */
    public APoCPath checkAPoCPathExistence (Application application, String path) {
        if (application.getAPoCPaths() != null) {
            for (int i = 0; i < application.getAPoCPaths().getAPoCPath().size(); i++) {
                if (application.getAPoCPaths().getAPoCPath().get(i).getPath().equalsIgnoreCase(path)) {
                    return application.getAPoCPaths().getAPoCPath().get(i);
                }
            }
        }
        return null;
    }
}
