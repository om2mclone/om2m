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

import org.eclipse.om2m.commons.resource.Container;
import org.eclipse.om2m.commons.resource.ContainerAnnc;
import org.eclipse.om2m.commons.resource.Containers;
import org.eclipse.om2m.commons.resource.LocationContainer;
import org.eclipse.om2m.commons.resource.LocationContainerAnnc;
import org.eclipse.om2m.commons.resource.ReferenceToNamedResource;
import org.eclipse.om2m.commons.resource.Subscriptions;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link Containers} collection resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.Feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class ContainersDAO extends DAO<Containers> {

    /**
     * Creates an {@link Containers} collection resource in the DataBase.
     * @param resource - The {@link Containers} collection resource to create
     */
    public void create(Containers resource) {
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
     * Retrieves the {@link Containers} collection resource based on its uri with sub-resources references
     * @param uri - uri of the {@link Containers} collection resource
     * @return The requested {@link Containers} collection resource otherwise null
     */
    public Containers find(String uri) {
        Containers containers = lazyFind(uri);

        if(containers != null) {
        	ObjectContainer session = DB.ext().openSession();

            // Find Container sub-resources and add their references
            containers.getContainerCollection().getNamedReference().clear();
            Query queryContainer = session.query();
            queryContainer.constrain(Container.class);
            queryContainer.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<Container> resultContainer = queryContainer.execute();

            for (int i = 0; i < resultContainer.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(resultContainer.get(i).getId());
                reference.setValue(resultContainer.get(i).getUri());
                containers.getContainerCollection().getNamedReference().add(reference);
            }

            // Find ContainerAnnc sub-resources and add their references
            containers.getContainerAnncCollection().getNamedReference().clear();
            Query queryContainerAnnc = session.query();
            queryContainerAnnc.constrain(ContainerAnnc.class);
            queryContainerAnnc.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<ContainerAnnc> resultContainerAnnc = queryContainerAnnc.execute();

            for (int i = 0; i < resultContainerAnnc.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(resultContainerAnnc.get(i).getId());
                reference.setValue(resultContainerAnnc.get(i).getUri());
                containers.getContainerAnncCollection().getNamedReference().add(reference);
            }

            // Find LocationContainer sub-resources and add their references
            containers.getLocationContainerCollection().getNamedReference().clear();
            Query queryLocationContainer = session.query();
            queryLocationContainer.constrain(LocationContainer.class);
            queryLocationContainer.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<LocationContainer> resultLocationContainer = queryLocationContainer.execute();

            for (int i = 0; i < resultLocationContainer.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(resultLocationContainer.get(i).getId());
                reference.setValue(resultLocationContainer.get(i).getUri());
                containers.getLocationContainerCollection().getNamedReference().add(reference);
            }

            // Find LocationContainerAnnc sub-resources and add their references
            containers.getLocationContainerAnncCollection().getNamedReference().clear();
            Query queryLocationContainerAnnc = session.query();
            queryLocationContainerAnnc.constrain(LocationContainerAnnc.class);
            queryLocationContainerAnnc.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<LocationContainerAnnc> resultLocationContainerAnnc = queryLocationContainerAnnc.execute();

            for (int i = 0; i < resultLocationContainerAnnc.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(resultLocationContainerAnnc.get(i).getId());
                reference.setValue(resultLocationContainerAnnc.get(i).getUri());
                containers.getLocationContainerAnncCollection().getNamedReference().add(reference);
            }
        }
        return containers;
    }

    /**
     * Retrieves the {@link Containers} collection resource based on its uri without sub-resources references.
     * @param uri - uri of the {@link Containers} collection resource
     * @return The requested {@link Containers} collection resource otherwise null
     */
    public Containers lazyFind (String uri) {
        // Create the query based on the uri constraint
    	ObjectContainer session = DB.ext().openSession();

        Query query = session.query();
        query.constrain(Containers.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<Containers> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if (!result.isEmpty()) {
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Updates an existing {@link Containers} collection resource in the DataBase
     * @param resource - The {@link Containers} the updated resource
     */
    public void update(Containers resource) {
        // Store the updated resource
        DB.store(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link Containers} collection resource from the DataBase and validates the transaction
     * @Param the {@link Containers} collection resource to delete
     */
    public void delete(Containers resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link Containers} collection resource from the DataBase without validating the transaction
     * @Param the {@link Containers} collection resource to delete
     */
    public void lazyDelete(Containers resource) {
        // Delete subscriptions
        DAOFactory.getSubscriptionsDAO().lazyDelete(DAOFactory.getSubscriptionsDAO().lazyFind(resource.getSubscriptionsReference()));

        // Delete Container sub-resources
        Query queryContainer = DB.query();
        queryContainer.constrain(Container.class);
        queryContainer.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<Container> resultContainer = queryContainer.execute();

        for (int i = 0; i < resultContainer.size(); i++) {
            DAOFactory.getContainerDAO().lazyDelete(resultContainer.get(i));
        }

        // Delete ContainerAnnc sub-resources
        Query queryContainerAnnc = DB.query();
        queryContainerAnnc.constrain(ContainerAnnc.class);
        queryContainerAnnc.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<ContainerAnnc> resultContainerAnnc = queryContainerAnnc.execute();

        for (int i = 0; i < resultContainerAnnc.size(); i++) {
            DAOFactory.getContainerAnncDAO().lazyDelete(resultContainerAnnc.get(i));
        }

        // Delete locationContainer sub-resources
        Query queryLocationContainer = DB.query();
        queryLocationContainer.constrain(LocationContainer.class);
        queryLocationContainer.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<LocationContainer> resultLocationContainer = queryLocationContainer.execute();

        for (int i = 0; i < resultLocationContainer.size(); i++) {
            DAOFactory.getLocationContainerDAO().lazyDelete(resultLocationContainer.get(i));
        }

        // Delete locationContainerAnnc sub-resources
        Query queryLocationContainerAnnc = DB.query();
        queryLocationContainerAnnc.constrain(LocationContainerAnnc.class);
        queryLocationContainerAnnc.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<LocationContainerAnnc> resultLocationContainerAnnc = queryLocationContainerAnnc.execute();

        for (int i = 0; i < resultLocationContainerAnnc.size(); i++) {
            DAOFactory.getLocationContainerAnncDAO().lazyDelete(resultLocationContainerAnnc.get(i));
        }
        // Delete the resource
        DB.delete(resource);
    }
}
