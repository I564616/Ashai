package com.apb.storefront.breadcrumb.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.ProductBreadcrumbBuilder;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

public class APBProductBreadcrumbBuilder extends ProductBreadcrumbBuilder{
	
	private static final String LAST_LINK_CLASS = "active";
	
	/**
	 * Returns a list of breadcrumbs for the given product.
	 *
	 * @param productCode
	 * @return breadcrumbs for the given product
	 */
	@Override
	public List<Breadcrumb> getBreadcrumbs(final String productCode)
	{
		final ProductModel productModel = getProductService().getProductForCode(productCode);
		final List<Breadcrumb> breadcrumbs = new ArrayList<>();

		final Collection<CategoryModel> categoryModels = new ArrayList<>();
		final Breadcrumb last;

		final ProductModel baseProductModel = getBaseProduct(productModel);
		last = getProductBreadcrumb(baseProductModel);
		categoryModels.addAll(baseProductModel.getSupercategories());
		last.setLinkClass(LAST_LINK_CLASS);

		breadcrumbs.add(last);

		while (!categoryModels.isEmpty())
		{
			CategoryModel toDisplay = null;
			toDisplay = processCategoryModels(categoryModels, toDisplay);
			categoryModels.clear();

			if (toDisplay != null  )
			{	
				if(!toDisplay.isHideInBreadcrumb())
				{
					breadcrumbs.add(getCategoryBreadcrumb(toDisplay));
				}
				categoryModels.addAll(toDisplay.getSupercategories());
			}
		}
		Collections.reverse(breadcrumbs);
		return breadcrumbs;
	}

}
