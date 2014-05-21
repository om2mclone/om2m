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

import org.eclipse.om2m.commons.resource.AccessRight;
import org.eclipse.om2m.commons.resource.AccessRightAnnc;
import org.eclipse.om2m.commons.resource.AccessRights;
import org.eclipse.om2m.commons.resource.Application;
import org.eclipse.om2m.commons.resource.ApplicationAnnc;
import org.eclipse.om2m.commons.resource.Applications;
import org.eclipse.om2m.commons.resource.AttachedDevice;
import org.eclipse.om2m.commons.resource.AttachedDevices;
import org.eclipse.om2m.commons.resource.Container;
import org.eclipse.om2m.commons.resource.ContainerAnnc;
import org.eclipse.om2m.commons.resource.Containers;
import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.commons.resource.ContentInstances;
import org.eclipse.om2m.commons.resource.ExecInstance;
import org.eclipse.om2m.commons.resource.ExecInstances;
import org.eclipse.om2m.commons.resource.Group;
import org.eclipse.om2m.commons.resource.GroupAnnc;
import org.eclipse.om2m.commons.resource.Groups;
import org.eclipse.om2m.commons.resource.LocationContainer;
import org.eclipse.om2m.commons.resource.LocationContainerAnnc;
import org.eclipse.om2m.commons.resource.M2MPoc;
import org.eclipse.om2m.commons.resource.M2MPocs;
import org.eclipse.om2m.commons.resource.MgmtCmd;
import org.eclipse.om2m.commons.resource.MgmtObj;
import org.eclipse.om2m.commons.resource.MgmtObjs;
import org.eclipse.om2m.commons.resource.NotificationChannel;
import org.eclipse.om2m.commons.resource.NotificationChannels;
import org.eclipse.om2m.commons.resource.Parameters;
import org.eclipse.om2m.commons.resource.Resource;
import org.eclipse.om2m.commons.resource.Resources;
import org.eclipse.om2m.commons.resource.Scl;
import org.eclipse.om2m.commons.resource.SclBase;
import org.eclipse.om2m.commons.resource.Scls;
import org.eclipse.om2m.commons.resource.Subscription;
import org.eclipse.om2m.commons.resource.Subscriptions;

/**
 * Pattern Factory
 *
 * <p>
 * DAOFactory is used to build our object instances data access
 *
 * @author <ul>
 *         <li>Yessine Feki < yfeki@laas.fr > < yessine.feki@ieee.org ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>  
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */


public class DAOFactory {

    /**
     * Returns an instance of the {@link SclBaseDAO}
     * @return {@link SclBaseDAO} instance
     */
    public static DAO<SclBase> getSclBaseDAO(){
        return new SclBaseDAO();
    }

    /**
     * Returns an instance of the {@link SclsDAO}
     * @return {@link SclsDAO} instance
     */
    public static DAO<Scls> getSclsDAO(){
        return new SclsDAO();
    }

    /**
     * Returns an instance of the {@link SclDAO}
     * @return {@link SclDAO} instance
     */
    public static DAO<Scl> getSclDAO(){
        return new SclDAO();
    }

    /**
     * Returns an instance of the {@link ApplicationDAO}
     * @return {@link ApplicationDAO} instance
     */
    public static DAO<Application> getApplicationDAO(){
        return new ApplicationDAO();
    }

    /**
     * Returns an instance of the {@link ApplicationsDAO}
     * @return {@link ApplicationsDAO} instance
     */
    public static DAO<Applications> getApplicationsDAO(){
        return new ApplicationsDAO();
    }

    /**
     * Returns an instance of the {@link SubscriptionsDAO}
     * @return {@link SubscriptionsDAO} instance
     */
    public static DAO<Subscriptions> getSubscriptionsDAO() {
        return new SubscriptionsDAO();
    }

    /**
     * Returns an instance of the {@link AccessRightsDAO}
     * @return {@link AccessRightsDAO} instance
     */
    public static DAO<AccessRights> getAccessRightsDAO() {
        return new AccessRightsDAO();
    }

    /**
     * Returns an instance of the {@link GroupsDAO}
     * @return {@link GroupsDAO} instance
     */
    public static DAO<Groups> getGroupsDAO() {
        return new GroupsDAO();
    }

    /**
     * Returns an instance of the {@link ContainersDAO}
     * @return {@link ContainersDAO} instance
     */
    public static DAO<Containers> getContainersDAO() {
        return new ContainersDAO();
    }

    /**
     * Returns an instance of the {@link MgmtObjsDAO}
     * @return {@link MgmtObjsDAO} instance
     */
    public static DAO<MgmtObjs> getMgmtObjsDAO() {
        return new MgmtObjsDAO();
    }

    /**
     * Returns an instance of the {@link NotificationChannelsDAO}
     * @return {@link NotificationChannelsDAO} instance
     */
    public static DAO<NotificationChannels> getNotificationChannelsDAO() {
        return new NotificationChannelsDAO();
    }

    /**
     * Returns an instance of the {@link ContentInstancesDAO}
     * @return {@link ContentInstancesDAO} instance
     */
    public static DAO<ContentInstances> getContentInstancesDAO() {
        return new ContentInstancesDAO();
    }

    /**
     * Returns an instance of the {@link ContentInstanceDAO}
     * @return {@link ContentInstanceDAO} instance
     */
    public static DAO<ContentInstance> getContentInstanceDAO() {
        return new ContentInstanceDAO();
    }

