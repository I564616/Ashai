/**
 *
 */
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


/**
 * @author EG588BU
 *
 */
public class CDLCheckLevel4ValueResolver extends AbstractValueResolver<ProductModel, Void, Void>
{
	private static final Logger LOG = LoggerFactory.getLogger(CDLCheckLevel4ValueResolver.class);
	@Override
	protected void addFieldValues(final InputDocument inputDocument, final IndexerBatchContext indexerBatchContext,
			final IndexedProperty indexedProperty, final ProductModel productModel,
			final ValueResolverContext<Void, Void> valueResolverContext) throws FieldValueProviderException
	{
		try
		{
			if (SABMAlcoholVariantProductEANModel.class.isInstance(productModel))
			{
				final SABMAlcoholVariantProductEANModel eanProductModel = (SABMAlcoholVariantProductEANModel) productModel;
				addFieldValue(inputDocument, indexerBatchContext, indexedProperty, (eanProductModel.getLevel4()),
						valueResolverContext.getFieldQualifier());
			}
		}
		catch (final Exception e)
		{
			LOG.error("Unable to index Cdl value (level4) for Product " + productModel.getCode());
			e.printStackTrace();
		}

	}

}
