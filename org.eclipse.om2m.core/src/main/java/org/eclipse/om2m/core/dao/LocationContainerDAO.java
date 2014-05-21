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

import java.util.Date;

import org.eclipse.om2m.commons.resource.Containers;
import org.eclipse.om2m.commons.resource.ContentInstances;
import org.eclipse.om2m.commons.resource.LocationContainer;
import org.eclipse.om2m.commons.resource.Subscriptions;
import org.eclipse.om2m.commons.utils.DateConverter;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link LocationContainer} resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class LocationContainerDAO extends DAO<LocationContainer> {

    /**
     * Creates an {@link LocationContainer} resource in the DataBase and validates the transaction
     * @param resource - The {@link LocationContainer} resource to create
     */
    public void create(LocationContainer resource) {
        // Store the created resource
        DB.store(resource);
        // ContentInstances
        ContentInstances contentInstances = new ContentInstances();
        contentInstances.setUri(resource.getUri()+"/contentInstances");
        contentInstances.setCreationTime(resource.getCreationTime());
        contentInstances.setLastModifiedTime(resource.getLastModifiedTime());
        DAOFactory.getContentInstancesDAO().create(contentInstances);
        // Subscriptions
        Subscriptions subscriptions = new Subscriptions();
        subscriptions.setUri(resource.getUri()+"/subscriptions");
        DAOFactory.getSubscriptionsDAO().create(subscriptions);
        // Update the lastModifiedTime attribute of the parent
        Containers containers = DAOFactory.getContainersDAO().lazyFind(resource.getUri().split("/"+resource.getId())[0]);
        containers.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DB.store(containers.getLastModifiedTime());
        // Validate the current transaction
        commit();
    }

    /**
     * Retrieves the {@link LocationContainer} resource from the Database based on its uri
     * @param uri - uri of the {@link LocationContainer} resource to retrieve
     * @return The requested {@link LocationContainer} resource otherwise null
     */
    public LocationContainer find(String uri) {
        // Create the query based on the uri constraint
        Query query=DB.query();
        query.constrain(LocationContainer.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<LocationContainer> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if(!result.isEmpty()){
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Retrieves the {@link LocationContainer} resource from the Database based on the uri
     * @param uri - uri of the {@link LocationContainer} resource
     * @return The requested {@link LocationContainer} resource otherwise null
     */
    public LocationContainer lazyFind(String uri) {
        return find(uri);
    }

    /**
     * Updates an existing {@link LocationContainer} resource in the DataBase
     * @param resource - The {@link LocationContainer} the updated resource
     */
    public void update(LocationContainer resource) {
        // Store the updated resource
        DB.store(resource);
        // Update the lastModifiedTime attribute of the parent
        Containers containers = DAOFactory.getContainersDAO().lazyFind(resource.getUri().split("/"+resource.getId())[0]);
        containers.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DB.store(containers.getLastModifiedTime());
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link LocationContainer} resource from the DataBase and validates the transaction
     * @Param the {@link LocationContainer} resource to delete
     */
    public void delete(LocationContainer resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link LocationContainer} resource from the DataBase without validating the transaction
     * @param resource - The {@link LocationContainer} resource to delete
     */
    public void lazyDelete(LocationContainer resource) {
        //delete subscriptions
        DAOFactory.getSubscriptionsDAO().lazyDelete(DAOFactory.getSubscriptionsDAO().lazyFind(resource.getSubscriptionsReference()));
        //delete contentInstances
        DAOFactory.getContentInstancesDAO().lazyDelete(DAOFactory.getContentInstancesDAO().lazyFind(resource.getContentInstancesReference()));
        // Delete the resource
        DB.delete(resource);
        // Update the lastModifiedTime attribute of the parent
        Containers containers = DAOFactory.getContainersDAO().lazyFind(resource.getUri().split("/"+resource.getId())[0]);
        containers.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DB.store(containers.getLastModifiedTime());
    }
}
