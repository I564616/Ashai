/**
 *
 */
package com.sabmiller.core.b2bunit.converters.populator;

import de.hybris.platform.converters.Populator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.ShippingCarrierModel;
import com.sabmiller.facades.b2bunit.data.ShippingCarrier;
import de.hybris.platform.util.Config;

/**
 * @author joshua.a.antony
 *
 */
public class ShippingCarrierReversePopulator implements Populator<ShippingCarrier, ShippingCarrierModel>
{

	private static final Logger LOG = LoggerFactory.getLogger(ShippingCarrierReversePopulator.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.converters.impl.AbstractConverter#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final ShippingCarrier source, final ShippingCarrierModel target)
	{
		if (source != null && target != null)
		{
			target.setCarrierCode(source.getCode());
			target.setCarrierDescription(source.getDescription());
//			shipping.carriers.wrongcustomerowned=0006000044,0006000074,0006000208
			if (Config.getString("shipping.carriers.wrongcustomerowned", "").contains(source.getCode())
					&& null != source.getCustomerOwned() && source.getCustomerOwned().equals(true))
			{
				target.setCustomerOwned(false);
			}
			else
			{
				target.setCustomerOwned(source.getCustomerOwned());
			}
			target.setShippingCondition(source.getShippingCondition());
		}
		else
		{
			LOG.warn("Either source or target or both are null. There may be a bug in the application!");
		}
	}

}
