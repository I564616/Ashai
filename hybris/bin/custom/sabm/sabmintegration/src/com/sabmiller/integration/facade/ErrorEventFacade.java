package com.sabmiller.integration.facade;

import com.sabmiller.integration.enums.ErrorEventType;
import com.sabmiller.integration.model.WebServiceLogModel;

public interface ErrorEventFacade {

    String createErrorEntry(final Throwable cause, final String integration, final WebServiceLogModel webServiceLogModel,
                            final ErrorEventType errorEventType, final String additionalDetails);

}
