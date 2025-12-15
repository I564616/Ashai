package com.apb.integration.stock.service;

import java.util.List;

import com.apb.integration.data.ApbStockonHandData;




/**
 * The Interface AsahiStockIntegrationService.
 */
@FunctionalInterface
public interface AsahiStockIntegrationService
{

	/**
	 * Gets the stockon hand.
	 *
	 * @param wareHouse the ware house
	 * @param products the products
	 * @return the stockon hand
	 */
	ApbStockonHandData getStockonHand(String wareHouse, List<String> products);
}
