package com.apb.core.process.log.dao;

import com.sabmiller.core.enums.AsahiProcessObject;
import com.apb.core.model.ProcessingJobLogModel;

@FunctionalInterface
public interface AsahiProcessLogDao {

	ProcessingJobLogModel findProcessLogById(AsahiProcessObject objectType, String objectId);

}
