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

import org.eclipse.om2m.commons.resource.ExecInstances;
import org.eclipse.om2m.commons.resource.M2MPocs;
import org.eclipse.om2m.commons.resource.MgmtCmd;
import org.eclipse.om2m.commons.resource.MgmtObjs;
import org.eclipse.om2m.commons.resource.Subscriptions;
import org.eclipse.om2m.commons.utils.DateConverter;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link MgmtCmd} resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class MgmtCmdDAO extends DAO<MgmtCmd> {

    /**
     * Creates an {@link MgmtCmd} resource in the DataBase and validates the transaction
     * @param resource - The {@link MgmtCmd} resource to create
     */
    public void create(MgmtCmd resource) {
        // Store the created resource
        DB.store(resource);
        //Subscriptions
        Subscriptions subscriptions = new Subscriptions();
        subscriptions.setUri(resource.getSubscriptionsReference());
        DAOFactory.getSubscriptionsDAO().create(subscriptions);
        //ExecInstances
        ExecInstances execInstances = new ExecInstances();
        execInstances.setUri(resource.getExecInstancesReference());
        execInstances.setCreationTime(resource.getCreationTime());
        execInstances.setLastModifiedTime(resource.getLastModifiedTime());
        DAOFactory.getExecInstancesDAO().create(execInstances);
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(MgmtObjs.class);
        query.descend("uri").constrain(resource.getUri().split("/"+resource.getId())[0]);
        // Store all the founded resources
        ObjectSet<MgmtObjs> result = query.execute();
        
        // Update the lastModifiedTime attribute of the parent
        MgmtObjs mgmtObjs = result.get(0);
        // Update the lastModifiedTime attribute of the parent
        mgmtObjs.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        DB.store(mgmtObjs);
        // Validate the current transaction
        commit();
    }

    /**
     * Retrieves the {@link MgmtCmd} resource from the Database based on its uri
     * @param uri - uri of the {@link MgmtCmd} resource to retrieve
     * @return The requested {@link MgmtCmd} resource otherwise null
     */
    public MgmtCmd find(String uri) {
        // Create the query based on the uri constraint
        Query query=DB.query();
        query.constrain(MgmtCmd.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<MgmtCmd> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if(!result.isEmpty()){
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Retrieves the {@link MgmtCmd} resource from the Database based on the uri
     * @param uri - uri of the {@link MgmtCmd} resource
     * @return The requested {@link MgmtCmd} resource otherwise null
     */
    public MgmtCmd lazyFind(String uri) {
        return find(uri);
    }

    /**
     * Updates an existing {@link MgmtCmd} resource in the DataBase
     * @param resource - The {@link MgmtCmd} the updated resource
     */
    public void update(MgmtCmd resource) {
        // Store the updated resource
        DB.store(resource);
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(MgmtObjs.class);
        query.descend("uri").constrain(resource.getUri().split("/"+resource.getId())[0]);
        // Store all the founded resources
        ObjectSet<MgmtObjs> result = query.execute();
        
        // Update the lastModifiedTime attribute of the parent
        MgmtObjs mgmtObjs = result.get(0);
        // Update the lastModifiedTime attribute of the parent
        mgmtObjs.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        DB.store(mgmtObjs);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link MgmtCmd} resource from the DataBase and validates the transaction
     * @Param the {@link MgmtCmd} resource to delete
     */
    public void delete(MgmtCmd resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link MgmtCmd} resource from the DataBase without validating the transaction
     * @param resource - The {@link MgmtCmd} resource to delete
     */
    public void lazyDelete(MgmtCmd resource) {
        // Delete the resource
        DB.delete(resource);

        // Delete subscriptions
        DAOFactory.getSubscriptionsDAO().lazyDelete(DAOFactory.getSubscriptionsDAO().lazyFind(resource.getSubscriptionsReference()));
        // Delete ExecInstances
        DAOFactory.getExecInstancesDAO().lazyDelete(DAOFactory.getExecInstancesDAO().lazyFind(resource.getExecInstancesReference()));

     // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(MgmtObjs.class);
        query.descend("uri").constrain(resource.getUri().split("/"+resource.getId())[0]);
        // Store all the founded resources
        ObjectSet<MgmtObjs> result = query.execute();
        
        // Update the lastModifiedTime attribute of the parent
        MgmtObjs mgmtObjs = result.get(0);
        // Update the lastModifiedTime attribute of the parent
        mgmtObjs.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
        DB.store(mgmtObjs);
    }
}
