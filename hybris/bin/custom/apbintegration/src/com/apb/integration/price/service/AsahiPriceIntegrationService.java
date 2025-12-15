package com.apb.integration.price.service;

import com.apb.integration.data.ApbPriceData;
import com.apb.integration.price.dto.ApbPriceRequestData;




/**
 * The Interface AsahiPriceIntegrationService.
 */
@FunctionalInterface
public interface AsahiPriceIntegrationService
{

	/**
	 * Get the price data from dynamics
	 *
	 * @param requestData
	 * @return price data
	 */
	ApbPriceData getProductsPrice(final ApbPriceRequestData requestData);
}
