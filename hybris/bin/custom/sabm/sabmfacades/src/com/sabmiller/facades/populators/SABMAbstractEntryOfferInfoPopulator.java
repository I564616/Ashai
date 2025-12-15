/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.commercefacades.order.data.EntryOfferInfoData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.sabmiller.core.model.EntryOfferInfoModel;
import com.sabmiller.facades.product.data.UomData;
import com.sabmiller.integration.sap.enums.PricingCalculationType;


/**
 *
 */
public class SABMAbstractEntryOfferInfoPopulator implements Populator<EntryOfferInfoModel, EntryOfferInfoData>
{
	private Converter<ProductModel, ProductData> productConverter;

	private PriceDataFactory priceDataFactory;


	@Override
	public void populate(final EntryOfferInfoModel source, final EntryOfferInfoData target) throws ConversionException
	{
		target.setScaleAmountType(source.getScaleAmountType());
		target.setOfferType(source.getOfferType());
		target.setScaleQuantity(source.getScaleQuantity());


		if (StringUtils.equalsIgnoreCase(PricingCalculationType.PERCENTAGE.getType(), target.getScaleAmountType()))
		{
			target.setScaleAmount(formatPercentageValue(source.getScaleAmount()));
		}
		else if (StringUtils.isNotBlank(source.getCurrencyCode()) && StringUtils.isNotBlank(source.getScaleAmount()))
		{
			target.setScaleAmount(formatPrice(source.getCurrencyCode(), source.getScaleAmount()));
		}

		if (source.getScaleUnit() != null)
		{
			target.setScaleUnit(convertUnit(source.getScaleUnit()));
		}
		if (source.getFreeGoodProduct() != null)
		{

			target.setFreeGoodProduct(productConverter.convert(source.getFreeGoodProduct()));
		}
		target.setFreeGoodQuantity(source.getFreeGoodQuantity());

	}

	protected String formatPercentageValue(final String val)
	{
		String formattedVal = val;
		String decimalVal = null;
		if (StringUtils.indexOf(val, ".") > 0)
		{
			formattedVal = StringUtils.substringBefore(val, ".");
			decimalVal = StringUtils.substringAfter(val, ".");
			if (Integer.parseInt(decimalVal) > 0)
			{
				formattedVal = formattedVal + "." + StringUtils.substring(decimalVal, 0, 2);
			}
		}
		return formattedVal;
	}

	protected String formatPrice(final String currencyCode, final String val)
	{

		final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY,
				BigDecimal.valueOf(NumberUtils.toDouble(val, 0)), currencyCode);
		return priceData.getFormattedValue();
	}

	private UomData convertUnit(final UnitModel unitModel)
	{
		final UomData uomData = new UomData();
		uomData.setCode(unitModel.getCode());
		uomData.setName(unitModel.getName());
		uomData.setPluralName(unitModel.getPluralName());
		return uomData;
	}

	public Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}


	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}


	/**
	 * @return the priceDataFactory
	 */
	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}


	/**
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}
}
