package com.apb.facades.populators;

import de.hybris.platform.commercefacades.order.data.OrderTemplateData;
import de.hybris.platform.commercefacades.order.data.OrderTemplateEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.model.OrderTemplateEntryModel;
import com.apb.core.model.OrderTemplateModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.cart.ApbProductStockInCartEntryService;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.populators.ApbStockPopulator;


public class AsahiOrderTemplatePopulator implements Populator<OrderTemplateModel, OrderTemplateData>
{

	/** The product converter. */
	private Converter<ProductModel, ProductData> productConverter;

	/** The price data factory. */
	@Autowired
	private PriceDataFactory priceDataFactory;

	/** The asahi configuration service. */
	@Resource
	private AsahiConfigurationService asahiConfigurationService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private ApbProductStockInCartEntryService apbProductStockInCartEntryService;

	@Resource
	private ApbStockPopulator<ProductModel,StockData> apbStockPopulator;

	/**
	 * Populate.
	 *
	 * @param source
	 *           the source
	 * @param target
	 *           the target
	 */
	@Override
	public void populate(final OrderTemplateModel source, final OrderTemplateData target) throws ConversionException
	{
		this.addOrderTemplateData(source, target);
		final List<OrderTemplateEntryModel> templateEntries = source.getTemplateEntry();
		if (CollectionUtils.isNotEmpty(templateEntries))
		{
			final List<OrderTemplateEntryData> templateDataList = new ArrayList<OrderTemplateEntryData>();

			long totalOrderedQty = 0;
			for (final OrderTemplateEntryModel entry : templateEntries)
			{
				final OrderTemplateEntryData entryData = new OrderTemplateEntryData();
				entryData.setEntryNumber(entry.getEntryNumber());
				totalOrderedQty = totalOrderedQty + entry.getQuantity();
				entryData.setQuantity(entry.getQuantity());
				entryData.setTotalPrice(entry.getTotalPrice());
				entryData.setBasePrice(entry.getBasePrice());

				entryData.setTemplateTotalPrice(createPrice(source, entry.getTotalPrice()));
				entryData.setTemplateBasePrice(createPrice(source, entry.getBasePrice()));

				entryData.setPk(entry.getPk().toString());
				

				if (asahiSiteUtil.isSga())
				{
					final ProductData product = productConverter.convert(entry.getProduct());
					final StockData stock = new StockData();
					apbStockPopulator.populate(entry.getProduct(), stock);
					product.setStock(stock);
					entryData.setProduct(product);

					entryData.setMaxQty((int) (asahiSiteUtil.getSgaGlobalMaxOrderQty()
							- apbProductStockInCartEntryService.getProductQtyFromCart(entry.getProduct().getCode())));
				}

				else
				{
					entryData.setProduct(productConverter.convert(entry.getProduct()));
				}
				templateDataList.add(entryData);
			}
			target.setTotalOrderedQty(Long.valueOf(templateEntries.size()));
			target.setTemplateEntry(templateDataList);

		}
	}

	/**
	 * Adds the order template data.
	 *
	 * @param source
	 *           the source
	 * @param target
	 *           the target
	 */
	protected void addOrderTemplateData(final OrderTemplateModel source, final OrderTemplateData target)
	{
		target.setCode(source.getCode());
		target.setName(source.getName());
		if (null != source.getSaveTime())
		{
			final DateFormat df = new SimpleDateFormat(this.asahiConfigurationService
					.getString(ApbCoreConstants.ASAHI_DATE_FORMAT_KEY, ApbCoreConstants.DEFER_DELIVERY_DATEPATTERN));
			target.setCreatedDate(df.format(source.getSaveTime()));
			target.setSaveTime(source.getSaveTime());
		}
		if (null != source.getExpirationTime())
		{
			target.setExpirationTime(source.getExpirationTime());
		}

		if (null != source.getSavedBy())
		{
			final PrincipalData savedBy = new PrincipalData();
			if (StringUtils.isNotEmpty(source.getSavedBy().getName()))
			{
				savedBy.setName(source.getSavedBy().getName());
			}

			if (StringUtils.isNotEmpty(source.getSavedBy().getUid()))
			{
				savedBy.setUid(source.getSavedBy().getUid());

			}
			target.setSavedBy(savedBy);
		}
	}

	/**
	 * Creates the price.
	 *
	 * @param source
	 *           the source
	 * @param val
	 *           the val
	 * @return the price data
	 */
	protected PriceData createPrice(final AbstractOrderModel source, final Double val)
	{
		if (source == null)
		{
			throw new IllegalArgumentException("source order must not be null");
		}

		final CurrencyModel currency = source.getCurrency();
		if (currency == null)
		{
			throw new IllegalArgumentException("source order currency must not be null");
		}

		// Get double value, handle null as zero
		final double priceValue = val != null ? val.doubleValue() : 0d;

		return this.priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(priceValue), currency);
	}

	/**
	 * @return the productConverter
	 */
	public Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	/**
	 * @param productConverter
	 *           the productConverter to set
	 */
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}
}
