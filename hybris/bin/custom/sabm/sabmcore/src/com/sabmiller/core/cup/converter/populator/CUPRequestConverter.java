/**
 *
 */
package com.sabmiller.core.cup.converter.populator;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.sabmiller.core.util.SabmConverter;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.integration.sap.cup.request.CustomerUnitPricingRequest;


/**
 * The Class CUPRequestConverter.
 *
 * @author joshua.a.antony
 */

public class CUPRequestConverter implements SabmConverter<B2BUnitModel, CustomerUnitPricingRequest, Date>
{

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(CUPRequestConverter.class);

	private String requestType;

	/** The sync indicator. */
	@Value(value = "${sap.services.syncIndicator:TRUE}")
	private String syncIndicator;

	/** The date format. */
	@Value(value = "${sap.service.cup.dateformat:yyyy-MM-dd}")
	private String dateFormat;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public CustomerUnitPricingRequest convert(final B2BUnitModel source) throws ConversionException
	{
		throw new ConversionException("Operation not supported");
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public CustomerUnitPricingRequest convert(final B2BUnitModel source, final CustomerUnitPricingRequest target)
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
	public CustomerUnitPricingRequest convert(final B2BUnitModel source, final CustomerUnitPricingRequest target,
			final Date deliveryDate) throws ConversionException
	{
		try
		{
			final GregorianCalendar c = new GregorianCalendar();
			c.setTime(SabmDateUtils.getCurrentDate(dateFormat));

			target.setSyncIndicator(syncIndicator);
			target.setDate(SabmDateUtils.getGregorianCalendar(dateFormat, deliveryDate));
			target.setCustomer(source.getUid());
			target.setSalesOrganisation(source.getSalesData() != null ? source.getSalesData().getSalesOrgId() : null);

			return target;
		}
		catch (final Exception e)
		{
			LOG.error("Error occured trying to create CustomerUnitPricingRequest object from B2BUnitModel", e);
		}
		return null;
	}
}
