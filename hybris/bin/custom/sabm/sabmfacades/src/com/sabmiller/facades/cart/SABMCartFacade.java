/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.facades.cart;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.CartModel;

import java.util.Date;
import java.util.List;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sabmiller.core.enums.DealConditionStatus;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.facades.deal.data.CartDealsJson;
import com.sabmiller.facades.deal.data.DealJson;


/**
 * The Interface SABMCartFacade extends the hybris OOB CartFacade and contains all the new functionality required by SAB
 * Miller.
 */
public interface SABMCartFacade extends CartFacade
{

	/**
	 * Adds the product to cart.
	 *
	 * @param code
	 *           the product code
	 * @param fromUnit
	 *           the unit of measure chosen by the Customer
	 * @param quantity
	 *           the quantity
	 * @return the cart modification data
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	CartModificationData addToCart(String code, String fromUnit, long quantity) throws CommerceCartModificationException;

	/**
	 * Method for updating the number of products.
	 *
	 * @param entryNumber
	 *           the entry number
	 * @param quantity
	 *           new value of quantity for product
	 * @param fromUnit
	 *           the from unit
	 * @return the cart modification data that includes a statusCode and the actual quantity that the entry was updated
	 *         to
	 * @throws CommerceCartModificationException
	 *            if the cart cannot be modified
	 */
	CartModificationData updateCartEntry(long entryNumber, long quantity, String fromUnit)
			throws CommerceCartModificationException;

	/**
	 * Update cart entry.
	 *
	 * @param cartModel
	 *           the cart model
	 * @param entryNumber
	 *           the entry number
	 * @param quantity
	 *           the quantity
	 * @param fromUnit
	 *           the from unit
	 * @return the cart modification data
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	CartModificationData updateCartEntry(final CartModel cartModel, final long entryNumber, final long quantity,
			final String fromUnit) throws CommerceCartModificationException;

	/**
	 * add by SAB-535
	 *
	 * Method for updating the deliveryInstructions of cart.
	 *
	 * @param deliveryInstructions
	 *           the deliveryInstructions value
	 */
	void saveDeliveryInstructions(final String deliveryInstructions);

	/**
	 * add by SAB-535
	 *
	 * Method for update the ShippingCarriers of cart.
	 *
	 * @param shippingCarrierCode
	 *           the shippingCarrierCode value
	 * @return boolean
	 */
	boolean saveShippingCarriers(final String shippingCarrierCode);

	/**
	 * set default shipping carrier fo customer.
	 *
	 * @return true, if successful
	 */
	boolean saveDefaultShippingCarriers();

	/**
	 * Save purchase order number.
	 *
	 * @param poNumber
	 *           the po number
	 */
	void savePurchaseOrderNumber(String poNumber);

	/**
	 * Save requested delivery date.
	 *
	 * @param date
	 *           the date
	 * @param packType
	 * @return true, if successful
	 */
	boolean saveRequestedDeliveryDate(Date date, String packType);

	/**
	 * Gets the session mini cart.
	 *
	 * @return the session mini cart
	 */
	CartData getSessionMiniCart();

	/**
	 * Adds the apply deal to cart.
	 *
	 * @param dealModel
	 *           the deal model
	 * @param addMethod
	 *           the add method
	 */
	void addApplyDealToCart(DealModel dealModel, DealConditionStatus addMethod);

	/**
	 * Applied of Deal is added to cart.
	 *
	 * @param dealCode
	 *           this is DealModel code
	 * @param addMethod
	 *           the add method
	 * @return the deal json
	 */
	DealJson addApplyDealToCart(String dealCode, DealConditionStatus addMethod);

	/**
	 * Cart contains dcn.
	 *
	 * @param cart
	 *           the cart
	 * @param deal
	 *           the deal
	 * @param checkConflicting
	 *           the check conflicting
	 * @return true, if successful
	 */
	boolean cartContainsDCN(CartModel cart, DealModel deal, boolean checkConflicting);

	/**
	 * Gets the cart code.
	 *
	 * @return the cart code
	 */
	String getCartCode();

	/**
	 * Gets the rejected deal from cart.
	 *
	 * @return the rejected deal titles
	 */
	List<String> getRejectedDealFromCart();

	/**
	 * Checks if is current user cash only customer.
	 *
	 * @return true, if is current user cash only customer
	 */
	boolean isCurrentUserCashOnlyCustomer();

	/**
	 * Gets conflict deals.
	 *
	 * @param cartDealsJson
	 *           the cart deals json
	 */
	void findConflictingDeals(CartDealsJson cartDealsJson);

	/**
	 * Gets the default delivery address for the customer settings.
	 *
	 * @param unitId
	 *           {@link B2BUnitModel} uid
	 * @return AddressData if not found return null
	 */
	AddressData getDeliveryDefaultAddress(String unitId);

	/**
	 * if the product is not purchasable, it will return the product title. else it will return null;
	 *
	 * @param baseProducts
	 *           the base products
	 * @return product title
	 */
	public String validateProductsBeforeAddtoCart(String baseProducts);

	/**
	 * if user removed the product associated the rejected deal. the deal need to be removed.
	 */
	public void removeRejectedDealIfNotQualify();

