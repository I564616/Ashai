package com.sabmiller.integration.service;

import com.sabmiller.webservice.model.MasterImportModel;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.List;

public interface MasterRecordService {

    PerformResult processRecords(final List<MasterImportModel> masterRecords, final String serviceUrl);
}
