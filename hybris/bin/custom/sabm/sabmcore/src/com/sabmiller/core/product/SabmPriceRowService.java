/**
 *
 */
package com.sabmiller.core.product;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.europe1.model.PriceRowModel;

import java.util.Date;
import java.util.List;


/**
 * SAB-560, This is PriceRow service class
 *
 * @author xue.zeng
 *
 */
public interface SabmPriceRowService
{
	/**
	 * According to product code to get pricerow
	 *
	 * @param code
	 *           product code attribute
	 * @return PriceRow
	 */
	PriceRowModel getPriceRowByProduct(String code);

	/**
	 * Fetch the price row based on product code, it won't retrieve the ProductModel from database
	 * @param code
	 * @return
	 */
	PriceRowModel getPriceRowByProductCode(String code);

	/**
	 * Gets the pricerow by product and b2bUnit
	 * @param producCode
	 * @param b2BUnit
	 * @return
	 */
	PriceRowModel getPriceRowByProductCodeAndUnit(final String producCode, final B2BUnitModel b2BUnit);

	/**
	 * According to product code and b2bunit to get pricerow
	 *
	 * @param code
	 *           product code attribute
	 * @param b2bUnitId
	 *           B2BUnit ID
	 * @return PriceRow
	 */
	PriceRowModel getPriceRowByProduct(String code, String b2bUnitId);

	/**
	 * According to product model bject to get pricerow
	 *
	 * @param product
	 *           product model object
	 * @return PriceRow
	 */
	PriceRowModel getPriceRowByProduct(ProductModel product);

	/**
	 * According to product model and b2bunit to get pricerow
	 *
	 * @param product
	 *           product model object
	 * @param b2bUnit
	 *           B2BUnit model object
	 * @return PriceRow
	 */
	PriceRowModel getPriceRowByProduct(ProductModel product, B2BUnitModel b2bUnit);



	PriceRowModel getPriceRow(String b2bUnitId, String product);

	PriceRowModel getPriceRow(String b2bUnitId, ProductModel productModel);

	PriceRowModel getPriceRow(UserPriceGroup userPriceGroup, ProductModel productModel);


	/**
	 * @param b2bUnitId
	 * @param productModel
	 * @param date
	 * @return
	 */
	PriceRowModel getPriceRowByDate(String b2bUnitId, ProductModel productModel, Date date);

	/**
	 * Find old price row.
	 *
	 * @param startBefore
	 *           the started before
	 * @param batchSize
	 *           the batch size
	 * @return list of @PriceRowModel
	 */
	List<PriceRowModel> findOldPriceRow(final Date startBefore, final int batchSize);

}
