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

package org.eclipse.om2m.core;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.comm.service.RestClientService;
import org.eclipse.om2m.commons.resource.AccessRight;
import org.eclipse.om2m.commons.resource.AnyURIList;
import org.eclipse.om2m.commons.resource.HolderRefListType;
import org.eclipse.om2m.commons.resource.MgmtProtocolType;
import org.eclipse.om2m.commons.resource.OnlineStatus;
import org.eclipse.om2m.commons.resource.PermissionFlagListType;
import org.eclipse.om2m.commons.resource.PermissionFlagType;
import org.eclipse.om2m.commons.resource.PermissionHolderType;
import org.eclipse.om2m.commons.resource.PermissionListType;
import org.eclipse.om2m.commons.resource.PermissionType;
import org.eclipse.om2m.commons.resource.Resource;
import org.eclipse.om2m.commons.resource.Scl;
import org.eclipse.om2m.commons.resource.SclBase;
import org.eclipse.om2m.commons.resource.SearchStrings;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.commons.utils.DateConverter;
import org.eclipse.om2m.commons.utils.XmlMapper;
import org.eclipse.om2m.commons.utils.XmlValidator;
import org.eclipse.om2m.core.comm.RestClient;
import org.eclipse.om2m.core.constants.Constants;
import org.eclipse.om2m.core.controller.InterworkingProxyController;
import org.eclipse.om2m.core.dao.DAO;
import org.eclipse.om2m.core.dao.DAOFactory;
import org.eclipse.om2m.core.dao.DBClientConnection;
import org.eclipse.om2m.core.notifier.Notifier;
import org.eclipse.om2m.core.router.Router;
import org.eclipse.om2m.core.service.SclService;
import org.eclipse.om2m.ipu.service.IpuService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

