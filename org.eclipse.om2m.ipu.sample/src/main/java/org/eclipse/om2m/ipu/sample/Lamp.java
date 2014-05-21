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

import obix.Bool;
import obix.Contract;
import obix.Obj;
import obix.Op;
import obix.Str;
import obix.Uri;
import obix.io.ObixEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *  Provides different Lamps information.
 *  @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         </ul>
 */
public class Lamp {
    /** Logger */
    private static Log LOGGER = LogFactory.getLog(Lamp.class);
    /** Application point of contact for the lamps controller {@link SampleController} */
    public final static String APOCPATH = "lamps";
    /** Default Lamps location */
    public final static String LOCATION = "Home";
    /** Toggle */
    public final static String TOGGLE = "toggle";
    /** Default Lamps type */
    public final static String TYPE = "LAMP";
    /** Lamp state */
    private boolean state = false;


    /**
     * Returns an obix XML representation describing the lamp.
     * @param sclId - SclBase id
     * @param appId - Application Id
     * @param stateCont - the STATE container id
     * @return Obix XML representation
     */
    public static String getDescriptorRep(String sclId, String appId, String stateCont) {
        LOGGER.info("Descriptor Representation Construction");
        // oBIX
        Obj obj = new Obj();
        obj.add(new Str("type",TYPE));
        obj.add(new Str("location",LOCATION));
        obj.add(new Str("appId",appId));
        // OP GetState from SCL DataBase
        Op opState = new Op();
        opState.setName("getState");
        opState.setHref(new Uri(sclId+"/"+"applications/"+appId+"/containers/"+stateCont+"/contentInstances/latest/content"));
        opState.setIs(new Contract("retrieve"));
        opState.setIn(new Contract("obix:Nil"));
        opState.setOut(new Contract("obix:Nil"));
        obj.add(opState);
        // OP GetState from SCL IPU
        Op opStateDirect = new Op();
        opStateDirect.setName("getState(Direct)");
        opStateDirect.setHref(new Uri(sclId+"/"+"applications/"+appId+"/"+APOCPATH));
        opStateDirect.setIs(new Contract("retrieve"));
        opStateDirect.setIn(new Contract("obix:Nil"));
        opStateDirect.setOut(new Contract("obix:Nil"));
        obj.add(opStateDirect);
        // OP SwitchON
        Op opON = new Op();
        opON.setName("switchON");
        opON.setHref(new Uri(sclId+"/"+"applications/"+appId+"/"+APOCPATH+"/true"));
        opON.setIs(new Contract("execute"));
        opON.setIn(new Contract("obix:Nil"));
        opON.setOut(new Contract("obix:Nil"));
        obj.add(opON);
        // OP SwitchOFF
        Op opOFF = new Op();
        opOFF.setName("switchOFF");
        opOFF.setHref(new Uri(sclId+"/"+"applications/"+appId+"/"+APOCPATH+"/false"));
        opOFF.setIs(new Contract("execute"));
        opOFF.setIn(new Contract("obix:Nil"));
        opOFF.setOut(new Contract("obix:Nil"));
        obj.add(opOFF);
        // OP Toggle
        Op opToggle = new Op();
        opToggle.setName("toggle");
        opToggle.setHref(new Uri(sclId+"/"+"applications/"+appId+"/"+APOCPATH+"/"+TOGGLE));
        opToggle.setIs(new Contract("execute"));
        opToggle.setIn(new Contract("obix:Nil"));
        opToggle.setOut(new Contract("obix:Nil"));
        obj.add(opToggle);

        return ObixEncoder.toString(obj);
    }

    /**
     * Returns an obix XML representation describing the current state.
     * @param appId - Application Id
     * @param value - current lamp state
     * @return Obix XML representation
     */
    public static String getStateRep(String appId, boolean value) {
        // oBIX
        Obj obj = new Obj();
        obj.add(new Str("type",TYPE));
        obj.add(new Str("location",LOCATION));
        obj.add(new Str("appId",appId));
        obj.add(new Bool("state",value));
        return ObixEncoder.toString(obj);

    }

    /**
     * Gets lampState
     * @return lampState
     */
    public boolean getState() {
        return state;
    }

    /**
     * Sets lampState
     */
    public void setState(boolean state) {
        this.state = state;
    }

}
