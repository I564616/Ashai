package com.sabmiller.facades.search.converters.populator;

import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseSortsPopulator;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * Custom class to populate visible attribute in SortData object. Attribute was included in SolrSortModel object but never used.
 *
 * Also see the following for the OOTB implementation:
 * @see de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseSortsPopulator
 *
 * Created by wei.yang.ng on 1/08/2016.
 */
public class SABMSearchResponseSortsPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SEARCH_RESULT_TYPE, ITEM>
		extends SearchResponseSortsPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SEARCH_RESULT_TYPE, ITEM>
{
	@Override
	public void populate(
			final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SearchQuery, IndexedTypeSort, SEARCH_RESULT_TYPE> source,
			final SearchPageData<ITEM> target)
	{
		target.setSorts(buildSorts(source));
	}

	@Override
	protected void addSortData(final List<SortData> result, final String currentSortCode, final IndexedTypeSort sort) {
		final SortData sortData = createSortData();
		sortData.setCode(sort.getCode());
		sortData.setName(sort.getName());

		if (currentSortCode != null && currentSortCode.equals(sort.getCode()))
		{
			sortData.setSelected(true);
		}
		//As per part of Upgrade
		/*
		 * if (sort.getSort() != null) { sortData.setVisible(sort.getSort().getVisible()); }
		 */

		result.add(sortData);
	}
}
