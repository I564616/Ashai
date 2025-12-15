/**
 *
 */
package com.sabmiller.core.strategy.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.impl.DefaultCommerceDeliveryModeStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;


public class DefaultSABMCommerceDeliveryModeStrategy extends DefaultCommerceDeliveryModeStrategy
{
	@Override
	public boolean setDeliveryMode(final CommerceCheckoutParameter parameter)
	{
		final DeliveryModeModel deliveryModeModel = parameter.getDeliveryMode();
		final CartModel cartModel = parameter.getCart();

		validateParameterNotNull(cartModel, "Cart model cannot be null");
		validateParameterNotNull(deliveryModeModel, "Delivery mode model cannot be null");

		cartModel.setDeliveryMode(deliveryModeModel);
		getModelService().save(cartModel);

		return true;
	}
}
