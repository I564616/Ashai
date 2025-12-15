package com.apb.facades.stock.check;

import java.util.List;

import com.apb.integration.data.ApbStockonHandProductData;


/**
 * The Interface ApbStockOnHandFacade.
 */
@FunctionalInterface
public interface ApbStockOnHandFacade

{
	
	/**
	 * Check stock.
	 *
	 * @param wareHouse the ware house
	 * @param productList the product list
	 * @return the list
	 */
	List<ApbStockonHandProductData> checkStock(String wareHouse, List<String> productList);
}
