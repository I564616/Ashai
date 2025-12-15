/**
 *
 */
package com.sabmiller.core.deals.converter.populator;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.util.Config;

import java.util.Date;

import org.apache.log4j.Logger;

import com.sabmiller.core.util.SabmConverter;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.integration.sap.deals.pricediscount.request.PricingDiscountConditionsRequest;


/**
 * @author joshua.a.antony
 *
 */

public class DiscountDealsRequestConverter implements SabmConverter<B2BUnitModel, PricingDiscountConditionsRequest, Date>
{

	private static final Logger LOG = Logger.getLogger(DiscountDealsRequestConverter.class);

	@Override
	public PricingDiscountConditionsRequest convert(final B2BUnitModel source) throws ConversionException
	{
		throw new ConversionException("Operation not supported");
	}


	@Override
	public PricingDiscountConditionsRequest convert(final B2BUnitModel source, final PricingDiscountConditionsRequest target)
			throws ConversionException
	{
		throw new ConversionException("Operation not supported");
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.util.SabmConverter#convert(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public PricingDiscountConditionsRequest convert(final B2BUnitModel source, final PricingDiscountConditionsRequest target,
			final Date deliveryDate) throws ConversionException
	{
		final String syncIndicator = Config.getString("sap.services.syncIndicator", "TRUE");
		final String dateFormat = Config.getString("sap.service.bogof.dateformat", "yyyy-MM-dd");
		try
		{
			target.setSyncIndicator(syncIndicator);
			target.setCustomer(source.getUid());
			target.setSalesOrganisation(source.getSalesData().getSalesOrgId());
			target.setDate(SabmDateUtils.getGregorianCalendar(dateFormat, deliveryDate));

			return target;
		}
		catch (final Exception e)
		{
			LOG.error("Error occured trying to create Request object from B2BUnitModel", e);
		}
		return null;
	}

}
