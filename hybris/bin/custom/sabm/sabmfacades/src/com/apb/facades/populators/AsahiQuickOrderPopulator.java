/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.apb.facades.populators;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.apb.core.model.ApbProductModel;
import com.apb.core.model.PackageSizeModel;
import com.apb.core.services.cart.ApbProductStockInCartEntryService;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.deal.data.AsahiDealData;
import com.apb.facades.order.data.AsahiQuickOrderData;
import com.apb.facades.order.data.AsahiQuickOrderEntryData;
import com.apb.facades.product.data.PackageSizeData;
import com.apb.facades.populators.ApbStockPopulator;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;

/**
 * @author Naveen.Wadhwani Populator to populate the values of Quickorderdata
 *         from
 */
public class AsahiQuickOrderPopulator implements
		Populator<List<OrderModel>, AsahiQuickOrderData> {
	private Converter<PackageSizeModel, PackageSizeData> apbPackageSizeConverter;

	private Converter<MediaModel, ImageData> imageConverter;

	private UrlResolver<ProductModel> productModelUrlResolver;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private ApbProductStockInCartEntryService apbProductStockInCartEntryService;

	@Resource(name = "sabmDealsSearchFacade")
	private SABMDealsSearchFacade sabmDealsSearchFacade;

	@Resource
	private ApbB2BUnitService apbB2BUnitService;

	@Resource
	private ApbStockPopulator<ProductModel,StockData> apbStockPopulator;

	@Override
	public void populate(final List<OrderModel> source,
			final AsahiQuickOrderData target) throws ConversionException {
		final Set<String> dateRange = new LinkedHashSet<String>();
		final Map<String, AsahiQuickOrderEntryData> map = new HashMap<String, AsahiQuickOrderEntryData>();

		for (final OrderModel om : source) {
			dateRange.add(getFormattedDate(om.getCreationtime()));
			for (final AbstractOrderEntryModel orderEntry : om.getEntries()) {
				if (BooleanUtils.isNotTrue(orderEntry.getIsBonusStock()))
				{
					final AsahiQuickOrderEntryData asahiquickOrderEntryData = new AsahiQuickOrderEntryData();
					final Map<String, Long> date = new HashMap<String, Long>();
					if (null != orderEntry.getProduct() && map.containsKey(orderEntry.getProduct().getCode()))
					{
						map.get(orderEntry.getProduct().getCode()).getDateRange().put(getFormattedDate(om.getCreationtime()),
								orderEntry.getQuantity());
					}
					else
					{
						final ApbProductModel apbproduct = (ApbProductModel) orderEntry.getProduct();
						if (null != apbproduct)
						{
							if (null != apbproduct.getBrand())
							{
								asahiquickOrderEntryData.setBrand(apbproduct.getBrand().getName());
							}
							if (null != apbproduct.getPortalUnitVolume())
							{
								asahiquickOrderEntryData.setPortalUnitVolume(apbproduct.getPortalUnitVolume().getName());
							}
							if (null != apbproduct.getPackageSize())
							{
								asahiquickOrderEntryData
										.setPackageSize(getApbPackageSizeConverter().convert(apbproduct.getPackageSize()));
							}

							asahiquickOrderEntryData.setCode(apbproduct.getCode());
							asahiquickOrderEntryData.setName(apbproduct.getName());
							if (null != apbproduct.getThumbnail())
							{
								asahiquickOrderEntryData.setImage(getImageConverter().convert(apbproduct.getThumbnail()));
							}
							asahiquickOrderEntryData.setUrl(getProductModelUrlResolver().resolve(orderEntry.getProduct()));
							date.put(getFormattedDate(om.getCreationtime()), orderEntry.getQuantity());
							asahiquickOrderEntryData.setActive(apbproduct.isActive());
							asahiquickOrderEntryData.setDateRange(date);
							asahiquickOrderEntryData.setMaxQty((int) (asahiSiteUtil.getSgaGlobalMaxOrderQty()
									- apbProductStockInCartEntryService.getProductQtyFromCart(apbproduct.getCode())));
							map.put(apbproduct.getCode(), asahiquickOrderEntryData);
						}

					}
					asahiquickOrderEntryData.setQuantity(Long.toString(orderEntry.getQuantity()));
					asahiquickOrderEntryData.setLastOrdered(new SimpleDateFormat("dd/MM").format(om.getCreationtime()));
					asahiquickOrderEntryData.setLastOrderedDate(om.getCreationtime());
					final List<AsahiDealData> asahiDealsInfo = sabmDealsSearchFacade
							.getSGADealsDataForProductAndUnit(orderEntry.getProduct().getCode(), apbB2BUnitService.getCurrentB2BUnit());
					if (CollectionUtils.isNotEmpty(asahiDealsInfo))
					{
						asahiquickOrderEntryData.setDealsFlag(true);
						final List<String> dealTitles = asahiDealsInfo.stream().map(dealData -> dealData.getTitle())
								.collect(Collectors.toList());
						SabmStringUtils.getSortedDealTitles(dealTitles);
						asahiquickOrderEntryData.setDealsTitle(dealTitles);
					}

					final StockData stock = new StockData();
					apbStockPopulator.populate(orderEntry.getProduct(), stock);
					asahiquickOrderEntryData.setStock(stock);
				}

			}

		}
		if(CollectionUtils.isNotEmpty(dateRange)){
			final List<String> rangeList = new ArrayList<>();
			rangeList.addAll(dateRange);
			Collections.reverse(rangeList);
			target.setDateRange(new LinkedHashSet<String>(rangeList));
		}
		if (CollectionUtils.isNotEmpty(map.values())) {
			target.setEntries(new ArrayList<AsahiQuickOrderEntryData>(map
					.values()));
		}
	}

	/**
	 * Gets date in dd MMM format
	 * @param date
	 */
	private String getFormattedDate(final Date date) {

		final Format formatter = new SimpleDateFormat("dd MMM");
		String dateString = null;
		try {
			dateString = formatter.format(date);
		} catch (final Exception conversionException) {
			dateString = "01 Jan";
		}
		return dateString;
	}

	/**
	 * @return apbpackagesize converter
	 */
	public Converter<PackageSizeModel, PackageSizeData> getApbPackageSizeConverter() {
		return apbPackageSizeConverter;
	}

	/**
	 * @param apbPackageSizeConverter
	 */
	public void setApbPackageSizeConverter(
			final Converter<PackageSizeModel, PackageSizeData> apbPackageSizeConverter) {
		this.apbPackageSizeConverter = apbPackageSizeConverter;
	}

	protected Converter<MediaModel, ImageData> getImageConverter() {
		return imageConverter;
	}

	public void setImageConverter(
			final Converter<MediaModel, ImageData> imageConverter) {
		this.imageConverter = imageConverter;
	}


	protected UrlResolver<ProductModel> getProductModelUrlResolver()
	{
		return productModelUrlResolver;
	}

	public void setProductModelUrlResolver(final UrlResolver<ProductModel> productModelUrlResolver)
	{
		this.productModelUrlResolver = productModelUrlResolver;
	}

}