/**
 *  Manages the starting and stopping of the bundle.
 *  @author <ul>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class Activator implements BundleActivator {
    /** Logger */
    private static Log LOGGER = LogFactory.getLog(Activator.class);
    /** IPU service tracker */
    private ServiceTracker<Object, Object> ipuServiceTracker;
    /** Rest Client service tracker */
    private ServiceTracker<Object, Object> restClientServiceTracker;

    public void start(BundleContext bundleContext) throws Exception {
        //Initiate Scl
        LOGGER.info("Starting SCL..");
        try{
            initScl();
        }catch(Exception e){
            LOGGER.error("SCL is Stopped",e);
        }
        LOGGER.info("SCL Started.");


        // Register Scl service
        LOGGER.info("Register SclService..");
        bundleContext.registerService(SclService.class.getName(), new Router(), null);
        LOGGER.info("SclService is registered.");

        // Track the Ipu service
        ipuServiceTracker = new ServiceTracker<Object, Object>(bundleContext, IpuService.class.getName(), null) {
            public void removedService(ServiceReference<Object> reference, Object service) {
                LOGGER.info("IpuService removed");
                IpuService ipu = (IpuService)service;
                LOGGER.info("Remove IPU [ path = "+ipu.getAPOCPath()+" ]");
                InterworkingProxyController.getIpUnits().remove(ipu.getAPOCPath());
            }

            public Object addingService(ServiceReference<Object> reference) {
                LOGGER.info("IpuService discovered");
                IpuService ipu = (IpuService) this.context.getService(reference);
                LOGGER.info("Add IPU [ path = "+ipu.getAPOCPath()+" ]");
                InterworkingProxyController.getIpUnits().put(ipu.getAPOCPath(), ipu);
                       
                return ipu;
            }
        };
        ipuServiceTracker.open();

        // Track the SclClient Service
        restClientServiceTracker = new ServiceTracker<Object, Object>(bundleContext, RestClientService.class.getName(), null) {
            public void removedService(ServiceReference<Object> reference, Object service) {
                LOGGER.info("RestClientService removed");
                RestClientService restClient = (RestClientService)service;
                LOGGER.info("Remove RestClientService [ protocol = "+restClient.getProtocol()+" ]");
                RestClient.getRestClients().remove(restClient.getProtocol());
            }

            public Object addingService(ServiceReference<Object> reference) {
                LOGGER.info("RestClientService discovered");
                RestClientService sclClient = (RestClientService) this.context.getService(reference);
                LOGGER.info("Add RestClientService  [ protocol = "+sclClient.getProtocol()+" ]");
                RestClient.getRestClients().put(sclClient.getProtocol(),sclClient);
                // Display to check on the discovered protocols
                //Map <String, RestClientService> map=RestClient.getRestClients();
                //for (Map.Entry< String,RestClientService > x: map.entrySet() )
                //{ LOGGER.info("the key: "+ x.getKey()+ " the value: "+ x.getValue());}

                return sclClient;
            }
        };
        restClientServiceTracker.open();
    }

    public void stop(BundleContext bundleContext) throws Exception {
    }

    /**
     * instantiates the Scl by invoking methods to create default local resources
     * and send scl registration request if it is not a NSCL.
     */
    public static void initScl() {
            // Init SCL if reset = true
            if(Constants.RESET){
                // Clear SCL DataBase
                LOGGER.info("Clear SCL DataBase");
                clearDB();

                // Create SclBase resource
                LOGGER.info("Create SclBase resource");
                initSclBase();

                // Create AccessRight resource
                LOGGER.info("Create AccessRight resource");
                initAccessRight();
            }

            // Create JAXBContext instance
            LOGGER.info("Init XmlMapper");
            XmlMapper.getInstance();

            // Create SAXParserFactory instance
            LOGGER.info("Init XmlValidator");
            XmlValidator.getInstance();

            // Manage registration in the case of a GSCL
            if("GSCL".equals(Constants.SCL_TYPE)){
                 registerScl();
            }

        }

    /**
     * Clears DataBase
     */
        public static void clearDB(){
            // Delete all objects from database
            ObjectContainer db = DBClientConnection.getInstance();
            try{
                ObjectSet<Resource> result=db.queryByExample(Resource.class);
                while(result.hasNext()) {
                    DBClientConnection.getInstance().delete(result.next());
                }
            }catch(Exception e){
                LOGGER.error("Error clearDB",e);
                registerScl();
            }
        }

        /**
         * Creates the root {@link SclBase} resource in DataBase.
         */
        public static void initSclBase(){
            // Create SclBase object
            SclBase sclBase = new SclBase();
            sclBase.setUri(Constants.SCL_ID);
            sclBase.setAccessRightID(sclBase.getUri()+"/accessRights/"+Constants.ADMIN_PROFILE_ID);
            sclBase.setCreationTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
            sclBase.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
            SearchStrings searchStrings = new SearchStrings();
            searchStrings.getSearchString().add(Constants.SEARCH_STRING_RES_TYPE+sclBase.getClass().getSimpleName());
            searchStrings.getSearchString().add(Constants.SEARCH_STRING_RES_ID+Constants.SCL_ID);
            sclBase.setSearchStrings(searchStrings);
            DAOFactory.getSclBaseDAO().create(sclBase);
        }

        /**
         * Sends {@link SclBase} resource in DataBase.
         */
        public static void registerScl(){
            //Create an GSCL Scl resource
            Scl gscl = new Scl();
            gscl.setSclId(Constants.SCL_ID);
            AnyURIList pocs = new AnyURIList();
            pocs.getReference().add("http://"+Constants.SCL_IP+":"+Constants.SCL_PORT+Constants.SCL_CONTEXT);
            //pocs.getReference().add("coap://"+Constants.SCL_IP+":"+Constants.COAP_PORT/*+Constants.SCL_CONTEXT*/);
            gscl.setPocs(pocs);
            gscl.setLink(Constants.SCL_ID);
            gscl.setMgmtProtocolType(MgmtProtocolType.OMA_DM);
            String base = Constants.SCL_DEFAULT_PROTOCOL+"://"+Constants.NSCL_IP+":"+ Constants.NSCL_PORT+Constants.NSCL_CONTEXT+"/";

            // Create RequestIndication
            final RequestIndication requestIndication = new RequestIndication();
            requestIndication.setMethod(Constants.METHOD_CREATE);
            requestIndication.setRequestingEntity(Constants.ADMIN_REQUESTING_ENTITY);            
            requestIndication.setTargetID(Constants.NSCL_ID+"/scls");
            requestIndication.setRepresentation(XmlMapper.getInstance().objectToXml(gscl));
            requestIndication.setBase(base);
            
            LOGGER.info("The requestIndication : " + requestIndication );

            // Start registration in a new Thread
            new Thread(){
                public void run(){
                    ResponseConfirm responseConfirm;
                    int sleepTime=10000;
                    boolean registred=false;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        LOGGER.error("Registration sleep error",e1);
                    }
                    // Loop until registration succeed or already registered
                    while(!registred){
                        // Send GSCL registration to NSCL
                        LOGGER.info("Send GSCL registration to NSCL");
                        responseConfirm = new RestClient().sendRequest(requestIndication);
                        //Stop registration if success of GSCL already registered
                        if(responseConfirm.getStatusCode().equals(StatusCode.STATUS_CREATED)){
                            LOGGER.info("GSCL is successfully registered to NSCL");
                            registred=true;
                        }else if(responseConfirm.getStatusCode().equals(StatusCode.STATUS_CONFLICT)){
                            LOGGER.info("GSCL is already registered to NSCL");
                            registred=true;
                        }else{
                            try {
                                // Retry if registration failed
                                LOGGER.info("GSCL registration failed");
                                LOGGER.info("Retrying registration after: "+sleepTime+" ms");
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                                LOGGER.error("Registration sleep error",e);
                            }
                        }
                    }
                  //Create an NSCL Scl resource
                	Router.readWriteLock.readLock().lock();

                    LOGGER.info("Create NSCL registration on GSCL");
                    Scl nscl = new Scl();
                    nscl.setUri(Constants.SCL_ID+""+"/scls/"+Constants.NSCL_ID);
                    nscl.setSclId(Constants.NSCL_ID);
                    nscl.setAccessRightID(Constants.SCL_ID+"/accessRights/"+Constants.ADMIN_PROFILE_ID);
                    nscl.setCreationTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
                    nscl.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
                    SearchStrings searchStrings = new SearchStrings();
                    searchStrings.getSearchString().add(Constants.SEARCH_STRING_RES_TYPE+nscl.getClass().getSimpleName());
                    searchStrings.getSearchString().add(Constants.SEARCH_STRING_RES_ID+Constants.NSCL_ID);
                    nscl.setSearchStrings(searchStrings);
                    AnyURIList pocs = new AnyURIList();
                    pocs.getReference().add("http://"+Constants.NSCL_IP+":"+Constants.NSCL_PORT+Constants.NSCL_CONTEXT);
                    //pocs.getReference().add("coap://"+Constants.NSCL_IP+":"+Constants.NSCL_COAP_PORT/*+Constants.SCL_CONTEXT*/);

                    nscl.setPocs(pocs);
                    nscl.setLink(Constants.NSCL_ID);
                    nscl.setMgmtProtocolType(MgmtProtocolType.OMA_DM);
                    nscl.setOnlineStatus(OnlineStatus.ONLINE);
                    nscl.setServerCapability(true);
                    // Set References
                    nscl.setContainersReference(nscl.getUri()+"/containers");
                    nscl.setGroupsReference(nscl.getUri()+"/groups");
                    nscl.setApplicationsReference(nscl.getUri()+"/applications");
                    nscl.setAccessRightsReference(nscl.getUri()+"/accessRights");
                    nscl.setSubscriptionsReference(nscl.getUri()+"/subscriptions");
                    nscl.setMgmtObjsReference(nscl.getUri()+"/mgmtObjs");
                    nscl.setNotificationChannelsReference(nscl.getUri()+"/notificationChannels");
                    nscl.setM2MPocsReference(nscl.getUri()+"/m2mPocs");
                    nscl.setAttachedDevicesReference(nscl.getUri()+"/attachedDevices");

                    // Store scl
                    DAOFactory.getSclDAO().create(nscl);

                    LOGGER.info("NSCL is successfully registred on GSCL");
                    Router.readWriteLock.readLock().unlock();
                }
            }.start();
        }

        /**
         * Creates a default {@link AccessRight} resource in DataBase.
         */
        public static void initAccessRight() {
            //Create AccessRight resource
            AccessRight accessRight = new AccessRight();
            accessRight.setId(Constants.ADMIN_PROFILE_ID);

            accessRight.setUri(Constants.SCL_ID+""+"/accessRights/"+accessRight.getId());
            accessRight.setSubscriptionsReference(accessRight.getUri()+"/subscriptions");
            //Add SearchStrings
            SearchStrings searchStrings = new SearchStrings();
            searchStrings.getSearchString().add(Constants.SEARCH_STRING_RES_TYPE+accessRight.getClass().getSimpleName());
            searchStrings.getSearchString().add(Constants.SEARCH_STRING_RES_ID+accessRight.getId());
            accessRight.setSearchStrings(searchStrings);
            //Add Permissions
            PermissionListType permissions = new PermissionListType();
            //Permission 1
            PermissionType permission1 = new PermissionType();
            permission1.setId("Admin_Permission");
            //Permissions Flags
            PermissionFlagListType permissionFlagListType = new PermissionFlagListType();
            //AllRights
            permissionFlagListType.getFlag().add(PermissionFlagType.CREATE);
            permissionFlagListType.getFlag().add(PermissionFlagType.WRITE);
            permissionFlagListType.getFlag().add(PermissionFlagType.READ);
            permissionFlagListType.getFlag().add(PermissionFlagType.DELETE);
            permissionFlagListType.getFlag().add(PermissionFlagType.DISCOVER);
            //Permissions Holders
            PermissionHolderType permissionHolderType = new PermissionHolderType();
            HolderRefListType holderRefListType = new HolderRefListType();
            holderRefListType.getHolderRef().add(Constants.ADMIN_REQUESTING_ENTITY);
            permissionHolderType.setHolderRefs(holderRefListType);
            permission1.setPermissionFlags(permissionFlagListType);
            permission1.setPermissionHolders(permissionHolderType);
            permissions.getPermission().add(permission1);

            //Permission 2
            PermissionType permission2 = new PermissionType();
            permission2.setId(Constants.GUEST_PROFILE_ID);
            //Permissions Flags
            PermissionFlagListType permissionFlagListType2 = new PermissionFlagListType();
            //Rights to Read and Discover
            permissionFlagListType2.getFlag().add(PermissionFlagType.READ);
            permissionFlagListType2.getFlag().add(PermissionFlagType.DISCOVER);
            //Permissions Holders
            PermissionHolderType permissionHolderType2 = new PermissionHolderType();
            HolderRefListType holderRefListType2 = new HolderRefListType();
            holderRefListType2.getHolderRef().add(Constants.GUEST_REQUESTING_ENTITY);
            permissionHolderType2.setHolderRefs(holderRefListType2);
            permission2.setPermissionFlags(permissionFlagListType2);
            permission2.setPermissionHolders(permissionHolderType2);
            permissions.getPermission().add(permission2);
            //Set Permissions Attribute
            accessRight.setPermissions(permissions);

            //Add SelfPermissions
            PermissionListType selfPermissions = new PermissionListType();

            //SelfPermission 1
            PermissionType selfPermission1 = new PermissionType();
            selfPermission1.setId("Self_Permission");
            //Permissions Flags
            PermissionFlagListType selfPermissionFlagListType = new PermissionFlagListType();
            //AllRights
            selfPermissionFlagListType.getFlag().add(PermissionFlagType.CREATE);
            selfPermissionFlagListType.getFlag().add(PermissionFlagType.WRITE);
            selfPermissionFlagListType.getFlag().add(PermissionFlagType.READ);
            selfPermissionFlagListType.getFlag().add(PermissionFlagType.DELETE);
            selfPermissionFlagListType.getFlag().add(PermissionFlagType.DISCOVER);
            //Permissions Holders
            PermissionHolderType selfPermissionHolderType = new PermissionHolderType();
            HolderRefListType selfPermissionHolderRefListType = new HolderRefListType();
            selfPermissionHolderRefListType.getHolderRef().add(Constants.ADMIN_REQUESTING_ENTITY);
            selfPermissionHolderType.setHolderRefs(selfPermissionHolderRefListType);
            selfPermission1.setPermissionFlags(selfPermissionFlagListType);
            selfPermission1.setPermissionHolders(selfPermissionHolderType);
            selfPermissions.getPermission().add(selfPermission1);
            //Set SelfPermissions in AccessRight
            accessRight.setSelfPermissions(selfPermissions);

            accessRight.setLastModifiedTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());
            accessRight.setCreationTime(DateConverter.toXMLGregorianCalendar(new Date()).toString());

            DAOFactory.getAccessRightDAO().create(accessRight);
        }

}
