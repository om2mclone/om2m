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
package org.eclipse.om2m.core.announcer;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.resource.AccessRightAnnc;
import org.eclipse.om2m.commons.resource.AnnounceTo;
import org.eclipse.om2m.commons.resource.AnyURIList;
import org.eclipse.om2m.commons.resource.ApplicationAnnc;
import org.eclipse.om2m.commons.resource.ContainerAnnc;
import org.eclipse.om2m.commons.resource.GroupAnnc;
import org.eclipse.om2m.commons.resource.Scl;
import org.eclipse.om2m.commons.resource.SearchStrings;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.commons.utils.XmlMapper;
import org.eclipse.om2m.core.comm.RestClient;
import org.eclipse.om2m.core.constants.Constants;
import org.eclipse.om2m.core.dao.DAOFactory;

/**
 *Announces/De-Announces resources for which the announcement attribute is activated for each Creation/Delete.
 *
 * @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         </ul>
 */

public class Announcer {
    /** Logger */
    private static Log LOGGER = LogFactory.getLog(Announcer.class);

    /**
     * Announces the created resource.
     * @param announceTo - sclId target.
     * @param uri - resource uri.
     * @param requestingEntity - requesting entity
     * @return
     */
    public AnnounceTo announce(AnnounceTo announceTo, String uri, SearchStrings searchStrings, String requestingEntity) {
        // Checks if the sclList contains a remote sclReference.
        if (!announceTo.getSclList().getReference().isEmpty()) {
            String resourceId = uri.split("/")[uri.split("/").length - 1];
            String parentUri = uri.split("/"+resourceId)[0];
            String parentId = parentUri.split("/")[parentUri.split("/").length - 1];

            // Retrieve the scls from the SclList without redundancies
            final ArrayList<String> uniqueReferencesList = new ArrayList<String>(
                    new HashSet<String>(announceTo.getSclList().getReference()));
            final AnyURIList sclnewList = new AnyURIList();
            final String decodedRequestingEntity = requestingEntity;

            String partialPath = null;
            String representation = null;

            // ApplicationAnnc
            if ("applications".equalsIgnoreCase(parentId)) {
                // scl/applications/App ==> distantScl/scls/scl/applications/AppAnnc
                ApplicationAnnc applicationAnnc = new ApplicationAnnc();
                applicationAnnc.setLink(uri);
                applicationAnnc.setId(resourceId+"Annc");
                applicationAnnc.setSearchStrings(searchStrings);
                representation = XmlMapper.getInstance().objectToXml(applicationAnnc);
                // Parent ApplicationAnnc partial path
                partialPath = "/scls/"+Constants.SCL_ID+"/applications";
            }
            // ContainerAnnc
            if ("containers".equalsIgnoreCase(parentId)) {
                ContainerAnnc containerAnnc = new ContainerAnnc();
                containerAnnc.setLink(uri);
                containerAnnc.setId(resourceId + "Annc");
                containerAnnc.setSearchStrings(searchStrings);
                representation = XmlMapper.getInstance().objectToXml(containerAnnc);
                // Parent ContainerAnnc partial path
                if((Constants.SCL_ID+"/containers").equalsIgnoreCase(parentUri)) {
                    // scl/containers/Container ==> distantScl/scls/scl/containers
                    partialPath = "/scls/"+Constants.SCL_ID+"/containers";
                } else {
                    // scl/applications/App/containers/Container ==> distantScl/scls/scl/applications/AppAnnc/containers
                    partialPath = "/scls/" + uri.split("/containers")[0]+"Annc"+"/containers";
                }
            }
            // AccessRightAnnc
            if ("accessRights".equalsIgnoreCase(parentId)) {
                AccessRightAnnc accessRightAnnc = new AccessRightAnnc();
                accessRightAnnc.setLink(uri);
                accessRightAnnc.setId(resourceId + "Annc");
                accessRightAnnc.setSearchStrings(searchStrings);
                representation = XmlMapper.getInstance().objectToXml(accessRightAnnc);
                // Parent AccessRightAnnc partial path
                if((Constants.SCL_ID+"/accessRights").equalsIgnoreCase(parentUri)) {
                    // scl/accessRights/AccessRight ==> -distantScl/scls/scl/accessRights
                    partialPath = "/scls/"+Constants.SCL_ID+"/accessRights";
                } else {
                    // scl/applications/App/accessRights/AccessRight ==> distantScl/scls/scl/applications/AppAnnc/accessRights
                    partialPath = "/scls/" + uri.split("/accessRights")[0]+"Annc"+"/accessRights";
                }
            }
            // GroupAnnc
            if ("groups".equalsIgnoreCase(parentId)) {
                GroupAnnc groupAnnc = new GroupAnnc();
                groupAnnc.setLink(uri);
                groupAnnc.setId(resourceId + "Annc");
                groupAnnc.setSearchStrings(searchStrings);
                representation = XmlMapper.getInstance().objectToXml(groupAnnc);
                // Parent GroupAnnc partial path
                if((Constants.SCL_ID+"/groups").equalsIgnoreCase(parentUri)) {
                    // scl/groups/Group ==> distantScl/scls/scl/groups
                    partialPath = "/scls/"+Constants.SCL_ID+"/groups";
                } else {
                    // scl/applications/App/groups/Group ==> distantScl/scls/scl/applications/AppAnnc/groups
                    partialPath = "/scls/" + uri.split("/groups")[0]+"Annc"+"/groups";
                }
            }

            final String resourceAnncRepresentation = representation;
            // Send to the remote scls
            for (int i=uniqueReferencesList.size()-1; i>=0; i--) {
                final String hostingScl = uniqueReferencesList.get(i);
                final String hostingSclURI = Constants.SCL_ID+"/scls/"+hostingScl;
                final Scl scl = DAOFactory.getSclDAO().find(hostingSclURI);

                if (scl != null) {
                    final String targetId = scl.getLink() + partialPath;
                    new Thread() {
                        public void run() {
                            // Set the request Base
                            String base = scl.getPocs().getReference().get(0)+ "/";
                            sclnewList.getReference().add(hostingScl);
                            // Set the Request
                            RequestIndication requestIndication = new RequestIndication();
                            requestIndication.setMethod("CREATE");
                            requestIndication.setRequestingEntity(decodedRequestingEntity);
                            requestIndication.setTargetID(targetId);
                            requestIndication.setRepresentation(resourceAnncRepresentation);
                            requestIndication.setBase(base);

                            LOGGER.info("Annoncement Request\n:"+requestIndication);
                            // Send the request
                            ResponseConfirm response = new RestClient().sendRequest(requestIndication);
                            LOGGER.info("Annoncement Response:\n"+response.toString());
                        }
                    }.start();
                } else {
                    // Remove unregistered SCL from sclList
                    uniqueReferencesList.remove(uniqueReferencesList.indexOf(uniqueReferencesList.get(i)));
                }
            }
            announceTo.setSclList(sclnewList);
        }
        return announceTo;
    }


