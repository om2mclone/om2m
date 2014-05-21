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
 *   Specific Sample controller to perform requests on Lamps.
 *  @author <ul>
 *         <li>Yassine Banouar < ybanouar@laas.fr > < yassine.banouar@gmail.com ></li>
 *         <li>Mahdi Ben Alaya < ben.alaya@laas.fr > < benalaya.mahdi@gmail.com ></li>
 *         </ul>
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.ipu.service.IpuService;

public class SampleController implements IpuService {
    /** Logger */
    private static Log LOGGER = LogFactory.getLog(SampleController.class);

    /** Returns the implemented Application Point of Contact id */
    public String getAPOCPath() {
        return Lamp.APOCPATH;
    }

    /**
     * Executes a resource on Lamps.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doExecute(RequestIndication requestIndication) {
        String[] info = requestIndication.getTargetID().split("/");
        String lampId = info[info.length-3];
        String type = lampId.split("_")[0];
        String value = info[info.length-1];
        try {
            if(Lamp.TYPE.equals(type)) {
                SampleMonitor.setLampState(lampId, value);
                return new ResponseConfirm(StatusCode.STATUS_OK);
            } else{
                return new ResponseConfirm(StatusCode.STATUS_NOT_FOUND,type+" Not found");
            }
        } catch (Exception e) {
            LOGGER.error("IPU Lamp Error",e);
            return new ResponseConfirm(StatusCode.STATUS_NOT_IMPLEMENTED,"IPU Lamp Error");
        }
    }

    /**
     * Retrieves a resource on lamps.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doRetrieve(RequestIndication requestIndication) {
        String[] info = requestIndication.getTargetID().split("/");
        String appId = info[info.length-2];
        boolean value;
        try {
            // Get the boolean Value for the lamp
            value = SampleMonitor.getLampValue(appId);
            String content = Lamp.getStateRep(appId, value);
            return new ResponseConfirm(StatusCode.STATUS_OK,content);

        } catch (Exception e) {
            LOGGER.error("IPU Sample Error",e);
            return new ResponseConfirm(StatusCode.STATUS_NOT_IMPLEMENTED,"IPU Sample Error" );
        }
    }

    /**
     * Creates a resource on Lamps.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doCreate(RequestIndication requestIndication) {
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method not Implemented"));
    }

    /**
     * Updates a resource on Lamps.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doUpdate(RequestIndication requestIndication) {
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method not Implemented"));
    }

    /**
     * Deletes a resource on Lamps.
     * @param requestIndication - The generic request to handle.
     * @return The generic returned response.
     */
    public ResponseConfirm doDelete(RequestIndication requestIndication) {
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_NOT_IMPLEMENTED,requestIndication.getMethod()+" Method not Implemented"));
    }

}