	/**
	 * check how many times the deal have been applied and modify the deal json.
	 *
	 * @param dealJson
	 *           the deal need to be check
	 */
	void checkFreeGoodsDealAppliedTimes(DealJson dealJson);

	/**
	 * Check if there is a BaseProduct in the cart.
	 *
	 * @return true if hava BaseProduct
	 */
	boolean isExistBaseProduct();


	/**
	 * Requires calculation.
	 *
	 * @return true, if successful
	 */
	boolean requiresCalculation();

	/**
	 * Gets the default delivery address for the customer settings.
	 *
	 * @param unitId
	 *           {@link B2BUnitModel} uid
	 * @return AddressData if not found return null
	 */
	AddressData getDefaultShipTo(String unitId);

	/**
	 * Clear the cart entries of the session cart
	 */
	void clearCartEntries();

	/**
	 * @param cartData
	 * @return
	 */
	List<String> validateCustomCartRules(CartData cartData);

	/**
	 * @param smartRecommendationModel
	 * @param entryNumber
	 * */
	void setSmartRecommendationModelToEntry (final String smartRecommendationModel, final int entryNumber);

	/**
	 * Removes the all products.
	 *
	 * @param redirectModel
	 *           the redirect model
	 * @return to cart page
	 * @Remove all products
	 */
	void removeAllProducts(final RedirectAttributes redirectModel);

	/**
	 * Update stock entry.
	 *
	 * @param cartData
	 *           the cart data
	 * @param productList
	 *           the product list
	 */
	void updateStockEntry(final CartData cartData, List<String> productList);

	/**
	 * Gets the session cart page.
	 *
	 * @return the session cart page
	 */
	CartData getSessionCartPage();

	/**
	 * Gets the session cart page.
	 *
	 * @param requireCartCalculation
	 *           the require cart calculation
	 * @return the session cart page
	 */
	CartData getSessionCartPage(boolean requireCartCalculation);

	/**
	 * Gets the session cart with credit surcharge.
	 *
	 * @param cardType
	 *           the card type
	 * @param paymentMethod
	 *           the payment method
	 * @return the session cart with credit surcharge
	 */
	CartData getSessionCartWithCreditSurcharge(CreditCardType cardType, String paymentMethod);


	/**
	 * Checks if is max qty reached.
	 *
	 * @param productCode
	 *           the product code
	 * @param quantityToBeAdded
	 *           the quantity to be added
	 * @param isUpdate
	 *           the is update
	 * @return true, if is max qty reached
	 */
	boolean isMaxQtyReached(String productCode, int quantityToBeAdded, boolean isUpdate);

	/**
	 * Checks if is adds the surcharge.
	 *
	 * @return true, if is adds the surcharge
	 */
	boolean isAddSurcharge();

	/**
	 * <p>
	 * This method will fetch the total quantity of packed products in cart.
	 * </p>
	 *
	 * @param cartData
	 *           the cart data
	 * @return the total qty for pack product
	 */
	Integer getTotalQtyForPackProduct(CartData cartData);

	/**
	 * <p>
	 * This method will fetch the total quantity of BIB products in cart.
	 * </p>
	 *
	 * @param cartData
	 *           the cart data
	 * @return the total qty for bib product
	 */
	Integer getTotalQtyForBibProduct(CartData cartData);

	/**
	 * *.
	 *
	 * @param entries
	 *           the entries
	 * @param prodShowMinicartCount
	 *           the prod show minicart count
	 * @return the no of Unavailable products in the Minicart top 3 list
	 */
	public int getRemainUnavProd(List<OrderEntryData> entries, int prodShowMinicartCount);

	/**
	 * <p>
	 * This method will validate the cart for min order qty for BIB and Packed product.
	 * </p>
	 *
	 * @param cartData
	 *           the cart data
	 * @return true, if successful
	 */
	boolean validateMinOrderQuantity(CartData cartData);

	/**
	 * This method is used to update price for cart.
	 */
	public void updateCartForPrice();

	/**
	 * This Method will add/Update the Bonus Stock in Cart
	 *
	 * @param code
	 *           , the Product Code
	 * @param quantity
	 *           , quantity to be added
	 * @param action
	 *           , whether for add to cart / Bonus
	 * @return cart Modification Data
	 */
	public CartModificationData addToCart(String code, long quantity, final String action)
			throws CommerceCartModificationException;

	/**
	 * This Method will check if the cart contain only the bonus stock products
	 *
	 * @return boolean true for only bonus stock
	 */
	public boolean isBonusStockProductsInCart();

	/**
	 * Updating Cartentries to show product and its Bonus continuously
	 *
	 * @return updated CartEntries
	 */
	public void updateProductEntries(final CartData cartdata);

	/**
	 * This Method will remove bonus product from the cart.
	 *
	 */
	public void removeBonusProductFromCart();

	/**
	 * This Method checks if the cart is empty.
	 *
	 */
	public Boolean isCartEmpty();

	/**
	 * This Method will check if the cart contain any bonus product
	 *
	 * @return boolean true for any bonus product
	 */
	public boolean hasAnyBonusProduct();

	/**
	 *
	 */
	void validateShippingCarrier();

	void removeOrUpdateFreeDealProductOnQtyUpdate(final long updatedQuantity, final long entryNumber)
			throws CommerceCartModificationException;
}