    /**
     * De-Announces the deleted resource.
     * @param announceTo - sclId target .
     * @param uri - resource uri.
     * @param requestingEntity - Requesting Entity
     */
    public void deAnnounce(AnnounceTo announceTo, String uri,String requestingEntity) {
        // De-Announcement to remote scls in sclList
        if (!announceTo.getSclList().getReference().isEmpty()) {

            String resourceId = uri.split("/")[uri.split("/").length - 1];
            String parent = uri.split("/" + resourceId)[0];
            String parentId = parent.split("/")[parent.split("/").length - 1];
            String partialPath = null;

            final String resourceAnncId = resourceId + "Annc";
            // ApplicationAnnc
            if ("applications".equalsIgnoreCase(parentId)) {
                // e.g. /scls/scl/applications/AppAnnc
                partialPath = "/scls/"+Constants.SCL_ID+"/applications/"+resourceAnncId;
            }
            // ContainerAnnc
            if ("containers".equalsIgnoreCase(parentId)) {
                // AccessRightAnnc partial path
                if((Constants.SCL_ID+"/containers").equalsIgnoreCase(uri.split("/"+resourceId)[0])) {
                    // scl/containers/Container ==> -distantScl/scls/scl/containers/ContainerAnnc
                    partialPath = "/scls/"+Constants.SCL_ID+"/containers/"+resourceAnncId;
                } else {
                    // scl/applications/App/containers/Container ==> distantScl/scls/scl/applications/AppAnnc/containers/ContainerAnnc
                    partialPath = "/scls/"+uri.split("/containers")[0]+"Annc"+"/containers/"+resourceAnncId;
                }
            }
            // AccessRightAnnc
            if ("accessRights".equalsIgnoreCase(parentId)) {
                // AccessRightAnnc partial path
                if((Constants.SCL_ID+"/accessRights").equalsIgnoreCase(uri.split("/"+resourceId)[0])) {
                    // scl/accessRights/AccessRight ==> -distantScl/scls/scl/accessRights/AccessRightAnnc
                    partialPath = "/scls/"+Constants.SCL_ID+"/accessRights/"+resourceAnncId;
                } else {
                    // scl/applications/App/accessRights/AccessRight ==> distantScl/scls/scl/applications/AppAnnc/accessRights/AccessRightAnnc
                    partialPath = "/scls/" + uri.split("/accessRights")[0]+"Annc"+"/accessRights/"+resourceAnncId;
                }
            }
            // GroupAnnc
            if ("groups".equalsIgnoreCase(parentId)) {
                // GroupAnnc partial path
                if((Constants.SCL_ID+"/groups").equalsIgnoreCase(uri.split("/"+resourceId)[0])) {
                    // scl/groups/Group ==> distantScl/scls/scl/groups/GroupAnnc
                    partialPath = "/scls/"+Constants.SCL_ID+"/groups/"+resourceAnncId;
                } else {
                    // scl/applications/App/groups/Group ==> distantScl/scls/scl/applications/AppAnnc/groups/GroupAnnc
                    partialPath = "/scls/" + uri.split("/groups")[0]+"Annc"+"/groups/"+resourceAnncId;
                }
            }
            // Retrieve the scls from the SclList without redundancies
            final ArrayList<String> uniqueReferencesList = new ArrayList<String>(new HashSet<String>(announceTo.getSclList().getReference()));
            final String decodedRequestingIndication = requestingEntity;

            for (int i = uniqueReferencesList.size() - 1; i >= 0; i--) {
                final String hostingScl = uniqueReferencesList.get(i);
                final String hostingSclURI = Constants.SCL_ID+"/scls/"+hostingScl;
                final Scl scl = DAOFactory.getSclDAO().find(hostingSclURI);

                if (scl != null) {
                    final String targetId = scl.getLink()+partialPath;
                    new Thread() {
                        public void run() {
                            // Set the request Base
                            String base = scl.getPocs().getReference().get(0)+ "/";
                            // Set the Request
                            RequestIndication requestIndication = new RequestIndication();
                            requestIndication.setMethod("DELETE");
                            requestIndication.setRequestingEntity(decodedRequestingIndication);
                            requestIndication.setTargetID(targetId);
                            requestIndication.setBase(base);

                            LOGGER.info("Annoncement Request:\n"+ requestIndication);
                            // Send the Request
                            ResponseConfirm response = new RestClient().sendRequest( requestIndication);
                            LOGGER.info("Annoucement Response:\n"+ response);
                        }
                    }.start();
                }
            }
        }
    }
}
