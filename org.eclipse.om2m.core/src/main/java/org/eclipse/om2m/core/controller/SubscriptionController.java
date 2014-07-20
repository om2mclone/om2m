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

import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.resource.Subscription;
import org.eclipse.om2m.commons.resource.SubscriptionType;
import org.eclipse.om2m.commons.resource.Subscriptions;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.commons.utils.DateConverter;
import org.eclipse.om2m.commons.utils.XmlMapper;
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

public class SubscriptionController extends Controller {

    /**
     * Creates {@link Subscription} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */

    public ResponseConfirm doCreate (RequestIndication requestIndication) {

        // expirationTime:                  (createReq O)  (response M*)
        // minimalTimeBetweenNotifications: (createReq O)  (response O)
        // delayTolerance:                  (createReq O)  (response O)
        // creationTime:                    (createReq NP) (response M)
        // lastModifiedTime:                (createReq NP) (response M)
        // filterCriteria:                  (createReq O)  (response O)
        // subscritpionType:                (createReq NP) (response M)
        // contact:                         (createReq M)  (response M)
        // Id:                              (createReq O)  (response M*)


        ResponseConfirm errorResponse = new ResponseConfirm();
        Subscriptions subscriptions = DAOFactory.getSubscriptionsDAO().lazyFind(requestIndication.getTargetID());

        // Check Parent Existence
        if (subscriptions == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(getAccessRightID(subscriptions.getUri()), requestIndication.getRequestingEntity(), Constants.AR_CREATE);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Check Resource Representation
        if (requestIndication.getRepresentation() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Resource Representation is EMPTY")) ;
        }
        // Check XML Validity
        errorResponse = checkMessageSyntax(requestIndication.getRepresentation(),"subscription.xsd");
        if (errorResponse != null) {
            return errorResponse;
        }
        // Checks on attributes
        Subscription subscription = (Subscription) XmlMapper.getInstance().xmlToObject(requestIndication.getRepresentation());
        // Check ExpirationTime
        if (subscription.getExpirationTime() != null && !checkExpirationTime(subscription.getExpirationTime())) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Expiration Time CREATE is Out of Date")) ;
        }
        // Contact Attribute is mandatory
        if (subscription.getContact() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Link attribute is mandatory")) ;
        }
        // CreationTime Must be NP
        if (subscription.getCreationTime() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Creation Time CREATE is Not Permitted")) ;
        }
        // LastModifiedTime Must be NP
        if (subscription.getLastModifiedTime() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Last Modified Time CREATE is Not Permitted")) ;
        }
        // subscriptionType Must be NP
        if (subscription.getSubscriptionType() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"SubscriptionType CREATE is Not Permitted")) ;
        }
        // Check ContactURI conflict
        if (checkContactURIExistence(requestIndication.getTargetID(), subscription.getContact())) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_CONFLICT,"Subscription ContactURI Conflict")) ;
        }
        // Storage
        // Check the Id uniqueness
        if (subscription.getId() != null && DAOFactory.getSubscriptionDAO().find(requestIndication.getTargetID()+"/"+subscription.getId()) != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_CONFLICT,"SubscriptionId Conflit")) ;
        }
        if (subscription.getId() == null || subscription.getId().isEmpty()) {
            subscription.setId(generateId("SUB_",""));
        }
        // Set URI
        subscription.setUri(requestIndication.getTargetID()+ "/" +subscription.getId());
        // Set Expiration Time if it's null
        if (subscription.getExpirationTime() == null) {
            //infinity expiration Time
            subscription.setExpirationTime(getNewExpirationTime(Constants.EXPIRATION_TIME));
        }
        // Set the subscription to asynchronous
        subscription.setSubscriptionType(SubscriptionType.ASYNCHRONOUS);

        //Set CreationTime
        subscription.setCreationTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        subscription.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());

        //Store subscription
        DAOFactory.getSubscriptionDAO().create(subscription);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_CREATED, subscription);
    }

    /**
     * Retrieves {@link Subscription} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doRetrieve (RequestIndication requestIndication) {

        // expirationTime:                  (response M*)
        // minimalTimeBetweenNotifications: (response O)
        // delayTolerance:                  (response O)
        // creationTime:                    (response M)
        // lastModifiedTime:                (response M)
        // filterCriteria:                  (response O)
        // subscritpionType:                (response M)
        // contact:                         (response M)
        // Id:                              (response M*)

        ResponseConfirm errorResponse = new ResponseConfirm();
        Subscription subscription = DAOFactory.getSubscriptionDAO().find(requestIndication.getTargetID());

        // Check if the resource exists in DataBase or Not
        if (subscription == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist in DataBase")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(getAccessRightID(subscription.getUri()), requestIndication.getRequestingEntity(), Constants.AR_READ);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, subscription);

    }

    /**
     * Updates {@link Subscription} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doUpdate (RequestIndication requestIndication) {

        // expirationTime:                  (updateReq O)  (response M*)
        // minimalTimeBetweenNotifications: (updateReq O)  (response O)
        // delayTolerance:                  (updateReq O)  (response O)
        // creationTime:                    (updateReq NP) (response M)
        // lastModifiedTime:                (updateReq NP) (response M)
        // filterCriteria:                  (updateReq NP) (response O)
        // subscritpionType:                (updateReq NP) (response M)
        // contact:                         (updateReq NP) (response M)
        // Id:                              (updateReq NP) (response M*)

        ResponseConfirm errorResponse = new ResponseConfirm();
        Subscription subscription = DAOFactory.getSubscriptionDAO().find(requestIndication.getTargetID());

        // Check Existence
        if (subscription == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist in DataBase")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(getAccessRightID(subscription.getUri()), requestIndication.getRequestingEntity(), Constants.AR_WRITE);
        if (errorResponse != null) {
            return errorResponse;
        }
        // Check Resource Representation
        if (requestIndication.getRepresentation() == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Resource Representation is EMPTY")) ;
        }
        // Check XML Validity
        errorResponse= checkMessageSyntax(requestIndication.getRepresentation(),"subscription.xsd");
        if (errorResponse != null) {
            return errorResponse;
        }
        // Check attributes
        Subscription subscriptionNew = (Subscription) XmlMapper.getInstance().xmlToObject(requestIndication.getRepresentation());

        // SubscriptionId UPDATE is NP
        if (subscriptionNew.getId() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"subscriptionId UPDATE is Not Permitted")) ;
        }
        // Contact Update is NP
        if (subscriptionNew.getContact() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Contact attribute UPDATE is Not Permitted")) ;
        }
        // SubscriptionType UPDATE is NP
        if (subscriptionNew.getSubscriptionType() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"subscriptionType UPDATE is Not Permitted")) ;
        }
        // FilterCriteria UPDATE is NP
        if (subscriptionNew.getFilterCriteria() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"filterCriteria UPDATE is Not Permitted")) ;
        }
        // CreattionTime UPDATE is NP
        if (subscriptionNew.getCreationTime() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"CreationTime UPDATE is Not Permitted")) ;
        }
        // lastModifiedTime UPDATE is NP
        if (subscriptionNew.getLastModifiedTime() != null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"LatModifiedTime UPDATE is Not Permitted")) ;
        }
        // Check ExpirationTime
        if (subscriptionNew.getExpirationTime() != null && !checkExpirationTime(subscriptionNew.getExpirationTime())) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Expiration Time Update is Out of Date")) ;
        }

        // Storage
        // Set ExpirationTime
        if (subscriptionNew.getExpirationTime() != null) {
            subscription.setExpirationTime(subscriptionNew.getExpirationTime());
        }
        // Set MinimalTimeBetweenNotifications
        if (subscriptionNew.getMinimalTimeBetweenNotifications() != null) {
            subscription.setMinimalTimeBetweenNotifications(subscriptionNew.getMinimalTimeBetweenNotifications());
        }
        // Set DelayTolerance
        if (subscriptionNew.getDelayTolerance() != null) {
            subscription.setDelayTolerance(subscriptionNew.getDelayTolerance());
        }
        // Set LastModifiedTime
        subscription.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());

        // Store container
        DAOFactory.getSubscriptionDAO().update(subscription);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, subscription);

    }

    /**
     * Deletes {@link Subscription} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doDelete (RequestIndication requestIndication) {

        Subscription subscription = DAOFactory.getSubscriptionDAO().find(requestIndication.getTargetID());
        ResponseConfirm errorResponse = new ResponseConfirm();

        // Check Resource Existence
        if (subscription == null) {
            return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_FOUND,requestIndication.getTargetID()+" does not exist")) ;
        }
        // Check AccessRight
        errorResponse = checkAccessRight(getAccessRightID(subscription.getUri()), requestIndication.getRequestingEntity(), Constants.AR_DELETE);
        if (errorResponse != null) {
            return errorResponse;
        }
        //Delete
        DAOFactory.getSubscriptionDAO().delete(subscription);
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK);

    }

    /**
     * Executes {@link Subscription} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doExecute (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method is not yet Implemented")) ;
    }

    /**
     * Checks Contact URI existence insure the uniqueness of the contact URI between the subscription resources in the same level
     * @param targetId
     * @param contact
     * @return true if contactURI exists otherwise false
     */
    public boolean checkContactURIExistence (String targetId, String contact) {
        Subscriptions subscriptions = DAOFactory.getSubscriptionsDAO().find(targetId);

        for (int i=0; i<subscriptions.getSubscriptionCollection().getNamedReference().size(); i++) {
            Subscription subscription = DAOFactory.getSubscriptionDAO().find(targetId+"/"+subscriptions.getSubscriptionCollection().getNamedReference().get(i).getId());

            if (subscription.getContact().equalsIgnoreCase(contact)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the subscriptions collection parent AccessRightID
     * @param subscriptionsUri
     * @return accessRightID of the subscriptions collection Parent
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
