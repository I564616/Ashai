/**
 *
 */
package com.sabmiller.core.search.solrfacetsearch.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;

import org.springframework.core.convert.converter.Converter;

import com.sabmiller.core.search.solrfacetsearch.SABMCustomerSearchService;
import com.sabmiller.facades.search.data.CustomerSearchPageData;


/**
 * DefaultSABMCCustomerSearchService
 */
@SuppressWarnings("rawtypes")
public class DefaultSABMCustomerSearchService<ITEM>
		implements SABMCustomerSearchService<SolrSearchQueryData, ITEM, CustomerSearchPageData<SolrSearchQueryData, ITEM>>
{

	/** The Converter */
	private Converter<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest> searchQueryPageableConverter;

	private Converter<SolrSearchRequest, SolrSearchResponse> searchRequestConverter;

	private Converter<SolrSearchResponse, CustomerSearchPageData<SolrSearchQueryData, ITEM>> sabmCustomerSearchResponseConverter;

	/**
	 * @return the searchQueryPageableConverter
	 */
	public Converter<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest> getSearchQueryPageableConverter()
	{
		return searchQueryPageableConverter;
	}


	/**
	 * @param searchQueryPageableConverter
	 *           the searchQueryPageableConverter to set
	 */
	public void setSearchQueryPageableConverter(
			final Converter<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest> searchQueryPageableConverter)
	{
		this.searchQueryPageableConverter = searchQueryPageableConverter;
	}


	/**
	 * @return the searchRequestConverter
	 */
	public Converter<SolrSearchRequest, SolrSearchResponse> getSearchRequestConverter()
	{
		return searchRequestConverter;
	}


	/**
	 * @param searchRequestConverter
	 *           the searchRequestConverter to set
	 */
	public void setSearchRequestConverter(final Converter<SolrSearchRequest, SolrSearchResponse> searchRequestConverter)
	{
		this.searchRequestConverter = searchRequestConverter;
	}


	/**
	 * @return the sabmCustomerSearchResponseConverter
	 */
	public Converter<SolrSearchResponse, CustomerSearchPageData<SolrSearchQueryData, ITEM>> getSabmCustomerSearchResponseConverter()
	{
		return sabmCustomerSearchResponseConverter;
	}


	/**
	 * @param sabmCustomerSearchResponseConverter
	 *           the sabmCustomerSearchResponseConverter to set
	 */
	public void setSabmCustomerSearchResponseConverter(
			final Converter<SolrSearchResponse, CustomerSearchPageData<SolrSearchQueryData, ITEM>> sabmCustomerSearchResponseConverter)
	{
		this.sabmCustomerSearchResponseConverter = sabmCustomerSearchResponseConverter;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.search.solrfacetsearch.SABMCCustomerSearchService#textSearch(java.lang.Object,
	 * de.hybris.platform.commerceservices.search.pagedata.PageableData)
	 */
	@Override
	public CustomerSearchPageData<SolrSearchQueryData, ITEM> textSearch(final SolrSearchQueryData searchQueryData,
			final PageableData pageableData)
	{
		return doSearch(searchQueryData, pageableData);
	}


	protected CustomerSearchPageData<SolrSearchQueryData, ITEM> doSearch(final SolrSearchQueryData searchQueryData,
			final PageableData pageableData)
	{
		validateParameterNotNull(searchQueryData, "SearchQueryData cannot be null");

		// Create the SearchQueryPageableData that contains our parameters
		final SearchQueryPageableData<SolrSearchQueryData> searchQueryPageableData = buildSearchQueryPageableData(searchQueryData,
				pageableData);

		// Build up the search request
		final SolrSearchRequest solrSearchRequest = getSearchQueryPageableConverter().convert(searchQueryPageableData);

		// Execute the search
		final SolrSearchResponse solrSearchResponse = getSearchRequestConverter().convert(solrSearchRequest);

		// Convert the response
		return getSabmCustomerSearchResponseConverter().convert(solrSearchResponse);
	}

	protected SearchQueryPageableData<SolrSearchQueryData> buildSearchQueryPageableData(final SolrSearchQueryData searchQueryData,
			final PageableData pageableData)
	{
		final SearchQueryPageableData<SolrSearchQueryData> searchQueryPageableData = createSearchQueryPageableData();
		searchQueryPageableData.setSearchQueryData(searchQueryData);
		searchQueryPageableData.setPageableData(pageableData);
		return searchQueryPageableData;
	}

	/**
	 * the method for data object - can be overridden in spring config
	 *
	 * @return SearchQueryPageableData<SolrSearchQueryData>
	 */
	protected SearchQueryPageableData<SolrSearchQueryData> createSearchQueryPageableData()
	{
		return new SearchQueryPageableData<SolrSearchQueryData>();
	}

	protected SolrSearchQueryData createSearchQueryData()
	{
		return new SolrSearchQueryData();
	}
}
