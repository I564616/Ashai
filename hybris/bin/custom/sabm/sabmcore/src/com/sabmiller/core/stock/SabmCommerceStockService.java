/**
 * 
 */
package com.sabmiller.core.stock;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * @author Varun.Goyal1
 *
 */
public interface SabmCommerceStockService
{
	
	StockLevelStatus getStockLevelForSGA(final ProductModel product);

}
