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

import org.eclipse.om2m.commons.resource.Application;
import org.eclipse.om2m.commons.resource.ApplicationAnnc;
import org.eclipse.om2m.commons.resource.Applications;
import org.eclipse.om2m.commons.resource.MgmtObjs;
import org.eclipse.om2m.commons.resource.ReferenceToNamedResource;
import org.eclipse.om2m.commons.resource.Subscriptions;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link Applications} collection resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class ApplicationsDAO extends DAO<Applications> {

    /**
     * Creates an {@link Applications} collection resource in the DataBase.
     * @param resource - The {@link Applications} collection resource to create
     */
    public void create(Applications resource) {
        // Add sub-collections References
        resource.setMgmtObjsReference(resource.getUri()+"/mgmtObjs");
        resource.setSubscriptionsReference(resource.getUri()+"/subscriptions");
        // Store the created resource
        DB.store(resource);

        // Subscriptions
        Subscriptions subscriptions = new Subscriptions();
        subscriptions.setUri(resource.getSubscriptionsReference());
        DAOFactory.getSubscriptionsDAO().create(subscriptions);
        // MgmtObjs
        MgmtObjs mgmtObjs = new MgmtObjs();
        mgmtObjs.setUri(resource.getMgmtObjsReference());
        mgmtObjs.setCreationTime(resource.getCreationTime());
        mgmtObjs.setLastModifiedTime(resource.getLastModifiedTime());
        mgmtObjs.setAccessRightID(resource.getAccessRightID());
        DAOFactory.getMgmtObjsDAO().create(mgmtObjs);
    }

    /**
     * Retrieves the {@link Applications} collection resource based on its uri with sub-resources references
     * @param uri - uri of the {@link Applications} collection resource
     * @return The requested {@link Applications} collection resource otherwise null
     */
    public Applications find(String uri) {
        Applications applications = lazyFind(uri);

        if(applications != null) {
            // Find Application sub-resources and add their references
            applications.getApplicationCollection().getNamedReference().clear();
            Query queryApplication = DB.query();
            queryApplication.constrain(Application.class);
            queryApplication.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<Application> resultApplication = queryApplication.execute();

            for (int i=0; i<resultApplication.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(resultApplication.get(i).getAppId());
                reference.setValue(resultApplication.get(i).getUri());
                applications.getApplicationCollection().getNamedReference().add(reference);
            }
            // Find ApplicationAnnc sub-resources and add their references
            applications.getApplicationAnncCollection().getNamedReference().clear();
            Query queryApplicationAnnc = DB.query();
            queryApplicationAnnc.constrain(ApplicationAnnc.class);
            queryApplicationAnnc.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<ApplicationAnnc> resultApplicationAnnc = queryApplicationAnnc.execute();

            for (int i = 0; i < resultApplicationAnnc.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(resultApplicationAnnc.get(i).getId());
                reference.setValue(resultApplicationAnnc.get(i).getUri());
                applications.getApplicationAnncCollection().getNamedReference().add(reference);
            }
        }
        return applications;
    }

    /**
     * Retrieves the {@link Applications} collection resource based on its uri without sub-resources references.
     * @param uri - uri of the {@link Applications} collection resource
     * @return The requested {@link Applications} collection resource otherwise null
     */
    public Applications lazyFind(String uri) {
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(Applications.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<Applications> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if(!result.isEmpty()){
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Updates an existing {@link Applications} collection resource in the DataBase
     * @param resource - The {@link Applications} the updated resource
     */
    public void update(Applications resource) {
        // Store the updated resource
        DB.store(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link Applications} collection resource from the DataBase and validates the transaction
     * @Param the {@link Applications} collection resource to delete
     */
    public void delete(Applications resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link Applications} collection resource from the DataBase without validating the transaction
     * @Param the {@link Applications} collection resource to delete
     */
    public void lazyDelete(Applications resource) {
        // Delete subscriptions
        DAOFactory.getSubscriptionsDAO().lazyDelete(DAOFactory.getSubscriptionsDAO().lazyFind(resource.getSubscriptionsReference()));
        // Delete mgmtObjs
        DAOFactory.getMgmtObjsDAO().lazyDelete(DAOFactory.getMgmtObjsDAO().lazyFind(resource.getMgmtObjsReference()));

        // Delete application sub-resources
        Query queryApplication = DB.query();
        queryApplication.constrain(Application.class);
        queryApplication.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<Application> resultApplication = queryApplication.execute();

        for (int i = 0; i < resultApplication.size(); i++) {
            DAOFactory.getApplicationDAO().lazyDelete(resultApplication.get(i));
        }
        // Delete applicationAnnc sub-resources
        Query queryApplicationAnnc = DB.query();
        queryApplicationAnnc.constrain(ApplicationAnnc.class);
        queryApplicationAnnc.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<ApplicationAnnc> resultApplicationAnnc = queryApplicationAnnc.execute();

        for (int i = 0; i < resultApplicationAnnc.size(); i++) {
            DAOFactory.getApplicationAnncDAO().lazyDelete(resultApplicationAnnc.get(i));
        }
        // Delete the resource
        DB.delete(resource);
    }
}
