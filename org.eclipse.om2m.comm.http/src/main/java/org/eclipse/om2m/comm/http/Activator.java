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
package org.eclipse.om2m.comm.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.comm.service.RestClientService;
import org.eclipse.om2m.core.service.SclService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

/**
 *  Manages the starting and stopping of the bundle.
 *  @author <ul>
 *            <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         </ul>
 */
public class Activator implements BundleActivator {
    /** Logger */
    private static Log LOGGER = LogFactory.getLog(Activator.class);
    /** HTTP service tracker */
    private ServiceTracker<Object, Object> httpServiceTracker;
    /** SCL service tracker */
    private ServiceTracker<Object, Object> sclServiceTracker;
    /** Sclbase listening context */
    private static final String sclBaseContext = System.getProperty("org.eclipse.om2m.sclBaseContext","/om2m");

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        // Register the Rest HTTP Client
        LOGGER.info("Register HTTP RestClientService..");
        bundleContext.registerService(RestClientService.class.getName(), new RestHttpClient(), null);
        LOGGER.info("HTTP RestClientService is registered.");

        // track the HTTP service
        httpServiceTracker = new ServiceTracker<Object, Object>(bundleContext, HttpService.class.getName(), null) {
            public void removedService(ServiceReference<Object> reference, Object service) {
                LOGGER.info("HttpService removed");
                try {
                    LOGGER.info("Unregister "+sclBaseContext+" http context");
                    ((HttpService) service).unregister(sclBaseContext);
                } catch (IllegalArgumentException e) {
                    LOGGER.error("Error unregistring SclServlet",e);
                }
            }

            public Object addingService(ServiceReference<Object> reference) {
                LOGGER.info("HttpService discovered");
                HttpService httpService = (HttpService) this.context.getService(reference);
                try {
                    LOGGER.info("Register "+sclBaseContext+" context");
                    httpService.registerServlet(sclBaseContext, new RestHttpServlet(), null,null);
                } catch (Exception e) {
                    LOGGER.error("Error registering SclServlet",e);
                }
                return httpService;
            }
        };
        httpServiceTracker.open();

        // track the SCL service
        sclServiceTracker = new ServiceTracker<Object, Object>(bundleContext, SclService.class.getName(), null) {
            public void removedService(ServiceReference<Object> reference, Object service) {
                LOGGER.info("SclService removed");
                try {
                    RestHttpServlet.setScl((SclService) service);
                } catch (IllegalArgumentException e) {
                    LOGGER.error("Error removing SclService",e);
                }
            }

            public Object addingService(ServiceReference<Object> reference) {
                LOGGER.info("SclService discovered");
                SclService scl = (SclService) this.context.getService(reference);
                try {
                    RestHttpServlet.setScl(scl);
                } catch (Exception e) {
                    LOGGER.error("Error adding SclService",e);

                }
                return scl;
            }
        };
        // Open service trackers
        sclServiceTracker.open();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
    }

}
