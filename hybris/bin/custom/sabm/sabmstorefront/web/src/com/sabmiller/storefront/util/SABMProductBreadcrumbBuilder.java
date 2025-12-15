/**
 *
 */
package com.sabmiller.storefront.util;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.ProductBreadcrumbBuilder;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class SABMProductBreadcrumbBuilder extends ProductBreadcrumbBuilder
{
	private static final String LAST_LINK_CLASS = "active";

	@Override
	public List<Breadcrumb> getBreadcrumbs(final String productCode) throws IllegalArgumentException
	{
		final ProductModel productModel = getProductService().getProductForCode(productCode);
		final List<Breadcrumb> breadcrumbs = new ArrayList<>();

		final Collection<CategoryModel> categoryModels = new ArrayList<>();
		final Breadcrumb last;

		final ProductModel baseProductModel = getBaseProduct(productModel);
		last = getProductBreadcrumb(productModel);
		categoryModels.addAll(baseProductModel.getSupercategories());
		last.setLinkClass(LAST_LINK_CLASS);

		breadcrumbs.add(last);

		while (!categoryModels.isEmpty())
		{
			CategoryModel toDisplay = null;
			for (final CategoryModel categoryModel : categoryModels)
			{
				if (!(categoryModel instanceof ClassificationClassModel))
				{
					if (toDisplay == null)
					{
						toDisplay = categoryModel;
					}
					if (getBrowseHistory().findEntryMatchUrlEndsWith(categoryModel.getCode()) != null)
					{
						break;
					}
				}
			}
			categoryModels.clear();
			if (toDisplay != null)
			{
				breadcrumbs.add(getCategoryBreadcrumb(toDisplay));
				categoryModels.addAll(toDisplay.getSupercategories());
			}
		}
		Collections.reverse(breadcrumbs);
		return breadcrumbs;
	}

	@Override
	protected Breadcrumb getProductBreadcrumb(final ProductModel product)
	{
		final String productUrl = getProductModelUrlResolver().resolve(product);

		String name = null;

		if (StringUtils.isNotEmpty(product.getSellingName()) && StringUtils.isNotEmpty(product.getPackConfiguration()))
		{
			name = product.getSellingName() + " " + product.getPackConfiguration();
		}
		else
		{
			name = product.getName();
		}

		return new Breadcrumb(productUrl, name, null);
	}
}
