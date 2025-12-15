/**
 *
 */
package com.sabmiller.core.b2bunit.strategy;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;

import jakarta.annotation.Resource;

import com.sabmiller.core.model.SalesOrgDataModel;
import com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy;
import com.sabmiller.facades.b2bunit.data.SalesOrgData;


/**
 * @author joshua.a.antony
 *
 */
public class SalesOrgDataUpdateStrategy
		extends
		AbstractRelationshipUpdateStrategy<B2BUnitModel, SalesOrgData, SalesOrgDataModel, Populator<SalesOrgData, SalesOrgDataModel>>
{
	@Resource(name = "salesOrgDataReversePopulator")
	private Populator<SalesOrgData, SalesOrgDataModel> salesOrgDataReversePopulator;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy#lookup(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected SalesOrgDataModel lookup(final B2BUnitModel b2bUnitModel, final SalesOrgData salesData)
	{
		final SalesOrgDataModel salesDataModel = b2bUnitModel.getSalesOrgData();
		if (salesDataModel != null && salesData.getSalesGroup().equals(salesDataModel.getSalesGroup()))
		{
			return salesDataModel;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy#getRelatedEntityModelConverter()
	 */
	@Override
	protected Populator<SalesOrgData, SalesOrgDataModel> getRelatedEntityModelPopulator()
	{
		return salesOrgDataReversePopulator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy#createModel()
	 */
	@Override
	protected SalesOrgDataModel createModel(final B2BUnitModel model)
	{
		return getModelService().create(SalesOrgDataModel.class);
	}

}
