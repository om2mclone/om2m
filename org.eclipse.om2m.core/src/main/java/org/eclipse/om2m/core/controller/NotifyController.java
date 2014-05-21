package org.eclipse.om2m.core.controller;

import org.eclipse.om2m.commons.resource.Application;
import org.eclipse.om2m.commons.resource.ErrorInfo;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.rest.RequestIndication;
import org.eclipse.om2m.commons.rest.ResponseConfirm;
import org.eclipse.om2m.core.comm.RestClient;
import org.eclipse.om2m.core.constants.Constants;
import org.eclipse.om2m.core.dao.DAOFactory;
import org.eclipse.om2m.core.redirector.Redirector;

public class NotifyController extends Controller{

    @Override
    public ResponseConfirm doCreate(RequestIndication requestIndication) {
        String sclId = requestIndication.getTargetID().split("/")[0];
        if(Constants.SCL_ID.equals(sclId)){
                String appId = requestIndication.getTargetID().split("/")[0];
                Application application = DAOFactory.getApplicationDAO().find(Constants.SCL_ID+"/applications/"+appId);
                requestIndication.setBase(application.getAPoC());
                return new RestClient().sendRequest(requestIndication);
        }else{
            return new Redirector().retarget(requestIndication);
        }
    }

    @Override
    public ResponseConfirm doRetrieve(RequestIndication requestIndication) {
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed"));

    }

    @Override
    public ResponseConfirm doUpdate(RequestIndication requestIndication) {
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed"));

    }

    @Override
    public ResponseConfirm doDelete(RequestIndication requestIndication) {
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed"));
    }

    @Override
    public ResponseConfirm doExecute(RequestIndication requestIndication) {
        return new ResponseConfirm(new ErrorInfo(StatusCode.STATUS_METHOD_NOT_ALLOWED,requestIndication.getMethod()+" Method is not allowed"));
    }

}
