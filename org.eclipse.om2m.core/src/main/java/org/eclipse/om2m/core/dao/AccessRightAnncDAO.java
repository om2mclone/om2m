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

import org.eclipse.om2m.commons.resource.AccessRightAnnc;
import org.eclipse.om2m.commons.resource.AccessRights;
import org.eclipse.om2m.commons.utils.DateConverter;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link AccessRightAnnc} resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 *
 */
public class AccessRightAnncDAO extends DAO<AccessRightAnnc> {

    /**
     * Creates an {@link AccessRightAnnc} resource in the DataBase and validates the transaction
     * @param resource - The {@link AccessRightAnnc} resource to create
     */
    public void create(AccessRightAnnc resource) {
        // Store the created resource
        DB.store(resource);
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(AccessRights.class);
        query.descend("uri").constrain(resource.getUri().split("/"+resource.getId())[0]);
        // Store all the founded resources
        ObjectSet<AccessRights> result = query.execute();
        
        // Update the lastModifiedTime attribute of the parent
        AccessRights accessRights = result.get(0);
        // Update the lastModifiedTime attribute of the parent
        accessRights.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        DB.store(accessRights);
        commit();
    }

    /**
     * Retrieves the {@link AccessRightAnnc} resource from the Database based on its uri
     * @param uri - uri of the {@link AccessRightAnnc} resource to retrieve
     * @return The requested {@link AccessRightAnnc} resource otherwise null
     */
    public AccessRightAnnc find(String uri) {
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(AccessRightAnnc.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<AccessRightAnnc> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if (!result.isEmpty()) {
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Retrieves the {@link AccessRightAnnc} resource from the Database based on the uri
     * @param uri - uri of the {@link AccessRightAnnc} resource
     * @return The requested {@link AccessRightAnnc} resource otherwise null
     */
    public AccessRightAnnc lazyFind (String uri) {
        return find(uri);
    }

    /**
     * Updates an existing {@link AccessRightAnnc} resource in the DataBase
     * @param resource - The {@link AccessRightAnnc} the updated resource
     */
    public void update(AccessRightAnnc resource) {
        // Store the updated resource
        DB.store(resource);
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(AccessRights.class);
        query.descend("uri").constrain(resource.getUri().split("/"+resource.getId())[0]);
        // Store all the founded resources
        ObjectSet<AccessRights> result = query.execute();
        
        // Update the lastModifiedTime attribute of the parent
        AccessRights accessRights = result.get(0);
        // Update the lastModifiedTime attribute of the parent
        accessRights.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        DB.store(accessRights);
        commit();
    }

    /**
     * Deletes the {@link AccessRightAnnc} resource from the DataBase and validates the transaction
     * @Param the {@link AccessRightAnnc} resource to delete
     */
    public void delete (AccessRightAnnc resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link AccessRightAnnc} resource from the DataBase without validating the transaction
     * @param resource - The {@link AccessRightAnnc} resource to delete
     */
    public void lazyDelete (AccessRightAnnc resource) {
        // Delete the resource
        DB.delete(resource);
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(AccessRights.class);
        query.descend("uri").constrain(resource.getUri().split("/"+resource.getId())[0]);
        // Store all the founded resources
        ObjectSet<AccessRights> result = query.execute();
        
        // Update the lastModifiedTime attribute of the parent
        AccessRights accessRights = result.get(0);
        // Update the lastModifiedTime attribute of the parent
        accessRights.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        DB.store(accessRights);
    }

}
