/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.facades.search.converters.populator;

import com.sabmiller.facades.search.handler.SABMCustomSOLRSortHandler;
import de.hybris.platform.solrfacetsearch.model.SolrSortFieldModel;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.OrderField;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.*;

import org.apache.log4j.Logger;

import static com.sabmiller.facades.constants.SabmFacadesConstants.SOLR_BESTSELLER_FIELD;
import static com.sabmiller.facades.constants.SabmFacadesConstants.SOLR_POPULAR_FIELD;


/**
 * Overriden class to tweak default SOLR search sort populator. This is to overcome the limitation of extending from the OOTB
 * @see de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchSortPopulator
 * where java generics in methods causes issues with overriding.
 *
 * The OOTB source code shipped with Hybris 5.7.0.3 has been used in this class.
 *
 * Added
 *
 * Created by wei.yang.ng on 22/07/2016.
 */
public class SABMSearchSortPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_PROPERTY_TYPE> implements
		Populator<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest<FACET_SEARCH_CONFIG_TYPE, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, IndexedTypeSort>>
{
	private static final Logger LOG = Logger.getLogger(SABMSearchSortPopulator.class);
	protected static final String SOLR_SCORE_FIELD = "score";

	private HashMap<String, SABMCustomSOLRSortHandler> handlers;

	@Override
	public void populate(
			final SearchQueryPageableData<SolrSearchQueryData> source,
			final SolrSearchRequest<FACET_SEARCH_CONFIG_TYPE, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, IndexedTypeSort> target)
	{
		// Work out the selected sort option

		final IndexedType commerceIndexedType = target.getIndexedType();

		// Try to get the sort from the pageableData
		if (target.getPageableData() != null && target.getPageableData().getSort() != null
				&& !target.getPageableData().getSort().isEmpty())
		{
			target.setCurrentSort(commerceIndexedType.getSortsByCode().get(target.getPageableData().getSort()));
		}

		// Fall-back to the last sort used in the searchQueryData
		if (target.getCurrentSort() == null && target.getSearchQueryData().getSort() != null
				&& !target.getSearchQueryData().getSort().isEmpty())
		{
			target.setCurrentSort(commerceIndexedType.getSortsByCode().get(target.getSearchQueryData().getSort()));
		}


		if (target.getCurrentSort() == null)
		{
			// Fallback to first available sort

			final List<IndexedTypeSort> sorts = getFilteredSorts(target.getIndexedType());
			if (sorts != null && !sorts.isEmpty())
			{
				target.setCurrentSort(sorts.get(0));
			}

		}

		if (target.getCurrentSort() != null)
		{
			boolean scoreSort = false;

			// Set the sort options on the query
			for (final SolrSortFieldModel sortFieldModel : target.getCurrentSort().getSort().getFields())
			{
				if (SOLR_SCORE_FIELD.equalsIgnoreCase(sortFieldModel.getFieldName()))
				{
					scoreSort = true;
				}
				//target.getSearchQuery().addOrderField(sortFieldModel.getFieldName(), sortFieldModel.isAscending());
				target.getSearchQuery().addSort(sortFieldModel.getFieldName(), OrderField.SortOrder.ASCENDING);

				final SABMCustomSOLRSortHandler handler = handlers.get(sortFieldModel.getFieldName());
				if (handler != null) handler.handleCustomSortProperty(source, target);
			}

			// If we are not already sorting by the relevance score then add it as a final ordering clause
			if (!scoreSort)
			{
				//target.getSearchQuery().addOrderField(SOLR_SCORE_FIELD, false);
				target.getSearchQuery().addSort(SOLR_SCORE_FIELD, OrderField.SortOrder.DESCENDING);
			}
		}
	}

	/*
	 * Returns a filtered list of sorts depending on the current query and indexed type.
	 */
	protected List<IndexedTypeSort> getFilteredSorts(final IndexedType indexedType)
	{

		final List<IndexedTypeSort> sorts = indexedType.getSorts();
		if (sorts != null && !sorts.isEmpty())
		{
			final List<IndexedTypeSort> existingSorts = new ArrayList<IndexedTypeSort>(sorts);
			// Filter indexed sorts
			for (final IndexedTypeSort sort : sorts)
			{
				final List<SolrSortFieldModel> sortFields = sort.getSort().getFields();
				if (sortFields != null && !sortFields.isEmpty())
				{
					for (final SolrSortFieldModel sortField : sortFields)
					{
						if (!sortFieldExists(sortField.getFieldName(), indexedType))
						{
							existingSorts.remove(sort);
						}
					}
				}
			}
			return existingSorts;
		}

		return Collections.emptyList();
	}


	/**
	 * Method checks if the field with the given <code>sortFieldName</code> exists in the index configuration of the
	 * <code>indexedType</code>.
	 *
	 * @param sortFieldName
	 * @param indexedType
	 * @return true if exists
	 */
	protected boolean sortFieldExists(final String sortFieldName, final IndexedType indexedType)
	{
		// Special case for 'score', 'bestseller' and 'popular' field which is always available as a sort field
		if (SOLR_SCORE_FIELD.equalsIgnoreCase(sortFieldName)) return true;
		if (SOLR_BESTSELLER_FIELD.equalsIgnoreCase(sortFieldName)) return true;
		if (SOLR_POPULAR_FIELD.equalsIgnoreCase(sortFieldName)) return true;

		if (indexedType != null)
		{
			if (indexedType.getIndexedProperties().get(sortFieldName) != null)
			{
				return true;
			}
			else
			{
				LOG.warn("Cannot use [" + sortFieldName
						+ "] as sort field as it doesn't exist in the solr index. Please review you SolrIndexType ["
						+ indexedType.getCode() + "] instance");
			}
		}
		return false;
	}

	public HashMap<String, SABMCustomSOLRSortHandler> getHandlers()
	{
		return handlers;
	}

	public void setHandlers(HashMap<String, SABMCustomSOLRSortHandler> handlers)
	{
		this.handlers = handlers;
	}
}
