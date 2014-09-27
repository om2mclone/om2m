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

import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.commons.resource.ContentInstances;
import org.eclipse.om2m.commons.resource.Subscriptions;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link ContentInstances} collection resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class ContentInstancesDAO extends DAO<ContentInstances> {

    /**
     * Creates an {@link ContentInstances} collection resource in the DataBase.
     * @param resource - The {@link ContentInstances} collection resource to create
     */
    public void create(ContentInstances resource) {
    	
        //Set subscriptions reference
        resource.setSubscriptionsReference(resource.getUri()+"/subscriptions");
        // Store the created resource
        DB.store(resource);

        // Subscriptions
        Subscriptions subscriptions = new Subscriptions();
        subscriptions.setUri(resource.getSubscriptionsReference());
        DAOFactory.getSubscriptionsDAO().create(subscriptions);
    }

    /**
     * Retrieves the {@link ContentInstances} collection resource based on its uri with sub-resources references
     * @param uri - uri of the {@link ContentInstances} collection resource
     * @return The requested {@link ContentInstances} collection resource otherwise null
     */
    public ContentInstances find(String uri){
        ContentInstances contentInstances = lazyFind(uri);

        if(contentInstances != null){
        	ObjectContainer session = DB.ext().openSession();
            // Find contentInstance sub-resources and add their references
            Query query = session.query();
            query.constrain(ContentInstance.class);
            query.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<ContentInstance> result = query.execute();
            contentInstances.getContentInstanceCollection().getContentInstance().clear();
            for(int i = 0; i < result.size(); i++) {
                contentInstances.getContentInstanceCollection().getContentInstance().add(result.get(i));
            }
        }
        return contentInstances;

        
    }

    /**
     * Retrieves the {@link ContentInstances} collection resource based on its uri without sub-resources references.
     * @param uri - uri of the {@link ContentInstances} collection resource
     * @return The requested {@link ContentInstances} collection resource otherwise null
     */
    public ContentInstances lazyFind(String uri) {

    	ObjectContainer session = DB.ext().openSession();

        // Create the query based on the uri constraint
        Query query = session.query();
        query.constrain(ContentInstances.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<ContentInstances> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if (!result.isEmpty()) {
            return result.get(0);
        }

        // Return null if the resource is not found
        return null;
        
    }

    /**
     * Updates an existing {@link ContentInstances} collection resource in the DataBase
     * @param resource - The {@link ContentInstances} the updated resource
     */
    public void update(ContentInstances resource) {
        // Store the updated resource
        DB.store(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link ContentInstances} collection resource from the DataBase and validates the transaction
     * @Param the {@link ContentInstances} collection resource to delete
     */
    public void delete(ContentInstances resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link ContentInstances} collection resource from the DataBase without validating the transaction
     * @Param the {@link ContentInstances} collection resource to delete
     */
    public void lazyDelete(ContentInstances resource){
        // Delete subscriptions
        DAOFactory.getSubscriptionsDAO().lazyDelete(DAOFactory.getSubscriptionsDAO().lazyFind(resource.getSubscriptionsReference()));
        // Delete contentInstance sub-resources
        Query query = DB.query();
        query.constrain(ContentInstance.class);
        query.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<ContentInstance> resultContentInstance = query.execute();

        for (int i = 0; i < resultContentInstance.size(); i++) {
            DAOFactory.getContentInstanceDAO().lazyDelete(resultContentInstance.get(i));
        }

        // Delete the resource
        DB.delete(resource);
    }
}
