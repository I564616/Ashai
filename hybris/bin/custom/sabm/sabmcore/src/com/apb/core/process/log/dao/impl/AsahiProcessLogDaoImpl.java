package com.apb.core.process.log.dao.impl;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.apb.core.constants.ApbQueryConstant;
import com.sabmiller.core.enums.AsahiProcessObject;
import com.apb.core.model.ProcessingJobLogModel;
import com.apb.core.process.log.dao.AsahiProcessLogDao;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

public class AsahiProcessLogDaoImpl implements AsahiProcessLogDao {

	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Override
	public ProcessingJobLogModel findProcessLogById(AsahiProcessObject objectType, String objectId) {

		final FlexibleSearchQuery query = new FlexibleSearchQuery(ApbQueryConstant.GET_PROCESS_LOG_FOR_OBJECT);
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("objectType", objectType);
		params.put("objectId", objectId);
		query.addQueryParameters(params);
		final SearchResult<ProcessingJobLogModel> result = flexibleSearchService.search(query);
		if (CollectionUtils.isNotEmpty(result.getResult())) {
			return result.getResult().get(0);
		}
		return null;
	}

}
