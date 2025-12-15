package com.sabmiller.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;


public class WetPriceFlagValueResolver extends AbstractValueResolver<ProductModel, Void, Void>
{
	private static final Logger LOG = LoggerFactory.getLogger(WetPriceFlagValueResolver.class);
	@Override
	protected void addFieldValues(final InputDocument inputDocument, final IndexerBatchContext indexerBatchContext,
			final IndexedProperty indexedProperty, final ProductModel productModel,
			final ValueResolverContext<Void, Void> valueResolverContext) throws FieldValueProviderException
	{
		try {
			if (SABMAlcoholVariantProductEANModel.class.isInstance(productModel))
			{
				final SABMAlcoholVariantProductEANModel eanProductModel = (SABMAlcoholVariantProductEANModel) productModel;
				addFieldValue(inputDocument, indexerBatchContext, indexedProperty,
						(eanProductModel.getWetEligible() != null ? eanProductModel.getWetEligible() : false),
						valueResolverContext.getFieldQualifier());
			}
		}
		catch (final Exception e)
		{
			LOG.error("Unable to index wet flag for Product " + productModel.getCode());
			e.printStackTrace();
		}
	}
}
