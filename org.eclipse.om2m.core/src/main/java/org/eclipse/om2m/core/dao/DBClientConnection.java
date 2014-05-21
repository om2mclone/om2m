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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.resource.APoCPath;
import org.eclipse.om2m.commons.resource.APoCPaths;
import org.eclipse.om2m.commons.resource.AnnounceTo;
import org.eclipse.om2m.commons.resource.AnyURIList;
import org.eclipse.om2m.commons.resource.ApplicationIDs;
import org.eclipse.om2m.commons.resource.Applications;
import org.eclipse.om2m.commons.resource.Containers;
import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.commons.resource.ContentInstanceCollection;
import org.eclipse.om2m.commons.resource.ContentInstances;
import org.eclipse.om2m.commons.resource.ContentTypes;
import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.FilterCriteriaType;
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
import org.eclipse.om2m.commons.resource.SclIDs;
import org.eclipse.om2m.commons.resource.SearchStrings;
import org.eclipse.om2m.commons.resource.TrpdtType;
import org.eclipse.om2m.core.constants.Constants;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
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
            EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();

            configuration.common().objectClass(Resource.class).objectField("uri").indexed(true);
            configuration.common().updateDepth(7);
            configuration.common().activationDepth(7);
            configuration.common().objectClass(Containers.class).maximumActivationDepth(0);
            configuration.common().objectClass(ContentInstances.class).maximumActivationDepth(0);
            configuration.common().objectClass(Applications.class).maximumActivationDepth(0);

            configuration.common().objectClass(Resource.class).cascadeOnDelete(true);
            configuration.common().objectClass(SearchStrings.class).cascadeOnDelete(true);
            configuration.common().objectClass(PermissionListType.class).cascadeOnDelete(true);
            configuration.common().objectClass(PermissionType.class).cascadeOnDelete(true);
            configuration.common().objectClass(PermissionHolderType.class).cascadeOnDelete(true);
            configuration.common().objectClass(HolderRefListType.class).cascadeOnDelete(true);
            configuration.common().objectClass(ApplicationIDs.class).cascadeOnDelete(true);
            configuration.common().objectClass(SclIDs.class).cascadeOnDelete(true);
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

            try {
                db = Db4oEmbedded.openFile(configuration, Constants.DB_FILE);
            } catch (Db4oException e) {
                LOGGER.error("Database File locked",e);
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
}
