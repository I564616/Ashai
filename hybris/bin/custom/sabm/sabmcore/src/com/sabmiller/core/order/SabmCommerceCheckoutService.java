package com.sabmiller.core.order;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;


public interface SabmCommerceCheckoutService extends CommerceCheckoutService
{


	void clearPreviousPaymentAttempts(final CartModel cartModel);

	/**
	 * Initiates checkout countdown
	 *
	 * @param cartModel
	 *           the cart
	 */
	void startCheckoutCountdown(final CartModel cartModel);

	/**
	 * Update the current user session B2BUnit default address.
	 * <p>
	 * if the default address for the current B2BUnit is not set, will create a new @DeliveryDefaultAddressModel
	 * </p>
	 * 
	 * @param addressModel
	 * @param b2bCustomer
	 */
	void updateDefaultAddress(final AddressModel addressModel, final B2BCustomerModel b2bCustomer);
}
