/**
 *
 */
package com.sabmiller.facades.deal;

import java.util.List;

import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.facades.complexdeals.data.ComplexDealData;
import com.sabmiller.facades.dataimport.response.DataImportResponse;
import com.sabmiller.facades.deal.data.LostdealJson;


/**
 * The Interface SABMDealsFacade.
 */
public interface SABMDealsFacade
{

	/**
	 * Import complex deal.
	 *
	 * @param complexDeal
	 *           the complex deal
	 * @return the data import response
	 */
	DataImportResponse importComplexDeal(final ComplexDealData complexDeal, final DealsService.ImportContext importContext);

	DealsService.ImportContext createImportContext();

	/**
	 * Checks if is lost deal.
	 *
	 * @param entryNumber
	 *           the entry number
	 * @param quantity
	 *           the quantity
	 * @param uom
	 *           the uom
	 * @return the lostdeal json
	 */
	LostdealJson isLostDeal(String entryNumber, int quantity, String uom);

	/**
	 * Delete cart deal.
	 *
	 * @param dealCode
	 *           the deal code
	 * @return true, if successful
	 */
	boolean deleteCartDeal(List<String> dealCode);
}
