package com.asahi.core.search.populators;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchFiltersPopulator;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.integration.data.AsahiProductInfo;
import com.sabmiller.core.constants.SabmCoreConstants;


/**
 * @author Pankaj.Gandhi
 *
 *         Class to add filters to the query before query is executed on solr
 *
 * @param <FACET_SEARCH_CONFIG_TYPE>
 * @param <INDEXED_TYPE_SORT_TYPE>
 */
public class AsahiSearchFiltersPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_SORT_TYPE> extends
		SearchFiltersPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_SORT_TYPE>
{

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	private static final Logger LOG = Logger.getLogger(AsahiSearchFiltersPopulator.class);

	@Override
	public void populate(
			final SearchQueryPageableData<SolrSearchQueryData> source,
			final SolrSearchRequest<FACET_SEARCH_CONFIG_TYPE, IndexedType, IndexedProperty, SearchQuery, INDEXED_TYPE_SORT_TYPE> target)
	{
		super.populate(source, target);
		
		if(!asahiSiteUtil.isCub())
		{
   		/*
   		 * adding filter to the solr query, to fetch only customer associated catalog products
   		 */
   		
   		final Map<String, AsahiProductInfo> responseData = asahiCoreUtil.getSessionInclusionMap();
   		
   		if(!asahiCoreUtil.getShowProductWithoutPrice()){
   			if (null != responseData && !responseData.isEmpty())
   			{
   				final String ids = StringUtils.join(responseData.keySet(), ",");
   				LOG.debug("Product IDs filtered : " + ids);
   				target.getSearchQuery().addFilterRawQuery("code_string:(" + ids.replace(",", " OR ") + ")");
   			}
   			else if (asahiCoreUtil.getSessionProductBlockFlag())
   			{
   				target.getSearchQuery().addFilterRawQuery("code_string:(BLOCKED_PRODUCTS)");
   			}
   		}
   		
   		/*
   		 * Added to check whether the product is Active or not
   		 */
   		if (asahiSiteUtil.isSga())
   		{
   			target.getSearchQuery().addFilterRawQuery("active_boolean:(true)");
   		}
		}
	}
}
