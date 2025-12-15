/**
 *
 */
package com.sabmiller.core.deals.strategies;

import de.hybris.platform.b2b.model.B2BUnitModel;

import com.sabmiller.core.model.DealModel;


/**
 * The Interface SABMDealValidationStrategy.
 */
public interface SABMDealValidationStrategy
{

	/**
	 * Validate deal.
	 *
	 * @param deal
	 *           the deal
	 * @return true, if successful
	 */
	boolean validateDeal(DealModel deal);

	/**
	 * Validate deal.
	 *
	 * @param deal
	 *           the deal
	 * @param b2bUnit
	 *           the b2b unit
	 * @return true, if successful
	 */
	boolean validateDeal(DealModel deal, B2BUnitModel b2bUnit);

	/**
	 * Validate now available deal
	 *
	 * @param deal
	 * @return true, if validFrom <= now <= validTo
	 */
	boolean validateNowAvailableDeal(DealModel deal);
}
