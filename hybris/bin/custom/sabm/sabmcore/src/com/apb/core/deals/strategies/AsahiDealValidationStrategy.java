/**
 *
 */
package com.apb.core.deals.strategies;

import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiDealModel;


/**
 * The Interface AsahiDealValidationStrategy.
 */
public interface AsahiDealValidationStrategy
{

	/**
	 * Validate deal.
	 *
	 * @param deal
	 *           the deal
	 * @return true, if successful
	 */
	boolean validateDeal(AsahiDealModel deal);

	/**
	 * Validate deal.
	 *
	 * @param deal
	 *           the deal
	 * @param b2bUnit
	 *           the b2b unit
	 * @return true, if successful
	 */
	boolean validateDeal(AsahiDealModel deal, AsahiB2BUnitModel b2bUnit);

	/**
	 * @param deal
	 * @return
	 */
	boolean validateDealForCustomer(AsahiDealModel deal);
}
