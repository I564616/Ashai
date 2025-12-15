package com.apb.core.attribute;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;



/**
 * Attribute Handler to display total order price.
 */
public class AsahiOrderNetPriceAttributeHandler implements DynamicAttributeHandler<Double, AbstractOrderModel>
{
	private final static Logger LOG = LoggerFactory.getLogger("AsahiOrderNetPriceAttributeHandler");

	@Override
	public Double get(final AbstractOrderModel orderModel)
	{
		Double netPrice = 0.0;
		if (null != orderModel)
		{
			netPrice = orderModel.getTotalPrice() + orderModel.getOrderGST();
		}
		LOG.debug("Order net price : " + netPrice);
		return netPrice;
	}

	@Override
	public void set(final AbstractOrderModel arg0, final Double arg1)
	{
		throw new UnsupportedOperationException();
	}


}
