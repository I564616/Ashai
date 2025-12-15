package com.sabmiller.core.cart.service;

import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.List;

import com.sabmiller.core.model.OrderMessageModel;


public interface SabmCommerceCartService extends CommerceCartService
{
	/**
	 * Retrieve the latest list of changes (messages) done to the by cart by Order simulate. Once these messages are
	 * retrieved they will be removed from the cart. Only use this to actually retrieve the messages, to check if there
	 * are any use @see hasSAPOrderSimulateChanges
	 *
	 * @return List of messages or empty collection if none available
	 */
	List<OrderMessageModel> getSAPOrderSimulateChanges(final AbstractOrderModel orderModel);

	/**
	 * Check if the cart has SAP changes to show to the user
	 *
	 * @return false if there is no cart or no messages
	 */
	boolean hasSAPOrderSimulateChanges(final AbstractOrderModel orderModel);

}
