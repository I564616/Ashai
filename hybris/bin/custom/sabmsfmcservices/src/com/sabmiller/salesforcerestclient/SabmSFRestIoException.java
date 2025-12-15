package com.sabmiller.salesforcerestclient;

import com.sabmiller.integration.model.WebServiceLogModel;

import java.io.IOException;

public class SabmSFRestIoException extends IOException {
    private WebServiceLogModel errorLog;

    public SabmSFRestIoException(Throwable e, WebServiceLogModel error){
        super(e);
        this.errorLog = error;
    }

    public WebServiceLogModel getErrorLog() {
        return errorLog;
    }
}
