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

import org.eclipse.om2m.commons.resource.Group;
import org.eclipse.om2m.commons.resource.GroupAnnc;
import org.eclipse.om2m.commons.resource.Groups;
import org.eclipse.om2m.commons.resource.ReferenceToNamedResource;
import org.eclipse.om2m.commons.resource.Subscriptions;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link Groups} collection resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class GroupsDAO extends DAO<Groups> {

    /**
     * Creates an {@link Groups} collection resource in the DataBase.
     * @param resource - The {@link Groups} collection resource to create
     */
    public void create(Groups resource) {
        //Set subscriptions reference
        resource.setSubscriptionsReference(resource.getUri()+"/subscriptions");
        // Store the created resource
        DB.store(resource);

        //Subscriptions
        Subscriptions subscriptions = new Subscriptions();
        subscriptions.setUri(resource.getSubscriptionsReference());
        DAOFactory.getSubscriptionsDAO().create(subscriptions);
    }

    /**
     * Retrieves the {@link Groups} collection resource based on its uri with sub-resources references
     * @param uri - uri of the {@link Groups} collection resource
     * @return The requested {@link Groups} collection resource otherwise null
     */
    public Groups find(String uri) {
        Groups groups = lazyFind(uri);

        if (groups != null){
        	ObjectContainer session = DB.ext().openSession();

            //Find Group sub-resources and add their references
            groups.getGroupCollection().getNamedReference().clear();
            Query queryGroup = session.query();
            queryGroup.constrain(Group.class);
            queryGroup.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<Group> resultGroup = queryGroup.execute();

            for (int i = 0; i < resultGroup.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(resultGroup.get(i).getId());
                reference.setValue(resultGroup.get(i).getUri());
                groups.getGroupCollection().getNamedReference().add(reference);
            }

            //Find GroupAnnc sub-resources and add their references
            groups.getGroupAnncCollection().getNamedReference().clear();
            Query queryGroupAnnc = session.query();
            queryGroupAnnc.constrain(GroupAnnc.class);
            queryGroupAnnc.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<GroupAnnc> resultGroupAnnc = queryGroupAnnc.execute();

            for (int i = 0; i < resultGroupAnnc.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(resultGroupAnnc.get(i).getId());
                reference.setValue(resultGroupAnnc.get(i).getUri());
                groups.getGroupAnncCollection().getNamedReference().add(reference);
            }
        }
        return groups;
    }

    /**
     * Retrieves the {@link Groups} collection resource based on its uri without sub-resources references.
     * @param uri - uri of the {@link Groups} collection resource
     * @return The requested {@link Groups} collection resource otherwise null
     */
    public Groups lazyFind(String uri) {
    	ObjectContainer session = DB.ext().openSession();

        // Create the query based on the uri constraint
        Query query = session.query();
        query.constrain(Groups.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<Groups> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if(!result.isEmpty()){
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Updates an existing {@link Groups} collection resource in the DataBase
     * @param resource - The {@link Groups} the updated resource
     */
    public void update(Groups resource) {
        // Store the updated resource
        DB.store(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link Groups} collection resource from the DataBase and validates the transaction
     * @Param the {@link Groups} collection resource to delete
     */
    public void delete(Groups resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link Groups} collection resource from the DataBase without validating the transaction
     * @Param the {@link Groups} collection resource to delete
     */
    public void lazyDelete(Groups resource) {
        // Delete subscriptions
        DAOFactory.getSubscriptionsDAO().lazyDelete(DAOFactory.getSubscriptionsDAO().lazyFind(resource.getSubscriptionsReference()));

        // Delete group sub-resources
        Query queryGroup = DB.query();
        queryGroup.constrain(Group.class);
        queryGroup.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<Group> resultGroup = queryGroup.execute();

        for (int i = 0; i < resultGroup.size(); i++) {
            DAOFactory.getGroupDAO().lazyDelete(resultGroup.get(i));
        }

        // Delete groupAnnc sub-resources
        Query queryGroupAnnc = DB.query();
        queryGroupAnnc.constrain(GroupAnnc.class);
        queryGroupAnnc.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<GroupAnnc> resultGroupAnnc = queryGroupAnnc.execute();

        for (int i = 0; i < resultGroupAnnc.size(); i++) {
            DAOFactory.getGroupAnncDAO().lazyDelete(resultGroupAnnc.get(i));
        }
        // Delete the resource
        DB.delete(resource);
    }
}
