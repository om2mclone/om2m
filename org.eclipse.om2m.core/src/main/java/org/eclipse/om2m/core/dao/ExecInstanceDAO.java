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

import org.eclipse.om2m.commons.resource.ExecInstance;
import org.eclipse.om2m.commons.resource.ExecInstances;
import org.eclipse.om2m.commons.resource.Subscriptions;
import org.eclipse.om2m.commons.utils.DateConverter;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link ExecInstance} resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class ExecInstanceDAO extends DAO<ExecInstance>{

    /**
     * Creates an {@link ExecInstance} resource in the DataBase and validates the transaction
     * @param resource - The {@link ExecInstance} resource to create
     */
    public void create(ExecInstance resource) {
        // Store the created resource
        DB.store(resource);

        // Subscriptions
        Subscriptions subscriptions = new Subscriptions();
        subscriptions.setUri(resource.getSubscriptionsReference());
        DAOFactory.getSubscriptionsDAO().create(subscriptions);

        // Update the lastModifiedTime attribute of the parent
        ExecInstances execInstances = DAOFactory.getExecInstancesDAO().lazyFind(resource.getUri().split("/"+resource.getId())[0]);
        execInstances.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DB.store(execInstances.getLastModifiedTime());
        // Validate the current transaction
        commit();
    }

    /**
     * Retrieves the {@link ExecInstance} resource from the Database based on its uri
     * @param uri - uri of the {@link ExecInstance} resource to retrieve
     * @return The requested {@link ExecInstance} resource otherwise null
     */
    public ExecInstance find(String uri){
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(ExecInstance.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<ExecInstance> result=query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if (!result.isEmpty()) {
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Retrieves the {@link ExecInstance} resource from the Database based on the uri
     * @param uri - uri of the {@link ExecInstance} resource
     * @return The requested {@link ExecInstance} resource otherwise null
     */
    public ExecInstance lazyFind(String uri) {
        return find(uri);
    }

    /**
     * Updates an existing {@link ExecInstance} resource in the DataBase
     * @param resource - The {@link ExecInstance} the updated resource
     */
    public void update(ExecInstance resource) {
        // Store the updated resource
        DB.store(resource);
        // Update the lastModifiedTime attribute of the parent
        ExecInstances execInstances = DAOFactory.getExecInstancesDAO().lazyFind(resource.getUri().split("/"+resource.getId())[0]);
        execInstances.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DB.store(execInstances.getLastModifiedTime());
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link ExecInstance} resource from the DataBase and validates the transaction
     * @Param the {@link ExecInstance} resource to delete
     */
    public void delete(ExecInstance resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link ExecInstance} resource from the DataBase without validating the transaction
     * @param resource - The {@link ExecInstance} resource to delete
     */
    public void lazyDelete(ExecInstance resource) {
        //delete subscriptions
        DAOFactory.getSubscriptionsDAO().lazyDelete(DAOFactory.getSubscriptionsDAO().lazyFind(resource.getSubscriptionsReference()));
        // Delete the resource
        DB.delete(resource);
        // Update the lastModifiedTime attribute of the parent
        ExecInstances execInstances = DAOFactory.getExecInstancesDAO().lazyFind(resource.getUri().split("/"+resource.getId())[0]);
        execInstances.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DB.store(execInstances.getLastModifiedTime());
    }
}
