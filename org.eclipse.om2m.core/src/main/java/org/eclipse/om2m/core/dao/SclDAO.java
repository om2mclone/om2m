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

import org.eclipse.om2m.commons.resource.AccessRights;
import org.eclipse.om2m.commons.resource.Applications;
import org.eclipse.om2m.commons.resource.AttachedDevices;
import org.eclipse.om2m.commons.resource.Containers;
import org.eclipse.om2m.commons.resource.Groups;
import org.eclipse.om2m.commons.resource.M2MPocs;
import org.eclipse.om2m.commons.resource.MgmtObjs;
import org.eclipse.om2m.commons.resource.NotificationChannels;
import org.eclipse.om2m.commons.resource.Scl;
import org.eclipse.om2m.commons.resource.Scls;
import org.eclipse.om2m.commons.resource.Subscriptions;
import org.eclipse.om2m.commons.utils.DateConverter;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link Scl} resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class SclDAO extends DAO<Scl> {

    /**
     * Creates an {@link Scl} resource in the DataBase and validates the transaction
     * @param resource - The {@link Scl} resource to create
     */
    public void create(Scl resource) {
        // Store the created resource
        DB.store(resource);

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
        // AttachedDevices
        AttachedDevices attachedDevices = new AttachedDevices();
        attachedDevices.setUri(resource.getAttachedDevicesReference());
        attachedDevices.setCreationTime(resource.getCreationTime());
        attachedDevices.setLastModifiedTime(resource.getLastModifiedTime());
        attachedDevices.setAccessRightID(resource.getAccessRightID());
        DAOFactory.getAttachedDevicesDAO().create(attachedDevices);
        // NotificationChannels
        NotificationChannels notificationChannels = new NotificationChannels();
        notificationChannels.setAccessRightID(resource.getAccessRightID());
        notificationChannels.setUri(resource.getNotificationChannelsReference());
        notificationChannels.setCreationTime(resource.getCreationTime());
        notificationChannels.setLastModifiedTime(resource.getLastModifiedTime());
        DAOFactory.getNotificationChannelsDAO().create(notificationChannels);
        // MgmtObjs
        MgmtObjs mgmtObjs = new MgmtObjs();
        mgmtObjs.setUri(resource.getMgmtObjsReference());
        mgmtObjs.setCreationTime(resource.getCreationTime());
        mgmtObjs.setLastModifiedTime(resource.getLastModifiedTime());
        mgmtObjs.setAccessRightID(resource.getAccessRightID());
        DAOFactory.getMgmtObjsDAO().create(mgmtObjs);
        // M2MPocs
        M2MPocs m2mPocs = new M2MPocs();
        m2mPocs.setUri(resource.getM2MPocsReference());
        m2mPocs.setCreationTime(resource.getCreationTime());
        m2mPocs.setLastModifiedTime(resource.getLastModifiedTime());
        DAOFactory.getM2MPocsDAO().create(m2mPocs);

        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(Scls.class);
        query.descend("uri").constrain(resource.getUri().split("/"+resource.getSclId())[0]);
        // Store all the founded resources
        ObjectSet<Scls> result = query.execute();
        
        // Update the lastModifiedTime attribute of the parent
        Scls scls = result.get(0);
        // Update the lastModifiedTime attribute of the parent
        scls.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        DB.store(scls);

        // Validate the current transaction
        commit();
    }

    /**
     * Retrieves the {@link Scl} resource from the Database based on its uri
     * @param uri - uri of the {@link Scl} resource to retrieve
     * @return The requested {@link Scl} resource otherwise null
     */
    public Scl find(String uri) {
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(Scl.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<Scl> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if(!result.isEmpty()){
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Retrieves the {@link Scl} resource from the Database based on the uri
     * @param uri - uri of the {@link Scl} resource
     * @return The requested {@link Scl} resource otherwise null
     */
    public Scl lazyFind(String uri) {
        return find(uri);
    }

    /**
     * Updates an existing {@link Scl} resource in the DataBase
     * @param resource - The {@link Scl} the updated resource
     */
    public void update(Scl resource) {
        // Store the updated resource
        DB.store(resource);
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(Scls.class);
        query.descend("uri").constrain(resource.getUri().split("/"+resource.getSclId())[0]);
        // Store all the founded resources
        ObjectSet<Scls> result = query.execute();
        
        // Update the lastModifiedTime attribute of the parent
        Scls scls = result.get(0);
        // Update the lastModifiedTime attribute of the parent
        scls.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        DB.store(scls);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link Scl} resource from the DataBase and validates the transaction
     * @Param the {@link Scl} resource to delete
     */
    public void delete(Scl resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link Scl} resource from the DataBase without validating the transaction
     * @param resource - The {@link Scl} resource to delete
     */
    public void lazyDelete(Scl resource) {
        // Delete notificationsChannels
        DAOFactory.getNotificationChannelsDAO().lazyDelete(DAOFactory.getNotificationChannelsDAO().lazyFind(resource.getNotificationChannelsReference()));
        // Delete accessRights
        DAOFactory.getAccessRightsDAO().lazyDelete(DAOFactory.getAccessRightsDAO().lazyFind(resource.getAccessRightsReference()));
        // Delete containers
        DAOFactory.getContainersDAO().lazyDelete(DAOFactory.getContainersDAO().lazyFind(resource.getContainersReference()));
        // Delete groups
        DAOFactory.getGroupsDAO().lazyDelete(DAOFactory.getGroupsDAO().lazyFind(resource.getGroupsReference()));
        // Delete subscriptions
        DAOFactory.getSubscriptionsDAO().lazyDelete(DAOFactory.getSubscriptionsDAO().lazyFind(resource.getSubscriptionsReference()));
        // Delete attachedDevices
        DAOFactory.getAttachedDevicesDAO().lazyDelete(DAOFactory.getAttachedDevicesDAO().lazyFind(resource.getAttachedDevicesReference()));
        // Delete applications
        DAOFactory.getApplicationsDAO().lazyDelete(DAOFactory.getApplicationsDAO().lazyFind(resource.getApplicationsReference()));
        // Delete the resource
        DB.delete(resource);

     // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(Scls.class);
        query.descend("uri").constrain(resource.getUri().split("/"+resource.getSclId())[0]);
        // Store all the founded resources
        ObjectSet<Scls> result = query.execute();
        
        // Update the lastModifiedTime attribute of the parent
        Scls scls = result.get(0);
        // Update the lastModifiedTime attribute of the parent
        scls.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        DB.store(scls);
    }
}
