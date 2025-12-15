package com.sabmiller.facades.search.converters.populator;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SolrSearchRequestResponsePopulator;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.solrfacetsearch.search.*;
import de.hybris.platform.solrfacetsearch.search.impl.SolrSearchResult;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrException;


/**
 * Created by wei.yang.ng on 29/07/2016.
 */
public class SABMSolrSearchRequestResponsePopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE>
	implements Populator<SolrSearchRequest<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SearchQuery,
		INDEXED_TYPE_SORT_TYPE>, SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SearchQuery,
		INDEXED_TYPE_SORT_TYPE, SearchResult>>
{
	private static final Logger LOG = Logger.getLogger(SolrSearchRequestResponsePopulator.class);

	private FacetSearchService solrFacetSearchService;
	private SolrKeywordRedirectService solrKeywordRedirectService;

	protected FacetSearchService getSolrFacetSearchService()
	{
		return solrFacetSearchService;
	}

	public void setSolrFacetSearchService(final FacetSearchService solrFacetSearchService)
	{
		this.solrFacetSearchService = solrFacetSearchService;
	}

	public SolrKeywordRedirectService getSolrKeywordRedirectService()
	{
		return solrKeywordRedirectService;
	}

	public void setSolrKeywordRedirectService(final SolrKeywordRedirectService solrKeywordRedirectService)
	{
		this.solrKeywordRedirectService = solrKeywordRedirectService;
	}

	@Override
	public void populate(
			final SolrSearchRequest<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE> source,
			final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult> target)
	{
		LOG.debug("Calling custom SolrSearchRequestResponsePopulator");
		try
		{
			target.setRequest(source);
			final SearchResult searchResult = getSolrFacetSearchService().search(source.getSearchQuery());
			if (searchResult instanceof SolrSearchResult)
			{
				getSolrKeywordRedirectService().attachKeywordRedirect((SolrSearchResult) searchResult);
			}
			target.setSearchResult(searchResult);
		}
		catch (final FacetSearchException | SolrException ex)
		{
			LOG.error("Exception while executing SOLR search :" + ex.getMessage());
			LOG.debug("Root Cause:", ex);
			throw new ConversionException("Exception while executing SOLR search :", ex);
		}
	}
}
