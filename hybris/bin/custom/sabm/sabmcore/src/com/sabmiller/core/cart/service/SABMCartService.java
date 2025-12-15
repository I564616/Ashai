/**
 *
 */
package com.sabmiller.core.cart.service;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCartService;
import de.hybris.platform.commercefacades.order.data.EntryOfferInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sabmiller.core.deals.services.response.ConflictGroup;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.MaxOrderQtyModel;
import com.sabmiller.core.model.SabmCartRuleModel;


/**
 *
 */
public interface SABMCartService extends B2BCartService
{

	/**
	 * SAB-535 Method for update DeliveryInstructions of cart
	 *
	 * @param deliveryInstructions
	 * @param abstractOrder
	 */
	void saveDeliveryInstructions(final String deliveryInstructions, final AbstractOrderModel abstractOrder);

	/**
	 * SAB-535 Method for update ShippingCarrier of cart
	 *
	 * @param parameter
	 * @return boolean
	 */
	boolean setShippingCarrier(final CommerceCheckoutParameter parameter);

	/**
	 * @param poNumber
	 * @param abstractOrder
	 */
	void savePurchaseOrderNumber(String poNumber, AbstractOrderModel abstractOrder);

	/**
	 * Save requested delivery date.
	 *
	 * @param date
	 *           the date
	 * @param packType
	 * @return true, if successful
	 */
	boolean saveRequestedDeliveryDate(Date date, String packType);

	public void markCartForRecalculation();

	/**
	 * Get the applied deal for cart entry
	 *
	 * @param cartModel
	 *           the cart model
	 * @param triggerReject
	 *           if true,only the rejected deal will be apply to the result; if false,the rejected deal will not be apply
	 *           to the result
	 * @return the Map of the result key: entry number. value:applied deals
	 */
	public Map<Integer, List<DealModel>> getEntryApplyDeal(final CartModel cartModel, final boolean triggerReject);

	public String returnOfferTitle(final EntryOfferInfoData offerInfo, final OrderEntryData entry);

	/**
	 * if the product is not purchasable, it will return the product title. else it will return null;
	 *
	 * @param baseProducts
	 * @return product title
	 */
	public String validateProductsBeforeAddtoCart(String baseProducts);

	public boolean deleteCartDeal(List<String> dealCode);

	public ConflictGroup getAllConflictingDealsInCart(final CartModel cart, final B2BUnitModel b2bUnit);

	public boolean isConflictingDeals(final List<DealModel> dealModels, final CartModel cart, final B2BUnitModel b2bUnit);

	public List<DealModel> findConflictDealForCurrentDeal(final DealModel deal, final CartModel cart, final B2BUnitModel b2bUnit);

	/**
	 * if user removed the product associated the rejected deal. the deal need to be removed.
	 */
	public void removeRejectedDealIfNotQualify(CartModel cart, B2BUnitModel b2bUnit);

	List<SabmCartRuleModel> getCustomCartRules();

	Map<String, Object> getOrderBasedProductMaxOrderQty(final ProductModel productModel, final MaxOrderQtyModel maxOrderQtyModel,
			final Date requestedDispatchDate);

	Map<String, Object> getFinalMaxOrderQty(ProductModel productModel, Date requestedDispatchDate);
}
