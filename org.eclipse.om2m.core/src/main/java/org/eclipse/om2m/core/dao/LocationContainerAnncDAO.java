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
import org.eclipse.om2m.commons.resource.LocationContainerAnnc;
import org.eclipse.om2m.commons.utils.DateConverter;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link LocationContainerAnnc} resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class LocationContainerAnncDAO extends DAO<LocationContainerAnnc> {

    /**
     * Creates an {@link LocationContainerAnnc} resource in the DataBase and validates the transaction
     * @param resource - The {@link LocationContainerAnnc} resource to create
     */
    public void create(LocationContainerAnnc resource) {
        // Store the created resource
        DB.store(resource);
        // Update the lastModifiedTime attribute of the parent
        Containers containers = DAOFactory.getContainersDAO().lazyFind(resource.getUri().split("/"+resource.getId())[0]);
        containers.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DB.store(containers.getLastModifiedTime());
        // Validate the current transaction
        commit();
    }

    /**
     * Retrieves the {@link LocationContainerAnnc} resource from the Database based on its uri
     * @param uri - uri of the {@link LocationContainerAnnc} resource to retrieve
     * @return The requested {@link LocationContainerAnnc} resource otherwise null
     */
    public LocationContainerAnnc find(String uri) {
        // Create the query based on the uri constraint
        Query query=DB.query();
        query.constrain(LocationContainerAnnc.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<LocationContainerAnnc> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if(!result.isEmpty()){
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Retrieves the {@link LocationContainerAnnc} resource from the Database based on the uri
     * @param uri - uri of the {@link LocationContainerAnnc} resource
     * @return The requested {@link LocationContainerAnnc} resource otherwise null
     */
    public LocationContainerAnnc lazyFind(String uri) {
        return find(uri);
    }

    /**
     * Updates an existing {@link LocationContainerAnnc} resource in the DataBase
     * @param resource - The {@link LocationContainerAnnc} the updated resource
     */
    public void update(LocationContainerAnnc resource) {
        // Store the updated resource
        DB.store(resource);
        // Update the lastModifiedTime attribute of the parent
        Containers containers =  DAOFactory.getContainersDAO().lazyFind(resource.getUri().split("/"+resource.getId())[0]);
        containers.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DB.store(containers.getLastModifiedTime());
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link LocationContainerAnnc} resource from the DataBase and validates the transaction
     * @Param the {@link LocationContainerAnnc} resource to delete
     */
    public void delete(LocationContainerAnnc resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link LocationContainerAnnc} resource from the DataBase without validating the transaction
     * @param resource - The {@link LocationContainerAnnc} resource to delete
     */
    public void lazyDelete(LocationContainerAnnc resource) {
        // Delete the resource
        DB.delete(resource);
        // Update the lastModifiedTime attribute of the parent
        Containers containers =  DAOFactory.getContainersDAO().lazyFind(resource.getUri().split("/"+resource.getId())[0]);
        containers.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DAOFactory.getContainersDAO().update(containers);
    }
}
