/**
 *
 */
package com.sabmiller.core.strategy;

import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;


/**
 * SABMDeliveryShippingCarrierStrategy
 *
 * @author yaopeng
 *
 */
public interface SABMDeliveryShippingCarrierStrategy
{
	/**
	 * Strategy to create/update ShippingCarrier for model.
	 *
	 * @param parameter
	 * @return boolean
	 */
	boolean setShippingCarrier(CommerceCheckoutParameter parameter);
}
