package com.apb.core.services.cart.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.apb.core.services.cart.ApbProductStockInCartEntryService;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;


/**
 *
 */
public class ApbProductStockInCartEntryServiceImpl implements ApbProductStockInCartEntryService
{
	@Resource
	private CartService cartService;
	/**
	 * @return
	 */
	public boolean getProductOutOfStock()
	{
		// call rest service here
		final boolean availablePhysical = Boolean.TRUE;
		return availablePhysical;

	}
	
	@Override
	public long getProductQtyFromCart(String productCode){
		final CartModel cart = cartService.getSessionCart();
		final List<AbstractOrderEntryModel> productEntries = cart.getEntries().stream()
				.filter(entry -> entry.getProduct().getCode().equals(productCode)).collect(Collectors.toList());
		long totalStocks = 0L;
		for (final AbstractOrderEntryModel entry : productEntries)
		{
			totalStocks += entry.getQuantity();
		}
		return totalStocks;
	}
}
