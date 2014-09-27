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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.resource.APoCPath;
import org.eclipse.om2m.commons.resource.APoCPaths;
import org.eclipse.om2m.commons.resource.AccessRights;
import org.eclipse.om2m.commons.resource.AnnounceTo;
import org.eclipse.om2m.commons.resource.AnyURIList;
import org.eclipse.om2m.commons.resource.Application;
import org.eclipse.om2m.commons.resource.Applications;
import org.eclipse.om2m.commons.resource.Containers;
import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.commons.resource.ContentInstanceCollection;
import org.eclipse.om2m.commons.resource.ContentInstances;
import org.eclipse.om2m.commons.resource.ContentTypes;
import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.FilterCriteriaType;
import org.eclipse.om2m.commons.resource.Groups;
import org.eclipse.om2m.commons.resource.HolderRefListType;
import org.eclipse.om2m.commons.resource.IntegrityValResults;
import org.eclipse.om2m.commons.resource.NamedReferenceCollection;
import org.eclipse.om2m.commons.resource.PermissionHolderType;
import org.eclipse.om2m.commons.resource.PermissionListType;
import org.eclipse.om2m.commons.resource.PermissionType;
import org.eclipse.om2m.commons.resource.RcatList;
import org.eclipse.om2m.commons.resource.ReferenceToNamedResource;
import org.eclipse.om2m.commons.resource.Resource;
import org.eclipse.om2m.commons.resource.Schedule;
import org.eclipse.om2m.commons.resource.Scl;
import org.eclipse.om2m.commons.resource.Scls;
import org.eclipse.om2m.commons.resource.SearchStrings;
import org.eclipse.om2m.commons.resource.Subscriptions;
import org.eclipse.om2m.commons.resource.TrpdtType;
import org.eclipse.om2m.core.constants.Constants;
import org.eclipse.om2m.core.router.Router;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.defragment.Defragment;
import com.db4o.defragment.DefragmentConfig;
import com.db4o.ext.Db4oException;


/**
 * DBClientConnection satisfy the singleton design pattern.
 * It is used to open a unique connection with dataBase.
 * A Client connection is opened to communicate with the database that can be local or distant.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */

public class DBClientConnection {
    private static Log LOGGER = LogFactory.getLog(DBClientConnection.class);

    private static ObjectContainer db;


    /**
     * Open the connection with the DataBase
     */
    private DBClientConnection(){
    		
            try {
                db = Db4oEmbedded.openFile(getConfiguration(true), Constants.DB_FILE);
            } catch (Db4oException e) {
                LOGGER.error("Database File locked",e);
            }
            if(Constants.DB_DEFRAGMENT_PERIOD!=-1){
            	LOGGER.error("Defragment DB enabled each "+ Constants.DB_DEFRAGMENT_PERIOD+" ms");
				new Thread() {
					public void run() {
						while (true) {
							try {
								Thread.sleep(Constants.DB_DEFRAGMENT_PERIOD);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
	
							Router.readWriteLock.writeLock().lock();
							LOGGER.info("DB Defragmenting..");
							DAO.DB.close();
							try {
								DefragmentConfig config = new DefragmentConfig(Constants.DB_FILE);
								config.forceBackupDelete(true);
								Defragment.defrag(config);
							} catch (IOException e) {
								e.printStackTrace();
							}
							DAO.DB = Db4oEmbedded.openFile(getConfiguration(false),
									Constants.DB_FILE);
							LOGGER.info("DB Defragmented");
							Router.readWriteLock.writeLock().unlock();
						}
					}
				}.start();
            }else{
            	LOGGER.error("Defragment DB disabled");
            }
    }

    /**
     * Gets a new instance of DBClientConnection.
     * @return object
     */
    public static ObjectContainer getInstance(){
        if(db == null){
            new DBClientConnection();
        }
        return db;
    }

    /**
     *  Closes DataBase connection.
     */
    public static void closeDataBase() {
        if (db != null) {
            db.close();
        }
    }
    
    public EmbeddedConfiguration getConfiguration(boolean isIndex){
    	EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().objectClass(Resource.class).objectField("uri").indexed(isIndex);
        configuration.common().updateDepth(7);
        configuration.common().activationDepth(7);
        configuration.common().objectClass(Application.class).cascadeOnUpdate(true);
        configuration.common().objectClass(Containers.class).maximumActivationDepth(0);
        configuration.common().objectClass(ContentInstances.class).maximumActivationDepth(0);
        configuration.common().objectClass(Applications.class).maximumActivationDepth(0);
        configuration.common().objectClass(Groups.class).maximumActivationDepth(0);
        configuration.common().objectClass(AccessRights.class).maximumActivationDepth(0);
        configuration.common().objectClass(Subscriptions.class).maximumActivationDepth(0);
        configuration.common().objectClass(Scls.class).maximumActivationDepth(0);
        configuration.common().objectClass(Resource.class).cascadeOnDelete(true);
        configuration.common().objectClass(SearchStrings.class).cascadeOnDelete(true);
        configuration.common().objectClass(PermissionListType.class).cascadeOnDelete(true);
        configuration.common().objectClass(PermissionType.class).cascadeOnDelete(true);
        configuration.common().objectClass(PermissionHolderType.class).cascadeOnDelete(true);
        configuration.common().objectClass(HolderRefListType.class).cascadeOnDelete(true);
        configuration.common().objectClass(Application.class).cascadeOnDelete(true);
        configuration.common().objectClass(Scl.class).cascadeOnDelete(true);
        configuration.common().objectClass(AnnounceTo.class).cascadeOnDelete(true);
        configuration.common().objectClass(AnyURIList.class).cascadeOnDelete(true);
        configuration.common().objectClass(APoCPath.class).cascadeOnDelete(true);
        configuration.common().objectClass(APoCPaths.class).cascadeOnDelete(true);
        configuration.common().objectClass(ContentTypes.class).cascadeOnDelete(true);
        configuration.common().objectClass(ErrorInfo.class).cascadeOnDelete(true);
        configuration.common().objectClass(FilterCriteriaType.class).cascadeOnDelete(true);
        configuration.common().objectClass(IntegrityValResults.class).cascadeOnDelete(true);
        configuration.common().objectClass(NamedReferenceCollection.class).cascadeOnDelete(true);
        configuration.common().objectClass(RcatList.class).cascadeOnDelete(true);
        configuration.common().objectClass(ReferenceToNamedResource.class).cascadeOnDelete(true);
        configuration.common().objectClass(Schedule.class).cascadeOnDelete(true);
        configuration.common().objectClass(TrpdtType.class).cascadeOnDelete(true);
        configuration.common().objectClass(ContentInstanceCollection.class).cascadeOnDelete(true);
        configuration.common().objectClass(ContentInstance.class).cascadeOnDelete(true);
        configuration.common().objectClass(ContentInstances.class).cascadeOnDelete(true);
        return configuration;
    }
}
