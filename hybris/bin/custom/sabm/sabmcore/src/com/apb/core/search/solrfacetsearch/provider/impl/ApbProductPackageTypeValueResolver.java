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
 * @see Package Type Value Resolver for indexing package type product attribute.
 */
public class ApbProductPackageTypeValueResolver extends AbstractValueResolver<ProductModel, Object, Object>
{
	Logger Log = LoggerFactory.getLogger(ApbProductPackageTypeValueResolver.class);

	/* Indexing package type attribute to solr. */
	@Override
	protected void addFieldValues(final InputDocument document, final IndexerBatchContext batchContext,
			final IndexedProperty indexedProperty, final ProductModel productModel,
			final ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException
	{
		if (productModel instanceof ApbProductModel && null != ((ApbProductModel) productModel).getPackageSize())
		{
			final String packType = ((ApbProductModel) productModel).getPackageSize().getName();
			if (!StringUtils.isBlank(packType))
			{
				document.addField(indexedProperty, packType, resolverContext.getFieldQualifier());
			}
		}
	}
}

