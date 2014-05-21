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
import org.eclipse.om2m.commons.resource.Application;
import org.eclipse.om2m.commons.resource.Applications;
import org.eclipse.om2m.commons.resource.Containers;
import org.eclipse.om2m.commons.resource.Groups;
import org.eclipse.om2m.commons.resource.NotificationChannels;
import org.eclipse.om2m.commons.resource.Subscriptions;
import org.eclipse.om2m.commons.utils.DateConverter;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link Application} resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */

public class ApplicationDAO extends DAO<Application> {

    /**
     * Creates an {@link Application} resource in the DataBase and validates the transaction
     * @param resource - The {@link Application} resource to create
     */
    public void create(Application resource) {
        // Store the created resource
        DB.store(resource);
        //Subscriptions
        Subscriptions subscriptions = new Subscriptions();
        subscriptions.setUri(resource.getSubscriptionsReference());
        DAOFactory.getSubscriptionsDAO().create(subscriptions);
        // Containers
        Containers containers = new Containers();
        containers.setUri(resource.getContainersReference());
        containers.setCreationTime(resource.getCreationTime());
        containers.setLastModifiedTime(resource.getLastModifiedTime());
        containers.setAccessRightID(resource.getAccessRightID());
        DAOFactory.getContainersDAO().create(containers);
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
        // NotificationChannels
        NotificationChannels notificationChannels = new NotificationChannels();
        notificationChannels.setAccessRightID(resource.getAccessRightID());
        notificationChannels.setUri(resource.getNotificationChannelsReference());
        notificationChannels.setCreationTime(resource.getCreationTime());
        notificationChannels.setLastModifiedTime(resource.getLastModifiedTime());
        DAOFactory.getNotificationChannelsDAO().create(notificationChannels);

        // Update the lastModifiedTime attribute of the parent
        Applications applications = DAOFactory.getApplicationsDAO().lazyFind(resource.getUri().split("/"+resource.getAppId())[0]);
        applications.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DB.store(applications.getLastModifiedTime());
        // Validate the current transaction
        commit();

    }

    /**
     * Retrieves the {@link Application} resource from the Database based on its uri
     * @param uri - uri of the {@link Application} resource to retrieve
     * @return The requested {@link Application} resource otherwise null
     */
    public Application find(String uri) {
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(Application.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<Application> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if (!result.isEmpty()) {
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Retrieves the {@link Application} resource from the Database based on the uri
     * @param uri - uri of the {@link Application} resource
     * @return The requested {@link Application} resource otherwise null
     */
    public Application lazyFind(String uri) {
        return find(uri);
    }

    /**
     * Updates an existing {@link Application} resource in the DataBase
     * @param resource - The {@link Application} the updated resource
     */
    public void update(Application resource) {
        // Store the updated resource
        DB.store(resource);
        // Update the lastModifiedTime attribute of the parent
        Applications applications = DAOFactory.getApplicationsDAO().lazyFind(resource.getUri().split("/"+resource.getAppId())[0]);
        applications.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DB.store(applications.getLastModifiedTime());
        // Validate the current transaction
        commit();

    }

    /**
     * Deletes the {@link Application} resource from the DataBase and validates the transaction
     * @Param the {@link Application} resource to delete
     */
    public void delete( Application resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();

    }

    /**
     * Deletes the {@link Application} resource from the DataBase without validating the transaction
     * @param resource - The {@link Application} resource to delete
     */
    public void lazyDelete(Application resource) {
        // Delete accessRights
        DAOFactory.getAccessRightsDAO().lazyDelete(DAOFactory.getAccessRightsDAO().lazyFind(resource.getAccessRightsReference()));
        // Delete containers
        DAOFactory.getContainersDAO().lazyDelete(DAOFactory.getContainersDAO().lazyFind(resource.getContainersReference()));
        // Delete groups
        DAOFactory.getGroupsDAO().lazyDelete(DAOFactory.getGroupsDAO().lazyFind(resource.getGroupsReference()));
        // Delete subscriptions
        DAOFactory.getSubscriptionsDAO().lazyDelete(DAOFactory.getSubscriptionsDAO().lazyFind(resource.getSubscriptionsReference()));
        // Delete notificationsChannels
        DAOFactory.getNotificationChannelsDAO().lazyDelete(DAOFactory.getNotificationChannelsDAO().lazyFind(resource.getNotificationChannelsReference()));
        // Delete the resource
        DB.delete(resource);
        // Update the lastModifiedTime attribute of the parent
        Applications applications = DAOFactory.getApplicationsDAO().lazyFind(resource.getUri().split("/"+resource.getAppId())[0]);
        applications.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DB.store(applications.getLastModifiedTime());
    }
}
