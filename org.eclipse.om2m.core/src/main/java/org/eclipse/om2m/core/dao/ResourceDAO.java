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

import org.eclipse.om2m.commons.resource.Resource;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class ResourceDAO extends DAO<Resource>{

    @Override
    public void create(Resource resource) {
    }

    /**
     * Retrieves the {@link Resource} based on its uri
     * @param uri - uri of the {@link Resource}
     * @return The requested {@link Resource} otherwise null
     */
    public Resource find(String uri) {
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(Resource.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<Resource> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if (!result.isEmpty()) {
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    public Resource lazyFind(String uri) {
        return find(uri);
    }

    @Override
    public void update(Resource resource) {
    }

    @Override
    public void delete(Resource resource) {
    }

    @Override
    public void lazyDelete(Resource resource) {
    }
}
