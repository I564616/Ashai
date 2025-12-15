/**
 *
 */
package com.sabmiller.core.handlers;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.product.SabmProductService;


public class ProductPurchasableFlagHandler implements DynamicAttributeHandler<Boolean, ProductModel>
{
	private SabmProductService productService;

	/**
	 * Return if product is purchasable on site
	 *
	 * @param product
	 *           the product
	 * @return true if product is purchasable on site
	 */
	@Override
	public Boolean get(final ProductModel product)
	{
		return product instanceof SABMAlcoholVariantProductEANModel
				&& productService.isProductPurchasable((SABMAlcoholVariantProductEANModel) product);
	}

	/**
	 * setter of dynamic attribute unitList, throws exception because this is a dynamic attribute, only to fetch data.
	 *
	 */
	@Override
	public void set(final ProductModel arg0, final Boolean arg1)
	{
		throw new UnsupportedOperationException("Set of dynamic attribute 'purchasable' of Product is disabled!");
	}

	public void setProductService(final SabmProductService productService)
	{
		this.productService = productService;
	}
}
