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

import org.eclipse.om2m.commons.resource.ExecInstance;
import org.eclipse.om2m.commons.resource.ExecInstances;
import org.eclipse.om2m.commons.resource.ReferenceToNamedResource;
import org.eclipse.om2m.commons.resource.Subscriptions;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link ExecInstances} collection resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class ExecInstancesDAO extends DAO<ExecInstances> {

    /**
     * Creates an {@link ExecInstances} collection resource in the DataBase.
     * @param resource - The {@link ExecInstances} collection resource to create
     */
    public void create(ExecInstances resource) {
        resource.setSubscriptionsReference(resource.getUri()+"/subscriptions");
        // Store the created resource
        DB.store(resource);

        // Subscriptions
        Subscriptions subscriptions = new Subscriptions();
        subscriptions.setUri(resource.getSubscriptionsReference());
        DAOFactory.getSubscriptionsDAO().create(subscriptions);
    }

    /**
     * Retrieves the {@link ExecInstances} collection resource based on its uri with sub-resources references
     * @param uri - uri of the {@link ExecInstances} collection resource
     * @return The requested {@link ExecInstances} collection resource otherwise null
     */
    public ExecInstances find(String uri) {
        ExecInstances execInstances = lazyFind(uri);

        if(execInstances != null){
        	ObjectContainer session = DB.ext().openSession();

            //Find ExecInstances sub-resources and add their references
            Query query = session.query();
            query.constrain(ExecInstances.class);
            query.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<ExecInstances> result = query.execute();
            execInstances.getExecInstanceCollection().getNamedReference().clear();

            for (int i = 0; i < result.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(result.get(i).getId());
                reference.setValue(result.get(i).getUri());
                execInstances.getExecInstanceCollection().getNamedReference().add(reference);
            }
        }
        return execInstances;
    }

    /**
     * Retrieves the {@link ExecInstances} collection resource based on its uri without sub-resources references.
     * @param uri - uri of the {@link ExecInstances} collection resource
     * @return The requested {@link ExecInstances} collection resource otherwise null
     */
    public ExecInstances lazyFind(String uri) {
    	ObjectContainer session = DB.ext().openSession();

        // Create the query based on the uri constraint
        Query query=session.query();
        query.constrain(ExecInstances.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<ExecInstances> result=query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if (!result.isEmpty()) {
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Updates an existing {@link ExecInstances} collection resource in the DataBase
     * @param resource - The {@link ExecInstances} the updated resource
     */
    public void update(ExecInstances resource) {
        // Store the updated resource
        DB.store(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link ExecInstances} collection resource from the DataBase and validates the transaction
     * @Param the {@link ExecInstances} collection resource to delete
     */
    public void delete(ExecInstances resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link ExecInstances} collection resource from the DataBase without validating the transaction
     * @Param the {@link ExecInstances} collection resource to delete
     */
    public void lazyDelete(ExecInstances resource) {
        // Delete subscriptions
        DAOFactory.getSubscriptionsDAO().lazyDelete(DAOFactory.getSubscriptionsDAO().lazyFind(resource.getSubscriptionsReference()));

        // Delete execInstance sub-resources
        Query query = DB.query();
        query.constrain(ExecInstance.class);
        query.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<ExecInstance> result = query.execute();

        for (int i = 0; i < result.size(); i++) {
            DAOFactory.getExecInstanceDAO().lazyDelete(result.get(i));
        }
        // Delete the resource
        DB.delete(resource);
    }
}
