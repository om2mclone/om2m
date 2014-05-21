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

import org.eclipse.om2m.commons.resource.MgmtObj;
import org.eclipse.om2m.commons.resource.Parameters;
import org.eclipse.om2m.commons.resource.ReferenceToNamedResource;
import org.eclipse.om2m.commons.resource.Subscriptions;
import org.eclipse.om2m.commons.utils.DateConverter;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * Implements CRUD Methods for {@link Parameters} collection resource persistence.
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class ParametersDAO extends DAO<Parameters> {

    /**
     * Creates an {@link Parameters} collection resource in the DataBase.
     * @param resource - The {@link Parameters} collection resource to create
     */
    public void create(Parameters resource) {
        //Set subscriptions
        resource.setSubscriptionsReference(resource.getUri()+"/subscriptions");
        // Store the created resource
        DB.store(resource);

        // Subscriptions
        Subscriptions subscriptions = new Subscriptions();
        subscriptions.setUri(resource.getSubscriptionsReference());
        DAOFactory.getSubscriptionsDAO().create(subscriptions);

        //Add mgmtObj reference to mgmtObjCollection
        String target = resource.getUri().split("/"+resource.getId())[0];
        String[] parameter = target.split("mgmtObjs/");
        ReferenceToNamedResource reference = new ReferenceToNamedResource();
        reference.setId(resource.getId());
        reference.setValue(resource.getUri());

        if (!parameter[1].contains("/")){
            MgmtObj mgmtObj = DAOFactory.getMgmtObjDAO().find(target);
            mgmtObj.getParametersCollection().getNamedReference().add(reference);
            mgmtObj.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
            DAOFactory.getMgmtObjDAO().update(mgmtObj);
        } else {
            Parameters parameters = DAOFactory.getParametersDAO().find(target);
            parameters.getParametersCollection().getNamedReference().add(reference);
            parameters.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()));
            DAOFactory.getParametersDAO().update(parameters);
        }
    }

    /**
     * Retrieves the {@link Parameters} collection resource based on its uri with sub-resources references
     * @param uri - uri of the {@link Parameters} collection resource
     * @return The requested {@link Parameters} collection resource otherwise null
     */
    public Parameters find(String uri) {
        // Create the query based on the uri constraint
        Query query=DB.query();
        query.constrain(Parameters.class);
        query.descend("uri").constrain(uri);
        // Store all the founded resources
        ObjectSet<Parameters> result = query.execute();
        // Retrieve the first element corresponding to the researched resource if result is not empty
        if(!result.isEmpty()){
            return result.get(0);
        }
        // Return null if the resource is not found
        return null;
    }

    /**
     * Retrieves the {@link Parameters} collection resource based on its uri without sub-resources references.
     * @param uri - uri of the {@link Parameters} collection resource
     * @return The requested {@link Parameters} collection resource otherwise null
     */
    public Parameters lazyFind(String uri) {
        return find(uri);
    }

    /**
     * Updates an existing {@link Parameters} collection resource in the DataBase
     * @param resource - The {@link Parameters} the updated resource
     */
    public void update(Parameters resource) {
        // Store the updated resource
        DB.store(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link Parameters} collection resource from the DataBase and validates the transaction
     * @Param the {@link Parameters} collection resource to delete
     */
    public void delete(Parameters resource) {
        // Delete the resource
        lazyDelete(resource);
        // Validate the current transaction
        commit();
    }

    /**
     * Deletes the {@link Parameters} collection resource from the DataBase without validating the transaction
     * @Param the {@link Parameters} collection resource to delete
     */
    public void lazyDelete(Parameters resource) {
        // Delete subscriptions
        DAOFactory.getSubscriptionsDAO().lazyDelete(DAOFactory.getSubscriptionsDAO().lazyFind(resource.getSubscriptionsReference()));

        // Delete parametersCollection
        while (resource.getParametersCollection().getNamedReference().size() !=0){
            ReferenceToNamedResource reference =resource.getParametersCollection().getNamedReference().get(0);
            Parameters parameters = DAOFactory.getParametersDAO().lazyFind(reference.getValue());
            DAOFactory.getParametersDAO().lazyDelete(parameters);
        }

        String parentUri = resource.getUri().replace("/"+resource.getId(),"");
        Parameters parent = DAOFactory.getParametersDAO().lazyFind(parentUri);
        ReferenceToNamedResource reference = new ReferenceToNamedResource();
        reference.setValue(resource.getUri());
        ObjectSet<ReferenceToNamedResource> result = DB.queryByExample(reference);
        ReferenceToNamedResource reference1 = result.get(0);
        parent.getParametersCollection().getNamedReference().remove(reference1);
        DB.delete(reference1);
        // Delete the resource
        DB.delete(resource);
        DB.store(parent);
    }
}
