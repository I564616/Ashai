/**
 *
 */
package com.sabmiller.core.deals.strategies;

import de.hybris.platform.core.model.product.ProductModel;


/**
 * The Interface SABMProductUOMConversionStrategy.
 */
public interface SABMProductUOMConversionStrategy
{

	/**
	 * Convert quantity.
	 *
	 * @param product
	 *           the product
	 * @param quantity
	 *           the quantity
	 * @param uom
	 *           the uom
	 * @return the int
	 */
	int convertQuantity(ProductModel product, int quantity, String uom);
}
