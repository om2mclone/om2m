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

import org.eclipse.om2m.commons.resource.MgmtObjs;
import org.eclipse.om2m.commons.resource.ReferenceToNamedResource;
import org.eclipse.om2m.commons.resource.Scl;
import org.eclipse.om2m.commons.resource.Scls;
import org.eclipse.om2m.commons.resource.Subscriptions;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link Scls} collection resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class SclsDAO extends DAO<Scls>{

    /**
     * Creates an {@link Scls} collection resource in the DataBase.
     * @param resource - The {@link Scls} collection resource to create
     */
    public void create(Scls resource) {
        // Add sub-collections References
        resource.setMgmtObjsReference(resource.getUri() + "/mgmtObjs");
        resource.setSubscriptionsReference(resource.getUri() + "/subscriptions");
        // Store the created resource
        DB.store(resource);
        // Subscriptions
        Subscriptions subscriptions = new Subscriptions();
        subscriptions.setUri(resource.getSubscriptionsReference());
        DAOFactory.getSubscriptionsDAO().create(subscriptions);
        // MgmtObjs
        // MgmtObjs
        MgmtObjs mgmtObjs = new MgmtObjs();
        mgmtObjs.setUri(resource.getMgmtObjsReference());
        mgmtObjs.setCreationTime(resource.getCreationTime());
        mgmtObjs.setLastModifiedTime(resource.getLastModifiedTime());
        mgmtObjs.setAccessRightID(resource.getAccessRightID());
        DAOFactory.getMgmtObjsDAO().create(mgmtObjs);
    }

    /**
     * Retrieves the {@link Scls} collection resource based on its uri with sub-resources references
     * @param uri - uri of the {@link Scls} collection resource
     * @return The requested {@link Scls} collection resource otherwise null
     */
    public Scls find(String uri){
        Scls scls = lazyFind(uri);

        if(scls != null) {
        	ObjectContainer session = DB.ext().openSession();

            // Find Scl sub-resources and add their references
            scls.getSclCollection().getNamedReference().clear();
            Query queryScl = session.query();
            queryScl.constrain(Scl.class);
            queryScl.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<Scl> resultScl = queryScl.execute();

            for (int i = 0; i < resultScl.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(resultScl.get(i).getSclId());
                reference.setValue(resultScl.get(i).getUri());
                scls.getSclCollection().getNamedReference().add(reference);
            }
        }
        return scls;
    }

    /**
     * Retrieves the {@link Scls} collection resource based on its uri without sub-resources references.
     * @param uri - uri of the {@link Scls} collection resource
     * @return The requested {@link Scls} collection resource otherwise null
     */
    public Scls lazyFind(String uri) {
    	ObjectContainer session = DB.ext().openSession();

        // Create the query based on the uri constraint
        Query query = session.query();
        query.constrain(Scls.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<Scls> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if(!result.isEmpty()){
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Updates an existing {@link Scls} collection resource in the DataBase
     * @param resource - The {@link Scls} the updated resource
     */
    public void update(Scls resource) {
        // Store the updated resource
        DB.store(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link Scls} collection resource from the DataBase and validates the transaction
     * @Param the {@link Scls} collection resource to delete
     */
    public void delete(Scls resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link Scls} collection resource from the DataBase without validating the transaction
     * @Param the {@link Scls} collection resource to delete
     */
    public void lazyDelete(Scls resource) {
        // Delete subscriptions
        DAOFactory.getSubscriptionsDAO().lazyDelete(DAOFactory.getSubscriptionsDAO().lazyFind(resource.getSubscriptionsReference()));
        // Delete mgmtObjs
        DAOFactory.getMgmtObjsDAO().lazyDelete(DAOFactory.getMgmtObjsDAO().lazyFind(resource.getMgmtObjsReference()));

        // Delete scl sub-resources
        Query queryScl = DB.query();
        queryScl.constrain(Scl.class);
        queryScl.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<Scl> resultScl = queryScl.execute();

        for (int i = 0; i < resultScl.size(); i++) {
            DAOFactory.getSclDAO().lazyDelete( resultScl.get(i));
        }
        // Delete the resource
        DB.delete(resource);
    }
}
