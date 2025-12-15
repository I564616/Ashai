package com.sabmiller.integration.service;

import com.sabmiller.integration.enums.ErrorEventType;
import com.sabmiller.integration.model.WebServiceLogModel;

public interface ErrorEventService {

    String createErrorEntry(final Throwable cause, final String integration, final WebServiceLogModel webServiceLogModel,
                    final ErrorEventType errorEventType, final String additionalDetails);

    void cleanupOldEntries();
}