    /**
     * Returns an instance of the {@link AttachedDevicesDAO}
     * @return {@link AttachedDevicesDAO} instance
     */
    public static DAO<AttachedDevices> getAttachedDevicesDAO() {
        return new AttachedDevicesDAO();
    }

    /**
     * Returns an instance of the {@link AccessRightDAO}
     * @return {@link AccessRightDAO} instance
     */
    public static DAO<AccessRight> getAccessRightDAO() {
        return new AccessRightDAO();
    }

    /**
     * Returns an instance of the {@link SubscriptionDAO}
     * @return {@link SubscriptionDAO} instance
     */
    public static DAO<Subscription> getSubscriptionDAO() {
        return new SubscriptionDAO();
    }

    /**
     * Returns an instance of the {@link GroupDAO}
     * @return {@link GroupDAO} instance
     */
    public static DAO<Group> getGroupDAO() {
        return new GroupDAO();
    }

    /**
     * Returns an instance of the {@link AttachedDeviceDAO}
     * @return {@link AttachedDeviceDAO} instance
     */
    public static DAO<AttachedDevice> getAttachedDeviceDAO() {
        return new AttachedDeviceDAO();
    }

    /**
     * Returns an instance of the {@link ApplicationAnncDAO}
     * @return {@link ApplicationAnncDAO} instance
     */
    public static DAO<ApplicationAnnc> getApplicationAnncDAO() {
        return new ApplicationAnncDAO();
    }

    /**
     * Returns an instance of the {@link ContainerDAO}
     * @return {@link ContainerDAO} instance
     */
    public static DAO<Container> getContainerDAO() {
        return new ContainerDAO();
    }

    /**
     * Returns an instance of the {@link AccessRightAnncDAO}
     * @return {@link AccessRightAnncDAO} instance
     */
    public static DAO<AccessRightAnnc> getAccessRightAnncDAO(){
        return new AccessRightAnncDAO();
    }

    /**
     * Returns an instance of the {@link LocationContainerAnncDAO}
     * @return {@link LocationContainerAnncDAO} instance
     */
    public static DAO<LocationContainerAnnc> getLocationContainerAnncDAO(){
        return new LocationContainerAnncDAO();
    }

    /**
     * Returns an instance of the {@link LocationContainerDAO}
     * @return {@link LocationContainerDAO} instance
     */
    public static DAO<LocationContainer> getLocationContainerDAO(){
        return new LocationContainerDAO();
    }

    /**
     * Returns an instance of the {@link MgmtObjDAO}
     * @return {@link MgmtObjDAO} instance
     */
    public static DAO<MgmtObj> getMgmtObjDAO(){
        return new MgmtObjDAO();
    }

    /**
     * Returns an instance of the {@link MgmtCmdDAO}
     * @return {@link MgmtCmdDAO} instance
     */
    public static DAO<MgmtCmd> getMgmtCmdDAO(){
        return new MgmtCmdDAO();
    }

    /**
     * Returns an instance of the {@link ExecInstancesDAO}
     * @return {@link ExecInstancesDAO} instance
     */
    public static DAO<ExecInstances> getExecInstancesDAO() {
        return new ExecInstancesDAO();
    }

    /**
     * Returns an instance of the {@link ExecInstanceDAO}
     * @return {@link ExecInstanceDAO} instance
     */
    public static DAO<ExecInstance> getExecInstanceDAO() {
        return new ExecInstanceDAO();
    }

    /**
     * Returns an instance of the {@link NotificationChannelDAO}
     * @return {@link NotificationChannelDAO} instance
     */
    public static DAO<NotificationChannel> getNotificationChannelDAO() {
        return new NotificationChannelDAO();
    }

    /**
     * Returns an instance of the {@link M2MPocsDAO}
     * @return {@link M2MPocsDAO} instance
     */
    public static DAO<M2MPocs> getM2MPocsDAO(){
        return new M2MPocsDAO();
    }

    /**
     * Returns an instance of the {@link M2MPocDAO}
     * @return {@link M2MPocDAO} instance
     */
    public static DAO<M2MPoc> getM2MPocDAO(){
        return new M2MPocDAO();
    }

    /**
     * Returns an instance of the {@link ContainerAnncDAO}
     * @return {@link ContainerAnncDAO} instance
     */
    public static DAO<ContainerAnnc> getContainerAnncDAO(){
        return new ContainerAnncDAO();
    }

    /**
     * Returns an instance of the {@link GroupAnncDAO}
     * @return {@link GroupAnncDAO} instance
     */
    public static DAO<GroupAnnc> getGroupAnncDAO(){
        return new GroupAnncDAO();
    }

    /**
     * Returns an instance of the {@link ParametersDAO}
     * @return {@link ParametersDAO} instance
     */
    public static DAO<Parameters> getParametersDAO(){
        return new ParametersDAO();
    }

    /**
     * Returns an instance of the ResourcesDAO
     * @return {@link ResourcesDAO} instance
     */
    public static DAO<Resources> getResourcesDAO(){
        return new ResourcesDAO();
    }

    /**
     * Returns an instance of the {@link ResourceDAO}
     * @return {@link ResourceDAO} instance
     */
    public static DAO<Resource> getResourceDAO(){
        return new ResourceDAO();
    }
}
