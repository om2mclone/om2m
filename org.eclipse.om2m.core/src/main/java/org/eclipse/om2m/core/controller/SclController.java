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

import java.util.Date;

import org.eclipse.om2m.commons.resource.AnyURIList;
import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.MgmtProtocolType;
import org.eclipse.om2m.commons.resource.OnlineStatus;
import org.eclipse.om2m.commons.resource.Scl;
import org.eclipse.om2m.commons.resource.Scls;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.commons.utils.DateConverter;
import org.eclipse.om2m.commons.utils.XmlMapper;
import org.eclipse.om2m.core.comm.RestClient;
import org.eclipse.om2m.core.constants.Constants;
import org.eclipse.om2m.core.dao.DAOFactory;
import org.eclipse.om2m.core.notifier.Notifier;

/**
 * Implements Create, Retrieve, Update, Delete and Execute methods to handle
 * generic REST request for {@link Scl} resource.
 *
 * @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         </ul>
 */

public class SclController extends Controller {

    /**
     * Creates {@link Scl} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doCreate (RequestIndication requestIndication) {

        // containersReference:             (createReq NP) (response M)
        // groupsReference:                 (createReq NP) (response M)
        // applicationsReference:           (createReq NP) (response M)
        // accessRightsReference:           (createReq NP) (response M)
        // subscriptionsReference:          (createReq NP) (response M)
        // mgmtObjsReference:               (createReq NP) (response M)
        // notificationChannelsReference:   (createReq NP) (response M)
        // m2mpocsReference:                (createReq NP) (response M)
        // attachedDevicesReference:        (createReq NP) (response M)
        // sclId:                           (createReq M)  (response M)
        // pocs:                            (createReq O)  (response M)
        // remTriggerAddr:                  (createReq O)  (response O)
        // onlineStatus:                    (createReq NP) (response M)
        // serverCapability:                (createReq NP) (response M)
        // link:                            (createReq M)  (response M)
        // schedule:                        (createReq O)  (response O)
        // expirationTime:                  (createReq O)  (response M)
        // accessRightID:                   (createReq O)  (response O)
        // searchStrings:                   (createReq O)  (response M)
        // creationTime:                    (createReq NP) (response M)
        // lastModifiedTime:                (createReq NP) (response M)
        // locTargetDevice:                 (createReq O)  (response O)
        // mgmtProtocolType:                (createReq M)  (response M)
        // integrityValResult:              (createReq O)  (response O)
        // aPocHandling:                    (createReq NP) (response O)

        ResponseConfirm errorResponse = new ResponseConfirm();
        Scls scls = DAOFactory.getSclsDAO().lazyFind(requestIndication.getTargetID());

        // CheckResourceParentExistence
        if (scls == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(scls.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_CREATE);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Check Resource Representation
        if (requestIndication.getRepresentation() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Resource Representation is EMPTY")) ;
        }
        // Check XML Validity
        errorResponse = checkMessageSyntax(requestIndication.getRepresentation(),"scl.xsd");
        if (errorResponse != null) {
            return errorResponse;
        }
        // Checks on attributes
        Scl scl = (Scl) XmlMapper.getInstance().xmlToObject(requestIndication.getRepresentation());
        // SclId is Mandatory
        if (scl.getSclId() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST," sclId is Mandatory")) ;
        }
        // Check the Id uniqueness
        if (DAOFactory.getSclDAO().find(requestIndication.getTargetID()+"/"+scl.getSclId()) != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_CONFLICT,"SclId Conflit")) ;
        }
        // Check ExpirationTime
        if (scl.getExpirationTime() != null && !checkExpirationTime(scl.getExpirationTime())) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Expiration Time CREATE is Out of Date")) ;
        }
        // CreationTime Must be NP
        if (scl.getCreationTime() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Creation Time CREATE is Not Permitted")) ;
        }
        // LastModifiedTime Must be NP
        if (scl.getLastModifiedTime() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Last Modified Time CREATE is Not Permitted")) ;
        }
        // Containers Reference Must be NP
        if (scl.getContainersReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Containers Reference is not permitted")) ;
        }
        //Groups Reference Must be NP
        if (scl.getGroupsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Groups Reference is not permitted")) ;
        }
        // Applications Reference Must be NP
        if (scl.getApplicationsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Applications Reference is not permitted")) ;
        }
        // AccessRights Reference Must be NP
        if (scl.getAccessRightsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"AccessRights Reference is not permitted")) ;
        }
        // Subscriptions Reference Must be NP
        if (scl.getSubscriptionsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Subscriptions Reference is not permitted")) ;
        }
        // MgmtObjs Reference Must be NP
        if (scl.getMgmtObjsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"MgmtObjs Reference is not permitted")) ;
        }
        // NotificationChannels Reference Must be NP
        if (scl.getNotificationChannelsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"NotificationChannels Reference is not permitted")) ;
        }
        // m2mPocs Reference Must be NP
        if (scl.getM2MPocsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"M2MPocs Reference is not permitted")) ;
        }
        // AttachedDevices Reference Must be NP
        if (scl.getAttachedDevicesReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"AttachedDevices Reference is not permitted")) ;
        }
        // OnlineStatus attribute Must be NP
        if (scl.getOnlineStatus() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"OnLineStatus is not permitted")) ;
        }
        // ServerCapability Attribute Must be NP
        if (scl.isServerCapability() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"ServerCapability is not permitted")) ;
        }
        // Link attribute Must be M
        if (scl.getLink() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Link attribute is mandatory")) ;
        }
        // MgmtProtocolType attribute Must be M
        if (scl.getMgmtProtocolType() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"MgmtProtocol attribute is mandatory")) ;
        }
        // aPocHandling attribute Must be NP
        if (scl.getAPocHandling() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"aPocHandling is not permitted")) ;
        }

        // Storage
        // Set URI
        scl.setUri(requestIndication.getTargetID()+ "/" +scl.getSclId());
        // Set Expiration Time if it is null
        if (scl.getExpirationTime() == null) {
            // ExpirationTime infinity value
            scl.setExpirationTime(getNewExpirationTime(Constants.EXPIRATION_TIME));
        }
        // Set AccessRightID from the Parent if it's null or nonexistent
        if (DAOFactory.getAccessRightDAO().find(scl.getAccessRightID()) == null) {
            scl.setAccessRightID(scls.getAccessRightID());
        }
        // Set searchString if it's null
        if (scl.getSearchStrings() == null) {
            scl.setSearchStrings(generateSearchStrings(scl.getClass().getSimpleName(), scl.getSclId()));
        }
        // Mandatory Attributes
        // OnlineStatus
        scl.setOnlineStatus(OnlineStatus.ONLINE);
        scl.setServerCapability(true);
        // Set References
        scl.setContainersReference(scl.getUri()+"/containers");
        scl.setGroupsReference(scl.getUri()+"/groups");
        scl.setApplicationsReference(scl.getUri()+"/applications");
        scl.setAccessRightsReference(scl.getUri()+"/accessRights");
        scl.setSubscriptionsReference(scl.getUri()+"/subscriptions");
        scl.setMgmtObjsReference(scl.getUri()+"/mgmtObjs");
        scl.setNotificationChannelsReference(scl.getUri()+"/notificationChannels");
        scl.setM2MPocsReference(scl.getUri()+"/m2mPocs");
        scl.setAttachedDevicesReference(scl.getUri()+"/attachedDevices");
        // Set CreationTime
        scl.setCreationTime(DateConverter.toXMLGregorianCalendar(new Date()));
        // Set LastModifiedTime
        scl.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));

        // Notify the subscribers
        Notifier.notify(StatusCode.STATUS_CREATED, scl);

        // Store scl
        DAOFactory.getSclDAO().create(scl);

        // Response
        return new ResponseConfirm(StatusCode.STATUS_CREATED, scl);
    }

    /**
     * Retrieves {@link Scl} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doRetrieve (RequestIndication requestIndication) {

        // containersReference:             (response M)
        // groupsReference:                 (response M)
        // applicationsReference:           (response M)
        // accessRightsReference:           (response M)
        // subscriptionsReference:          (response M)
        // mgmtObjsReference:               (response M)
        // notificationChannelsReference:   (response M)
        // m2mpocsReference:                (response M)
        // attachedDevicesReference:        (response M)
        // sclId:                           (response M)
        // pocs:                            (response M)
        // remTriggerAddr:                  (response O)
        // onlineStatus:                    (response M)
        // serverCapability:                (response M)
        // link:                            (response M)
        // schedule:                        (response O)
        // expirationTime:                  (response M)
        // accessRightID:                   (response O)
        // searchStrings:                   (response M)
        // creationTime:                    (response M)
        // lastModifiedTime:                (response M)
        // locTargetDevice:                 (response O)
        // mgmtProtocolType:                (response M)
        // integrityValResult:              (response O)
        // aPocHandling:                    (response O)

        ResponseConfirm errorResponse = new ResponseConfirm();
        Scl scl = DAOFactory.getSclDAO().find(requestIndication.getTargetID());

        // Check resource existence
        if (scl == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist in DataBase")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(scl.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_READ);
        if (errorResponse != null) {
            return errorResponse;
        }

        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, scl);

    }

    /**
     * Updates {@link Scl} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doUpdate (RequestIndication requestIndication) {

        // containersReference:             (updateReq NP) (response M)
        // groupsReference:                 (updateReq NP) (response M)
        // applicationsReference:           (updateReq NP) (response M)
        // accessRightsReference:           (updateReq NP) (response M)
        // subscriptionsReference:          (updateReq NP) (response M)
        // mgmtObjsReference:               (updateReq NP) (response M)
        // notificationChannelsReference:   (updateReq NP) (response M)
        // m2mpocsReference:                (updateReq NP) (response M)
        // attachedDevicesReference:        (updateReq NP) (response M)
        // sclId:                           (updateReq NP) (response M)
        // pocs:                            (updateReq O)  (response M)
        // remTriggerAddr:                  (updateReq O)  (response O)
        // onlineStatus:                    (updateReq NP) (response M)
        // serverCapability:                (updateReq NP) (response M)
        // link:                            (updateReq NP) (response M)
        // schedule:                        (updateReq O)  (response O)
        // expirationTime:                  (updateReq O)  (response M)
        // accessRightID:                   (updateReq O)  (response O)
        // searchStrings:                   (updateReq O)  (response M)
        // creationTime:                    (updateReq NP) (response M)
        // lastModifiedTime:                (updateReq NP) (response M)
        // locTargetDevice:                 (updateReq O)  (response O)
        // mgmtProtocolType:                (updateReq M)  (response M)
        // integrityValResult:              (updateReq O)  (response O)
        // aPocHandling:                    (updateReq NP) (response O)

        ResponseConfirm errorResponse = new ResponseConfirm();
        Scl scl = DAOFactory.getSclDAO().find(requestIndication.getTargetID());

        // Check Existence
        if (scl == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist in DataBase")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(scl.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_WRITE);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Check Resource Representation
        if (requestIndication.getRepresentation() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Resource Representation is EMPTY")) ;
        }
        // Check XML Validity
        errorResponse= checkMessageSyntax(requestIndication.getRepresentation(),"scl.xsd");
        if (errorResponse != null) {
            return errorResponse;
        }
        // Checks attributes
        // Construct New Resource
        Scl sclNew = (Scl) XmlMapper.getInstance().xmlToObject(requestIndication.getRepresentation());
        // The Update of the SclId is NP
        if (sclNew.getSclId() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"SclId UPDATE is Not Permitted")) ;
        }
        // Check ExpirationTime
        if (sclNew.getExpirationTime() != null && !checkExpirationTime(sclNew.getExpirationTime())) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Expiration Time UPDATE is Out of Date")) ;
        }
        // Containers Reference Must be NP
        if (sclNew.getContainersReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Containers Reference UPDATE is Not Permitted")) ;
        }
        // Groups Reference Must be NP
        if (sclNew.getGroupsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Groups Reference UPDATE is Not Permitted")) ;
        }
        // Applications Reference Must be NP
        if (sclNew.getApplicationsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Applications Reference UPDATE is Not Permitted")) ;
        }
        // AccessRights Reference Must be NP
        if (sclNew.getAccessRightsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"AccessRights Reference UPDATE is Not Permitted")) ;
        }
        // Subscriptions Reference Must be NP
        if (sclNew.getSubscriptionsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Subscriptions Reference UPDATE is Not Permitted")) ;
        }
        // MgmtObjs Reference Must be NP
        if (sclNew.getMgmtObjsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"MgmtObjs Reference UPDATE is Not Permitted")) ;
        }
        // NotificationChannels Reference Must be NP
        if (sclNew.getNotificationChannelsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"NotificationChannels Reference UPDATE is Not Permitted")) ;
        }
        // m2mPocs Reference Must be NP
        if (sclNew.getM2MPocsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"M2MPocs Reference UPDATE is Not Permitted")) ;
        }
        // AttachedDevices Reference Must be NP
        if (sclNew.getAttachedDevicesReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"AttachedDevices Reference UPDATE is Not Permitted")) ;
        }
        // OnlineStatus attribute Must be NP
        if (sclNew.getOnlineStatus() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"OnLineStatus attribute UPDATE is Not Permitted")) ;
        }
        // ServerCapability Attribute Must be NP
        if (sclNew.isServerCapability() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"ServerCapability attrivute UPDATE is Not Permitted")) ;
        }

        // Link Attribute Must be NP
        if (sclNew.getLink() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Link attrivute UPDATE is Not Permitted")) ;
        }
        // MgmtProtocolType is Mandatory
        if (sclNew.getMgmtProtocolType() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"MgmtProtocolType attrivute UPDATE is Mandatory")) ;
        }
        // CreationTime Must be NP
        if (sclNew.getCreationTime() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"CreationTime UPDATE is Not Permitted")) ;
        }
        // LastModifiedTime Must be NP
        if (sclNew.getLastModifiedTime() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"LatModifiedTime UPDATE is Not Permitted")) ;
        }
        // aPocHandling Must be NP
        if (sclNew.getAPocHandling() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"aPocHandling attribute UPDATE is Not Permitted")) ;
        }
        // Storage
        // Set Expiration Time
        if (sclNew.getExpirationTime() != null) {
            scl.setExpirationTime(sclNew.getExpirationTime());
        }
        // Set accessRightID if it exists
        if (DAOFactory.getAccessRightDAO().find(sclNew.getAccessRightID()) != null) {
            scl.setAccessRightID(sclNew.getAccessRightID());
        }
        // Set searchStrings from New Scl if it exists
        if (sclNew.getSearchStrings() != null) {
            scl.setSearchStrings(sclNew.getSearchStrings());
        }
        // Set pocs from New Scl if it exists
        if (sclNew.getPocs() != null) {
            scl.setPocs(sclNew.getPocs());
        }
        // Set remTriggerAddr from New Scl if it exists
        if (sclNew.getRemTriggerAddr() != null) {
            scl.setRemTriggerAddr(sclNew.getRemTriggerAddr());
        }
        // Set schedule from New Scl if it exists
        if (sclNew.getSchedule() != null) {
            scl.setSchedule(sclNew.getSchedule());
        }
        // Set locTargetDevice from New Scl if it exists
        if (sclNew.getLocTargetDevice() != null) {
            scl.setLocTargetDevice(sclNew.getLocTargetDevice());
        }
        // Set mgmtProtocolType from New Scl if it exists
        scl.setMgmtProtocolType(sclNew.getMgmtProtocolType());
        // Set integrityValResults from New Scl if it exists
        if (sclNew.getIntegrityValResults() != null) {
            scl.setIntegrityValResults(sclNew.getIntegrityValResults());
        }
        // Set LastModifiedTime
        scl.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));

        // Notify the subscribers
        Notifier.notify(StatusCode.STATUS_OK, scl);

        // Store scl
        DAOFactory.getSclDAO().update(scl);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, scl);
    }

    /**
     * Deletes {@link Scl} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doDelete (RequestIndication requestIndication) {

        ResponseConfirm errorResponse = new ResponseConfirm();
        Scl scl = DAOFactory.getSclDAO().find(requestIndication.getTargetID());

        // Check Resource Existence
        if (scl == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(scl.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_DELETE);
        if (errorResponse != null) {
            return errorResponse;
        }

        // Notify the subscribers
        Notifier.notify(StatusCode.STATUS_DELETED, scl);
        //Delete
        DAOFactory.getSclDAO().delete(scl);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK);

    }

    /**
     * Executes {@link Scl} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doExecute (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not implemented")) ;
    }

}
