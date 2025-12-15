package com.sabmiller.facades.search.handler;

import de.hybris.platform.solrfacetsearch.model.SolrSortFieldModel;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import static com.sabmiller.facades.constants.SabmFacadesConstants.SOLR_BESTSELLER_FIELD;
import static com.sabmiller.facades.constants.SabmFacadesConstants.SOLR_BESTSELLER_INDEXED_PROPERTY_PREFIX;

/**
 * Handler class for bestseller SOLR indexed properties. On the fly evaluation of the right SOLR indexed property to use based
 * on the session current B2BUnit.
 *
 * Created by wei.yang.ng on 25/07/2016.
 */
public class SABMBestsellerSOLRSortHandler<FACET_SEARCH_CONFIG_TYPE, INDEXED_PROPERTY_TYPE> extends SABMAbstractCustomSOLRSortHandler
		implements SABMCustomSOLRSortHandler<SearchQueryPageableData<SolrSearchQueryData>,
		SolrSearchRequest<FACET_SEARCH_CONFIG_TYPE, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, IndexedTypeSort>>
{
	private static final Logger LOG = Logger.getLogger(SABMBestsellerSOLRSortHandler.class);

	/**
	 * Handler method for bestseller type indexed properties.
	 *
	 * @param source	the source to copy from.
	 * @param target	the targe to copy to.
	 */
	@Override
	public void handleCustomSortProperty(final SearchQueryPageableData<SolrSearchQueryData> source,
			final SolrSearchRequest<FACET_SEARCH_CONFIG_TYPE, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, IndexedTypeSort> target)
	{
		LOG.debug("handleBestsellerSortProperty");

		for (final SolrSortFieldModel sortFieldModel : target.getCurrentSort().getSort().getFields())
		{
			if (SOLR_BESTSELLER_FIELD.equalsIgnoreCase(sortFieldModel.getFieldName()))
			{
				//target.getSearchQuery().removeOrderField(SOLR_BESTSELLER_FIELD);
				final String alteredFieldName = constructSolrSortFieldNameWithGivenSortPrefix(SOLR_BESTSELLER_INDEXED_PROPERTY_PREFIX);
				//if (StringUtils.isNotEmpty(alteredFieldName))
					//target.getSearchQuery().addOrderField(alteredFieldName, sortFieldModel.isAscending());
			}
		}

		return;
	}
}

