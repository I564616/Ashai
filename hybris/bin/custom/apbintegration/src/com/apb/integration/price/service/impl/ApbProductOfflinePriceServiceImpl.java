package com.apb.integration.price.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.apb.core.model.OfflineProductPriceModel;
import com.apb.integration.data.ApbPriceData;
import com.apb.integration.data.ApbProductPriceInfo;
import com.apb.integration.price.dao.ApbProductOfflinePriceDao;
import com.apb.integration.price.service.ApbProductOfflinePriceService;
import com.apb.integration.service.config.AsahiConfigurationService;


public class ApbProductOfflinePriceServiceImpl implements ApbProductOfflinePriceService
{
	@Resource
	private ApbProductOfflinePriceDao apbProductOfflinePriceDao;

	public static final String PRODUCT_CODE_FOR_DELIVERY_SURCHARGE = "product.code.for.delivery.surcharge.apb";
	public static final String TOTAL_GST = "totalGST";
	public static final String TOTAL_FREIGHT = "totalFreight";
	public static final String SUBTOTAL = "subTotal";

	@Resource(name = "asahiIconfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Override
	public ApbPriceData getPricesForProducts(final Map<String, Map<String, Long>> requestedProductMap,
			final Map<String, Map<String, Long>> bonusStatusMap, final String accNum)
	{
		final Map<String, Double> priceMap = new HashMap<>();
		priceMap.put(TOTAL_GST, 0D);
		priceMap.put(SUBTOTAL, 0D);
		priceMap.put(TOTAL_FREIGHT, 0D);

		final String deliverySurchargeCode = asahiConfigurationService.getString(PRODUCT_CODE_FOR_DELIVERY_SURCHARGE, "delivery_product");
		final List<ApbProductPriceInfo> productList = new ArrayList<>();
		final List<String> requestedProducts = new ArrayList<>(requestedProductMap.keySet());
		if (requestedProducts.contains(deliverySurchargeCode))
		{
			requestedProducts.remove(deliverySurchargeCode);
			setDeliverySurchargePrice(deliverySurchargeCode, productList, priceMap);
		}
		final List<OfflineProductPriceModel> productPrices = apbProductOfflinePriceDao.getProductPrices(requestedProducts, accNum);
		final ApbPriceData priceData = new ApbPriceData();
		priceData.setFreight(priceMap.get(TOTAL_FREIGHT));
		priceData.setGST(priceMap.get(TOTAL_GST));
		priceData.setSubTotal(priceMap.get(SUBTOTAL) + priceMap.get(TOTAL_FREIGHT));

		
		if (CollectionUtils.isNotEmpty(requestedProducts))
		{
			
			double totalFreight = priceMap.get(TOTAL_FREIGHT);
			double totalGST = priceMap.get(TOTAL_GST);
			double subtotal = priceMap.get(SUBTOTAL);
			final List<String> resultProducts = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(productPrices))
			{
				for (final OfflineProductPriceModel productPriceData : productPrices)
				{
					final ApbProductPriceInfo productData = new ApbProductPriceInfo();
					productData.setCode(productPriceData.getProductCode());
					productData.setNetPrice(productPriceData.getNetPrice());
					productData.setListPrice(productPriceData.getListPrice());
					productData.setWET(productPriceData.getWET());
					productData.setBonus(false);

					final Map<String, Long> lineAndQuantity = requestedProductMap.get(productPriceData.getProductCode());
					final Long quantity = lineAndQuantity.entrySet().iterator().next().getValue();
					totalFreight += quantity * (null != productPriceData.getFreight() ? productPriceData.getFreight() : 0);
					totalGST += quantity * (null != productPriceData.getGST() ? productPriceData.getGST() : 0);
					subtotal += quantity * ((null != productPriceData.getNetPrice() ? productPriceData.getNetPrice() : 0)
							+ (null != productPriceData.getWET() ? productPriceData.getWET() : 0));
					productList.add(productData);
					resultProducts.add(productPriceData.getProductCode());
				}
				priceMap.put(TOTAL_GST, totalGST);
				priceMap.put(SUBTOTAL, subtotal);
				priceMap.put(TOTAL_FREIGHT, totalFreight);
			}
			requestedProducts.removeAll(resultProducts);
			if (CollectionUtils.isNotEmpty(requestedProducts))
			{
				setDefaultPriceForProductsWithoutPrice(requestedProducts, productList, priceMap, requestedProductMap);
			}
			priceData.setProductPriceInfo(productList);
			priceData.setFreight(priceMap.get(TOTAL_FREIGHT));
			priceData.setGST(priceMap.get(TOTAL_GST));
			priceData.setSubTotal(priceMap.get(SUBTOTAL)+priceMap.get(TOTAL_FREIGHT));
		}
		if (MapUtils.isNotEmpty(bonusStatusMap))
		{
			for (final Map.Entry<String, Map<String, Long>> entry : bonusStatusMap.entrySet())
			{
				final ApbProductPriceInfo productData = new ApbProductPriceInfo();
				productData.setCode(entry.getKey());
				productData.setNetPrice(0.0D);
				productData.setListPrice(0.0D);
				productData.setWET(0.0D);
				productData.setBonus(true);
				final Map.Entry<String, Long> lineAndQty = entry.getValue().entrySet().iterator().next();
				productData.setLineNumber(lineAndQty.getKey());
				productList.add(productData);
			}
		}
		priceData.setProductPriceInfo(productList);
		
		return priceData;
	}

