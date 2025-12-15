package com.apb.facades.price.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.price.ApbPriceUpdateFacade;
import com.apb.facades.price.PriceInfo;
import com.apb.facades.price.PriceInfoData;
import com.apb.integration.data.ApbPriceData;
import com.apb.integration.data.ApbProductPriceInfo;
import com.apb.integration.data.AsahiProductInfo;
import com.apb.integration.price.dto.ApbPriceRequestData;
import com.apb.integration.price.service.AsahiPriceIntegrationService;
import com.apb.service.b2bunit.ApbB2BUnitService;

public class ApbPriceUpdateFacadeImpl implements ApbPriceUpdateFacade
{
	private static final String DEFAULT_CURRENCY_FOR_PRICE = "AUD";

	@Resource
	UserService userService;

	@Resource
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Resource
	private AsahiPriceIntegrationService asahiPriceIntegrationService;

	@Resource(name = "priceDataFactory")
	private PriceDataFactory priceDataFactory;

	@Resource(name = "apbB2BUnitService")
	private ApbB2BUnitService apbB2BUnitService;

	@Resource
	private SessionService sessionService;

	@Resource
	private AsahiSiteUtil  asahiSiteUtil;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	private static final Logger LOG = LoggerFactory.getLogger(ApbPriceUpdateFacadeImpl.class);


	@Override
	public PriceInfoData updatePriceInfoData(final Map<String, Long> productQuantityMap, final boolean isFreightIncluded)
	{
		final String accountNumber = apbB2BUnitService.getAccNumForCurrentB2BUnit();
		if (null != accountNumber)
		{
			final ApbPriceRequestData requestData = new ApbPriceRequestData();
			Map<String,  Map<String, Long>> qtyWithLineNumber = new HashMap<>();
			
			final AtomicInteger counter = new AtomicInteger(0);
			productQuantityMap.entrySet().forEach(entry -> {
				Map<String, Long> lineAndQty = new HashMap<>();
				lineAndQty.put(String.valueOf(counter.getAndIncrement()), entry.getValue());
				qtyWithLineNumber.put(entry.getKey(), lineAndQty);
			});
			
			requestData.setProductQuantityMap(qtyWithLineNumber);
			requestData.setBonusStatusMap(null);
			requestData.setAccNum(accountNumber);
			requestData.setFreightIncluded(isFreightIncluded);

			final ApbPriceData priceData = asahiPriceIntegrationService.getProductsPrice(requestData);

			return convertPriceResponseToPriceInfo(priceData);
		}
		return null;
	}

	/**
	 * Convert price response to price info.
	 *
	 * @param priceData the price data
	 * @return the price info data
	 */
	private PriceInfoData convertPriceResponseToPriceInfo(final ApbPriceData priceData)
	{
		if (null == priceData)
		{
			return null;
		}
		final PriceInfoData priceInfoData = new PriceInfoData();
		if (CollectionUtils.isNotEmpty(priceData.getProductPriceInfo()))
		{
			final List<PriceInfo> productsInfo = new ArrayList<>();
			for (final ApbProductPriceInfo productPriceData : priceData.getProductPriceInfo())
			{

				final PriceInfo priceDetails = new PriceInfo();
				priceDetails.setCode(productPriceData.getCode());

				priceDetails.setWET(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(productPriceData.getWET()),
						DEFAULT_CURRENCY_FOR_PRICE));
				priceDetails.setNetPrice(priceDataFactory.create(PriceDataType.BUY,
						BigDecimal.valueOf(productPriceData.getNetPrice()), DEFAULT_CURRENCY_FOR_PRICE));
				priceDetails.setListPrice(priceDataFactory.create(PriceDataType.BUY,
						BigDecimal.valueOf(productPriceData.getListPrice()), DEFAULT_CURRENCY_FOR_PRICE));

				productsInfo.add(priceDetails);
			}
			priceInfoData.setProductPriceInfo(productsInfo);
		}
		return priceInfoData;
	}

	/**
	 * This method will return the price object associated with product id in a map
	 *
	 * @param productIds
	 *           id's
	 * @return Map
	 */
	@Override
	public Map<String, PriceInfo> getPriceMapFromSession(final Set<String> productIds) {
		final Map<String, AsahiProductInfo> data = asahiCoreUtil.getPriceMapFromSession(productIds);
		final Map<String, PriceInfo> priceMap = new HashMap<>();

		if (null == data) {
			LOG.debug("Inclusion list empty for products : " + productIds);
			productIds.stream().forEach(id -> {
				priceMap.put(id, null);
			});
		} else {
			data.entrySet().stream().forEach(entry -> {
				final AsahiProductInfo product = entry.getValue();
				PriceInfo info = null;
				if (null != product)
				{
					info = new PriceInfo();
					info.setCode(product.getMaterialNumber());
					final BigDecimal cdlCost = BigDecimal
							.valueOf(product.getContainerDepositLevy() != null ? product.getContainerDepositLevy() : 0.0D);
					final BigDecimal listPrice = BigDecimal
							.valueOf(product.getListPrice() != null ? product.getListPrice() + cdlCost.doubleValue() : 0.0D);
					final BigDecimal promoPrice = BigDecimal
							.valueOf(product.getNetPrice() != null ? product.getNetPrice() + cdlCost.doubleValue() : 0.0D);
					info.setListPrice(priceDataFactory.create(PriceDataType.BUY, listPrice, asahiSiteUtil.getCurrency()));
					info.setNetPrice(priceDataFactory.create(PriceDataType.BUY, promoPrice, asahiSiteUtil.getCurrency()));
				}
				priceMap.put(entry.getKey(), info);
		});
		}
		return priceMap;
	}
}
