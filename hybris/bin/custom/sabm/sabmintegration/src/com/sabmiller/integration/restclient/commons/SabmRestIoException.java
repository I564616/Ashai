package com.sabmiller.integration.restclient.commons;

import com.sabmiller.integration.model.WebServiceLogModel;

import java.io.IOException;

public class SabmRestIoException extends IOException {
    private WebServiceLogModel errorLog;

    public SabmRestIoException(Throwable e, WebServiceLogModel error){
        super(e);
        this.errorLog = error;
    }

    public WebServiceLogModel getErrorLog() {
        return errorLog;
    }
}
