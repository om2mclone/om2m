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

import org.eclipse.om2m.commons.resource.AccessRight;
import org.eclipse.om2m.commons.resource.AccessRightAnnc;
import org.eclipse.om2m.commons.resource.AccessRights;
import org.eclipse.om2m.commons.resource.ReferenceToNamedResource;
import org.eclipse.om2m.commons.resource.Subscriptions;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link AccessRights} collection resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class AccessRightsDAO extends DAO<AccessRights> {

    /**
     * Creates an {@link AccessRights} collection resource in the DataBase.
     * @param resource - The {@link AccessRights} collection resource to create
     */
    public void create(AccessRights resource) {
        // Set subscriptions Reference
        resource.setSubscriptionsReference(resource.getUri()+"/subscriptions");
        // Store the created resource
        DB.store(resource);

        //Subscriptions
        Subscriptions subscriptions = new Subscriptions();
        subscriptions.setUri(resource.getSubscriptionsReference());
        DAOFactory.getSubscriptionsDAO().create(subscriptions);
    }

    /**
     * Retrieves the {@link AccessRights} collection resource based on its uri with sub-resources references
     * @param uri - uri of the {@link AccessRights} collection resource
     * @return The requested {@link AccessRights} collection resource otherwise null
     */
    public AccessRights find(String uri) {
        AccessRights accessRights = lazyFind(uri);

        if(accessRights != null){
            // Find AccessRight sub-resources and add their references
            Query queryAccessRight = DB.query();
            queryAccessRight.constrain(AccessRight.class);
            queryAccessRight.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<AccessRight> resultAccessRight = queryAccessRight.execute();
            accessRights.getAccessRightCollection().getNamedReference().clear();

            for (int i = 0; i < resultAccessRight.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(resultAccessRight.get(i).getId());
                reference.setValue(resultAccessRight.get(i).getUri());
                accessRights.getAccessRightCollection().getNamedReference().add(reference);
            }

            // Find AccessRightAnnc sub-resources Resources and add their references
            accessRights.getAccessRightAnncCollection().getNamedReference().clear();
            Query queryAccessRightAnnc = DB.query();
            queryAccessRightAnnc.constrain(AccessRightAnnc.class);
            queryAccessRightAnnc.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<AccessRightAnnc> resultAccessRightAnnc = queryAccessRightAnnc.execute();

            for (int i = 0; i < resultAccessRightAnnc.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(resultAccessRightAnnc.get(i).getId());
                reference.setValue(resultAccessRightAnnc.get(i).getUri());
                accessRights.getAccessRightAnncCollection().getNamedReference().add(reference);
            }
        }
        return accessRights;
    }

    /**
     * Retrieves the {@link AccessRights} collection resource based on its uri without sub-resources references.
     * @param uri - uri of the {@link AccessRights} collection resource
     * @return The requested {@link AccessRights} collection resource otherwise null
     */
    public AccessRights lazyFind(String uri) {
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(AccessRights.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<AccessRights> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if (!result.isEmpty()) {
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Updates an existing {@link AccessRights} collection resource in the DataBase
     * @param resource - The {@link AccessRights} the updated resource
     */
    public void update(AccessRights resource) {
        // Store the updated resource
        DB.store(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link AccessRights} collection resource from the DataBase and validates the transaction
     * @Param the {@link AccessRights} collection resource to delete
     */
    public void delete(AccessRights resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link AccessRights} collection resource from the DataBase without validating the transaction
     * @Param the {@link AccessRights} collection resource to delete
     */
    public void lazyDelete(AccessRights resource) {
        // Delete sub-resources
        // Delete subscriptions
        DAOFactory.getSubscriptionsDAO().lazyDelete(DAOFactory.getSubscriptionsDAO().lazyFind(resource.getSubscriptionsReference()));

        // Delete accessRight sub-resources
        Query queryAccessRight = DB.query();
        queryAccessRight.constrain(AccessRight.class);
        queryAccessRight.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<AccessRight> resultAccessRight = queryAccessRight.execute();

        for (int i = 0; i < resultAccessRight.size(); i++) {
            DAOFactory.getAccessRightDAO().lazyDelete(resultAccessRight.get(i));
        }

        // Delete accessRightAnnc sub-resources
        Query queryAccessRightAnnc = DB.query();
        queryAccessRightAnnc.constrain(AccessRightAnnc.class);
        queryAccessRightAnnc.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<AccessRightAnnc> resultAccessRightAnnc = queryAccessRightAnnc.execute();

        for (int i = 0; i < resultAccessRightAnnc.size(); i++) {
            DAOFactory.getAccessRightAnncDAO().lazyDelete(resultAccessRightAnnc.get(i));
        }

        // Delete the resource
        DB.delete(resource);
    }
}
