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

import java.util.List;

import org.eclipse.om2m.commons.resource.Resource;
import org.eclipse.om2m.commons.resource.Resources;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link Resources} persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.Feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class ResourcesDAO extends DAO<Resources>{

    @Override
    public void create(Resources resource) {
    }

    /**
     * Retrieves resources based on its uri
     * @param uri - uri of the {@link Resource} or beginning with
     * @return The requested {@link Resources} otherwise null
     */
    public Resources find(String uri) {
        // Create the query based on the uri constraint
        Query query = DB.query();
        query.constrain(Resource.class);
        if(uri != null){
            // Store all the founded resources
            query.descend("uri").constrain(uri).startsWith(false);
        }
        ObjectSet<Resource> result = query.execute();
        Resources resources = new Resources();
        resources.setResources((List<Resource>)result);
        return resources;
    }

    public Resources lazyFind(String uri) {
        return find(uri);
    }

    @Override
    public void update(Resources resource) {
    }

    @Override
    public void delete(Resources resource) {
    }

    @Override
    public void lazyDelete(Resources resource) {
    }
}
