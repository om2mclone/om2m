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

import org.eclipse.om2m.commons.resource.AttachedDevice;
import org.eclipse.om2m.commons.resource.AttachedDevices;
import org.eclipse.om2m.commons.resource.ReferenceToNamedResource;
import org.eclipse.om2m.commons.resource.Subscriptions;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link AttachedDevices} collection resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class AttachedDevicesDAO extends DAO<AttachedDevices> {

    /**
     * Creates an {@link AttachedDevices} collection resource in the DataBase.
     * @param resource - The {@link AttachedDevices} collection resource to create
     */
    public void create(AttachedDevices resource) {
        // Add subscription Reference
        resource.setSubscriptionsReference(resource.getUri()+"/subscriptions");
        // Store the created resource
        DB.store(resource);

        // Subscriptions
        Subscriptions subscriptions = new Subscriptions();
        subscriptions.setUri(resource.getSubscriptionsReference());
        DAOFactory.getSubscriptionsDAO().create(subscriptions);
    }

    /**
     * Retrieves the {@link AttachedDevices} collection resource based on its uri with sub-resources references
     * @param uri - uri of the {@link AttachedDevices} collection resource
     * @return The requested {@link AttachedDevices} collection resource otherwise null
     */
    public AttachedDevices find(String uri) {
        AttachedDevices attachedDevices = lazyFind(uri);

        if(attachedDevices != null){
        	ObjectContainer session = DB.ext().openSession();

            // Find AttachedDevice sub-resources and add their references
            Query query = session.query();
            query.constrain(AttachedDevice.class);
            query.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<AttachedDevice> result = query.execute();
            attachedDevices.getAttachedDeviceCollection().getNamedReference().clear();

            for (int i = 0; i < result.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(result.get(i).getId());
                reference.setValue(result.get(i).getUri());
                attachedDevices.getAttachedDeviceCollection().getNamedReference().add(reference);
            }
        }
        return attachedDevices;
    }

    /**
     * Retrieves the {@link AttachedDevices} collection resource based on its uri without sub-resources references.
     * @param uri - uri of the {@link AttachedDevices} collection resource
     * @return The requested {@link AttachedDevices} collection resource otherwise null
     */
    public AttachedDevices lazyFind(String uri) {
        // Create the query based on the uri constraint
    	ObjectContainer session = DB.ext().openSession();

        Query query = session.query();
        query.constrain(AttachedDevices.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<AttachedDevices> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if (!result.isEmpty()) {
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Updates an existing {@link AttachedDevices} collection resource in the DataBase
     * @param resource - The {@link AttachedDevices} the updated resource
     */
    public void update(AttachedDevices resource) {
        // Store the updated resource
        DB.store(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link AttachedDevices} collection resource from the DataBase and validates the transaction
     * @Param the {@link AttachedDevices} collection resource to delete
     */
    public void delete(AttachedDevices resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link AttachedDevices} collection resource from the DataBase without validating the transaction
     * @Param the {@link AttachedDevices} collection resource to delete
     */
    public void lazyDelete(AttachedDevices resource) {
        // Delete subscriptions
        DAOFactory.getSubscriptionsDAO().lazyDelete(DAOFactory.getSubscriptionsDAO().lazyFind(resource.getSubscriptionsReference()));

        // Delete attachedDevice sub-resources
        Query query = DB.query();
        query.constrain(AttachedDevice.class);
        query.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<AttachedDevice> result = query.execute();

        for (int i = 0; i < result.size(); i++) {
            DAOFactory.getAttachedDeviceDAO().lazyDelete(result.get(i));
        }
        // Delete the resource
        DB.delete(resource);
    }
}
