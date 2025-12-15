/**
 *
 */
package com.sabmiller.facades.search.solrfacetsearch;

import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import com.sabmiller.facades.search.data.CustomerSearchPageData;


/**
 * SABMCustomerSearchFacade
 */
public interface SABMCustomerSearchFacade<ITEM extends CustomerData>
{
	/**
	 * Refine an exiting search. The query object allows more complex queries using facet selection. The SearchStateData
	 * must have been obtained from the results of a call to {@link #textSearch(String)}.
	 *
	 * @param searchState
	 *           the search query object
	 * @param pageableData
	 *           the page to return
	 * @return the search results
	 */

	CustomerSearchPageData<SearchStateData, ITEM> textSearch(SearchStateData searchState, PageableData pageableData);


}
