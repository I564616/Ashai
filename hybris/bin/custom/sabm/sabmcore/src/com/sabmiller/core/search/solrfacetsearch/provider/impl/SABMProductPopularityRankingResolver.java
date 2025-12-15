package com.sabmiller.core.search.solrfacetsearch.provider.impl;

import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.product.SabmProductService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wei.yang.ng on 26/07/2016.
 */
public class SABMProductPopularityRankingResolver extends AbstractValueResolver<ProductModel, Object, Object>
{
	private static final Logger LOG = LoggerFactory.getLogger(SABMProductPopularityRankingResolver.class);

	final static int INDEX_PROPERTY_SUBCHANNEL_LOCATION = 2;

	private SabmProductService productService;

	@Override
	protected void addFieldValues(InputDocument inputDocument, IndexerBatchContext indexerBatchContext,
			IndexedProperty indexedProperty, ProductModel productModel, ValueResolverContext<Object, Object> valueResolverContext)
			throws FieldValueProviderException
	{
		if (StringUtils.isEmpty(indexedProperty.getName()))
		{
			LOG.error("Indexed Property cannot be empty, not indexing.");
			return;
		}

		if (!(productModel instanceof SABMAlcoholVariantProductEANModel))
		{
			LOG.error("Indexed Product is not of instance type [{}]", SABMAlcoholVariantProductEANModel._TYPECODE);
			return;
		}

		final String propertyName = indexedProperty.getName();

		final String[] splittedProperty = propertyName.split("_");
		final String subChannel = splittedProperty[INDEX_PROPERTY_SUBCHANNEL_LOCATION];

		final int popularityRank = getProductService().getProductSubchannelPopularityRankBySubchannel((SABMAlcoholVariantProductEANModel)productModel, subChannel);

		LOG.debug("Indexing popularity rank for product [{}] for sub channel [{}] with value [{}]", productModel.getCode(), subChannel,
				popularityRank);

		inputDocument.addField(indexedProperty, popularityRank, valueResolverContext.getFieldQualifier());
	}

	public SabmProductService getProductService()
	{
		return productService;
	}

	public void setProductService(SabmProductService productService)
	{
		this.productService = productService;
	}
}
