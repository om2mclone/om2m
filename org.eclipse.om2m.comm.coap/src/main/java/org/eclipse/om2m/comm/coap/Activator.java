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
 *     Khalil Drira - Management and initial specification.
 *     Samir Medjiah - Conception and implementation of the CoAP binding.
 *     Rim Frikha - Implementation of the CoAP binding.
 ******************************************************************************/

package org.eclipse.om2m.comm.coap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.comm.service.RestClientService;
import org.eclipse.om2m.core.service.SclService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class Activator  implements BundleActivator{
	
	   /** Logger */
	   private static Log LOGGER = LogFactory.getLog(Activator.class);	       
	   /** SCL service tracker */
	   private ServiceTracker<Object, Object> sclServiceTracker;	    
	   /**the coap server*/
	   CoapServer server;

	   public void start(BundleContext bundleContext) throws Exception {
	    	
	    	// Register the Rest CoAP Client 
	        LOGGER.info("Register CoAP RestClientService..");
	        bundleContext.registerService(RestClientService.class.getName(), new CoapClient(), null);
	        LOGGER.info("CoAP RestClientService is registered.");	    
	        
	    	// Start the CoAP server 
	        server= new CoapServer();	  	       
	        server.startServer();

	        // Track the SCL service
	        sclServiceTracker = new ServiceTracker<Object, Object>(bundleContext, SclService.class.getName(), null) {
	            public void removedService(ServiceReference<Object> reference, Object service) {
	                LOGGER.info("SclService removed");
	                try {
	                    CoapMessageDeliverer.setScl((SclService) service);
	                } catch (IllegalArgumentException e) {
	                    LOGGER.error("Error removing SclService",e);
	                }
	            }
	            public Object addingService(ServiceReference<Object> reference) {
	                LOGGER.info("SclService discovered");
	                SclService scl = (SclService) this.context.getService(reference);
	                try {
	                    CoapMessageDeliverer.setScl(scl);
	                } catch (Exception e) {
	                    LOGGER.error("Error adding SclService",e);

	                }
	                return scl;
	            }
	        };
	        
	        // Open service trackers 
	        sclServiceTracker.open();
	        LOGGER.info("SclService opened");
	        
         
	    }
	    @Override
	    public void stop(BundleContext bundleContext) throws Exception {
	    }
}
