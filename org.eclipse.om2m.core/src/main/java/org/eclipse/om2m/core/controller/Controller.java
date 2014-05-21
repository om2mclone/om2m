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

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.resource.AccessRight;
import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.PermissionListType;
import org.eclipse.om2m.commons.resource.PermissionType;
import org.eclipse.om2m.commons.resource.SearchStrings;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.commons.utils.DateConverter;
import org.eclipse.om2m.commons.utils.XmlValidator;
import org.eclipse.om2m.core.constants.Constants;
import org.eclipse.om2m.core.dao.DAOFactory;
import org.xml.sax.SAXException;

/**
 * Controller class contains generic and abstract Create, Retrieve, Update, Delete and Execute
 * methods to handle generic REST request that will be implemented in extended-to classes.
 *
 * @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         </ul>
 */
public abstract class Controller {
    /** Logger */
    protected static Log LOGGER = LogFactory.getLog(Controller.class);

    /**
     * Abstract Create method to handle generic REST request.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public abstract ResponseConfirm doCreate (RequestIndication requestIndication);

    /**
     * Abstract Retrieve method to handle generic REST request.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public abstract ResponseConfirm doRetrieve (RequestIndication requestIndication);

    /**
     * Abstract Update method to handle generic REST request.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public abstract ResponseConfirm doUpdate (RequestIndication requestIndication);

    /**
     * Abstract Delete method to handle generic REST request.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public abstract ResponseConfirm doDelete (RequestIndication requestIndication);

    /**
     * Abstract Execute method to handle generic REST request.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public abstract ResponseConfirm doExecute (RequestIndication requestIndication);

    /**
     * Checks the validity of the resource representation syntax based on the xsd schema verification
     * @param resourceRepresentation - the XML representation of the resource
     * @param xsd - XML Schema Definition
     * @return the error with a specific status code if the representation is wrong otherwise null
     */
    public ResponseConfirm checkMessageSyntax(String resourceRepresentation, String xsd) {
        try {
            XmlValidator.getInstance().validate(resourceRepresentation, xsd);
        } catch (SAXException e) {
            LOGGER.debug("Resource representation syntax error",e);
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Resource representation syntax error: "+e.getMessage())) ;
        } catch (IOException e) {
            LOGGER.debug("XSD not found",e);
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"XSD not found: "+e.getMessage())) ;
        }
        return null;
    }

    /**
     * Checks the Access Right based on accessRightID (Permission)
     * @param accessRightID - Id of the accessRight
     * @param requestingEntity - requesting entity used by the requester
     * @param method - requested method
     * @return error with a specific status code if the requesting Entity or the method does not exist otherwise null
     */
    public ResponseConfirm checkAccessRight(String accessRightID, String requestingEntity, String method) {
        boolean holderFound = false;
        boolean flagFound = false;
        AccessRight accessRightFound = DAOFactory.getAccessRightDAO().find(accessRightID);
        // Check Resource accessRight existence not found
        if (accessRightFound == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,"AccessRight for this resource is not found"));
        }
        // Check permissions if the accessRight is found
        if (accessRightFound != null) {
            // Get Permissions List
            PermissionListType permissions = accessRightFound.getPermissions();
            for (int j=0; j<permissions.getPermission().size(); j++) {
                holderFound = false;
                PermissionType permission = permissions.getPermission().get(j);
                // Specific Holder
                String holder;
                String flag;
                // Holders in a permission
                for (int i=0; i<permission.getPermissionHolders().getHolderRefs().getHolderRef().size(); i++) {
                    holder = permission.getPermissionHolders().getHolderRefs().getHolderRef().get(i);
                    if (holder.equalsIgnoreCase(requestingEntity)) {
                        holderFound = true;
                        break;
                    }
                }
                if (holderFound) {
                    for (int k=0; k<permission.getPermissionFlags().getFlag().size(); k++) {
                        flag = permission.getPermissionFlags().getFlag().get(k).toString();
                        if (flag.equalsIgnoreCase(method)) {
                            flagFound = true;
                            break;
                        }
                    }
                    // The holder exists just in one permission of all permissions
                    break;
                }
            }
        }
        // returns STATUS_NOT_FOUND error if the holder is not found
        if (!holderFound) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,"Requesting Entity ["+requestingEntity+"] does not exist in permissions"));
        }
        // returns STATUS_PERMISSION_DENIED error if the holder is found but the flag is not.
        if (!flagFound) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_PERMISSION_DENIED,method+" Method does not exist in permissions"));
        }
        return null;
    }

    /**
     * Checks AccessRight based on selfPermission
     * @param selfPermissions - selfPermissions attribute of the accessRight
     * @param Client requesting entity
     * @param method - requested method
     * @return error with a specific status code if the requesting Entity or the method does not exist otherwise null
     */
    public ResponseConfirm checkSelfPermissions(PermissionListType selfPermissions, String requestingEntity, String method) {
        boolean holderFound = false;
        boolean flagFound = false;
        for (int j=0; j<selfPermissions.getPermission().size(); j++) {
            holderFound = false;
            PermissionType selfPermission = selfPermissions.getPermission().get(j);
            // Specific Holder
            String holder;
            String flag;
            // Holders in a permission
            for (int i=0; i<selfPermission.getPermissionHolders().getHolderRefs().getHolderRef().size(); i++) {
                holder = selfPermission.getPermissionHolders().getHolderRefs().getHolderRef().get(i);
                if (holder.equalsIgnoreCase(requestingEntity)) {
                    holderFound = true;
                    break;
                }
            }
            if (holderFound) {
                for (int k=0; k<selfPermission.getPermissionFlags().getFlag().size(); k++) {
                    flag = selfPermission.getPermissionFlags().getFlag().get(k).toString();
                    if (flag.equalsIgnoreCase(method)) {
                        flagFound = true;
                        break;
                    }
                }
                // The holder exists just in one permission of all permissions
                break;
            }
        }
        // returns STATUS_NOT_FOUND error if the holder is not found
        if (!holderFound) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,"Requesting Entity ["+requestingEntity+"] does not exist in permissions")) ;
        }
        // returns STATUS_PERMISSION_DENIED error if the holder is found but the flag is not.
        if (!flagFound) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_PERMISSION_DENIED,method+" Method does not exist in permissions")) ;
        }
        return null;
    }

    /**
     * Checks if the expirationTime is out of date or not
     * @param expirationTime - expiration time present in the request representation
     * @return false if the expirationTime attribute is out of date otherwise true
     */
    public boolean checkExpirationTime(XMLGregorianCalendar expirationTime) {
        Boolean isNotExpired;
        XMLGregorianCalendar now = DateConverter.toXMLGregorianCalendar(new Date());

        if (expirationTime != null && expirationTime.toGregorianCalendar().compareTo(now.toGregorianCalendar()) > 0) {
            isNotExpired = true;
            return isNotExpired;
        } else {
            isNotExpired = false;
            return isNotExpired;
        }
    }

    /**
     * Generates a new ExpirationTime by adding seconds to the current Time
     * @param addedSeconds - seconds to add to the current time
     * @return New expirationTime value of the resource
     */
    public XMLGregorianCalendar getNewExpirationTime(long addedSeconds) {
        long addedMilSeconds = addedSeconds * 1000;
        Date newDate = new Date((new Date()).getTime() + addedMilSeconds);
        return DateConverter.toXMLGregorianCalendar(newDate);
    }

    /**
     * Generates a new DelayTolerance by adding seconds to the current Time
     * @param addedSeconds - seconds to add to the current time
     * @return New delayTolerance of the resource
     */
    public XMLGregorianCalendar getNewDelayTolerance(long addedSeconds) {
        long addedMilSeconds = addedSeconds * 1000;
        Date newDate = new Date((new Date()).getTime() + addedMilSeconds);
        return DateConverter.toXMLGregorianCalendar(newDate);
    }

    /**
     * Generates an aleatory ID based on SecureRandom library
     * @param prefix - prefix of the resource ID
     * @param postfix - postfix of the resource ID
     * @return generated resource ID
     */
    public static String generateId(String prefix, String postfix) {
        SecureRandom secureRandom = new SecureRandom();
        return prefix+String.valueOf(secureRandom.nextInt(999999999))+postfix;
    }

    /**
     * Generates default resource {@link SearchStrings}
     * @param Resourcetype - The resource Type
     * @param resourceId - The resource ID
     * @return generated {@link SearchStrings}
     */
    public static SearchStrings generateSearchStrings (String Resourcetype, String resourceId) {
        SearchStrings searchStrings = new SearchStrings();
        searchStrings.getSearchString().add(Constants.SEARCH_STRING_RES_TYPE+Resourcetype);
        searchStrings.getSearchString().add(Constants.SEARCH_STRING_RES_ID+resourceId);
        return searchStrings;
    }
}
