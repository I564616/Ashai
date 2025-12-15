package com.apb.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;

import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiDealModel;


public class AsahiDealStatusValueResolver extends AbstractValueResolver<AsahiB2BUnitModel, Void, Void>
{
	private static final Logger LOG = LoggerFactory.getLogger(AsahiDealStatusValueResolver.class);
	/** The deals service. */
	@Resource
	protected DealsService dealsService;
	@Override
	protected void addFieldValues(final InputDocument inputDocument, final IndexerBatchContext indexerBatchContext,
			final IndexedProperty indexedProperty, final AsahiB2BUnitModel asahiB2BUnitModel,
			final ValueResolverContext<Void, Void> valueResolverContext) throws FieldValueProviderException
	{
		try {
			if (null != asahiB2BUnitModel)
			{
				final List<AsahiDealModel> deals = dealsService.getSGASpecificDeals(asahiB2BUnitModel);
				final Boolean fieldValue = CollectionUtils.isNotEmpty(deals)? true:false;
				addFieldValue(inputDocument, indexerBatchContext, indexedProperty,fieldValue,valueResolverContext.getFieldQualifier());
			}
		}
		catch (final Exception e)
		{
			LOG.error("Unable to index deal status for Customer " + asahiB2BUnitModel.getUid());
			e.printStackTrace();
		}
	}
}
