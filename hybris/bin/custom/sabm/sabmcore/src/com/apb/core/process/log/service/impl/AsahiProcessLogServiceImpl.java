package com.apb.core.process.log.service.impl;

import jakarta.annotation.Resource;

import com.sabmiller.core.enums.AsahiProcessObject;
import com.apb.core.model.ProcessingJobLogModel;
import com.apb.core.process.log.dao.AsahiProcessLogDao;
import com.apb.core.process.log.service.AsahiProcessLogService;

public class AsahiProcessLogServiceImpl implements AsahiProcessLogService {

	@Resource(name = "processLogDao")
	AsahiProcessLogDao processLogDao;

	@Override
	public ProcessingJobLogModel findProcessLogById(AsahiProcessObject objectType, String objectId) {
		return processLogDao.findProcessLogById(objectType, objectId);
	}

}
