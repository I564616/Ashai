package com.sabmiller.cockpits.services.label.impl;

import com.sabmiller.core.model.ProductSubchannelPopularityMappingModel;
import de.hybris.platform.cockpit.services.label.AbstractModelLabelProvider;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by wei.yang.ng on 27/07/2016.
 */
public class ProductSubchannelPopularityMappingLabelProvider extends AbstractModelLabelProvider<ProductSubchannelPopularityMappingModel>
{
	@Override
	protected String getItemLabel(ProductSubchannelPopularityMappingModel productSubchannelPopularityMappingModel)
	{
		return null;
	}

	@Override
	protected String getItemLabel(ProductSubchannelPopularityMappingModel productSubchannelPopularityMappingModel, String languageIso)
	{
		final StringBuilder sb = new StringBuilder();

		if (productSubchannelPopularityMappingModel == null) return StringUtils.EMPTY;

		final String subChannel = productSubchannelPopularityMappingModel.getSubChannel();
		final int ranking = productSubchannelPopularityMappingModel.getRanking();

		sb.append(" Sub-Channel: " + subChannel);
		sb.append(" - Ranking: " + ranking);

		return sb.toString();
	}

	@Override
	protected String getItemDescription(ProductSubchannelPopularityMappingModel productSubchannelPopularityMappingModel)
	{
		return null;
	}

	@Override
	protected String getItemDescription(ProductSubchannelPopularityMappingModel productSubchannelPopularityMappingModel, String s)
	{
		return null;
	}

	@Override
	protected String getIconPath(ProductSubchannelPopularityMappingModel productSubchannelPopularityMappingModel)
	{
		return null;
	}

	@Override
	protected String getIconPath(ProductSubchannelPopularityMappingModel productSubchannelPopularityMappingModel, String s)
	{
		return null;
	}
}
