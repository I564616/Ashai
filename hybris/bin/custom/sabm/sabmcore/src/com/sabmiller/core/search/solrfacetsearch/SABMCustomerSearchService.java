/**
 *
 */
package com.sabmiller.core.search.solrfacetsearch;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import com.sabmiller.facades.search.data.CustomerSearchPageData;


/**
 * SABMCCustomerSearchService
 */
public interface SABMCustomerSearchService<STATE, ITEM, RESULT extends CustomerSearchPageData<STATE, ITEM>>
{

	/**
	 * The query object allows more complex queries using facet selection. The SearchQueryData must have been obtained
	 * from the results of a call to {@link #textSearch(String,PageableData)}
	 *
	 *
	 * @param searchQueryData
	 *           the search query object
	 * @param pageableData
	 *           the page to return
	 * @return the search results
	 */
	RESULT textSearch(STATE searchQueryData, PageableData pageableData);


}
