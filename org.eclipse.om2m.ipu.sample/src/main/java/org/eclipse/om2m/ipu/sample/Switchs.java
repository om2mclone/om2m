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

import java.util.List;

import obix.Contract;
import obix.Obj;
import obix.Op;
import obix.Str;
import obix.Uri;
import obix.io.ObixEncoder;

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

/**
 *  Provides methods to create groups to switchON/OFF all lamps and send switch request to specific lamp.
 *  @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         </ul>
 */
public class Switchs {
    /** Logger */
    private static Log LOGGER = LogFactory.getLog(Switchs.class);
    /** AppId */
    public final static String APP_ID = "LAMP_ALL";
    /** GrouON ID */
    final static String GROUP_ON = "ON_ALL";
    /** GrouOFF ID */
    final static String GROUP_OFF = "OFF_ALL";
    /** Generic create method name */
    public final static String METHOD = "CREATE";

    /**
     * Creates a {@link Group} resource including
     * the lamps references.
     * @param lamp1Id - LAMP1 Application Resource ID.
     * @param lamp2Id - LAMP2 Application Resource ID.
     */
    public static void postGroups(String aPoCPath, String type, String location, List<String> keys) {
        // Groups
        // GroupON
        Group groupON = new Group();
        groupON.setId(GROUP_ON);
        groupON.setMemberType(MemberType.APPLICATION);
        // GroupOFF
        Group groupOFF = new Group();
        groupOFF.setId(GROUP_OFF);
        groupOFF.setMemberType(MemberType.APPLICATION);
        // GroupMembers
        AnyURIList membersON = new AnyURIList();
        AnyURIList membersOFF = new AnyURIList();
        for (int i=0; i<keys.size(); i++) {
            membersON.getReference().add(SampleMonitor.SCLID+"/applications/"+keys.get(i)+"/"+aPoCPath+"/true");
            membersOFF.getReference().add(SampleMonitor.SCLID+"/applications/"+keys.get(i)+"/"+aPoCPath+"/false");
        }
        groupON.setMembers(membersON);
        groupOFF.setMembers(membersOFF);

        // Groups Creation Request
        SampleMonitor.SCL.doRequest(new RequestIndication(METHOD,SampleMonitor.SCLID+"/groups",SampleMonitor.REQENTITY,groupON));
        SampleMonitor.SCL.doRequest(new RequestIndication(METHOD,SampleMonitor.SCLID+"/groups",SampleMonitor.REQENTITY,groupOFF));

        // Application Creation
        ResponseConfirm response = SampleMonitor.SCL.doRequest(new RequestIndication("CREATE",SampleMonitor.SCLID+"/applications",SampleMonitor.REQENTITY,new Application(APP_ID)));
        if (response.getStatusCode().equals(StatusCode.STATUS_CREATED)) {
            // Create DESCRIPTOR container sub-resource
            SampleMonitor.SCL.doRequest(new RequestIndication(METHOD,SampleMonitor.SCLID+"/applications/"+APP_ID+"/containers",SampleMonitor.REQENTITY,new Container(SampleMonitor.DESC)));
            // Create DESCRIPTION contentInstance on the DESCRIPTOR container resource
            String content = getDescriptorRep(SampleMonitor.SCLID, APP_ID, type, location, SampleMonitor.DESC);
            String targetID= SampleMonitor.SCLID+"/applications/"+APP_ID+"/containers/"+SampleMonitor.DESC+"/contentInstances";
            SampleMonitor.SCL.doRequest(new RequestIndication(METHOD,targetID,SampleMonitor.REQENTITY,new ContentInstance(content.getBytes())));
        }
    }

    /**
     * Returns an obix XML representation describing the switchAll.
     * @param sclId - SclBase id
     * @param appId - Application Id
     * @param type - lamp type
     * @param location - lamp location
     * @param dataCont - the STATE container id
     * @return Obix XML representation
     */
    public static String getDescriptorRep(String sclId, String appId, String type, String location, String stateCont) {
        LOGGER.info("Descriptor Representation Construction");
        // oBIX
        Obj obj = new Obj();
        obj.add(new Str("type",type));
        obj.add(new Str("location",location));
        obj.add(new Str("appId",appId));
        // OP SwitchAllON
        Op opON = new Op();
        opON.setName("switchAllON");
        opON.setHref(new Uri(sclId+"/groups/"+GROUP_ON+"/membersContent"));
        opON.setIs(new Contract("execute"));
        opON.setIn(new Contract("obix:Nil"));
        opON.setOut(new Contract("obix:Nil"));
        obj.add(opON);
        // OP SwitchAllOFF
        Op opOFF = new Op();
        opOFF.setName("switchAllOFF");
        opOFF.setHref(new Uri(sclId+"/groups/"+GROUP_OFF+"/membersContent"));
        opOFF.setIs(new Contract("execute"));
        opOFF.setIn(new Contract("obix:Nil"));
        opOFF.setOut(new Contract("obix:Nil"));
        obj.add(opOFF);

        return ObixEncoder.toString(obj);

    }

    /**
     * Sends a request to switch ON/OFF specific lamp.
     * @param appId - The LAMP AppId
     * @param newState - The state to switch-to
     */
    public static void switchLamp(String appId, boolean newState) {
        SampleMonitor.SCL.doRequest(new RequestIndication("EXECUTE",SampleMonitor.SCLID+"/applications/"+appId+"/"+Lamp.APOCPATH+"/"+newState,SampleMonitor.REQENTITY, ""));
    }

    /**
     * Sends a request to switch ON/OFF a group of lamps.
     * @param newState - the state to switch-To
     */
    public static void switchAll(boolean newState) {
        if(newState) {
            // Sends a request to groupON to switchAll lamps ON
            SampleMonitor.SCL.doRequest(new RequestIndication("EXECUTE",SampleMonitor.SCLID+"/groups/"+GROUP_ON+"/membersContent",SampleMonitor.REQENTITY, ""));
        } else {
            // Sends a request to groupOFF to switchAll lamps OFF
            SampleMonitor.SCL.doRequest(new RequestIndication("EXECUTE",SampleMonitor.SCLID+"/groups/"+GROUP_OFF+"/membersContent",SampleMonitor.REQENTITY, ""));
        }

    }
}
