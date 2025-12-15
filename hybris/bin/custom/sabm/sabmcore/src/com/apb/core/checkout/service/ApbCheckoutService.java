package com.apb.core.checkout.service;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.EmployeeModel;

import java.util.List;
import java.util.Set;

import com.apb.facades.delivery.data.DeliveryInfoData;
import com.apb.integration.data.AsahiProductInfo;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.facades.bdeordering.BdeOrderDetailsForm;


public interface ApbCheckoutService
{
	DeliveryInfoData getDeliveryInfo(String addressRecordId);

	/**
	 * Gets the b 2 B unit for uid.
	 *
	 * @param b2bUnit
	 *           the b 2 b unit
	 * @return the b 2 B unit for uid
	 */
	B2BUnitModel getB2BUnitForUid(String b2bUnit);

	List<B2BPaymentTypeData> getPaymentTypesForCustomer(AsahiB2BUnitModel asahiB2BUnitModel,
			List<B2BPaymentTypeData> paymentTypes);

	/**
	 * @param cartModel
	 *           This method will set the device type of user in cartModel.
	 */
	void setDeviceType(CartModel cartModel);

	/**
	 * @param cartModel
	 *           <p>
	 *           This method will set the SGA specific custom fields in cart model
	 *           </p>
	 */
	void setCustomFields(CartModel cartModel);

	/**
	 * This method will get products present in cart or order based on code passed as parameter.
	 *
	 * @param updateCart the update cart
	 * @param formQty the form qty
	 * @param cartCode the cart code
	 * @return List of product id
	 */
	List<AsahiProductInfo> getProductDetailsFromCart(boolean updateCart, long formQty,String cartCode);

	/**
	 * This method evaluates for the fringe case delivery scenario while placing the order.
	 * @param recordId
	 * @param deliveryDate
	 * @return
	 */
	boolean isDeliveryDateInValid(String recordId, String deliveryDate);

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
