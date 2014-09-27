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
package org.eclipse.om2m.core.router;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.Resource;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.commons.utils.XmlMapper;

import org.eclipse.om2m.core.constants.Constants;
import org.eclipse.om2m.core.controller.AccessRightAnncController;
import org.eclipse.om2m.core.controller.AccessRightController;
import org.eclipse.om2m.core.controller.AccessRightsController;
import org.eclipse.om2m.core.controller.ApplicationAnncController;
import org.eclipse.om2m.core.controller.ApplicationController;
import org.eclipse.om2m.core.controller.ApplicationsController;
import org.eclipse.om2m.core.controller.AttachedDeviceController;
import org.eclipse.om2m.core.controller.AttachedDevicesController;
import org.eclipse.om2m.core.controller.ContainerAnncController;
import org.eclipse.om2m.core.controller.ContainerController;
import org.eclipse.om2m.core.controller.ContainersController;
import org.eclipse.om2m.core.controller.ContentController;
import org.eclipse.om2m.core.controller.ContentInstanceController;
import org.eclipse.om2m.core.controller.ContentInstancesController;
import org.eclipse.om2m.core.controller.Controller;
import org.eclipse.om2m.core.controller.DiscoveryController;
import org.eclipse.om2m.core.controller.ExecInstanceController;
import org.eclipse.om2m.core.controller.ExecInstancesController;
import org.eclipse.om2m.core.controller.GroupAnncController;
import org.eclipse.om2m.core.controller.GroupController;
import org.eclipse.om2m.core.controller.GroupsController;

import org.eclipse.om2m.core.controller.LocationContainerAnncController;
import org.eclipse.om2m.core.controller.LocationContainerController;
import org.eclipse.om2m.core.controller.M2MPocController;
import org.eclipse.om2m.core.controller.M2MPocsController;
import org.eclipse.om2m.core.controller.MembersContentController;
import org.eclipse.om2m.core.controller.MgmtCmdController;
import org.eclipse.om2m.core.controller.MgmtObjController;
import org.eclipse.om2m.core.controller.MgmtObjsController;
import org.eclipse.om2m.core.controller.NotificationChannelController;
import org.eclipse.om2m.core.controller.NotificationChannelsController;
import org.eclipse.om2m.core.controller.ParametersController;
import org.eclipse.om2m.core.controller.SclBaseController;
import org.eclipse.om2m.core.controller.SclController;
import org.eclipse.om2m.core.controller.SclsController;
import org.eclipse.om2m.core.controller.SubscriptionController;
import org.eclipse.om2m.core.controller.SubscriptionsController;
//<<<<<<< HEAD
import org.eclipse.om2m.core.controller.APocController;
import org.eclipse.om2m.core.notifier.Notifier;
//=======
//>>>>>>> refs/remotes/origin/master
import org.eclipse.om2m.core.redirector.Redirector;
import org.eclipse.om2m.core.service.SclService;

/**
 * Routes a generic request to the appropriate resource controller to handle it based on the request method and URI.
 * @author <ul>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.fr ></li>
 *         <li>Marouane El kiasse < melkiasse@laas.fr > < kiasmarouane@gmail.com ></li>
 *         </ul>
 */

public class Router implements SclService {
    /** Logger */
    private static Log LOGGER = LogFactory.getLog(Router.class);
    public static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    /** Resource id pattern. */
    private static String idPattern="(?!(sclBase|scls|scl|applications|application|applicationAnnc|containers|container|content|subscriptions|subscription|"
            + "groups|group|accessRights|accessRight|discovery|mgmtObjs|mgmtObj|mgmtCmd|attahchedDevices|attachedDevice|notificationChannels|"
            + "notificationChannel|execInstances|execInstance|parameters|parameter|m2mPocs|m2mPoc))\\b\\w+\\b";

    /** Announced resource id pattern. */
    private static String idAnncPattern = "\\w+";

    /** SclBase resource uri pattern. */
    private static Pattern sclBasePattern= Pattern.compile(Constants.SCL_ID+"/*");

    /** Re-targeting uri pattern. */
    private static Pattern retargetingPattern= Pattern.compile("(?!"+Constants.SCL_ID+")\\b\\w+\\b/*.*");

    /** Scls resource uri pattern. */
    private static Pattern  sclsPattern= Pattern.compile(sclBasePattern+"/+scls/*");
    
