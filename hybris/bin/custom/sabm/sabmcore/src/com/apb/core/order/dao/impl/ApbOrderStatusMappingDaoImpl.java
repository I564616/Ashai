package com.apb.core.order.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.apb.core.constants.ApbQueryConstant;
import com.apb.core.model.OrderStatusMappingModel;
import com.apb.core.order.dao.ApbOrderStatusMappingDao;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;


public class ApbOrderStatusMappingDaoImpl implements ApbOrderStatusMappingDao
{
	/** The flexible search service. */
	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Override
	public String getOrderMapping(String backendStatusCode)
	{
		validateParameterNotNull(backendStatusCode, "status must not be null!");

		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_ORDER_STATUS_MAPPING);

		params.put("dynamicsStatusCode", backendStatusCode);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<OrderStatusMappingModel> result = flexibleSearchService.search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0).getStatusCode();
		}
		return null;
	}

	@Override
	public String getDisplayOrderStatus(String statusCode,String companyCode)
	{
		validateParameterNotNull(statusCode, "status must not be null!");
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_ORDER_DISPLAY_STATUS_MAPPING);

		params.put("statusCode", statusCode);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<OrderStatusMappingModel> result = flexibleSearchService.search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			if(companyCode.equalsIgnoreCase("apb")) 
			{
				return result.getResult().get(0).getApbDisplayStatus();
			}
			else
			{
				return result.getResult().get(0).getDisplayStatus();
			}
			
		}
		return null;
	}

}
