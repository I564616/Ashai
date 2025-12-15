/**
 *
 */
package com.sabmiller.facades.search.converters.populator;

import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchSolrQueryPopulator;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.facades.constants.SabmFacadesConstants;


/**
 * SABMSearchSolrQueryPopulator
 */
public class SABMSearchSolrQueryPopulator extends SearchSolrQueryPopulator<Object, Object>
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
		final IndexConfig indexConfig = config.getIndexConfig();

		// Strategy for working out which of the available indexed types to use
		final Collection<IndexedType> indexedTypes = indexConfig.getIndexedTypes().values();

		if (CollectionUtils.isNotEmpty(indexedTypes))
		{
			final Iterator<IndexedType> indexTyIt = indexedTypes.iterator();
			while (indexTyIt.hasNext())
			{
				final IndexedType indexty = indexTyIt.next();
				if (indexty.getCode().equals(SabmFacadesConstants.INDEXEDTYPE_B2BCUSTOMER) || indexty.getCode().equals(SabmFacadesConstants.INDEXEDTYPE_ASAHIB2BCUSTOMER))
				{
					final ComposedTypeModel composedType = indexty.getComposedType();
					composedType.setCatalogItemType(Boolean.FALSE);
					indexty.setComposedType(composedType);
					indexty.setGroup(Boolean.FALSE);
					return indexty;
				}
			}
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("The {} not have indexedTypes.B2BCustomer", indexConfig);
		}
		// No indexed types
		return null;
	}


}
