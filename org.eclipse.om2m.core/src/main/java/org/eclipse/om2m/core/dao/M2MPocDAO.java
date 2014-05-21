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

import org.eclipse.om2m.commons.resource.M2MPoc;
import org.eclipse.om2m.commons.resource.M2MPocs;
import org.eclipse.om2m.commons.utils.DateConverter;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link M2mPoc} resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class M2MPocDAO extends DAO<M2MPoc> {

    /**
     * Creates an {@link M2MPoc} resource in the DataBase and validates the transaction
     * @param resource - The {@link M2MPoc} resource to create
     */
    public void create(M2MPoc resource) {
        // Store the created resource
        DB.store(resource);
        // Update the lastModifiedTime attribute of the parent
        M2MPocs m2mPocs = DAOFactory.getM2MPocsDAO().lazyFind(resource.getUri().split("/"+resource.getId())[0]);
        m2mPocs.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DB.store(m2mPocs.getLastModifiedTime());
        // Validate the current transaction
        commit();
    }

    /**
     * Retrieves the {@link M2MPoc} resource from the Database based on its uri
     * @param uri - uri of the {@link M2MPoc} resource to retrieve
     * @return The requested {@link M2MPoc} resource otherwise null
     */
    public M2MPoc find(String uri) {
        // Create the query based on the uri constraint
        Query query=DB.query();
        query.constrain(M2MPoc.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<M2MPoc> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if(!result.isEmpty()){
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Retrieves the {@link M2MPoc} resource from the Database based on the uri
     * @param uri - uri of the {@link M2MPoc} resource
     * @return The requested {@link M2MPoc} resource otherwise null
     */
    public M2MPoc lazyFind(String uri) {
        return find(uri);
    }

    /**
     * Updates an existing {@link M2MPoc} resource in the DataBase
     * @param resource - The {@link M2MPoc} the updated resource
     */
    public void update(M2MPoc resource) {
        // Store the updated resource
        DB.store(resource);

        // Update the lastModifiedTime attribute of the parent
        M2MPocs m2mPocs = DAOFactory.getM2MPocsDAO().lazyFind(resource.getUri().split("/"+resource.getId())[0]);
        m2mPocs.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DB.store(m2mPocs.getLastModifiedTime());
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link M2MPoc} resource from the DataBase and validates the transaction
     * @Param the {@link M2MPoc} resource to delete
     */
    public void delete(M2MPoc resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link M2MPoc} resource from the DataBase without validating the transaction
     * @param resource - The {@link M2MPoc} resource to delete
     */
    public void lazyDelete(M2MPoc resource) {
        // Delete the resource
        DB.delete(resource);

        // Update the lastModifiedTime attribute of the parent
        M2MPocs m2mPocs = DAOFactory.getM2MPocsDAO().lazyFind(resource.getUri().split("/"+resource.getId())[0]);
        m2mPocs.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
        DB.store(m2mPocs.getLastModifiedTime());
    }
}
