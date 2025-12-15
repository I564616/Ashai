package com.sabmiller.integration.facade.impl;

import com.sabmiller.integration.enums.ErrorEventType;
import com.sabmiller.integration.facade.ErrorEventFacade;
import com.sabmiller.integration.model.WebServiceLogModel;
import com.sabmiller.integration.service.ErrorEventService;

public class ErrorEventFacadeImpl implements ErrorEventFacade {

    private ErrorEventService errorEventService;

    @Override
    public String createErrorEntry(final Throwable cause, final String integration,
                                   final WebServiceLogModel webServiceLogModel,
                                   final ErrorEventType errorEventType,
                                   final String additionalDetails) {
        return errorEventService.createErrorEntry(cause, integration, webServiceLogModel, errorEventType, additionalDetails);
    }

    public void setErrorEventService(final ErrorEventService errorEventService) {
        this.errorEventService = errorEventService;
    }
}
