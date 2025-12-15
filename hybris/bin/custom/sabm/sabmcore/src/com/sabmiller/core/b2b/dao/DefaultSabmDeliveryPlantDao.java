/**
 *
 */
package com.sabmiller.core.b2b.dao;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;

import com.sabmiller.core.model.PlantModel;


/**
 * The Class DefaultSabmDeliveryPlantDao.
 */
public class DefaultSabmDeliveryPlantDao implements SabmDeliveryPlantDao
{

	/** The Constant SEARCH_PLANT. */
	private static final String SEARCH_PLANT = "SELECT {" + PlantModel.PK + "} " + "FROM {" + PlantModel._TYPECODE + "} WHERE {"
			+ PlantModel.PLANTID + "}=?plantId";

	/** The flexible search service. */
	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.dao.SabmDeliveryPlantDao#lookupPlant(java.lang.String)
	 */
	@Override
	public PlantModel lookupPlant(final String plantId)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("plantId", plantId);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(SEARCH_PLANT, params);
		final SearchResult<PlantModel> result = flexibleSearchService.search(fsq);
		return result.getCount() > 0 ? result.getResult().get(0) : null;
	}
}
