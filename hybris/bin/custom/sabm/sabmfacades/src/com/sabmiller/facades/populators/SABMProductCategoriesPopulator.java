/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.converters.populator.ProductCategoriesPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Collection;

import jakarta.annotation.Resource;

import com.apb.core.util.AsahiSiteUtil;


/**
 * This is the populate for the product categories
 *
 * @author xiaowu.a.zhang
 */
public class SABMProductCategoriesPopulator<SOURCE extends ProductModel, TARGET extends ProductData>
		extends ProductCategoriesPopulator<SOURCE, TARGET>
{
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException
	{
		
		if(asahiSiteUtil.isCub())
		{
   		// change for populate the categories from the base product
   		final Collection<CategoryModel> categories = getCommerceProductService()
   				.getSuperCategoriesExceptClassificationClassesForProduct(getBaseProduct(productModel));
   		productData.setCategories(Converters.convertAll(categories, getCategoryConverter()));
		}
		else
		{
			super.populate(productModel, productData);
		}
	}


	/**
	 * @param product
	 * @return Base ProductModel
	 */
	protected ProductModel getBaseProduct(final ProductModel product)
	{
		if (product instanceof VariantProductModel)
		{
			return getBaseProduct(((VariantProductModel) product).getBaseProduct());
		}
		return product;
	}
}
