/**
 *
 */
package com.sabmiller.core.deals.services;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.CartModel;

import java.util.List;
import java.util.Map;

import com.sabmiller.core.deals.services.DefaultDealConditionService.DealQualificationStatus;
import com.sabmiller.core.deals.services.response.DealQualificationResponse;
import com.sabmiller.core.deals.services.response.PartialDealQualificationResponse;
import com.sabmiller.core.model.DealModel;


/**
 * The Interface DealConditionService.
 */
public interface DealConditionService
{

	/**
	 * Find qualified deals.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param cartModel
	 *           the cart model
	 * @return the deal qualification response
	 */
	public DealQualificationResponse findQualifiedDeals(B2BUnitModel b2bUnitModel, CartModel cartModel);

	/**
	 * Find conflicting deals.
	 *
	 * @param deals
	 *           the deals
	 * @return the map
	 */
	public Map<DealModel, List<DealModel>> findConflictingDeals(final List<DealModel> deals, CartModel cart);

	/**
	 * Check deal qualification.
	 *
	 * @param deal
	 *           the deal
	 * @param cart
	 *           the cart
	 * @return the deal qualification status
	 */
	public DealQualificationStatus checkDealQualification(final DealModel deal, final CartModel cart);

	/**
	 * Find fully qualified deals.
	 *
	 * @param deals
	 *           the deals
	 * @param cart
	 *           the cart
	 * @return the list
	 */
	public List<DealModel> findFullyQualifiedDeals(List<DealModel> deals, CartModel cart);

	/**
	 * Find partially qualified deals.
	 *
	 * @param deals
	 *           the deals
	 * @param cart
	 *           the cart
	 * @return the partial deal qualification response
	 */
	public PartialDealQualificationResponse findPartiallyQualifiedDeals(List<DealModel> deals, CartModel cart, boolean addAll);

}