	private void setDefaultPriceForProductsWithoutPrice(final List<String> requestedProducts,
			final List<ApbProductPriceInfo> responseProductList, final Map<String, Double> priceMap, final Map<String, Map<String, Long>> requestedProductMap)
	{
		final OfflineProductPriceModel defaultProductResponse = apbProductOfflinePriceDao.getDefaultPrice();
		double totalFreight = priceMap.get(TOTAL_FREIGHT);
		double totalGST = priceMap.get(TOTAL_GST);
		double subtotal = priceMap.get(SUBTOTAL);

		for (final String productCode : requestedProducts)
		{
			final ApbProductPriceInfo responseProduct = new ApbProductPriceInfo();
			responseProduct.setCode(productCode);
			responseProduct.setNetPrice(defaultProductResponse.getNetPrice());
			responseProduct.setListPrice(defaultProductResponse.getListPrice());
			responseProduct.setWET(defaultProductResponse.getWET());
			responseProductList.add(responseProduct);
			final Map<String, Long> lineAndQuantity = requestedProductMap.get(productCode);
			final Long quantity = lineAndQuantity.entrySet().iterator().next().getValue();
			totalFreight += quantity * defaultProductResponse.getFreight();
			totalGST += quantity * defaultProductResponse.getGST();
			subtotal += quantity * (defaultProductResponse.getNetPrice() + defaultProductResponse.getWET());
		}
		priceMap.put(TOTAL_GST, totalGST);
		priceMap.put(SUBTOTAL, subtotal);
		priceMap.put(TOTAL_FREIGHT, totalFreight);
	}

	private void setDeliverySurchargePrice(final String deliverySurchargeCode, final List<ApbProductPriceInfo> responseProductList,
			final Map<String, Double> priceMap)
	{
		final OfflineProductPriceModel deliverySurChargeResponse = apbProductOfflinePriceDao.getDeliveryPrice(deliverySurchargeCode);
		final ApbProductPriceInfo responseProduct = new ApbProductPriceInfo();
		responseProduct.setCode(deliverySurchargeCode);
		responseProduct.setNetPrice(deliverySurChargeResponse.getNetPrice());
		responseProduct.setWET(deliverySurChargeResponse.getWET());
		//responseProduct.setListPrice(deliverySurChargeResponse.getListPrice());
		responseProductList.add(responseProduct);
		priceMap.put(TOTAL_GST, deliverySurChargeResponse.getGST());
		priceMap.put(SUBTOTAL, deliverySurChargeResponse.getNetPrice());
	}
}
