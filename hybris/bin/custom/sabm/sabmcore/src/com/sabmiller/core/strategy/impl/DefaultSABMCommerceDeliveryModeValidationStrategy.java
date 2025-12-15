/**
 *
 */
package com.sabmiller.core.strategy.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.impl.DefaultCommerceDeliveryModeValidationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;

import java.util.Collection;


public class DefaultSABMCommerceDeliveryModeValidationStrategy extends DefaultCommerceDeliveryModeValidationStrategy
{
	@Override
	public void validateDeliveryMode(final CommerceCheckoutParameter parameter)
	{
		final CartModel cartModel = parameter.getCart();
		validateParameterNotNull(cartModel, "Cart model cannot be null");

		final DeliveryModeModel currentDeliveryMode = cartModel.getDeliveryMode();
		if (currentDeliveryMode != null)
		{
			final Collection<DeliveryModeModel> supportedDeliveryModes = getDeliveryService()
					.getSupportedDeliveryModeListForOrder(cartModel);

			if (!supportedDeliveryModes.contains(currentDeliveryMode))
			{
				cartModel.setDeliveryMode(null);
				getModelService().save(cartModel);
				getModelService().refresh(cartModel);
			}
		}
	}
}
