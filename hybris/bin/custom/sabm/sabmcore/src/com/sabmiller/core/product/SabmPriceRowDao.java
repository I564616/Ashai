/**
 *
 */
package com.sabmiller.core.product;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.europe1.model.PriceRowModel;

import java.util.Date;
import java.util.List;


/**
 * @author joshua.a.antony
 *
 */
public interface SabmPriceRowDao
{

	public PriceRowModel getPriceRow(final UserPriceGroup userPriceGroup, ProductModel productModel, Date date);

	/**
	 * @param currentUser
	 * @param currentCurrency
	 * @param product
	 * @return
	 */
	public PriceRowModel getPriceRowByProduct(UserModel currentUser, CurrencyModel currentCurrency, ProductModel product);


	PriceRowModel getPriceRow(final UserPriceGroup userPriceGroup, final String code, Date date);

	/**
	 * Find old price row.
	 *
	 * @param startBefore
	 *           the started before
	 * @param batchSize
	 *           the batch size
	 * @return list of @PriceRowModel
	 */
	public List<PriceRowModel> findOldPriceRow(final Date startBefore, final int batchSize);
}
