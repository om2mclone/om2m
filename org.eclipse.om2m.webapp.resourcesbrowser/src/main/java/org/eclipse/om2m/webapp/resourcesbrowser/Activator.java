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
package org.eclipse.om2m.webapp.resourcesbrowser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
/**
 *  Manages the starting and stopping of the bundle.
 *  @author <ul>
 * 		   <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>         
 *         </ul>
 */
public class Activator implements BundleActivator {
	/** logger */
	private static Log LOGGER = LogFactory.getLog(Activator.class);
	public static String globalContext = System.getProperty("org.eclipse.om2m.globalContext","");
	public static String uiContext = System.getProperty("org.eclipse.om2m.webInterfaceContext","/");
	public static String sep ="/";
	/** HTTP service tracker */
	private ServiceTracker<Object, Object> httpServiceTracker;
	
	@Override
	public void start(BundleContext context) throws Exception {
		if(uiContext.equals("/")){
			sep="";
		}
		
		httpServiceTracker = new ServiceTracker<Object, Object>(context, HttpService.class.getName(), null) {
	      public void removedService(ServiceReference<Object> reference, Object service) {
			LOGGER.info("HttpService removed");
	        try {
				LOGGER.info("Unregister "+uiContext+sep+" http context");
	           ((HttpService) service).unregister(uiContext+sep);
	        } catch (IllegalArgumentException e) {
		        LOGGER.error("Error unregistring webapp servlet",e);
	        }
	      }

	      public Object addingService(ServiceReference<Object> reference) {
			LOGGER.info("HttpService discovered");
	        HttpService httpService = (HttpService) context.getService(reference);
	        try{
			LOGGER.info("Register "+uiContext+sep+" http context");
	          httpService.registerServlet(uiContext+sep, new WelcomeServlet(), null, null);
			  httpService.registerResources(uiContext+sep+"welcome", uiContext+sep+"webapps", null);
	        } catch (Exception e) {
	          LOGGER.error("Error registring webapp servlet",e);
	        }
	        return httpService;
	      }
	    };
	    httpServiceTracker.open();
	  }
	
	@Override
	public void stop(BundleContext context) throws Exception {
	}
}


