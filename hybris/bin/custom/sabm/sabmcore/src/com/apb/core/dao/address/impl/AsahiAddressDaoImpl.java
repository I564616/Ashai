package com.apb.core.dao.address.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.apb.core.constants.ApbQueryConstant;
import com.apb.core.dao.address.AsahiAddressDao;
import com.sabmiller.core.model.AddressStatusMappingModel;
import com.apb.core.model.OrderStatusMappingModel;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

public class AsahiAddressDaoImpl implements AsahiAddressDao
{

	@Resource
	FlexibleSearchService flexibleSearchService;
	
	@Override
	public String getAddressStatusMapping(String backendStatusCode) 
	{
		validateParameterNotNull(backendStatusCode, "status must not be null!");

		final Map<String, Object> params = new HashMap<>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_ADDRESS_STATUS_MAPPING);

		params.put("dynamicsStatusCode", backendStatusCode);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<AddressStatusMappingModel> result = flexibleSearchService.search(query);
		
		if(null!= result && CollectionUtils.isNotEmpty(result.getResult())){
			return result.getResult().get(0).getStatusCode();
		}
		return null;
	}
	
	
}
