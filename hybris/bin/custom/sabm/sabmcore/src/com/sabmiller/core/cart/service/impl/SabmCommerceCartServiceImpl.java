package com.sabmiller.core.cart.service.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.collect.Lists;
import com.sabmiller.core.cart.service.SabmCommerceCartService;
import com.sabmiller.core.model.OrderMessageModel;


public class SabmCommerceCartServiceImpl extends DefaultCommerceCartService implements SabmCommerceCartService
{

	@Override
	public List<OrderMessageModel> getSAPOrderSimulateChanges(final AbstractOrderModel orderModel)
	{
		return orderModel.getSimulationMessages();
	}

	@Override
	public boolean hasSAPOrderSimulateChanges(final AbstractOrderModel orderModel)
	{
		return CollectionUtils.isNotEmpty(orderModel.getSimulationMessages());
	}

	@Override
	public List<CommerceCartModification> validateCart(final CommerceCartParameter parameter)
			throws CommerceCartModificationException
	{
		final CartModel cartModel = parameter.getCart();
		ServicesUtil.validateParameterNotNull(cartModel, "Cart model cannot be null");
		final List<CommerceCartModification> modifications = this.getCartValidationStrategy().validateCart(parameter);
		final List<CommerceCartModification> errorModifications = Lists.newArrayList();

		for (final CommerceCartModification modification : modifications)
		{
			if (!"success".equals(modification.getStatusCode()))
			{
				errorModifications.add(modification);
			}
		}

		return errorModifications;
	}

}
