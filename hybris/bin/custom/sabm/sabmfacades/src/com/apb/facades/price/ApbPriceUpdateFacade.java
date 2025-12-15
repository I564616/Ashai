package com.apb.facades.price;

import java.util.Map;
import java.util.Set;

/**
 * The Interface ApbPriceUpdateFacade.
 */
public interface ApbPriceUpdateFacade {
	
	/**
	 * Update price info data.
	 *
	 * @param productQuantityMap the product quantity map
	 * @param isFreightIncluded the is freight included
	 * @return the price info data
	 */
	PriceInfoData updatePriceInfoData(Map<String, Long> productQuantityMap, boolean isFreightIncluded);
	
	
	/**
	 * This method will return the map of product codes along with price data
	 * @param productIds
	 * @return
	 */
	Map<String, PriceInfo> getPriceMapFromSession(Set<String> productIds);
}
