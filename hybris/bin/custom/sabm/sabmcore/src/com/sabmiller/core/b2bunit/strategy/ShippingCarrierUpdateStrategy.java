/**
 *
 */
package com.sabmiller.core.b2bunit.strategy;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;

import jakarta.annotation.Resource;

import com.sabmiller.core.model.ShippingCarrierModel;
import com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy;
import com.sabmiller.facades.b2bunit.data.ShippingCarrier;


/**
 * @author joshua.a.antony
 *
 */
public class ShippingCarrierUpdateStrategy
		extends
		AbstractRelationshipUpdateStrategy<B2BUnitModel, ShippingCarrier, ShippingCarrierModel, Populator<ShippingCarrier, ShippingCarrierModel>>
{
	@Resource(name = "shippingCarrierReversePopulator")
	private Populator<ShippingCarrier, ShippingCarrierModel> shippingCarrierReversePopulator;

	@Override
	protected ShippingCarrierModel lookup(final B2BUnitModel b2bUnitModel, final ShippingCarrier shippingCarrierData)
	{
		if (b2bUnitModel.getShippingCarriers() != null)
		{
			for (final ShippingCarrierModel scm : b2bUnitModel.getShippingCarriers())
			{
				if (scm.getCarrierCode().equals(shippingCarrierData.getCode()))
				{
					return scm;
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy#getRelatedEntityModelConverter()
	 */
	@Override
	protected Populator<ShippingCarrier, ShippingCarrierModel> getRelatedEntityModelPopulator()
	{
		return shippingCarrierReversePopulator;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.strategy.AbstractRelationshipUpdateStrategy#createModel()
	 */
	@Override
	protected ShippingCarrierModel createModel(final B2BUnitModel model)
	{
		return getModelService().create(ShippingCarrierModel.class);
	}
}
