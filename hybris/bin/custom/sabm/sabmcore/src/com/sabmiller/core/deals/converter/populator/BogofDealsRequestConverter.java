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
import com.sabmiller.integration.sap.deals.bogof.request.PricingBOGOFDealsRequest;


/**
 * @author joshua.a.antony
 *
 */

public class BogofDealsRequestConverter implements SabmConverter<B2BUnitModel, PricingBOGOFDealsRequest, Date>
{

	private static final Logger LOG = Logger.getLogger(BogofDealsRequestConverter.class);

	@Override
	public PricingBOGOFDealsRequest convert(final B2BUnitModel source) throws ConversionException
	{
		throw new ConversionException("Operation not supported");
	}


	@Override
	public PricingBOGOFDealsRequest convert(final B2BUnitModel source, final PricingBOGOFDealsRequest target)
			throws ConversionException
	{
		throw new ConversionException("Operation not supported");
	}

	@Override
	public PricingBOGOFDealsRequest convert(final B2BUnitModel source, final PricingBOGOFDealsRequest target,
			final Date deliveryDate) throws ConversionException
	{

		final String syncIndicator = Config.getString("sap.services.syncIndicator", "TRUE");
		final String dateFormat = Config.getString("sap.service.bogof.dateformat", "yyyy-MM-dd");
		final String conditionType = Config.getString("sap.service.bogof.condition.type", "YTPM");

		try
		{
			target.setSyncIndicator(syncIndicator);
			target.setConditionType(conditionType);
			target.setCustomer(source.getUid());
			target.setSalesOrganisation(source.getSalesData() != null ? source.getSalesData().getSalesOrgId() : null);
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
