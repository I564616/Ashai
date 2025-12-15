/**
 *
 */
package com.sabmiller.core.productexclusion.converter.populator;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.sabmiller.core.util.SabmConverter;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.integration.sap.productexclusion.request.ProductExclusionRequest;


/**
 * The Class ProductExclRequestConverter.
 *
 * @author ramsatish jagajyothi
 */

public class ProductExclRequestConverter implements SabmConverter<B2BUnitModel, ProductExclusionRequest, Date>
{

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(ProductExclRequestConverter.class);

	/** The sync indicator. */
	@Value(value = "${sap.services.syncIndicator:TRUE}")
	private String syncIndicator;

	/** The date format. */
	@Value(value = "${sap.service.productexclusion.dateformat:yyyy-MM-dd}")
	private String dateFormat;

	/** The condition type value. */
	@Value(value = "${sap.service.productexclusion.conditiontype:Z001}")
	private String conditionType;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public ProductExclusionRequest convert(final B2BUnitModel source) throws ConversionException
	{
		throw new ConversionException("Operation not supported");
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public ProductExclusionRequest convert(final B2BUnitModel source, final ProductExclusionRequest target)
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
	public ProductExclusionRequest convert(final B2BUnitModel source, final ProductExclusionRequest target,
			final Date deliveryDate) throws ConversionException
	{
		try
		{
			final GregorianCalendar c = new GregorianCalendar();
			c.setTime(SabmDateUtils.getCurrentDate(dateFormat));
			target.setConditionType(conditionType);
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
