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
package org.eclipse.om2m.core.notifier;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.resource.Application;
import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.commons.resource.Notify;
import org.eclipse.om2m.commons.resource.Resource;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.resource.Subscription;
import org.eclipse.om2m.commons.resource.Subscriptions;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.core.comm.RestClient;
import org.eclipse.om2m.core.constants.Constants;
import org.eclipse.om2m.core.dao.DAOFactory;
import org.eclipse.om2m.core.redirector.Redirector;
import org.eclipse.om2m.core.router.Router;

/**
 * Notifies subscribers when a change occurs on a resource according to their subscriptions.
 * @author <ul>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.fr ></li>
 *         <li> Marouane El kiasse < melkiasse@laas.fr > < kiasmarouane@gmail.com ></li>
 *         </ul>
 */
public class Notifier {
    /** Logger */
    private static Log LOGGER = LogFactory.getLog(Notifier.class);

    /**
     * Finds all resource subscribers and notifies them.
     * @param statusCode - Notification status code
     * @param resource - Notification resource
     */
    public static void notify(StatusCode statusCode, Resource resource) {

        // Get the subscriptions uri
        String subscriptionsUri = resource.getUri().substring(0, resource.getUri().lastIndexOf("/"))+"/subscriptions";
        // Get the subscriptions collection from data base
        Subscriptions subscriptions = DAOFactory.getSubscriptionsDAO().find(subscriptionsUri);

        if(subscriptions != null) {
            ArrayList<Subscription> subscriptionList = new ArrayList<Subscription>();
            // Finds each subscription resource and add it to the subscriptionList.
            for(int i=0; i<subscriptions.getSubscriptionCollection().getNamedReference().size() ; i++){
                subscriptionList.add(DAOFactory.getSubscriptionDAO().find(subscriptions.getSubscriptionCollection().getNamedReference().get(i).getValue()));
            }

            Notify notify;
            Subscription subscription;
            // Create Notify object and sends it to subscribers
            for(int i=0 ; i<subscriptionList.size();i++) {
                notify = new Notify();
                subscription = new Subscription();
                subscription =  subscriptionList.get(i);
                notify.setStatusCode(statusCode);
                notify.getRepresentation().setContentType("application/xml");

                // Check the FilterCriteria
                if(subscription.getFilterCriteria() != null) {
                    // Check the FilterCriteria "IfMach" attribute"
                    if(!subscription.getFilterCriteria().getIfMatch().isEmpty() && subscription.getFilterCriteria().getIfMatch().get(0) != null){
                        // In case of a ContentInstance resource, If "IfMatch" is equals to "content" then notify with the content
                        // instead of sending the full resource representation.
                        if("contentInstance".equalsIgnoreCase(resource.getClass().getSimpleName()) && "content".equalsIgnoreCase(subscription.getFilterCriteria().getIfMatch().get(0))){
                            ContentInstance contentInstance = (ContentInstance) resource;
                            notify.getRepresentation().setValue(contentInstance.getContent().getValue());
                        }
                        // Possibility to add other criteria
                    }
                }else {
                    // Notify if no "FilterCriteria" specified.
                    notify.getRepresentation().setValue(resource);
                }
                notify.setSubscriptionReference(subscription.getUri());
                
                final String contact =  subscription.getContact();

                // Create a RequestIndication with the notify as representation
                final RequestIndication requestIndication = new RequestIndication();
                requestIndication.setMethod(Constants.METHOD_CREATE);
                requestIndication.setRequestingEntity(Constants.ADMIN_REQUESTING_ENTITY);
                requestIndication.setRepresentation(notify);
                

                // Send notification on a new Thread
                new Thread() {
                    public void run() {
                        LOGGER.info("Notification Request:\n"+requestIndication);
                        ResponseConfirm responseConfirm = Notifier.notify(requestIndication,contact);
                        LOGGER.info("Notification Response:\n"+responseConfirm);
                    }
                }.start();
            }
        }
    }

    public static ResponseConfirm notify(RequestIndication requestIndication, String contact){
    	// Check whether the subscription contact is protocol-dependent or not.
    	if(contact.matches(".*://.*")){ 
    		// Contact = protocol-dependent -> direct notification using the rest client.
        	requestIndication.setBase(contact);
        	requestIndication.setTargetID("");
            return new RestClient().sendRequest(requestIndication);
        }else{
    		// Contact = protocol-independent -> Check whether the targeted SCL is local or remote.
            String sclId = contact.split("/")[0];
    		requestIndication.setTargetID(contact);
            if(Constants.SCL_ID.equals(sclId)){
        		// scl = local -> perform request on the local scl.
            	return new Router().doRequest(requestIndication);
            }else{
            	// scl = remote -> retarget request to the remote scl.
                return new Redirector().retarget(requestIndication);
            }
        }
    }
}
