/**
 *
 */
package com.sabmiller.facades.search.solrfacetsearch.impl;



import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.threadcontext.ThreadContextService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.util.Assert;

import com.sabmiller.core.search.solrfacetsearch.SABMCustomerSearchService;
import com.sabmiller.facades.search.data.CustomerSearchPageData;
import com.sabmiller.facades.search.solrfacetsearch.SABMCustomerSearchFacade;


/**
 * DefaultSABMCustomerSearchFacade
 */
public class DefaultSABMCustomerSearchFacade<ITEM extends CustomerData> implements SABMCustomerSearchFacade<ITEM>
{

	/** The Service */
	private SABMCustomerSearchService<SolrSearchQueryData, SearchResultValueData, CustomerSearchPageData<SolrSearchQueryData, SearchResultValueData>> customerSearchService;

	private ThreadContextService threadContextService;

	/** The Converter */

	private Converter<CustomerSearchPageData<SolrSearchQueryData, SearchResultValueData>, CustomerSearchPageData<SearchStateData, ITEM>> customerSearchPageConverter;

	private Converter<SearchQueryData, SolrSearchQueryData> solrSearchQueryDecoder;



	/**
	 * @return the customerSearchService
	 */
	public SABMCustomerSearchService<SolrSearchQueryData, SearchResultValueData, CustomerSearchPageData<SolrSearchQueryData, SearchResultValueData>> getCustomerSearchService()
	{
		return customerSearchService;
	}


	/**
	 * @param customerSearchService
	 *           the customerSearchService to set
	 */
	public void setCustomerSearchService(
			final SABMCustomerSearchService<SolrSearchQueryData, SearchResultValueData, CustomerSearchPageData<SolrSearchQueryData, SearchResultValueData>> customerSearchService)
	{
		this.customerSearchService = customerSearchService;
	}


	/**
	 * @return the threadContextService
	 */
	public ThreadContextService getThreadContextService()
	{
		return threadContextService;
	}


	/**
	 * @param threadContextService
	 *           the threadContextService to set
	 */
	public void setThreadContextService(final ThreadContextService threadContextService)
	{
		this.threadContextService = threadContextService;
	}


	/**
	 * @return the customerSearchPageConverter
	 */
	public Converter<CustomerSearchPageData<SolrSearchQueryData, SearchResultValueData>, CustomerSearchPageData<SearchStateData, ITEM>> getCustomerSearchPageConverter()
	{
		return customerSearchPageConverter;
	}


	/**
	 * @param customerSearchPageConverter
	 *           the customerSearchPageConverter to set
	 */
	public void setCustomerSearchPageConverter(
			final Converter<CustomerSearchPageData<SolrSearchQueryData, SearchResultValueData>, CustomerSearchPageData<SearchStateData, ITEM>> customerSearchPageConverter)
	{
		this.customerSearchPageConverter = customerSearchPageConverter;
	}


	/**
	 * @return the solrSearchQueryDecoder
	 */
	public Converter<SearchQueryData, SolrSearchQueryData> getSolrSearchQueryDecoder()
	{
		return solrSearchQueryDecoder;
	}


	/**
	 * @param solrSearchQueryDecoder
	 *           the solrSearchQueryDecoder to set
	 */
	public void setSolrSearchQueryDecoder(final Converter<SearchQueryData, SolrSearchQueryData> solrSearchQueryDecoder)
	{
		this.solrSearchQueryDecoder = solrSearchQueryDecoder;
	}


	/*
	 * textSearch
	 *
	 * @see com.sabmiller.facades.search.solrfacetsearch.SABMCCustomerSearchFacade#textSearch(de.hybris.platform.
	 * commercefacades.search.data.SearchStateData, de.hybris.platform.commerceservices.search.pagedata.PageableData)
	 */
	@Override
	public CustomerSearchPageData<SearchStateData, ITEM> textSearch(final SearchStateData searchState,
			final PageableData pageableData)
	{
		Assert.notNull(searchState, "SearchStateData must not be null.");
		return getThreadContextService().executeInContext(
				new ThreadContextService.Executor<CustomerSearchPageData<SearchStateData, ITEM>, ThreadContextService.Nothing>()
				{
					@Override
					public CustomerSearchPageData<SearchStateData, ITEM> execute()
					{
						return getCustomerSearchPageConverter()
								.convert(getCustomerSearchService().textSearch(decodeState(searchState, null), pageableData));
					}
				});

	}

	/**
	 * decode the SearchStateData
	 *
	 * @param searchState
	 * @param categoryCode
	 * @return SolrSearchQueryData
	 */
	protected SolrSearchQueryData decodeState(final SearchStateData searchState, final String categoryCode)
	{
		final SolrSearchQueryData searchQueryData = getSolrSearchQueryDecoder().convert(searchState.getQuery());
		if (categoryCode != null)
		{
			searchQueryData.setCategoryCode(categoryCode);
		}
		return searchQueryData;
	}

}
