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
 *      conception and documentation.
 *     Mahdi Ben Alaya (Project co-founder) - Management and initial specification,
 *      conception, implementation, test and documentation.
 *     Christophe Chassot - Management and initial specification.
 *     Khalil Drira - Management and initial specification.
 *     Yassine Banouar - Initial specification, conception, implementation, test
 *      and documentation.
 ******************************************************************************/
package org.eclipse.om2m.ipu.sample;

/**
 *  Provides different Lamps methods.
 *  @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         </ul>
 */
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.resource.AnyURIList;
import org.eclipse.om2m.commons.resource.Application;
import org.eclipse.om2m.commons.resource.Container;
import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.commons.resource.Group;
import org.eclipse.om2m.commons.resource.MemberType;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.core.service.SclService;

public class SampleMonitor {
    /** Logger */
    private static Log LOGGER = LogFactory.getLog(SampleMonitor.class);
    /** Sclbase id */
    public final static String SCLID = System.getProperty("org.eclipse.om2m.sclBaseId","");
    /** Admin requesting entity */
    static String REQENTITY = System.getProperty("org.eclipse.om2m.adminRequestingEntity","");
    /** Generic create method name */
    public final static String METHOD_CREATE = "CREATE";
    /** Generic execute method name */
    public final static String METHOD_EXECUTE = "EXECUTE";
    /** State container id */
    public final static String DATA = "DATA";
    /** Descriptor container id */
    public final static String DESC = "DESCRIPTOR";
    /** Discovered SCL service*/
    static SclService SCL;
    static Map<String, Lamp> LAMPS = new HashMap<String, Lamp>();

    /**
     * Constructor
     * @param scl - discovered SCL
     */
    public SampleMonitor(SclService scl) {
        SCL=scl;
    }

    /**
     * Starts monitoring and creating resources on the SCL
     */
    public void start() {
        LOGGER.info("Lamps waiting for attachement..");
        // Create initial resources for the 2 lamps
        for(int i=0; i<2; i++) {
            String lampId = Lamp.TYPE+"_"+i;
            LAMPS.put(lampId, new Lamp());
            createLampResources(lampId, false, Lamp.APOCPATH);
        }
        // Create an Application to switch all lamps
        postGroups(Lamp.APOCPATH, Lamp.TYPE, Lamp.LOCATION);
        GUI.init();
    }

    /**
     * Stops monitoring by closing the IPU GUI
     */
    public static void stop() {
        GUI.stop();
    }

    /**
     * Creates all required resources.
     * @param appId - Application ID
     * @param initValue - initial lamp value
     * @param aPoCPath - lamp aPocPath
     */
    public void createLampResources(String appId, boolean initValue, String aPoCPath) {
        // Create the Application resource
        ResponseConfirm response = SCL.doRequest(new RequestIndication(METHOD_CREATE,SCLID+"/applications",REQENTITY,new Application(appId,aPoCPath)));
        // Create Application sub-resources only if application not yet created
        if(response.getStatusCode().equals(StatusCode.STATUS_CREATED)) {
            // Create DESCRIPTOR container sub-resource
            SCL.doRequest(new RequestIndication(METHOD_CREATE,SCLID+"/applications/"+appId+"/containers",REQENTITY,new Container(DESC)));
            // Create STATE container sub-resource
            SCL.doRequest(new RequestIndication(METHOD_CREATE,SCLID+"/applications/"+appId+"/containers",REQENTITY,new Container(DATA)));

            String content, targetID;
            // Create DESCRIPTION contentInstance on the DESCRIPTOR container resource
            content = Lamp.getDescriptorRep(SCLID, appId, DATA);
            targetID= SCLID+"/applications/"+appId+"/containers/"+DESC+"/contentInstances";
            SCL.doRequest(new RequestIndication(METHOD_CREATE,targetID,REQENTITY,new ContentInstance(content.getBytes())));

            // Create initial contentInstance on the STATE container resource
            content = Lamp.getStateRep(appId, initValue);
            targetID = SCLID+"/applications/"+appId+"/containers/"+DATA+"/contentInstances";
            SCL.doRequest(new RequestIndication(METHOD_CREATE,targetID,REQENTITY,new ContentInstance(content.getBytes())));
        }
    }

