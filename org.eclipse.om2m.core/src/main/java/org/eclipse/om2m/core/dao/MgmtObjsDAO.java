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

import org.eclipse.om2m.commons.resource.MgmtCmd;
import org.eclipse.om2m.commons.resource.MgmtObj;
import org.eclipse.om2m.commons.resource.MgmtObjs;
import org.eclipse.om2m.commons.resource.ReferenceToNamedResource;
import org.eclipse.om2m.commons.resource.Subscriptions;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link MgmtObjs} collection resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class MgmtObjsDAO extends DAO<MgmtObjs> {

    /**
     * Creates an {@link MgmtObjs} collection resource in the DataBase.
     * @param resource - The {@link MgmtObjs} collection resource to create
     */
    public void create(MgmtObjs resource) {
        //Set subscriptions reference
        resource.setSubscriptionsReference(resource.getUri()+"/subscriptions");
        // Store the created resource
        DB.store(resource);

        // Subscriptions
        Subscriptions subscriptions = new Subscriptions();
        subscriptions.setUri(resource.getSubscriptionsReference());
        DAOFactory.getSubscriptionsDAO().create(subscriptions);
    }

    /**
     * Retrieves the {@link MgmtObjs} collection resource based on its uri with sub-resources references
     * @param uri - uri of the {@link MgmtObjs} collection resource
     * @return The requested {@link MgmtObjs} collection resource otherwise null
     */
    public MgmtObjs find(String uri) {
        MgmtObjs mgmtObjs = lazyFind(uri);

        if(mgmtObjs != null){
            // Find mgmtObj sub-resources and add their references
            Query queryMgmtObj = DB.query();
            queryMgmtObj.constrain(MgmtObj.class);
            queryMgmtObj.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<MgmtObj> resultMgmtObj = queryMgmtObj.execute();
            mgmtObjs.getMgmtObjCollection().getNamedReference().clear();

            for (int i = 0; i < resultMgmtObj.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(resultMgmtObj.get(i).getId());
                reference.setValue(resultMgmtObj.get(i).getUri());
                mgmtObjs.getMgmtObjCollection().getNamedReference().add(reference);
            }

            // Find mgmtCmd sub-resources and add their references
            Query queryMgmtCmd = DB.query();
            queryMgmtCmd.constrain(MgmtCmd.class);
            queryMgmtCmd.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<MgmtCmd> resultMgmtCmd = queryMgmtCmd.execute();
            mgmtObjs.getMgmtCmdCollection().getNamedReference().clear();

            for (int i = 0; i < resultMgmtCmd.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(resultMgmtCmd.get(i).getId());
                reference.setValue(resultMgmtCmd.get(i).getUri());
                mgmtObjs.getMgmtObjCollection().getNamedReference().add(reference);
            }
        }
        return mgmtObjs;
    }

    /**
     * Retrieves the {@link MgmtObjs} collection resource based on its uri without sub-resources references.
     * @param uri - uri of the {@link MgmtObjs} collection resource
     * @return The requested {@link MgmtObjs} collection resource otherwise null
     */
    public MgmtObjs lazyFind(String uri) {
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(MgmtObjs.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<MgmtObjs> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if (!result.isEmpty()) {
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Updates an existing {@link MgmtObjs} collection resource in the DataBase
     * @param resource - The {@link MgmtObjs} the updated resource
     */
    public void update(MgmtObjs resource) {
        // Store the updated resource
        DB.store(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link MgmtObjs} collection resource from the DataBase and validates the transaction
     * @Param the {@link MgmtObjs} collection resource to delete
     */
    public void delete(MgmtObjs resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link MgmtObjs} collection resource from the DataBase without validating the transaction
     * @Param the {@link MgmtObjs} collection resource to delete
     */
    public void lazyDelete(MgmtObjs resource) {
        // Delete subscriptions
        DAOFactory.getSubscriptionsDAO().lazyDelete(DAOFactory.getSubscriptionsDAO().lazyFind(resource.getSubscriptionsReference()));
        // Delete mgmtObj sub-resources
        Query queryMgmtObj = DB.query();
        queryMgmtObj.constrain(MgmtObj.class);
        queryMgmtObj.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<MgmtObj> resultMgmtObj = queryMgmtObj.execute();

        for (int i = 0; i < resultMgmtObj.size(); i++) {
            DAOFactory.getMgmtObjDAO().lazyDelete(resultMgmtObj.get(i));
        }

        // Delete mgmtCmd sub-resources
        Query queryMgmtCmd = DB.query();
        queryMgmtCmd.constrain(MgmtCmd.class);
        queryMgmtCmd.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<MgmtCmd> resultMgmtCmd = queryMgmtCmd.execute();

        for (int i = 0; i < resultMgmtCmd.size(); i++) {
            DAOFactory.getMgmtCmdDAO().lazyDelete(resultMgmtCmd.get(i));
        }
        // Delete the resource
        DB.delete(resource);
    }
}
