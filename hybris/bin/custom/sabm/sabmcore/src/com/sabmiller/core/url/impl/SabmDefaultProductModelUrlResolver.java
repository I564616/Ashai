package com.sabmiller.core.url.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.url.impl.DefaultProductModelUrlResolver;
import de.hybris.platform.core.model.product.ProductModel;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;


/**
 * The Class SabmDefaultProductModelUrlResolver.
 */
public class SabmDefaultProductModelUrlResolver extends DefaultProductModelUrlResolver
{
	
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commerceservices.url.impl.DefaultProductModelUrlResolver#resolveInternal(de.hybris.platform.
	 * core.model.product.ProductModel)
	 */
	@Override
	protected String resolveInternal(final ProductModel source)
	{
		final ProductModel baseProduct = getProductAndCategoryHelper().getBaseProduct(source);

		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();

		String url = getPattern();

		if (currentBaseSite != null && url.contains("{baseSite-uid}"))
		{
			url = url.replace("{baseSite-uid}", currentBaseSite.getUid());
		}
		if (url.contains("{category-path}"))
		{
			url = url.replace("{category-path}", buildPathString(getCategoryPath(baseProduct)));
		}
		if (url.contains("{product-name}"))
		{
			final String safeUrl = urlSafe(baseProduct.getName());
			if (StringUtils.isNotBlank(safeUrl))
			{
				url = url.replace("{product-name}", safeUrl);
			}
			else
			{
				url = url.replace("{product-name}/", "");
			}
		}
		if (url.contains("{product-code}"))
		{
			if (source instanceof SABMAlcoholVariantProductMaterialModel)
			{
				url = url.replace("{product-code}", ((SABMAlcoholVariantProductMaterialModel) source).getBaseProduct().getCode());
			}
			else
			{
				url = url.replace("{product-code}", source.getCode());
			}
		}

		return url;
	}
}
