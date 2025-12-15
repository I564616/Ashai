/**
 *
 */
package com.sabmiller.core.deals.converter.populator;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Date;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.integration.sap.deals.pricediscount.request.PricingDiscountConditionsRequest;


/**
 * @author joshua.a.antony
 *
 */

public class OnceOffDealsRequestConverter extends DiscountDealsRequestConverter
{

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.util.SabmConverter#convert(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public PricingDiscountConditionsRequest convert(final B2BUnitModel source, final PricingDiscountConditionsRequest target,
			final Date deliveryDate) throws ConversionException
	{
		final PricingDiscountConditionsRequest request = super.convert(source, target, deliveryDate);
		request.setConditionType(SabmCoreConstants.ONCE_OFF_DEALS_CONDITION_TYPE);
		return request;
	}

}
