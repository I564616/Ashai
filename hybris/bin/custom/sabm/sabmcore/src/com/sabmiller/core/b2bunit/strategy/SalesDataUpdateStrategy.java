/**
 *
 */
package com.sabmiller.core.b2bunit.strategy;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;

import jakarta.annotation.Resource;

import com.sabmiller.core.model.SalesDataModel;
import com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy;
import com.sabmiller.facades.b2bunit.data.SalesData;


/**
 * @author joshua.a.antony
 *
 */
public class SalesDataUpdateStrategy extends
		AbstractRelationshipUpdateStrategy<B2BUnitModel, SalesData, SalesDataModel, Populator<SalesData, SalesDataModel>>
{
	@Resource(name = "salesDataReversePopulator")
	private Populator<SalesData, SalesDataModel> salesDataReversePopulator;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy#lookup(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected SalesDataModel lookup(final B2BUnitModel b2bUnitModel, final SalesData salesData)
	{
		final SalesDataModel salesDataModel = b2bUnitModel.getSalesData();
		if (salesDataModel != null && salesData.getSalesOrgId().equals(salesDataModel.getSalesOrgId()))
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
	protected Populator<SalesData, SalesDataModel> getRelatedEntityModelPopulator()
	{
		return salesDataReversePopulator;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy#createModel()
	 */
	@Override
	protected SalesDataModel createModel(final B2BUnitModel model)
	{
		return getModelService().create(SalesDataModel.class);
	}

}
