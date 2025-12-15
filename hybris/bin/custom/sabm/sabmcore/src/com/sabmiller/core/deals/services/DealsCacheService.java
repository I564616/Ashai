/**
 *
 */
package com.sabmiller.core.deals.services;

import java.util.List;


/**
 * @author ramsatish.jagajyothi
 *
 */
public interface DealsCacheService
{


	/**
	 * Get the deals flag based on productCode
	 *
	 * @param productCode
	 *           the product code
	 * @return true if product has deals, false otherwise
	 */
	public boolean getDealsFlag(final String productCode);

	List<String> getDealTitlesForProduct(String productCode);


}
