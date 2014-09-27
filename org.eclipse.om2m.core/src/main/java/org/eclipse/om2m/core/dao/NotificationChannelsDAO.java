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

import org.eclipse.om2m.commons.resource.NotificationChannel;
import org.eclipse.om2m.commons.resource.NotificationChannels;
import org.eclipse.om2m.commons.resource.ReferenceToNamedResource;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link NotificationChannels} collection resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class NotificationChannelsDAO extends DAO<NotificationChannels> {

    /**
     * Creates an {@link NotificationChannels} collection resource in the DataBase.
     * @param resource - The {@link NotificationChannels} collection resource to create
     */
    public void create(NotificationChannels resource) {
        // Store the created resource
        DB.store(resource);
    }

    /**
     * Retrieves the {@link NotificationChannels} collection resource based on its uri with sub-resources references
     * @param uri - uri of the {@link NotificationChannels} collection resource
     * @return The requested {@link NotificationChannels} collection resource otherwise null
     */
    public NotificationChannels find(String uri) {
        NotificationChannels notificationChannels = lazyFind(uri);

        if (notificationChannels != null){
        	ObjectContainer session = DB.ext().openSession();

            // Find NotificationChannel sub-resources and add their references
            notificationChannels.getNotificationChannelCollection().getNamedReference().clear();

            Query query = session.query();
            query.constrain(NotificationChannel.class);
            query.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<NotificationChannel> result = query.execute();

            for (int i = 0; i < result.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(result.get(i).getId());
                reference.setValue(result.get(i).getUri());
                notificationChannels.getNotificationChannelCollection().getNamedReference().add(reference);
            }
        }
        return notificationChannels;
    }

    /**
     * Retrieves the {@link NotificationChannels} collection resource based on its uri without sub-resources references.
     * @param uri - uri of the {@link NotificationChannels} collection resource
     * @return The requested {@link NotificationChannels} collection resource otherwise null
     */
    public NotificationChannels lazyFind(String uri) {
    	ObjectContainer session = DB.ext().openSession();

        // Create the query based on the uri constraint
        Query query = session.query();
        query.constrain(NotificationChannels.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<NotificationChannels> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if(!result.isEmpty()){
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Updates an existing {@link NotificationChannels} collection resource in the DataBase
     * @param resource - The {@link NotificationChannels} the updated resource
     */
    public void update(NotificationChannels resource) {
        // Store the updated resource
        DB.store(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link NotificationChannels} collection resource from the DataBase and validates the transaction
     * @Param the {@link NotificationChannels} collection resource to delete
     */
    public void delete(NotificationChannels resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link NotificationChannels} collection resource from the DataBase without validating the transaction
     * @Param the {@link NotificationChannels} collection resource to delete
     */
    public void lazyDelete(NotificationChannels resource) {
        // Delete notificationChannel sub-resources
        Query query = DB.query();
        query.constrain(NotificationChannel.class);
        query.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<NotificationChannel> result = query.execute();

        for (int i = 0; i < result.size(); i++) {
            DAOFactory.getNotificationChannelDAO().lazyDelete(result.get(i));
        }
        // Delete the resource
        DB.delete(resource);
    }
}
