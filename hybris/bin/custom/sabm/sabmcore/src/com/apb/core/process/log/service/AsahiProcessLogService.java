package com.apb.core.process.log.service;

import com.sabmiller.core.enums.AsahiProcessObject;
import com.apb.core.model.ProcessingJobLogModel;

@FunctionalInterface
public interface AsahiProcessLogService {
	
	ProcessingJobLogModel findProcessLogById(AsahiProcessObject objectType, String objectId);

}
