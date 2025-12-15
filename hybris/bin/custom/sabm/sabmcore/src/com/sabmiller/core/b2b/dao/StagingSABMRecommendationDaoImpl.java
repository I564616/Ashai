/**
 *
 */
package com.sabmiller.core.b2b.dao;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

import jakarta.annotation.Resource;

import com.sabmiller.core.model.StagingSABMRecommendationModel;


/**
 * @author Siddarth
 *
 */
public class StagingSABMRecommendationDaoImpl implements StagingSABMRecommendationDao
{

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.dao.StagingSABMRecommendationDao#getAllRecords()
	 */

	private final String GETALLROWS = "select {" + StagingSABMRecommendationModel.PK + "} from {"
			+ StagingSABMRecommendationModel._TYPECODE + "}";

	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Override
	public List<StagingSABMRecommendationModel> getAllRecords()
	{
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(GETALLROWS);
		final SearchResult<StagingSABMRecommendationModel> result = flexibleSearchService.search(fsq);
		return result.getResult();
	}
}
