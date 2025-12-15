package com.apb.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import com.apb.core.model.ApbProductModel;


/**
 * @see APB specific AlcoholType Value Resolver.
 */
public class ApbProductAlcoholTypeValueResolver extends AbstractValueResolver<ProductModel, Object, Object>
{
	Logger Log = LoggerFactory.getLogger(ApbProductAlcoholTypeValueResolver.class);

	/* Indexing AlcoholType attribute to solr. */
	@Override
	protected void addFieldValues(final InputDocument document, final IndexerBatchContext batchContext,
			final IndexedProperty indexedProperty, final ProductModel productModel,
			final ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException
	{
		if (productModel instanceof ApbProductModel && null != ((ApbProductModel) productModel).getAlcoholType())
		{
			final String alcoholTypeCode = ((ApbProductModel) productModel).getAlcoholType().getCode();
			if (!StringUtils.isBlank((alcoholTypeCode)))
			{
				document.addField(indexedProperty, alcoholTypeCode, resolverContext.getFieldQualifier());
			}
		}
	}
}
