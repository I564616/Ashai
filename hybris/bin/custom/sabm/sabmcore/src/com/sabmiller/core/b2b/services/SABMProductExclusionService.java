/**
 *
 */
package com.sabmiller.core.b2b.services;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.security.PrincipalModel;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.sabmiller.core.model.ProductExclusionModel;


/**
 * A service for ProdcutExclusion management.
 */
public interface SABMProductExclusionService
{

	/**
	 * Find the Product exclusion list using the customer, product and date. The date is used as {validFrom}<=?date AND
	 * {validTo}>=?date
	 *
	 * @param customer
	 *           the PrincipalModel related to the product exclusion
	 * @param product
	 *           the code of the product excluded for the related customer
	 * @param date
	 *           the date between the validity period
	 * @return the list of Product Exclusion
	 */
	List<ProductExclusionModel> findProductExByCustomerProductDate(PrincipalModel customer, String product, Date date);

	/**
	 * Find the Product exclusion list using the session customer and parameter date. The date is used as
	 * {validFrom}<=?date AND {validTo}>=?date
	 *
	 * @param date
	 *           the date between the validity period
	 * @return the list of Product Exclusion
	 */
	List<ProductExclusionModel> findProductExByDate(Date date);


	/**
	 * Find the Product exclusion list using the session customer and now as date. The date is used as {validFrom}<=?date
	 * AND {validTo}>=?date
	 *
	 * @return the list of Product Exclusion
	 */
	List<ProductExclusionModel> findProductEx();


	/**
	 * @param b2bUnitModel
	 * @return
	 */
	List<ProductExclusionModel> getCustomerProductExclusions(B2BUnitModel b2bUnitModel);

	Set<String> getSessionProductExclusionEanCodes();

	Set<String> getAndSetSessionEanProductExclusion();

	<T,R> R executeWithoutProductExclusionSearchRestriction(final Function<T,R> function);

}
