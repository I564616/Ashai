package com.sabmiller.facades.order;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;

import java.util.List;

import com.sabmiller.facades.bdeordering.BdeOrderDetailsForm;
import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTransactionProcessData;

/**
 * The Interface SABMCheckoutFacade.
 */
public interface SABMCheckoutFacade extends AcceleratorCheckoutFacade
{

	/**
	 * Runs order simulate. Note that order simulate is part of cart calculation and it will run if needs be (calling
	 * calculate), recalculate will run it regardless the state of the cart.
	 *
	 * If the order simulate makes any changes to the cart the full list of changes (messages) will be kept in the cart.
	 * Use methods to retrieve them - @see getSapCartChanges Only the very last list of changes will be kept.
	 *
	 * @param forceRun
	 *           if false, runs order simulate if it passed the threshold or the cart is flagged that it requires
	 *           calculation
	 * @throws CalculationException
	 *            Error calculating cart
	 */
	void runOrderSimulate(boolean forceRun) throws CalculationException;

	/**
	 * Retrieve the latest list of changes (messages) done to the by cart by Order simulate. Once these messages are
	 * retrieved they will be removed from the cart. Only use this to actually retrieve the messages, to check if there
	 * are any use @see hasSapCartChanges
	 *
	 * @return List of messages or empty collection if none available
	 */
	List<OrderMessageData> getSapCartChanges();

	/**
	 * Check if the cart has SAP changes to show to the user.
	 *
	 * @return false if there is no cart or no messages
	 */
	boolean hasSapCartChanges();

	/**
	 * Runs all necessary checks before a CC payment - Cut off check - Order simulate check - Checkout countdown check.
	 *
	 * @return false if any of the above fails
	 * @throws IllegalStateException
	 *            order simulate failed
	 * @throws CartStateException
	 *            order simulate came back with changes
	 * @throws CutoffTimeoutException
	 *            cutoff validation failed
	 */
	boolean validateCartForCredictcardPayment() throws CartStateException, IllegalStateException, CutoffTimeoutException;

	/**
	 * Validate cutoff for checkout.
	 *
	 * @throws CutoffTimeoutException
	 *            the cutoff timeout exception
	 */
	void validateCutoffForCheckout() throws CutoffTimeoutException;

	/**
	 * Checks if checkout countdown is still valid.
	 *
	 * @return true if valid
	 */
	boolean isCheckoutCountdownValid();

	/**
	 * Checks for exceeded wait timeout.
	 *
	 * @return true, if successful
	 */
	boolean hasExceededWaitTimeout();

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade#placeOrder()
     */
    OrderData placeOrderForPostback(SABMMerchantSuiteTransactionProcessData data) throws InvalidCartException;

    /**
	 * Mark cart for recalculation.
	 */
	void markCartForRecalculation();

	/**
	 * save address to cart, if the defaultAddress is true then save the address to customer's de
	 *
	 * @param addressId
	 * @param defaultAddress
	 * @return boolean
	 */
	boolean setDeliveryAddress(String addressId, boolean defaultAddress);

	/**
	 * @param cartModel
	 */
	void validateDealconditions(AbstractOrderModel cartModel);

	void saveBDEOrderingDetails(BdeOrderDetailsForm form);
}
