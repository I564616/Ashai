package com.sabmiller.core.search.solrfacetsearch.provider.impl;

import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.order.SabmB2BOrderService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wei.yang.ng on 20/07/2016.
 */
public class SABMProductBestsellerResolver extends AbstractValueResolver<ProductModel, Object, Object>
{
	private static final Logger LOG = LoggerFactory.getLogger(SABMProductBestsellerResolver.class);

	final static int INDEX_PROPERTY_SUBCHANNEL_LOCATION = 2;

	private SabmB2BOrderService orderService;

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

		if(IndexOperation.UPDATE.equals(indexerBatchContext.getIndexOperation())){ // skip best seller for update index as its expensive. leaving only nightly during full index
			if(LOG.isDebugEnabled()){
				LOG.debug("Skipped indexing for s [{}] when it is an update.",indexedProperty.getName());
			}
			//inputDocument.addField(indexedProperty,createNoUpdateFieldModifier(),valueResolverContext.getFieldQualifier());
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

		final int bestsellerCount = getOrderService().getProductOrderCountBySubChannelAndEAN(subChannel,
				(SABMAlcoholVariantProductEANModel) productModel);

		LOG.debug("Indexing bestseller for product [{}] for sub channel [{}] with value [{}]", productModel.getCode(), subChannel,
				bestsellerCount);

		inputDocument.addField(indexedProperty, bestsellerCount, valueResolverContext.getFieldQualifier());
	}

	public SabmB2BOrderService getOrderService()
	{
		return orderService;
	}

	public void setOrderService(SabmB2BOrderService orderService)
	{
		this.orderService = orderService;
	}
}
