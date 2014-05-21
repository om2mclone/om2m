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

import org.eclipse.om2m.commons.resource.Group;
import org.eclipse.om2m.commons.resource.Groups;
import org.eclipse.om2m.commons.resource.Subscriptions;
import org.eclipse.om2m.commons.utils.DateConverter;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link Group} resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class GroupDAO extends DAO<Group>{

    /**
     * Creates an {@link Group} resource in the DataBase and validates the transaction
     * @param resource - The {@link Group} resource to create
     */
    public void create(Group resource) {
        //Set subscriptions reference
        resource.setSubscriptionsReference(resource.getUri()+"/subscriptions");
        // Store the created resource
        DB.store(resource);
        //Subscriptions
        Subscriptions subscriptions = new Subscriptions();
        subscriptions.setUri(resource.getSubscriptionsReference());
        DAOFactory.getSubscriptionsDAO().create(subscriptions);
        // MembersContent

        // Update the lastModifiedTime attribute of the parent
        Groups groups = DAOFactory.getGroupsDAO().lazyFind(resource.getUri().split("/"+resource.getId())[0]);
        groups.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DB.store(groups.getLastModifiedTime());
        // Validate the current transaction
        commit();
    }

    /**
     * Retrieves the {@link Group} resource from the Database based on its uri
     * @param uri - uri of the {@link Group} resource to retrieve
     * @return The requested {@link Group} resource otherwise null
     */
    public Group find(String uri) {
        // Create the query based on the uri constraint
        Query query=DB.query();
        query.constrain(Group.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<Group> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if(!result.isEmpty()){
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Retrieves the {@link Group} resource from the Database based on the uri
     * @param uri - uri of the {@link Group} resource
     * @return The requested {@link Group} resource otherwise null
     */
    public Group lazyFind(String uri) {
        return find(uri);
    }

    /**
     * Updates an existing {@link Group} resource in the DataBase
     * @param resource - The {@link Group} the updated resource
     */
    public void update(Group resource) {
        // Store the updated resource
        DB.store(resource);
        // Update the lastModifiedTime attribute of the parent
        Groups groups = DAOFactory.getGroupsDAO().lazyFind(resource.getUri().split("/"+resource.getId())[0]);
        groups.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DB.store(groups.getLastModifiedTime());
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link Group} resource from the DataBase and validates the transaction
     * @Param the {@link Group} resource to delete
     */
    public void delete(Group resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link Group} resource from the DataBase without validating the transaction
     * @param resource - The {@link Group} resource to delete
     */
    public void lazyDelete(Group resource) {
        // Delete subscriptions
        DAOFactory.getSubscriptionsDAO().lazyDelete(DAOFactory.getSubscriptionsDAO().lazyFind(resource.getSubscriptionsReference()));
        // Delete the resource
        DB.delete(resource);
        // Update the lastModifiedTime attribute of the parent
        Groups groups = DAOFactory.getGroupsDAO().lazyFind(resource.getUri().split("/"+resource.getId())[0]);
        groups.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DB.store(groups.getLastModifiedTime());
    }
}