    //private static Pattern ipuPattern2= Pattern.compile("("+Constants.SCL_ID+"/*"+"|"+Constants.SCL_ID+"/*"+"/+scls/*"+"/+"+idPattern+"/*"+")"+"/+applications/*"+"/+"+idPattern+"(?<!Annc)/*"+"/"+idPattern+"/*.*");

    /** Scl resource uri pattern. */
    private static Pattern sclPattern= Pattern.compile(sclsPattern+"/+"+idPattern+"/*");

    /** Applications resource uri pattern. */
    public static final Pattern applicationsPattern= Pattern.compile("("+sclBasePattern+"|"+sclPattern+")"+"/+applications/*");

    /** Application resource uri pattern. */
    private static Pattern applicationPattern= Pattern.compile(applicationsPattern+"/+"+idPattern+"(?<!Annc)/*");

    /** Interworking proxy unit uri pattern. */
    private static Pattern ipuPattern= Pattern.compile(applicationPattern+"/"+idPattern+"/*.*");
    
    /** Coap Comm uri pattern. */
    private static Pattern coapCommPattern= Pattern.compile("coap/"+applicationPattern+"/"+idPattern+"/*.*");

    /** ApplicationAnnc resource uri pattern. */
    private static Pattern applicationAnncPattern= Pattern.compile(applicationsPattern+"/+"+idAnncPattern+"Annc/*");

    /** Containers resource uri pattern. */
    private static Pattern containersPattern= Pattern.compile("("+sclBasePattern+"|"+sclPattern+"|"+applicationPattern+"|"+applicationAnncPattern+")"+"/+containers/*");

    /** Container resource uri pattern. */
    private static Pattern containerPattern= Pattern.compile(containersPattern+"/+"+idPattern+"(?<!Annc)(?<!Loc)(?<!LocAnnc)/*");

    /** ContainerAnnc resource uri pattern. */
    private static Pattern containerAnncPattern= Pattern.compile(containersPattern+"/+"+idAnncPattern+"(?<!Loc)(?<!LocAnnc)Annc/*");

    /** LocationContainer resource uri pattern. */
    private static Pattern locationContainerPattern= Pattern.compile(containersPattern+"/+"+idPattern+"Loc/*");

    /** LocationContainerAnnc resource uri pattern. */
    private static Pattern locationContainerAnncPattern= Pattern.compile(containersPattern+"/+"+idPattern+"LocAnnc/*");

    /** ContentInstances resource uri pattern. */
    private static Pattern contentInstancesPattern= Pattern.compile("("+containerPattern+"|"+locationContainerPattern+")"+"/+contentInstances/*");

    /** ContentInstance resource uri pattern. */
    private static Pattern contentInstancePattern= Pattern.compile(contentInstancesPattern+"/+"+idPattern+"/*");

    /** Content resource uri pattern. */
    private static Pattern contentPattern= Pattern.compile(contentInstancePattern+"/+content/*");

    /** AccessRights resource uri pattern. */
    private static Pattern accessRightsPattern= Pattern.compile("("+sclBasePattern+"|"+sclPattern+"|"+applicationPattern+"|"+applicationAnncPattern+")"+"/+accessRights/*");

    /** AccessRight resource uri pattern. */
    private static Pattern accessRightPattern= Pattern.compile(accessRightsPattern+"/+"+idPattern+"(?<!Annc)/*");

    /** AccessRightAnnc resource uri pattern. */
    private static Pattern accessRightAnncPattern= Pattern.compile(accessRightsPattern+"/+"+idAnncPattern+"Annc/*");

    /** Groups resource uri pattern. */
    private static Pattern groupsPattern= Pattern.compile("("+sclBasePattern+"|"+sclPattern+"|"+applicationPattern+"|"+applicationAnncPattern+")"+"/+groups/*");

    /** Group resource uri pattern. */
    private static Pattern groupPattern= Pattern.compile(groupsPattern+"/+"+idPattern+"(?<!Annc)/*");

    /** MembersContent resource uri pattern. */
    private static Pattern membersContentPattern= Pattern.compile(groupPattern+"/+"+idPattern+"/*");

    /** GroupAnnc resource uri pattern. */
    private static Pattern groupAnncPattern= Pattern.compile(groupsPattern+"/+"+idAnncPattern+"Annc/*");

    /** Discovery resource uri pattern. */
    private static Pattern discoveryPattern= Pattern.compile(sclBasePattern+"/+discovery/*.*");

