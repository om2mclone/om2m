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

import org.eclipse.om2m.commons.resource.AccessRights;
import org.eclipse.om2m.commons.resource.Applications;
import org.eclipse.om2m.commons.resource.Containers;
import org.eclipse.om2m.commons.resource.Groups;
import org.eclipse.om2m.commons.resource.SclBase;
import org.eclipse.om2m.commons.resource.Scls;
import org.eclipse.om2m.commons.resource.Subscriptions;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link SclBase} resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class SclBaseDAO extends DAO<SclBase>{

    public SclBaseDAO() {
        super();
    }

    /**
     * Creates an {@link SclBase} resource in the DataBase and validates the transaction
     * @param resource - The {@link SclBase} resource to create
     */
    public void create(SclBase resource) {
        //Set references
        resource.setAccessRightsReference(resource.getUri()+"/accessRights");
        resource.setApplicationsReference(resource.getUri()+ "/applications");
        resource.setContainersReference(resource.getUri()+ "/containers");
        resource.setDiscoveryReference(resource.getUri()+ "/discovery");
        resource.setGroupsReference(resource.getUri()+ "/groups");
        resource.setSclsReference(resource.getUri()+ "/scls");
        resource.setSubscriptionsReference(resource.getUri()+ "/subscriptions");
        // Store the created resource
        DB.store(resource);

        // Scls
        Scls scls = new Scls();
        scls.setUri(resource.getSclsReference());
        scls.setCreationTime(resource.getCreationTime());
        scls.setLastModifiedTime(resource.getLastModifiedTime());
        scls.setAccessRightID(resource.getAccessRightID());
        DAOFactory.getSclsDAO().create(scls);
        // Applications
        Applications applications = new Applications();
        applications.setUri(resource.getApplicationsReference());
        applications.setCreationTime(resource.getCreationTime());
        applications.setLastModifiedTime(resource.getLastModifiedTime());
        applications.setAccessRightID(resource.getAccessRightID());
        DAOFactory.getApplicationsDAO().create(applications);
        // AccessRights
        AccessRights accessRights = new AccessRights();
        accessRights.setUri(resource.getAccessRightsReference());
        accessRights.setCreationTime(resource.getCreationTime());
        accessRights.setLastModifiedTime(resource.getLastModifiedTime());
        accessRights.setAccessRightID(resource.getAccessRightID());
        DAOFactory.getAccessRightsDAO().create(accessRights);
        // Groups
        Groups groups = new Groups();
        groups.setUri(resource.getGroupsReference());
        groups.setCreationTime(resource.getCreationTime());
        groups.setLastModifiedTime(resource.getLastModifiedTime());
        groups.setAccessRightID(resource.getAccessRightID());
        DAOFactory.getGroupsDAO().create(groups);
        // Containers
        Containers containers = new Containers();
        containers.setUri(resource.getContainersReference());
        containers.setCreationTime(resource.getCreationTime());
        containers.setLastModifiedTime(resource.getLastModifiedTime());
        containers.setAccessRightID(resource.getAccessRightID());
        DAOFactory.getContainersDAO().create(containers);
        // Subscriptions
        Subscriptions subscriptions = new Subscriptions();
        subscriptions.setUri(resource.getSubscriptionsReference());
        DAOFactory.getSubscriptionsDAO().create(subscriptions);
        // Validate the current transaction
        commit();
    }

    /**
     * Retrieves the {@link SclBase} resource from the Database based on its uri
     * @param uri - uri of the {@link SclBase} resource to retrieve
     * @return The requested {@link SclBase} resource otherwise null
     */
    public SclBase find(String uri) {
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(SclBase.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<SclBase> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if (!result.isEmpty()) {
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Retrieves the {@link SclBase} resource from the Database based on the uri
     * @param uri - uri of the {@link SclBase} resource
     * @return The requested {@link SclBase} resource otherwise null
     */
    public SclBase lazyFind(String uri) {
        return find(uri);
    }

    /**
     * Updates an existing {@link SclBase} resource in the DataBase
     * @param resource - The {@link SclBase} the updated resource
     */
    public void update(SclBase resource) {
        // Store the updated resource
        DB.store(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link SclBase} resource from the DataBase and validates the transaction
     * @Param the {@link SclBase} resource to delete
     */
    public void delete(SclBase resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link SclBase} resource from the DataBase without validating the transaction
     * @param resource - The {@link SclBase} resource to delete
     */
    public void lazyDelete(SclBase resource) {
        // Delete scls
        DAOFactory.getSclsDAO().lazyDelete(DAOFactory.getSclsDAO().lazyFind(resource.getSclsReference()));
        // Delete applications
        DAOFactory.getApplicationsDAO().lazyDelete(DAOFactory.getApplicationsDAO().lazyFind(resource.getApplicationsReference()));
        // Delete accessRights
        DAOFactory.getAccessRightsDAO().lazyDelete(DAOFactory.getAccessRightsDAO().lazyFind(resource.getAccessRightsReference()));
        // Delete groups
        DAOFactory.getGroupsDAO().lazyDelete(DAOFactory.getGroupsDAO().lazyFind(resource.getGroupsReference()));
        // Delete containers
        DAOFactory.getContainersDAO().lazyDelete(DAOFactory.getContainersDAO().lazyFind(resource.getContainersReference()));
        // Delete subscriptions
        DAOFactory.getSubscriptionsDAO().lazyDelete(DAOFactory.getSubscriptionsDAO().lazyFind(resource.getSubscriptionsReference()));
        // Delete the resource
        DB.delete(resource);
    }
}
