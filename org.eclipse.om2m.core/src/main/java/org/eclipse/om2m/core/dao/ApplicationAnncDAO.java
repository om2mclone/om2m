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

import org.eclipse.om2m.commons.resource.AccessRights;
import org.eclipse.om2m.commons.resource.ApplicationAnnc;
import org.eclipse.om2m.commons.resource.Applications;
import org.eclipse.om2m.commons.resource.Containers;
import org.eclipse.om2m.commons.resource.Groups;
import org.eclipse.om2m.commons.utils.DateConverter;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link ApplicationAnnc} resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */

public class ApplicationAnncDAO extends DAO<ApplicationAnnc> {

    /**
     * Creates an {@link ApplicationAnnc} resource in the DataBase and validates the transaction
     * @param resource - The {@link ApplicationAnnc} resource to create
     */
    public void create(ApplicationAnnc resource) {
        // Store the created resource
        DB.store(resource);
        // Containers
        Containers containers = new Containers();
        containers.setUri(resource.getContainersReference());
        containers.setCreationTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        containers.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        containers.setAccessRightID(resource.getAccessRightID());
        DAOFactory.getContainersDAO().create(containers);
        // AccessRights
        AccessRights accessRights = new AccessRights();
        accessRights.setUri(resource.getAccessRightsReference());
        accessRights.setCreationTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        accessRights.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        accessRights.setAccessRightID(resource.getAccessRightID());
        DAOFactory.getAccessRightsDAO().create(accessRights);
        // Groups
        Groups groups = new Groups();
        groups.setUri(resource.getGroupsReference());
        groups.setCreationTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        groups.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        groups.setAccessRightID(resource.getAccessRightID());
        DAOFactory.getGroupsDAO().create(groups);
     // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(Applications.class);
        query.descend("uri").constrain(resource.getUri().split("/"+resource.getId())[0]);
        // Store all the founded resources
        ObjectSet<Applications> result = query.execute();
        
        // Update the lastModifiedTime attribute of the parent
        Applications applications = result.get(0);
        // Update the lastModifiedTime attribute of the parent
        applications.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        DB.store(applications);
        commit();
    }

    /**
     * Retrieves the {@link ApplicationAnnc} resource from the Database based on its uri
     * @param uri - uri of the {@link ApplicationAnnc} resource to retrieve
     * @return The requested {@link ApplicationAnnc} resource otherwise null
     */
    public ApplicationAnnc find(String uri) {
        // Create the query based on the uri constraint
        Query queryApplicationAnnc = DB.query();
        queryApplicationAnnc.constrain(ApplicationAnnc.class);
        queryApplicationAnnc.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<ApplicationAnnc> result = queryApplicationAnnc.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if (!result.isEmpty()) {
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Retrieves the {@link ApplicationAnnc} resource from the Database based on the uri
     * @param uri - uri of the {@link ApplicationAnnc} resource
     * @return The requested {@link ApplicationAnnc} resource otherwise null
     */
    public ApplicationAnnc lazyFind(String uri) {
        return find(uri);
    }

    /**
     * Updates an existing {@link ApplicationAnnc} resource in the DataBase
     * @param resource - The {@link ApplicationAnnc} the updated resource
     */
    public void update(ApplicationAnnc resource) {
        // Store the updated resource
        DB.store(resource);
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(Applications.class);
        query.descend("uri").constrain(resource.getUri().split("/"+resource.getId())[0]);
        // Store all the founded resources
        ObjectSet<Applications> result = query.execute();
        
        // Update the lastModifiedTime attribute of the parent
        Applications applications = result.get(0);
        // Update the lastModifiedTime attribute of the parent
        applications.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        DB.store(applications);
        commit();
    }

    /**
     * Deletes the {@link ApplicationAnnc} resource from the DataBase and validates the transaction
     * @Param the {@link ApplicationAnnc} resource to delete
     */
    public void delete(ApplicationAnnc resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link ApplicationAnnc} resource from the DataBase without validating the transaction
     * @param resource - The {@link ApplicationAnnc} resource to delete
     */
    public void lazyDelete(ApplicationAnnc resource) {
        // Delete accessRights
        DAOFactory.getAccessRightsDAO().lazyDelete(DAOFactory.getAccessRightsDAO().lazyFind(resource.getAccessRightsReference()));
        // Delete containers
        DAOFactory.getContainersDAO().lazyDelete(DAOFactory.getContainersDAO().lazyFind(resource.getContainersReference()));
        // Delete groups
        DAOFactory.getGroupsDAO().lazyDelete(DAOFactory.getGroupsDAO().lazyFind(resource.getGroupsReference()));
        // Delete the resource
        DB.delete(resource);
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(Applications.class);
        query.descend("uri").constrain(resource.getUri().split("/"+resource.getId())[0]);
        // Store all the founded resources
        ObjectSet<Applications> result = query.execute();
        
        // Update the lastModifiedTime attribute of the parent
        Applications applications = result.get(0);
        // Update the lastModifiedTime attribute of the parent
        applications.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        DB.store(applications);

    }
}