    /** AttachedDevices resource uri pattern. */
    private static Pattern attachedDevicesPattern= Pattern.compile(sclPattern+"/+attachedDevices/*");

    /** AttachedDevice resource uri pattern. */
    private static Pattern attachedDevicePattern= Pattern.compile(attachedDevicesPattern+"/+"+idPattern+"/*");

    /** MgmtObjs resource uri pattern. */
    private static Pattern mgmtObjsPattern= Pattern.compile("("+sclsPattern+"|"+sclPattern+"|"+applicationsPattern+"|"+attachedDevicePattern+")"+"/+mgmtObjs/*");

    /** MgmtObj resource uri pattern. */
    private static Pattern mgmtObjPattern= Pattern.compile(mgmtObjsPattern+"/+"+idPattern+"Obj/*");

    /** Parameters resource uri pattern. */
    private static Pattern parametersPattern= Pattern.compile(mgmtObjPattern+"/+parameters/*");

    /** Parameter resource uri pattern. */
    private static Pattern parameterPattern= Pattern.compile(parametersPattern+"/+"+idPattern+"/*");

    /** MgmtCmd resource uri pattern. */
    private static Pattern mgmtCmdPattern= Pattern.compile(mgmtObjsPattern+"/+"+idPattern+"Cmd/*");

    /** ExecInstances resource uri pattern. */
    private static Pattern execInstancesPattern= Pattern.compile(mgmtCmdPattern+"/+execInstances/*");

    /** ExecInstance resource uri pattern. */
    private static Pattern execInstancePattern= Pattern.compile(execInstancesPattern+"/+"+idPattern+"/*");

    /** NotificationChannels resource uri pattern. */
    private static Pattern notificationChannelsPattern= Pattern.compile("("+sclPattern+"|"+applicationPattern+")"+"/+notificationChannels/*");

    /** NotificationChannel resource uri pattern. */
    private static Pattern notificationChannelPattern= Pattern.compile(notificationChannelsPattern+"/+"+idPattern+"/*");

    /** M2mPocs resource uri pattern. */
    private static Pattern m2mPocsPattern= Pattern.compile(sclPattern+"/+m2mPocs/*");

    /** M2mPoc resource uri pattern. */
    private static Pattern m2mPocPattern= Pattern.compile(m2mPocsPattern+"/+"+idPattern+"/*");

    /** Subscriptions resource uri pattern. */
    private static Pattern subscriptionsPattern= Pattern.compile("("+sclBasePattern+"|"+sclPattern+"|"+sclsPattern+"|"+applicationsPattern+"|"+applicationPattern+
            "|"+containersPattern+"|"+containerPattern+"|"+contentInstancesPattern+"|"+accessRightsPattern+"|"+accessRightPattern+
            "|"+groupsPattern+"|"+groupPattern+"|"+mgmtObjsPattern+"|"+mgmtObjPattern+"|"+mgmtCmdPattern+"|"+attachedDevicesPattern+
            "|"+attachedDevicePattern+"|"+parametersPattern+"|"+parameterPattern+"|"+execInstancesPattern+"|"+execInstancePattern+
            "|"+locationContainerPattern+")"+"/+subscriptions/*");

    /** Subscription resource uri pattern. */
    private static Pattern subscriptionPattern= Pattern.compile(subscriptionsPattern+"/+"+idPattern+"/*");

    /**
     * Invokes the correct resource controller method.
     * @param method - Request method
     * @param targetID - Request target id
     * @param requestingEntity - Issuer requesting entity
     * @param resource - Resource object
     * @return The generic returned response
     */

    public ResponseConfirm doRequest(String method, String targetID,String requestingEntity, Resource resource ){
        // Convert the resource object to an xml String
        String representation = null;
        if(resource!=null){
            representation = XmlMapper.getInstance().objectToXml(resource);
        }
        // Create a RequestIndication object
        RequestIndication requestIndication = new RequestIndication(method, targetID, requestingEntity, representation);
        // Call doRequest method and return the received response.
        return doRequest(requestIndication);
    }

    /**
     * Invokes required resource controller method.
     * @param requestIndication - The generic request to handle
     * @return The generic returned response
     */

