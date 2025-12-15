/**
 *
 */
package com.sabmiller.facades.search.converters.populator;

import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchSolrQueryPopulator;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sabmiller.core.constants.SabmCoreConstants;

import com.sabmiller.facades.constants.SabmFacadesConstants;


/**
 * SABMSearchProductSolrQueryPopulator
 */
public class SABMSearchProductSolrQueryPopulator extends SearchSolrQueryPopulator<Object, Object>
{
	/** The Log */
	private static final Logger LOG = LoggerFactory.getLogger(SABMSearchSolrQueryPopulator.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected IndexedType getIndexedType(final FacetSearchConfig config)
	{
		
		if(SabmCoreConstants.CUB_FACET_SEARCH_CONFIG.equalsIgnoreCase(config.getName()))
		{
		
		final IndexConfig indexConfig = config.getIndexConfig();

		// Strategy for working out which of the available indexed types to use
		final Collection<IndexedType> indexedTypes = indexConfig.getIndexedTypes().values();

		if (CollectionUtils.isNotEmpty(indexedTypes))
		{
			final Iterator<IndexedType> indexTyIt = indexedTypes.iterator();
			while (indexTyIt.hasNext())
			{
				final IndexedType indexty = indexTyIt.next();
				if (indexty.getCode().equals(SabmFacadesConstants.INDEXEDTYPE_PRODUCT))
				{
					return indexty;
				}
			}
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("The {} not have indexedTypes.Product", indexConfig);
		}
		// No indexed types
		return null;
		}
		else
		{
			return super.getIndexedType(config);
		}
	}


}
