package com.apb.core.search.solrfacetsearch.provider.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.model.ApbProductModel;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;


/**
 * The class will index the New product variable
 */

public class AsahiNewProductValueResolver extends AbstractValueResolver<ProductModel, Object, Object>
{

	Logger Log = LoggerFactory.getLogger(AsahiProductPortalUnitVolumeValueResolver.class);

	
	/** The method will index the New Product value
	 * 
	 * @param InputDocument
	 * @param IndexerBatchContext
	 * @param ValueResolverContext
	 */
	@Override
	protected void addFieldValues(final InputDocument document, final IndexerBatchContext batchContext,
			final IndexedProperty indexedProperty, final ProductModel productModel,
			final ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException
	{
		if (productModel instanceof ApbProductModel)
		{
			final Boolean isNewProduct = ((ApbProductModel) productModel).getNewProduct();
			document.addField(indexedProperty, isNewProduct, resolverContext.getFieldQualifier());
		}
	}
}
