/**
 *
 */
package com.sabmiller.core.b2b.dao;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import com.sabmiller.core.model.MaxOrderQtyModel;


/**
 * @author Siddarth
 *
 */
public class CUBMaxOrderQuantityDaoImpl implements CUBMaxOrderQuantityDao
{
	private final String GET_MAX_ORDER_QUANTITIES_FOR_PRODUCT = "select {" + MaxOrderQtyModel.PK + "} from {"
			+ MaxOrderQtyModel._TYPECODE + "} where {" + MaxOrderQtyModel.PRODUCT + "} =?productCode";

	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;


	@Override
	public List<MaxOrderQtyModel> getCUBMaxOrderQuantityForProductCode(String productCode)
	{
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(GET_MAX_ORDER_QUANTITIES_FOR_PRODUCT);
		final Map<String, Object> params = new HashMap<>();
		params.put("productCode", productCode);
		fsq.addQueryParameters(params);
		final SearchResult<MaxOrderQtyModel> result = flexibleSearchService.search(fsq);
		return result.getCount() > 0 ? result.getResult() : null;
	}

}
