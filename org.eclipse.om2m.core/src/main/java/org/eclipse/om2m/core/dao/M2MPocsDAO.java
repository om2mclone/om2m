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

import org.eclipse.om2m.commons.resource.M2MPoc;
import org.eclipse.om2m.commons.resource.M2MPocs;
import org.eclipse.om2m.commons.resource.ReferenceToNamedResource;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link M2mPocs} collection resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class M2MPocsDAO extends DAO<M2MPocs> {

    /**
     * Creates an {@link M2MPocs} collection resource in the DataBase.
     * @param resource - The {@link M2MPocs} collection resource to create
     */
    public void create(M2MPocs resource) {
        // Store the created resource
        DB.store(resource);
    }

    /**
     * Retrieves the {@link M2MPocs} collection resource based on its uri with sub-resources references
     * @param uri - uri of the {@link M2MPocs} collection resource
     * @return The requested {@link M2MPocs} collection resource otherwise null
     */
    public M2MPocs find(String uri) {
        M2MPocs m2mPocs = lazyFind(uri);

        if (m2mPocs != null){
        	ObjectContainer session = DB.ext().openSession();

            //Find M2MPoc sub-resources and add their references
            m2mPocs.getM2MPocCollection().getNamedReference().clear();
            Query query = session.query();
            query.constrain(M2MPoc.class);
            query.descend("uri").constrain(uri).startsWith(true);
            ObjectSet<M2MPoc> result = query.execute();

            for (int i = 0; i < result.size(); i++) {
                ReferenceToNamedResource reference = new ReferenceToNamedResource();
                reference.setId(result.get(i).getId());
                reference.setValue(result.get(i).getUri());
                m2mPocs.getM2MPocCollection().getNamedReference().add(reference);
            }
        }
        return m2mPocs;
    }

    /**
     * Retrieves the {@link M2MPocs} collection resource based on its uri without sub-resources references.
     * @param uri - uri of the {@link M2MPocs} collection resource
     * @return The requested {@link M2MPocs} collection resource otherwise null
     */
    public M2MPocs lazyFind(String uri) {
    	ObjectContainer session = DB.ext().openSession();

        // Create the query based on the uri constraint
        Query query = session.query();
        query.constrain(M2MPocs.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<M2MPocs> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if(!result.isEmpty()){
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Updates an existing {@link M2MPocs} collection resource in the DataBase
     * @param resource - The {@link M2MPocs} the updated resource
     */
    public void update(M2MPocs resource) {
        // Store the updated resource
        DB.store(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link M2MPocs} collection resource from the DataBase and validates the transaction
     * @Param the {@link M2MPocs} collection resource to delete
     */
    public void delete(M2MPocs resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link M2MPocs} collection resource from the DataBase without validating the transaction
     * @Param the {@link M2MPocs} collection resource to delete
     */
    public void lazyDelete(M2MPocs resource) {
        // Delete m2mPocs sub-resources
        Query query = DB.query();
        query.constrain(M2MPoc.class);
        query.descend("uri").constrain(resource.getUri()).startsWith(true);
        ObjectSet<M2MPoc> result = query.execute();

        for (int i = 0; i < result.size(); i++) {
            DAOFactory.getM2MPocDAO().lazyDelete(result.get(i));
        }
        // Delete the resource
        DB.delete(resource);
    }
}
