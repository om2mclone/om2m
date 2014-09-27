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
package org.eclipse.om2m.core.constants;

/**
 * Initializes platform properties.
 * @author <ul>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.fr ></li>
 *         </ul>
 */
public class Constants {

    //SclBase resource properties
    /** SclBase type: NSCL or GSCL. */
    public static final String SCL_TYPE = System.getProperty("org.eclipse.om2m.sclType","NSCL");
    /** SclBase id. */
    public static final String SCL_ID = System.getProperty("org.eclipse.om2m.sclBaseId","nscl");
    /** Reset database after each restart. */
    public static final boolean RESET = Boolean.valueOf(System.getProperty("org.eclipse.om2m.reset","true"));
    /** Default admin access right profile */
    public static final String ADMIN_PROFILE_ID = "AR_ADMIN";
    /** Default admin requesting entity. (username/password) */
    public static final String ADMIN_REQUESTING_ENTITY = System.getProperty("org.eclipse.om2m.adminRequestingEntity","admin/admin");
    /** Default guest access right profile */
    public static final String GUEST_PROFILE_ID = "AR_GUEST";
    /** Default guest requesting entity. (username/password) */
    public static final String GUEST_REQUESTING_ENTITY = System.getProperty("org.eclipse.om2m.guestRequestingEntity","guest/guest");
    /** Default resources expiration time. */
    public static final long EXPIRATION_TIME = 999999999;
    /** Default ContentInstances collection maximum number of instance. */
    public static final Long MAX_NBR_OF_INSTANCES = Long.valueOf(System.getProperty("org.eclipse.om2m.maxNrOfInstances","10"));

    //SclBase communication properties
    /** SclBase default communication protocol. */
    public static final String SCL_DEFAULT_PROTOCOL = System.getProperty("org.eclipse.om2m.sclBaseProtocol.default","http");
    /** SclBase ip address. */
    public static final String SCL_IP = System.getProperty("org.eclipse.om2m.sclBaseAddress","127.0.0.1");
    /** SclBase listening port. */
    public static final int SCL_PORT = Integer.parseInt(System.getProperty("org.eclipse.equinox.http.jetty.http.port","8080"));    
    /** gscl coap port. */
    public static final int COAP_PORT = Integer.parseInt(System.getProperty("org.eclipse.om2m.coapPort","5684"));
    /** listening context. */
    public static final String SCL_CONTEXT = System.getProperty("org.eclipse.om2m.sclBaseContext","/om2m");
    
   
    //The following properties are required only for GSCL to perform authentication on a remote NSCL
    /** Remote Nscl Id. (Required only for GSCL)*/
    public static final String NSCL_ID = System.getProperty("org.eclipse.om2m.remoteNsclId","nscl");
    /** Remote Nscl ip address. (Required only for GSCL)*/
    public static final String NSCL_IP = System.getProperty("org.eclipse.om2m.remoteNsclAddress","127.0.0.1");
    /** Remote Nscl listening port. (Required only for GSCL)*/
    public static final int NSCL_PORT = Integer.parseInt(System.getProperty("org.eclipse.om2m.remoteNsclPort","8080"));
    /** Remote Nscl listening port. (Required only for GSCL)*/
    public static final int NSCL_COAP_PORT = Integer.parseInt(System.getProperty("org.eclipse.om2m.remoteNsclCoapPort","5683"));
    /** Remote Nscl listening context. */
    public static final String NSCL_CONTEXT = System.getProperty("org.eclipse.om2m.remoteNsclContext","/om2m");

    //Remote Data base specific parameters
    /** Database file name. (Required only for embeded data base)*/
    public static final String DB_FILE = System.getProperty("org.eclipse.om2m.dbFile","db");
    
    //DB Defragment period
    /** DB Defragment period in ms. */
    public static final int DB_DEFRAGMENT_PERIOD = Integer.parseInt(System.getProperty("org.eclipse.om2m.dbDefragmentPeriod","-1"));

    //Rest Method names
    /** Retrieve method name. */
    public static final String METHOD_RETREIVE = "RETRIEVE";
    /** Create method name. */
    public static final String METHOD_CREATE = "CREATE";
    /** Update method name. */
    public static final String METHOD_UPDATE = "UPDATE";
    /** Delete method name. */
    public static final String METHOD_DELETE = "DELETE";
    /** Execute method name. */
    public static final String METHOD_EXECUTE = "EXECUTE";

    //Access Right Method names
    /** Create Access Right method name. */
    public static final String AR_CREATE = "CREATE";
    /** Read Access Right method name. */
    public static final String AR_READ = "READ";
    /** Write Access Right method name. */
    public static final String AR_WRITE = "WRITE";
    /** Delete Access Right method name. */
    public static final String AR_DELETE = "DELETE";
    /** Discover Access Right method name. */
    public static final String AR_DISCOVER = "DISCOVER";

    //SearchStrings prefixes
    /** Search String resource type prefix. */
    public static final String SEARCH_STRING_RES_TYPE = "ResourceType/";
    /** Search String resource id prefix. */
    public static final String SEARCH_STRING_RES_ID = "ResourceID/";
    

}

