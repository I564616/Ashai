package com.sabmiller.facades.util;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;


/**
 * Created by evariz.d.paragoso on 5/29/17.
 */
public class SavePriceUtil
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SavePriceUtil.class);

	public static PriceData getSavingsPrice(final PriceDataType priceType, final PriceRowModel priceRow, CommonI18NService commonI18NService, PriceDataFactory priceDataFactory)
	{
		if (null != priceRow && null != priceRow.getPrice())
		{

			// calculated  the SavingsPrice by BasePrice subtract Price
			return priceDataFactory.create(priceType, BigDecimal.valueOf(checkDoubleEmpty(priceRow.getBasePrice()).doubleValue())
					.subtract(BigDecimal.valueOf(checkDoubleEmpty(priceRow.getPrice()).doubleValue())), priceRow.getCurrency());
		}
		else
		{

			return priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(0), commonI18NService.getCurrentCurrency());

		}
	}

	/**
	 * If the value is null or format problem then return 0
	 *
	 * @param value
	 * @return Double
	 */
	public static Double checkDoubleEmpty(final Object value)
	{
		Double dv;
		try
		{
			dv = value == null ? Double.valueOf(0d) : (Double) value;
		}
		catch (final RuntimeException e)
		{
			LOG.warn("The value translation exception", e);
			dv = Double.valueOf(0d);
		}
		return dv;
	}
}