    public ResponseConfirm doRequest(RequestIndication requestIndication) {
         LOGGER.info(requestIndication);
         ResponseConfirm  responseConfirm = new ResponseConfirm();

         // Check requesting entity not null.
         if(requestIndication.getRequestingEntity()==null){
             return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_AUTHORIZATION_NOT_ADDED,"Requesting Entity should not be null"));
         }

         // Remove the first "/" from the request uri if exist.
         if(requestIndication.getTargetID().startsWith("/")){
 requestIndication.setTargetID(requestIndication.getTargetID().substring(1));
         }
         // Remove the last "/" from the request uri if exist.
         if(requestIndication.getTargetID().endsWith("/")){
 requestIndication.setTargetID(requestIndication.getTargetID().substring(0,requestIndication.getTargetID().length()-1));
         }
         readWriteLock.readLock().lock();

         // Retagreting case
 if(match(retargetingPattern,requestIndication.getTargetID())){
             responseConfirm  = new Redirector().retarget(requestIndication);
         }else{
             // Determine the appropriate resource controller
             Controller controller = getResourceController(requestIndication.getTargetID(),requestIndication.getMethod(),requestIndication.getRepresentation());

             // Select the resource controller method and invoke it.
             if(controller!=null){

                     LOGGER.info("ResourceController ["+controller.getClass().getSimpleName()+"]");
                     try{

                             switch(requestIndication.getMethod()){
                             case Constants.METHOD_RETREIVE: responseConfirm = controller.doRetrieve(requestIndication);
                             break;
                             case Constants.METHOD_CREATE: responseConfirm = controller.doCreate(requestIndication);
                             break;
                             case Constants.METHOD_UPDATE: responseConfirm = controller.doUpdate(requestIndication);
                             break;
                             case Constants.METHOD_DELETE: responseConfirm = controller.doDelete(requestIndication);
                             break;
                             case Constants.METHOD_EXECUTE: responseConfirm = controller.doExecute(requestIndication);
                             break;
                             default: responseConfirm = new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Bad Method"));
                             break;
                         }
                     }catch(Exception e){
                         LOGGER.error("Controller Internal Error",e);
                         responseConfirm =  new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_INTERNAL_SERVER_ERROR,"Controller Internal Error"));
                     }
             }else{
                 responseConfirm = new  ResponseConfirm(new ErrorInfo(StatusCode.STATUS_BAD_REQUEST,"Bad TargetID"));
             }
         }
         readWriteLock.readLock().unlock();

         LOGGER.info(responseConfirm);
         return responseConfirm;
     }



    /**
     * Finds requried resource controller based on uri patterns.
     * @param uri - Generic request uri
     * @param method - Generic request method
     * @param representation - Resource representation
     * @return The matched resource controller otherwise null
     */
    public Controller getResourceController(String uri, String method, String representation){

        // Match the resource controller with an uri pattern and return it, otherwise return null*
        if(match(sclBasePattern,uri)){
            return new SclBaseController();
        }

        if(match(sclsPattern,uri) && !method.equals(Constants.METHOD_CREATE)){
            return new SclsController();
        }
        if(match(sclPattern,uri) && !method.equals(Constants.METHOD_CREATE)|| (match(sclsPattern,uri) && method.equals(Constants.METHOD_CREATE))){
            return new SclController();
        }
        if(match(applicationsPattern,uri) && !method.equals(Constants.METHOD_CREATE)){
            return new ApplicationsController();
        }
        // In some cases it is required to know the resource name to detemine the required resource controller.
        // This is the reason why resource representation is added as parameter for some methods.
        if(match(applicationPattern,uri) && !method.equals(Constants.METHOD_CREATE) || (match(applicationsPattern,uri) && method.equals(Constants.METHOD_CREATE) && !representation.contains(":applicationAnnc"))){
            return new ApplicationController();
        }
        if(match(applicationAnncPattern,uri) && !method.equals(Constants.METHOD_CREATE) || (match(applicationsPattern,uri) && method.equals(Constants.METHOD_CREATE) && representation.contains(":applicationAnnc"))){
            return new ApplicationAnncController();
        }
        if(match(ipuPattern,uri)){
            // will forward to a RestClientController or IPUController;
        	return new APocController();
        }
        if(match(containersPattern,uri) && !method.equals(Constants.METHOD_CREATE)){
            return new ContainersController();
        }
        if(match(containerPattern,uri) && !method.equals(Constants.METHOD_CREATE)|| (match(containersPattern,uri) && method.equals(Constants.METHOD_CREATE) && !representation.contains(":containerAnnc"))){
            return new ContainerController();
        }
        if(match(containerAnncPattern,uri)&& !method.equals(Constants.METHOD_CREATE) || (match(containersPattern,uri) && method.equals(Constants.METHOD_CREATE) && representation.contains(":containerAnnc"))){
            return new ContainerAnncController();
        }
        if(match(locationContainerPattern,uri)){
            return new LocationContainerController();
        }
        if(match(locationContainerAnncPattern,uri)){
            return new LocationContainerAnncController();
        }
        if(match(contentInstancesPattern,uri) && !method.equals(Constants.METHOD_CREATE)){
            return new ContentInstancesController();
        }
        if(match(contentInstancePattern,uri) && !method.equals(Constants.METHOD_CREATE)|| (match(contentInstancesPattern,uri) && method.equals(Constants.METHOD_CREATE))){
            return new ContentInstanceController();
        }
        if(match(contentPattern,uri)){
            return new ContentController();
        }
        if(match(subscriptionsPattern,uri) && !method.equals(Constants.METHOD_CREATE)){
            return new SubscriptionsController();
        }
        if(match(subscriptionPattern,uri) && !method.equals(Constants.METHOD_CREATE)|| (match(subscriptionsPattern,uri) && method.equals(Constants.METHOD_CREATE))){
            return new SubscriptionController();
        }
        if(match(accessRightsPattern,uri) && !method.equals(Constants.METHOD_CREATE)){
            return new AccessRightsController();
        }
        if(match(accessRightPattern,uri) && !method.equals(Constants.METHOD_CREATE) || (match(accessRightsPattern,uri) && method.equals(Constants.METHOD_CREATE) && !representation.contains(":accessRightAnnc"))){
            return new AccessRightController();
        }
        if(match(accessRightAnncPattern,uri) && !method.equals(Constants.METHOD_CREATE) || (match(accessRightsPattern,uri) && method.equals(Constants.METHOD_CREATE) && representation.contains(":accessRightAnnc"))){
            return new AccessRightAnncController();
        }
        if(match(groupsPattern,uri) && !method.equals(Constants.METHOD_CREATE)){
            return new GroupsController();
        }
        if(match(groupPattern,uri)&& !method.equals(Constants.METHOD_CREATE) || (match(groupsPattern,uri) && method.equals(Constants.METHOD_CREATE) && !representation.contains(":groupAnnc"))){
            return new GroupController();
        }
        if(match(groupAnncPattern,uri) && !method.equals(Constants.METHOD_CREATE) || (match(groupsPattern,uri) && method.equals(Constants.METHOD_CREATE) && representation.contains(":groupAnnc"))){
            return new GroupAnncController();
        }
        if(match(membersContentPattern,uri)){
            return new MembersContentController();
        }
        if(match(discoveryPattern,uri)){
            return new DiscoveryController();
        }
        if(match(mgmtObjsPattern,uri)){
            return new MgmtObjsController();
        }
        if(match(mgmtObjPattern,uri)){
            return new MgmtObjController();
        }
        if(match(parametersPattern,uri)){
            return new ParametersController();
        }
        if(match(parameterPattern,uri)){
            return null;
        }
        if(match(mgmtCmdPattern,uri)){
            return new MgmtCmdController();
        }
        if(match(execInstancesPattern,uri)){
            return new ExecInstancesController();
        }
        if(match(execInstancePattern,uri)){
            return new ExecInstanceController();
        }
        if(match(attachedDevicesPattern,uri)){
            return new AttachedDevicesController();
        }
        if(match(attachedDevicePattern,uri)){
            return new AttachedDeviceController();
        }
        if(match(notificationChannelsPattern,uri)){
            return new NotificationChannelsController();
        }
        if(match(notificationChannelPattern,uri)){
            return new NotificationChannelController();
        }
        if(match(m2mPocsPattern,uri)){
            return new M2MPocsController();
        }
        if(match(m2mPocPattern,uri)){
            return new M2MPocController();
        }

        return null;
    }

    /**
     * match uri with a pattern.
     * @param pattern - pattern
     * @param uri - resource uri
     * @return true if matched, otherwise false.
     */
    public static boolean match(Pattern pattern, String uri) {
        // Match uri with pattern
        Matcher m = pattern.matcher(uri);
        if (!m.matches()){
            return false;
        }
        return true;
    }
}
