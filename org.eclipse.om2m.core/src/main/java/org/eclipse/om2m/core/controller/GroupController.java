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

import org.eclipse.om2m.commons.resource.AnnounceTo;
import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.Group;
import org.eclipse.om2m.commons.resource.Groups;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.commons.utils.DateConverter;
import org.eclipse.om2m.commons.utils.XmlMapper;
import org.eclipse.om2m.core.announcer.Announcer;
import org.eclipse.om2m.core.constants.Constants;
import org.eclipse.om2m.core.dao.DAOFactory;
import org.eclipse.om2m.core.notifier.Notifier;

/**
 * Implements Create, Retrieve, Update, Delete and Execute methods to handle
 * generic REST request for {@link Group} resource.
 *
 * @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         </ul>
 */

public class GroupController extends Controller {

    /**
     * Creates {@link Group} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doCreate (RequestIndication requestIndication) {

        // membersContentReference: (createReq NP) (response M)
        // subscriptionsReference:  (createReq NP) (response M)
        // expirationTime:          (createReq O)  (response M*)
        // accessRightID:           (createReq NP) (response O)
        // searchStrings:           (createReq O)  (response M)
        // creationTime:            (createReq NP) (response M)
        // lastModifiedTime:        (createReq NP) (response M)
        // announceTo:              (createReq O)  (response M*)
        // memberType:              (createReq M)  (response M)
        // currentNrOfMembers:      (createReq NP) (response M)
        // maxNrOfMembers:          (createReq O)  (response M)
        // members:                 (createReq O)  (response M)
        // id:                      (createReq O)  (response M*)

        ResponseConfirm errorResponse = new ResponseConfirm();
        Groups groups = DAOFactory.getGroupsDAO().lazyFind(requestIndication.getTargetID());

        // Check Parent Existence
        if (groups == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(groups.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_CREATE);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Check Resource Representation
        if (requestIndication.getRepresentation() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Resource Representation is EMPTY")) ;
        }
        // Check XML Validity
        errorResponse = checkMessageSyntax(requestIndication.getRepresentation(),"group.xsd");
        if (errorResponse != null) {
            return errorResponse;
        }
        // Checks on attributes
        Group group = (Group) XmlMapper.getInstance().xmlToObject(requestIndication.getRepresentation());
        // Check the Id uniqueness
        if (group.getId() != null && DAOFactory.getGroupDAO().find(requestIndication.getTargetID()+"/"+group.getId()) != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_CONFLICT,"GroupId Conflit")) ;
        }
        if (group.getId() == null || group.getId().isEmpty()) {
            group.setId(generateId("GRP_",""));
        }
        // memberType is Mandatory
        if (group.getMemberType() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST," MemberType is Mandatory")) ;
        }
        // Check ExpirationTime
        if (group.getExpirationTime() != null && !checkExpirationTime(group.getExpirationTime())) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Expiration Time CREATE is Out of Date")) ;
        }
        // CurrentNrOfMembers Reference Must be NP
        if (group.getCurrentNrOfMembers() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST," CurrentNrOfMembers Reference is Not Permitted")) ;
        }
        // MembersContent Reference Must be NP
        if (group.getMembersContentReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST," MembersContent Reference is Not Permitted")) ;
        }
        //SubscriptionsReference Must be NP
        if (group.getSubscriptionsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"SubscriptionsReference is Not Permitted")) ;
        }
        // CreationTime Must be NP
        if (group.getCreationTime() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Creation Time is Not Permitted")) ;
        }
        // LastModifiedTime Must be NP
        if (group.getLastModifiedTime() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Last Modified Time is Not Permitted")) ;
        }
        // AccessRightID Must be NP
        if (group.getAccessRightID() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"AccessRightID is Not Permitted")) ;
        }
        // Verify currentNrOfInstance > MaxNrOfMembers
        if (group.getMaxNrOfMembers() != null && group.getMembers() != null) {
            if (group.getMaxNrOfMembers()>=0 && group.getMembers().getReference().size() > group.getMaxNrOfMembers()) {
                return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"CurrentNrOfMembers is greater than MaxNrOfMembers")) ;
            }
        }
        // Storage
        // Set URI
        group.setUri(requestIndication.getTargetID()+ "/" +group.getId());

        // Set MaxNrOfMembers
        if(group.getMaxNrOfMembers() == null) {
            group.setMaxNrOfMembers((long)-1);
        }
        // Set currentNrOfMembers
        if(group.getMembers() == null || group.getMembers().getReference().isEmpty()) {
            group.setCurrentNrOfMembers((long)0);
        } else {
            group.setCurrentNrOfMembers((long)group.getMembers().getReference().size());
        }
        // Set Expiration Time if it's null
        if (group.getExpirationTime() == null) {
            //Expiration default value
            group.setExpirationTime(getNewExpirationTime(Constants.EXPIRATION_TIME));
        }
        // Set AccessRightID from the Parent if it's null or nonexistent
            group.setAccessRightID(groups.getAccessRightID());
        // Set searchString if it's null
        if (group.getSearchStrings() == null) {
            group.setSearchStrings(generateSearchStrings(group.getClass().getSimpleName(), group.getId()));
        }
        // Set announceTo if it is null
        if (group.getAnnounceTo() == null) {
            AnnounceTo announceTo = new AnnounceTo();
            announceTo.setActivated(false);
            announceTo.setGlobal(false);
            group.setAnnounceTo(announceTo);
        }
        // Set references
        group.setMembersContentReference(group.getUri()+"/membersContent");
        group.setSubscriptionsReference(group.getUri()+"/subscriptions");
        // Set CreationTime
        group.setCreationTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        // Set LastModifiedTime
        group.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());

        // Announcement
        if (group.getAnnounceTo().isActivated()) {
            group.setAnnounceTo(new Announcer().announce(group.getAnnounceTo(), group.getUri(), group.getSearchStrings(), requestIndication.getRequestingEntity()));
        }

        // Notification
        Notifier.notify(StatusCode.STATUS_CREATED, group);

        // Store group
        DAOFactory.getGroupDAO().create(group);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_CREATED, group);

    }

    /**
     * Retrieves {@link Group} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doRetrieve (RequestIndication requestIndication) {

        // membersContentReference: (response M)
        // subscriptionsReference:  (response M)
        // expirationTime:          (response M*)
        // accessRightID:           (response O)
        // searchStrings:           (response M)
        // creationTime:            (response M)
        // lastModifiedTime:        (response M)
        // announceTo:              (response M*)
        // memberType:              (response M)
        // currentNrOfMembers:      (response M)
        // maxNrOfMembers:          (response M)
        // members:                 (response M)
        // id:                      (response M*)

        ResponseConfirm errorResponse = new ResponseConfirm();
        Group group = DAOFactory.getGroupDAO().find(requestIndication.getTargetID());

        // Check if the resource exists in DataBase or Not
        if (group == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist in DataBase")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(group.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_READ);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, group);
    }

    /**
     * Updates {@link Group} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doUpdate (RequestIndication requestIndication) {

        // membersContentReference: (updateReq NP) (response M)
        // subscriptionsReference:  (updateReq NP) (response M)
        // expirationTime:          (updateReq O)  (response M*)
        // accessRightID:           (updateReq NP) (response O)
        // searchStrings:           (updateReq O)  (response M)
        // creationTime:            (updateReq NP) (response M)
        // lastModifiedTime:        (updateReq NP) (response M)
        // announceTo:              (updateReq O)  (response M*)
        // memberType:              (updateReq NP) (response M)
        // currentNrOfMembers:      (updateReq NP) (response M)
        // maxNrOfMembers:          (updateReq O)  (response M)
        // members:                 (updateReq O)  (response M)
        // id:                      (updateReq NP) (response M*)

        ResponseConfirm errorResponse = new ResponseConfirm();
        Group group = DAOFactory.getGroupDAO().find(requestIndication.getTargetID());

        // Check Existence
        if (group == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist in DataBase")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(group.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_WRITE);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Check Resource Representation
        if (requestIndication.getRepresentation() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Resource Representation is EMPTY")) ;
        }
        // Check XML Validity
        errorResponse = checkMessageSyntax(requestIndication.getRepresentation(),"group.xsd");
        if (errorResponse != null) {
            return errorResponse;
        }
        // Checks on attributes
        Group groupNew = (Group) XmlMapper.getInstance().xmlToObject(requestIndication.getRepresentation());

        //The Update of the Id is NP
        if (groupNew.getId() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"GroupId UPDATE is Not Permitted")) ;
        }
        // Check ExpirationTime
        if (groupNew.getExpirationTime() != null && !checkExpirationTime(groupNew.getExpirationTime())) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Expiration Time UPDATE is Out of Date")) ;
        }
        // AccessRightID Must be NP
        if (groupNew.getAccessRightID() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"AccessRightID UPDATE is Not Permitted")) ;
        }
        // MembersContent Reference Must be NP
        if (groupNew.getMembersContentReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"MembersContent Reference UPDATE is Not Permitted")) ;
        }
        // SubscriptionsReference Must be NP
        if (groupNew.getSubscriptionsReference() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"SubscriptionsReference UPDATE is Not Permitted")) ;
        }
        // CreationTime Must be NP
        if (groupNew.getCreationTime() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Creation Time UPDATE is Not Permitted")) ;
        }
        // LastModifiedTime Must be NP
        if (groupNew.getLastModifiedTime() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Last Modified Time UPDATE is Not Permitted")) ;
        }
        // MemberType Must be NP
        if (groupNew.getMemberType() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"MemberType UPDATE is Not Permitted")) ;
        }
        // currentNrOfMembers Must be NP
        if (groupNew.getCurrentNrOfMembers() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"currentNrOfMembers UPDATE is Not Permitted")) ;
        }
        // Storage
        // Set currentNrOfMembers
        if(groupNew.getMembers() != null && !groupNew.getMembers().getReference().isEmpty()) {
            group.setMembers(groupNew.getMembers());
        }
        // Set MaxNrOfMembers if it is not null
        if (groupNew.getMaxNrOfMembers() != null) {
            group.setMaxNrOfMembers(groupNew.getMaxNrOfMembers());
        }

        // Verify currentNrOfInstance > MaxNrOfMembers
        if (group.getMaxNrOfMembers() != null && group.getMembers() != null) {
            if (group.getMaxNrOfMembers()>=0 && group.getMembers().getReference().size() > group.getMaxNrOfMembers()) {
                return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"CurrentNrOfMembers is greater than MaxNrOfMembers")) ;
            }
        }
        // Set Expiration Time (could be null)
        if (groupNew.getExpirationTime() != null) {
            group.setExpirationTime(groupNew.getExpirationTime());
        }
        // Set accessRightID if it exists
        if (DAOFactory.getAccessRightDAO().find(groupNew.getAccessRightID()) != null) {
            group.setAccessRightID(groupNew.getAccessRightID());
        }
        // Set SearchStrings
        if (groupNew.getSearchStrings() != null) {
            group.setSearchStrings(groupNew.getSearchStrings());
        }
        // Set AnnounceTo
        if (groupNew.getAnnounceTo() != null) {
            group.setAnnounceTo(groupNew.getAnnounceTo());
        }
        //Set LastModifiedTime
        group.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());

        // Notify the subscribers
        Notifier.notify(StatusCode.STATUS_OK, group);

        // Store updates
        DAOFactory.getGroupDAO().update(group);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, group);

    }

    /**
     * Deletes {@link Group} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doDelete (RequestIndication requestIndication) {

        ResponseConfirm errorResponse = new ResponseConfirm();
        Group group = DAOFactory.getGroupDAO().find(requestIndication.getTargetID());

        // Check Resource Existence
        if (group == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(group.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_DELETE);
        if (errorResponse != null) {
            return errorResponse;
        }

        // De-announcement
        if (group.getAnnounceTo().isActivated()) {
            new Announcer().deAnnounce(group.getAnnounceTo(), group.getUri(), requestIndication.getRequestingEntity());
        }

        // Notify the subscribers
        Notifier.notify(StatusCode.STATUS_DELETED, group);

        // Delete
        DAOFactory.getGroupDAO().delete(group);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK);
    }

    /**
     * Executes {@link Group} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doExecute (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }
}
