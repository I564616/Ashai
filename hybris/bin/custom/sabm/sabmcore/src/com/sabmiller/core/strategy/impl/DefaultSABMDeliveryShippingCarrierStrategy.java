/**
 *
 */
package com.sabmiller.core.strategy.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.ShippingCarrierModel;
import com.sabmiller.core.strategy.SABMDeliveryShippingCarrierStrategy;


/**
 * DefaultSABMDeliveryShippingCarrierStrategy Strategy to create/update ShippingCarrier for model.
 *
 * @author yaopeng
 *
 */
public class DefaultSABMDeliveryShippingCarrierStrategy implements SABMDeliveryShippingCarrierStrategy
{

	/** The Constant LOG. */
	protected static final Logger LOG = LoggerFactory.getLogger(DefaultSABMDeliveryShippingCarrierStrategy.class);

	/** The unit service. */
	private ModelService modelService;

	/*
	 * Strategy to create/update ShippingCarrier for model.
	 *
	 * @see com.sabmiller.core.strategy.SABMDeliveryShippingCarrierStrategy#storeShippingCarrier(de.hybris.platform.
	 * commerceservices.service.data.CommerceCheckoutParameter)
	 */
	@Override
	public boolean setShippingCarrier(final CommerceCheckoutParameter parameter)
	{
		final CartModel cartModel = parameter.getCart();
		final ShippingCarrierModel shippingCarrierModel = parameter.getShippingCarrier();

		validateParameterNotNull(cartModel, "Cart model cannot be null");

		LOG.debug("Setting shippingCarrier: {} in cart: {}", shippingCarrierModel, cartModel);

		try
		{
			cartModel.setDeliveryShippingCarrier(shippingCarrierModel);
			// Check that the shippingCarrier model belongs to the same user as the cart
			getModelService().save(cartModel);

			return true;
		}
		catch (final Exception e)
		{
			LOG.warn("Can not be saved the shippingCarrier: " + shippingCarrierModel + " in cart: " + cartModel.getCode(), e);
			return false;
		}

	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}


	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
