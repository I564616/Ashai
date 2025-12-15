/**
 *
 */
package com.sabmiller.core.solr.query.converter;

import de.hybris.platform.solrfacetsearch.search.QueryField;
import de.hybris.platform.solrfacetsearch.search.impl.DefaultSolrQueryConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.util.ClientSolrUtils;


/**
 * The Class SABMSolrQueryConverter overrides the OOB DefaultSolrQueryConverter to fix a bug during the creation of
 * filterQuery with multivalue element.
 */
public class SABMSolrQueryConverter extends DefaultSolrQueryConverter
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMSolrQueryConverter.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.solrfacetsearch.search.impl.DefaultSolrQueryConverter#convertQueryFields(java.util.List,
	 * java.util.Map)
	 */
	@Override
	protected String[] convertQueryFields(final List<QueryField> queryFields, final Map<String, IndexedFacetInfo> facetInfoMap)
	{
		final List<String> joinedQueries = new ArrayList<>();

		for (final QueryField qf : queryFields)
		{
			if (qf != null)
			{
				joinedQueries.add(convertQueryField(qf, facetInfoMap));
			}
		}

		return joinedQueries.toArray(new String[joinedQueries.size()]);
	}

	/**
	 * Convert query field.
	 *
	 * @param qf
	 *           the qf
	 * @param facetInfoMap
	 *           the facet info map
	 * @return the string
	 */
	protected String convertQueryField(final QueryField qf, final Map<String, IndexedFacetInfo> facetInfoMap)
	{
		String query = StringUtils.EMPTY;

		if (qf != null)
		{
			LOG.debug("Creating query for field: {}, values: {}, operator: {}", qf.getField(), qf.getValues(), qf.getOperator());

			final IndexedFacetInfo indexedFacetInfo = facetInfoMap == null ? null : facetInfoMap.get(qf.getField());
			String fieldPrefix = "";
			if (indexedFacetInfo != null)
			{
				fieldPrefix = "{!tag=" + indexedFacetInfo.getKey() + "}";
			}

			if (qf.getValues().size() == 1)
			{
				if ("fulltext".equals(qf.getField()))
				{
					query = fieldPrefix + "(" + qf.getValues().iterator().next() + ")";

				}
				else
				{
					query = fieldPrefix + "(" + escape(qf.getField()) + ":" + qf.getValues().iterator().next() + ")";
				}


			}
			else if ("fulltext".equals(qf.getField()))
			{
				query = fieldPrefix + "("
						+ combine(qf.getValues().toArray(new String[qf.getValues().size()]), qf.getOperator().getName()) + ")";

			}
			else
			{
				final String[] arrayValues = new String[qf.getValues().size()];
				//Combining field and value for each value.
				for (int i = 0; i < qf.getValues().size(); i++)
				{
					arrayValues[i] = escape(qf.getField()) + ":" + CollectionUtils.get(qf.getValues(), i);
				}
				query = fieldPrefix + "(" + combine(arrayValues, qf.getOperator().getName()) + ")";
			}
		}

		return query;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.solrfacetsearch.search.impl.DefaultSolrQueryConverter#escape(java.lang.String)
	 */
	@Override
	protected String escape(final String text)
	{
		return ClientSolrUtils.escapeQueryChars(text);
	}
}
