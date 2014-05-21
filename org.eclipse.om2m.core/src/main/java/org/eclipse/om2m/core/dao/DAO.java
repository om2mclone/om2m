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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.db4o.ObjectContainer;

/**
 * <p>
 * The DAO (Data Object Access) Pattern is used to make separation between data access layer and the business layer of an application.
 * It allows better control of changes that might be made on the system of data storage and therefore to prepare
 * a migration from one system to another (DB to XML files for example). This is done by separating data access (BDD)
 * and business objects (POJOs).
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */

public abstract class DAO<T> {
    private static Log LOGGER = LogFactory.getLog(DAO.class);
    public static final ObjectContainer DB = DBClientConnection.getInstance();

    /**
     * Abstract create resource method in database.
     * @param resource - The resource to create
     */
    public abstract void create (T resource);

    /**
     * Abstract find resource method in database based on its uri.
     * It returns collections with sub-resources references.
     * @param uri - The uri of the resource to find
     * @return The resource if it is found otherwise null
     */
    public abstract T find (String uri);

    /**
     * Abstract find resource method in database based on its uri.
     * It returns collections without sub-resources references.
     * @param uri - The uri of the resource to find
     * @return The resource if it is found otherwise null
     */
    public abstract T lazyFind (String uri);

    /**
     * Abstract update resource method in database.
     * @param resource - The updated resource.
     */
    public abstract void update (T resource);

    /**
     * Abstract delete resource method from the DataBase with validating the transaction.
     * @param resource - The resource to delete.
     */
    public abstract void delete (T resource);

    /**
     * Abstract delete resource method from the DataBase without validating the transaction.
     * @param resource - The resource to delete.
     */
    public abstract void lazyDelete (T resource);

    /**
     * Validates the transaction.
     */
    public void commit(){
        new Thread(){
            public void run(){
                DB.commit();
                LOGGER.info("Transaction committed successfully");
            }
        }.start();
    }
}
