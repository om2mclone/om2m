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
package org.eclipse.om2m.core.dao;

import org.eclipse.om2m.commons.resource.ReferenceToNamedResource;
import org.eclipse.om2m.commons.resource.Subscription;
import org.eclipse.om2m.commons.resource.Subscriptions;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link Subscriptions} collection resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class SubscriptionsDAO extends DAO<Subscriptions> {

    /**
     * Creates an {@link Subscriptions} collection resource in the DataBase.
     * @param resource - The {@link Subscriptions} collection resource to create
     */
    public void create(Subscriptions resource) {
        // Store the created resource
        DB.store(resource);
    }

    /**
     * Retrieves the {@link Subscriptions} collection resource based on its uri with sub-resources references
     * @param uri - uri of the {@link Subscriptions} collection resource
     * @return The requested {@link Subscriptions} collection resource otherwise null
     */
    public Subscriptions find(String uri) {
        Subscriptions subscriptions = lazyFind(uri);

        if(subscriptions != null) {
            // Find subscription sub-resources and add their references
            subscriptions.getSubscriptionCollection().getNamedReference().clear();
            Query query = DB.query();
            query.constrain(Subscription.class);
            query.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<Subscription> result = query.execute();

            for (int i = 0; i < result.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(result.get(i).getId());
                reference.setValue(result.get(i).getUri());
                subscriptions.getSubscriptionCollection().getNamedReference().add(reference);
            }
        }
        return subscriptions;
    }

    /**
     * Retrieves the {@link Subscriptions} collection resource based on its uri without sub-resources references.
     * @param uri - uri of the {@link Subscriptions} collection resource
     * @return The requested {@link Subscriptions} collection resource otherwise null
     */
    public Subscriptions lazyFind(String uri) {
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(Subscriptions.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<Subscriptions> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if (!result.isEmpty()) {
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Updates an existing {@link Subscriptions} collection resource in the DataBase
     * @param resource - The {@link Subscriptions} the updated resource
     */
    public void update(Subscriptions resource) {
        // Store the updated resource
        DB.store(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link Subscriptions} collection resource from the DataBase and validates the transaction
     * @Param the {@link Subscriptions} collection resource to delete
     */
    public void delete(Subscriptions resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link Subscriptions} collection resource from the DataBase without validating the transaction
     * @Param the {@link Subscriptions} collection resource to delete
     */
    public void lazyDelete(Subscriptions resource){
        // Delete subscription sub-resources
        Query query = DB.query();
        query.constrain(Subscription.class);
        query.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<Subscription> result = query.execute();

        for (int i = 0; i < result.size(); i++) {
            DAOFactory.getSubscriptionDAO().lazyDelete(result.get(i));
        }
        // Delete the resource
        DB.delete(resource);
    }
}
