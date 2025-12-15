/**
 *
 */
package com.sabmiller.core.deals.strategies;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;

import java.math.BigDecimal;


/**
 * The Interface SABMDiscountPerUnitCalculationStrategy.
 */
public interface SABMDiscountPerUnitCalculationStrategy
{

	/**
	 * Calculate discount per unit.
	 *
	 * @param product
	 *           the product
	 * @param percentage
	 *           the percentage
	 * @return the big decimal
	 */
	BigDecimal calculateDiscountPerUnit(ProductModel product, Double percentage);

	/**
	 * Calculate simple discount per unit.
	 *
	 * @param product
	 *           the product
	 * @param percentage
	 *           the percentage
	 * @return the big decimal
	 */
	BigDecimal calculateSimpleDiscountPerUnit(ProductModel product, Double percentage);

	/**
	 * Calculate discount per unit.
	 *
	 * @param product
	 *           the product
	 * @param percentage
	 *           the percentage
	 * @return the big decimal
	 */
	BigDecimal calculateDiscountPerUnit(String product, Double percentage);

	/**
	 * Calculate simple discount per unit.
	 *
	 * @param product
	 *           the product
	 * @param percentage
	 *           the percentage
	 * @return the big decimal
	 */
	BigDecimal calculateSimpleDiscountPerUnit(String product, Double percentage);

	/**
	 * Calculate discount per unit.
	 *
	 * @param product
	 *           the product
	 * @param percentage
	 *           the percentage
	 * @param b2bUnit
	 *           the b2b unit
	 * @return the big decimal
	 */
	BigDecimal calculateDiscountPerUnit(String product, Double percentage, B2BUnitModel b2bUnit);

	/**
	 * Round amount.
	 *
	 * @param amount
	 *           the amount
	 * @return the big decimal
	 */
	BigDecimal roundAmount(BigDecimal amount);

	/**
	 * Calculate discount per unit.
	 *
	 * @param priceRow
	 *           the price row
	 * @param percentage
	 *           the percentage
	 * @return the big decimal
	 */
	public BigDecimal calculateDiscountPerUnit(final PriceRowModel priceRow, final Double percentage);
}
