package com.apb.facades.checkout;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CheckoutFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.order.InvalidCartException;

import java.util.List;
import java.util.Set;

import com.apb.core.exception.AsahiPaymentException;
import com.apb.facades.card.payment.AsahiPaymentDetailsData;
import com.apb.facades.delivery.data.DeliveryInfoData;
import com.apb.storefront.data.LoginValidateInclusionData;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.facades.bdeordering.BdeOrderDetailsForm;


public interface APBCheckoutFacade extends CheckoutFacade
{

	DeliveryInfoData getDeliveryInfo(String addressRecordId);

	boolean setDeliveryAddressIfAvailable();

	/**
	 * Gets the b 2 B unit for uid.
	 *
	 * @param b2bUnit
	 *           the b 2 b unit
	 * @return the b 2 B unit for uid
	 */
	B2BUnitModel getB2BUnitForUid(String b2bUnit);

	boolean setPaymentTypeInfo(String paymentType, String poNumber);

	void createCartFromOrder(final String orderCode, boolean clearCart);

	void updateStockEntry(final CartData cartData);

	CartData getCheckoutCart();

	/**
	 * @param asahiPaymentDetailsData
	 * @throws AsahiPaymentException
	 */
	void makeCreditCardPayment(AsahiPaymentDetailsData asahiPaymentDetailsData) throws AsahiPaymentException;

	/**
	 * @return
	 * @throws InvalidCartException
	 */
	OrderData placeOrder(String cardType) throws InvalidCartException;


	CartData updateTotalwithCreditSurcharge(String cardType, String paymentMethod);

	boolean setDeliveryTypeDetails(String deliveryMode, String deferredDeliveryDate);

	List<B2BPaymentTypeData> getPaymentTypesForCustomer(final AsahiB2BUnitModel asahiB2BUnitModel);

	/**
	 * This method will update the cart products and prices as per ECC response.
	 *
	 * @param updateCart the update cart
	 * @param formQty the form qty
	 * @return the login validate inclusion data
	 */
	LoginValidateInclusionData updateCartWithInclusionList(boolean updateCart, long formQty);

	/**
	 * This method evaluates for the fringe case delivery scenario while placing the order.
	 * @param recordId
	 * @param deliveryDate
	 * @return
	 */
	boolean isDeliveryDateInValid(String recordId, String deliveryDate);
	/***
	 *
	 * @param cartData
	 * @return true if Any of the product is Excluded in Cart
	 */
	boolean isAnyProdExcl(CartData cartData);

	/**
	 * Gets the supported delivery addresses.
	 *
	 * @param visibleAddressesOnly the visible addresses only
	 * @return the supported delivery addresses
	 */
	public List<AddressData> getSupportedDeliveryAddresses(boolean visibleAddressesOnly);

	/**
	 * @return
	 */
	Set<String> getCustomerEmailIds();

	/**
	 * @param name
	 * @return
	 */
	EmployeeModel searchBDEByName(String name);

	/**
	 * @param bdeCheckoutForm
	 */
	void saveBDEOrderDetails(BdeOrderDetailsForm bdeCheckoutForm);
}
