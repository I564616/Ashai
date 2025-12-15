package com.apb.integration.price.service;

import java.util.Map;

import com.apb.integration.data.ApbPriceData;


/**
 * The Interface ApbProductOfflinePriceService.
 */
@FunctionalInterface
public interface ApbProductOfflinePriceService
{

	/**
	 * Gets the prices for products.
	 *
	 * @param requestedProductMap
	 *           the product codes
	 * @param bonusStatusMap
	 * @param accNum
	 *           the acc num
	 * @return the prices for products
	 */
	ApbPriceData getPricesForProducts(Map<String, Map<String, Long>> requestedProductMap, Map<String, Map<String, Long>> bonusStatusMap,
			String accNum);

}
