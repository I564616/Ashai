/**
 *
 */
package com.sabmiller.core.b2b.dao;

import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.Date;
import java.util.List;

import com.sabmiller.core.model.ProductExclusionModel;


/**
 * The Interface SABMProductExclusionDao.
 */
public interface SABMProductExclusionDao extends GenericDao<ProductExclusionModel>
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
	List<ProductExclusionModel> find(PrincipalModel customer, String product, Date date);

	/**
	 * Find the Product exclusion list using the customer and date. The date is used as {validFrom}<=?date AND
	 * {validTo}>=?date
	 *
	 * @param customer
	 *           the PrincipalModel related to the product exclusion
	 * @param date
	 *           the date between the validity period
	 * @return the list of Product Exclusion
	 */
	List<ProductExclusionModel> find(PrincipalModel customer, Date date);

	/**
	 * @param customer
	 * @return
	 */
	List<ProductExclusionModel> findCustomerProductExcl(PrincipalModel customer);
}
