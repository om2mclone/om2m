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
package org.eclipse.om2m.core.controller;

import java.util.Collections;
import java.util.List;

import org.eclipse.om2m.commons.resource.Discovery;
import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.FilterCriteriaType;
import org.eclipse.om2m.commons.resource.Resource;
import org.eclipse.om2m.commons.resource.Resources;
import org.eclipse.om2m.commons.resource.SclBase;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.core.constants.Constants;
import org.eclipse.om2m.core.dao.DAOFactory;

/**
 * Implements discovery method to perform a discovery of resources.
 *
 * @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         </ul>
 */

public class DiscoveryController extends Controller {

    /**
     * Creates {@link Discovery} resource. It is not allowed Through the API.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doCreate (RequestIndication requestIndication) {

        // Response
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed"));
    }

    /**
     * Retrieves {@link Discovery} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doRetrieve (RequestIndication requestIndication) {

        // matchSize:       (response O)
        // truncated:       (response O)
        // discoveryURI:    (response M)

        ResponseConfirm errorResponse = new ResponseConfirm();

        // Check AccessRight
        SclBase sclBase = DAOFactory.getSclBaseDAO().find(Constants.SCL_ID);
        errorResponse = checkAccessRight(sclBase.getAccessRightID(), requestIndication.getRequestingEntity(), Constants.AR_DISCOVER);
        if (errorResponse != null) {
            return errorResponse;
        }

        // Response
        // Initiate Parameters
        FilterCriteriaType filterCriteriaType = new FilterCriteriaType();
        String searchPrefix = "";
        int maxSize = -1;

        if (requestIndication.getParameters() != null) {
            // Set searchPrefix
            if (requestIndication.getParameters().get("searchPrefix") != null && requestIndication.getParameters().get("searchPrefix").get(0) != null) {
                searchPrefix = requestIndication.getParameters().get("searchPrefix").get(0);
                if (searchPrefix.endsWith("/")) {
                    searchPrefix = searchPrefix.substring(0,searchPrefix.length()-1);
                }
            }
            // Set maxSize
            if (requestIndication.getParameters().get("maxSize") != null && requestIndication.getParameters().get("maxSize").get(0) != null) {
                if (requestIndication.getParameters().get("maxSize").get(0).matches("-?\\d+")) {
                    maxSize = Integer.parseInt(requestIndication.getParameters().get("maxSize").get(0));
                }else{
                    return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"maxSize Must be an Integer")) ;
                }
            }
            // Set searchString
            if (requestIndication.getParameters().get("searchString") != null && requestIndication.getParameters().get("searchString").get(0) != null) {
                filterCriteriaType.getIfMatch().addAll(requestIndication.getParameters().get("searchString"));
                filterCriteriaType.getIfMatch().removeAll(Collections.singleton(null));
            }
        }
        // Response
        return new ResponseConfirm(StatusCode.STATUS_OK, discover(searchPrefix, maxSize, filterCriteriaType));


    }

    /**
     * Updates {@link Discovery} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm  doUpdate (RequestIndication requestIndication) {

        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed"));
    }

    /**
     * Deletes {@link Discovery} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doDelete (RequestIndication requestIndication)  {

        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed"));
    }

    /**
     * Executes {@link Discovery} resource.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doExecute (RequestIndication requestIndication) {

        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed"));
    }

    /**
     * discover method allows to discover resource depending on the parameters.
     * @param searchPrefix
     * @param maxSize
     * @param filterCriteriaType
     * @return discovery object
     */
    public static Discovery discover (String searchPrefix, int maxSize, FilterCriteriaType filterCriteriaType) {
        Discovery discovery = new Discovery();
        // Retrieve all resources
        Resources resourcesList = DAOFactory.getResourcesDAO().find(searchPrefix);
        List<Resource> resources = resourcesList.getResources();
        // Research
        for (int i=0; i<resources.size(); i++) {
            // if FilterCriteria is not empty
            if (!filterCriteriaType.getIfMatch().isEmpty()) {
                // ifMatch To Lower Case
                for (int k=0; k<filterCriteriaType.getIfMatch().size(); k++) {
                    filterCriteriaType.getIfMatch().set(k, filterCriteriaType.getIfMatch().get(k).toLowerCase());
                }
                if (resources.get(i).getSearchStrings() != null) {
                    // To lower case
                    for (int k=0; k<resources.get(i).getSearchStrings().getSearchString().size(); k++) {
                        resources.get(i).getSearchStrings().getSearchString().set(k, resources.get(i).getSearchStrings().getSearchString().get(k).toLowerCase());
                    }
                    // Compare resource searchString with ifMatch
                    if (resources.get(i).getSearchStrings().getSearchString().containsAll(filterCriteriaType.getIfMatch())) {
                        //infinity maxSize OR not yet reached
                        if (maxSize < 0 || discovery.getDiscoveryURI().getReference().size() < maxSize) {
                            // Add the resource URI to the discovered references list
                            discovery.getDiscoveryURI().getReference().add(resources.get(i).getUri());
                        }else {
                            // Break if the maxSize is reached
                            discovery.setTruncated(true);
                            break;
                        }
                    }
                }
            }else {
                // Empty FilterCriteria
                // infinity maxSize OR not yet reached
                if (maxSize<0 || discovery.getDiscoveryURI().getReference().size() < maxSize) {
                    // Add the resource URI to the discovered references list
                    discovery.getDiscoveryURI().getReference().add(resources.get(i).getUri());
                }else{
                    // Break if the maxSize is reached
                    discovery.setTruncated(true);
                    break;
                }
            }
        }
        // Set discovery matchSize
        discovery.setMatchSize((long)discovery.getDiscoveryURI().getReference().size());
        // Response
        return discovery;
    }
}