    /**
     * Creates a ContentInstance resource on STATE container.
     * @param lampId - Application ID
     * @param value - measured state
     */
    public static void createContentResource(String lampId, boolean value) {
        // Creates lampCI with new State
        String content = Lamp.getStateRep(lampId, value);
        String targetID = SCLID+"/applications/"+lampId+"/containers/"+DATA+"/contentInstances";
        SCL.doRequest(new RequestIndication(METHOD_CREATE,targetID,REQENTITY,new ContentInstance(content.getBytes())));
    }

    /**
     * Sets the lamp state.
     * @param appId - Application ID
     * @param value - measured state
     */
    public static void setLampState(final String appId, String value) {
        final boolean newState;
        boolean currentState = LAMPS.get(appId).getState();
        if(Lamp.TOGGLE.equalsIgnoreCase(value)) {
            newState = !currentState;
            GUI.setLabel(appId, newState);
            createContentResource(appId, newState);
        } else {
            newState = Boolean.parseBoolean(value);
            // Create the CI in the case when the newState is different to the Current Lamp State
            if (newState != currentState) {
                GUI.setLabel(appId, newState);
                createContentResource(appId, newState);
            }
        }
        //Lamps.LAMPS_STATES.set(index, newState);
        LAMPS.get(appId).setState(newState);
    }

    /**
     * Gets the direct current lamp state
     * @param appId
     * @return the direct current lamp state
     */
    public static boolean getLampValue(String appId) {
        return LAMPS.get(appId).getState();
    }

    public static void execute(String localTarget) {
        SCL.doRequest(new RequestIndication(METHOD_EXECUTE,SCLID+"/"+localTarget,REQENTITY, ""));
    }

    /**
     * Creates a {@link Group} resource including
     * the lamps references.
     * @param aPoCPath - The lamps ApOCPath
     * @param location - The lamps location.
     */
    public static void postGroups(String aPoCPath, String type, String location) {
        // Groups
        // GroupON
        Group groupON = new Group();
        groupON.setId(Switchs.GROUP_ON);
        groupON.setMemberType(MemberType.APPLICATION);
        // GroupOFF
        Group groupOFF = new Group();
        groupOFF.setId(Switchs.GROUP_OFF);
        groupOFF.setMemberType(MemberType.APPLICATION);
        // GroupMembers
        AnyURIList membersON = new AnyURIList();
        AnyURIList membersOFF = new AnyURIList();
        for (int i=0; i<LAMPS.size(); i++) {
            membersON.getReference().add(SCLID+"/applications/"+LAMPS.keySet().toArray()[i].toString()+"/"+aPoCPath+"/true");
            membersOFF.getReference().add(SCLID+"/applications/"+LAMPS.keySet().toArray()[i].toString()+"/"+aPoCPath+"/false");
        }
        groupON.setMembers(membersON);
        groupOFF.setMembers(membersOFF);

        // Groups Creation Request
        SCL.doRequest(new RequestIndication(METHOD_CREATE,SCLID+"/groups",REQENTITY,groupON));
        SCL.doRequest(new RequestIndication(METHOD_CREATE,SCLID+"/groups",REQENTITY,groupOFF));

        // Application Creation
        ResponseConfirm response = SCL.doRequest(new RequestIndication("CREATE",SCLID+"/applications",REQENTITY,new Application(Switchs.APP_ID)));
        if (response.getStatusCode().equals(StatusCode.STATUS_CREATED)) {
            // Create DESCRIPTOR container sub-resource
            SCL.doRequest(new RequestIndication(METHOD_CREATE,SCLID+"/applications/"+Switchs.APP_ID+"/containers",REQENTITY,new Container(DESC)));
            // Create DESCRIPTION contentInstance on the DESCRIPTOR container resource
            String content = Switchs.getDescriptorRep(SCLID, Switchs.APP_ID, type, location, DESC);
            String targetID = SCLID+"/applications/"+Switchs.APP_ID+"/containers/"+DESC+"/contentInstances";
            SCL.doRequest(new RequestIndication(METHOD_CREATE,targetID,REQENTITY,new ContentInstance(content.getBytes())));
        }
    }
}
