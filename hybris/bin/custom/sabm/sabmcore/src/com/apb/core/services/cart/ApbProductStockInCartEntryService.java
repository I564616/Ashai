package com.apb.core.services.cart;

/**
 *
 */
public interface ApbProductStockInCartEntryService
{
	/**
	 * @return
	 */
	public boolean getProductOutOfStock();
	
	/**
	 * @return - product quantity in cart
	 */
	public long getProductQtyFromCart(String productCode);
}