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
package org.eclipse.om2m.core.dao;

import java.util.Date;

import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.commons.resource.ContentInstances;
import org.eclipse.om2m.commons.utils.DateConverter;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link ContentInstance} resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class ContentInstanceDAO extends DAO<ContentInstance> {

    /**
     * Creates an {@link ContentInstance} resource in the DataBase and validates the transaction
     * @param resource - The {@link ContentInstance} resource to create
     */
    public void create(ContentInstance resource) {
    	 // Store the created resource
        DB.store(resource);
        // Update the lastModifiedTime attribute of the parent
        Query query = DB.query();
        query.constrain(ContentInstances.class);
        query.descend("uri").constrain(resource.getUri().split("/"+resource.getId())[0]);
        // Store all the founded resources
        ObjectSet<ContentInstances> result = query.execute();
		// Retrieve the first element corresponding to the researched resource
		// if result is not empty
		ContentInstances contentInstances = result.get(0);

		contentInstances.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
		// Add ContentSize to currentSize of the ContentInstances
		contentInstances.setCurrentByteSize(contentInstances.getCurrentByteSize() + resource.getContentSize());
		// Increment the currentInstances
		contentInstances.setCurrentNrOfInstances(contentInstances.getCurrentNrOfInstances() + 1);
		// Update
		DB.store(contentInstances);

		// Validate the current transaction
		commit();    
    }

    /**
     * Retrieves the {@link ContentInstance} resource from the Database based on its uri
     * @param uri - uri of the {@link ContentInstance} resource to retrieve
     * @return The requested {@link ContentInstance} resource otherwise null
     */
    public ContentInstance find(String uri) {
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(ContentInstance.class);

        if("latest".equals(uri.split("contentInstances/")[1])){
            query.descend("uri").constrain(uri.replaceAll("/latest/*", "")).startsWith(true);
            ObjectSet<ContentInstance> result=query.execute();

            if (!result.isEmpty()) {
                return result.get(result.size()-1);
            }

        }else if ("oldest".equals(uri.split("contentInstances/")[1])){
            query.descend("uri").constrain(uri.replaceAll("/oldest/*", "")).startsWith(true);
            ObjectSet<ContentInstance> result=query.execute();

            if (!result.isEmpty()) {
                return result.get(0);
            }
        }else {
            // Store all the founded resources
            query.descend("uri").constrain(uri);
            ObjectSet<ContentInstance> result = query.execute();
            // Retrieve the first element corresponding to the researched resource if result is not empty
            if (!result.isEmpty()) {
                return result.get(0);
            }
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Retrieves the {@link ContentInstance} resource from the Database based on the uri
     * @param uri - uri of the {@link ContentInstance} resource
     * @return The requested {@link ContentInstance} resource otherwise null
     */
    public ContentInstance lazyFind(String uri) {
        return find(uri);
    }

    /**
     * Updates an existing {@link ContentInstance} resource in the DataBase
     * @param resource - The {@link ContentInstance} the updated resource
     */
    public void update(ContentInstance resource) {
        // Store the updated resource
        DB.store(resource);
        // Update the lastModifiedTime attribute of the parent
        Query query = DB.query();
        query.constrain(ContentInstances.class);
        query.descend("uri").constrain(resource.getUri().split("/"+resource.getId())[0]);
        // Store all the founded resources
        ObjectSet<ContentInstances> result = query.execute();
		// Retrieve the first element corresponding to the researched resource
		// if result is not empty
		ContentInstances contentInstances = result.get(0);
        contentInstances.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        DB.store(contentInstances);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link ContentInstance} resource from the DataBase and validates the transaction
     * @Param the {@link ContentInstance} resource to delete
     */
    public void delete(ContentInstance resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link ContentInstance} resource from the DataBase without validating the transaction
     * @param resource - The {@link ContentInstance} resource to delete
     */
    public void lazyDelete(ContentInstance resource){
    	// Update the lastModifiedTime attribute of the parent
        Query query = DB.query();
        query.constrain(ContentInstances.class);
        query.descend("uri").constrain(resource.getUri().split("/"+resource.getId())[0]);
        // Store all the founded resources
        ObjectSet<ContentInstances> result = query.execute();
        // Update Parent
        ContentInstances contentInstances = result.get(0);
        // Update the lastModifiedTime attribute of the parent
        contentInstances.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        //subtract ContentSize to currentSize of the ContentInstances
        contentInstances.setCurrentByteSize(contentInstances.getCurrentByteSize()-resource.getContentSize());
        //Decrement the currentInstances
        contentInstances.setCurrentNrOfInstances(contentInstances.getCurrentNrOfInstances()-1);

        //DAOFactory.getContentInstancesDAO().update(contentInstances);
        DB.store(contentInstances);
        // Delete the resource
        DB.delete(resource);
    }
}
