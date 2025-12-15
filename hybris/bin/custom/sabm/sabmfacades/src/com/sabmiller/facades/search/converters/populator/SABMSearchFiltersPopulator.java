/**
 *
 */
package com.sabmiller.facades.search.converters.populator;

import com.sabmiller.facades.constants.SabmFacadesConstants;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.*;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.QueryField;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchQuery.Operator;
import de.hybris.platform.solrfacetsearch.search.SearchQuery.QueryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * SABMSearchFiltersPopulator
 */
public class SABMSearchFiltersPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_SORT_TYPE> implements
		Populator<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest<FACET_SEARCH_CONFIG_TYPE, IndexedType, IndexedProperty, SearchQuery, INDEXED_TYPE_SORT_TYPE>>
{

	/** The Log */
	private static final Logger LOG = LoggerFactory.getLogger(SABMSearchFiltersPopulator.class);

	@Override
	public void populate(final SearchQueryPageableData<SolrSearchQueryData> source,
			final SolrSearchRequest<FACET_SEARCH_CONFIG_TYPE, IndexedType, IndexedProperty, SearchQuery, INDEXED_TYPE_SORT_TYPE> target)
	{
		// Convert the facet filters into IndexedPropertyValueData
		final List<IndexedPropertyValueData<IndexedProperty>> indexedPropertyValues = new ArrayList<IndexedPropertyValueData<IndexedProperty>>();
		final List<SolrSearchQueryTermData> terms = target.getSearchQueryData().getFilterTerms();
		if (terms != null && !terms.isEmpty())
		{
			for (final SolrSearchQueryTermData term : terms)
			{
				final IndexedProperty indexedProperty = target.getIndexedType().getIndexedProperties().get(term.getKey());
				if (indexedProperty != null)
				{
					final IndexedPropertyValueData<IndexedProperty> indexedPropertyValue = new IndexedPropertyValueData<IndexedProperty>();
					indexedPropertyValue.setIndexedProperty(indexedProperty);
					indexedPropertyValue.setValue(term.getValue());
					indexedPropertyValues.add(indexedPropertyValue);
				}
				if ((SabmFacadesConstants.INDEXEDTYPE_B2BCUSTOMER).equals(target.getIndexedType().getCode()) 
						|| (SabmFacadesConstants.INDEXEDTYPE_ASAHIB2BCUSTOMER).equals(target.getIndexedType().getCode()))
				{
					final QueryField query = new QueryField(term.getKey(), Operator.AND, QueryOperator.CONTAINS, term.getValue());
					target.getSearchQuery().addFilterQuery(query);
					//				target.getSearchQuery().addQuery(term.getKey(), Operator.AND, QueryOperator.CONTAINS, term.getValue());
				}


			}
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("the searchQuery{} not have filterTerms for search b2bUnit", source);
			}

		}
		target.setIndexedPropertyValues(indexedPropertyValues);

	}
}
