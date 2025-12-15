/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.storefront.controllers.cms;

import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sabm.core.model.cms.components.CUBPicksComponentModel;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.storefront.controllers.ControllerConstants;



/**
 * Controller for CMS CUBPricksComponent.
 */
@Controller("CUBPicksComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.CUBPicksComponentController)
public class CUBPicksComponentController extends AbstractCMSComponentController<CUBPicksComponentModel>
{
	protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.CATEGORIES);

	@Resource(name = "accProductFacade")
	private ProductFacade productFacade;

	/** The session service. */
	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final CUBPicksComponentModel component)
	{
		final Map<String, ProductData> products = collectLinkedProducts(component, model);
		model.addAttribute("productData", products);
	}

	protected Map<String, ProductData> collectLinkedProducts(final CUBPicksComponentModel component,final Model model)
	{
		final Map<String, ProductData> products = new LinkedHashMap<String, ProductData>();

		final List<ProductModel> productCodes = new ArrayList<>();
		if (null != sessionService.getAttribute("smartOrderPage") && null != sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_SMARTORDERPRODUCTCODES))
		{
			productCodes.addAll(sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_SMARTORDERPRODUCTCODES));
			model.addAttribute("smartOrderPage", true);
		}

		for (final ProductCarouselComponentModel productCarouselComponentModel : component.getProducts())
		{
			if (productCarouselComponentModel == null)
			{
				break;
			}

			final List<ProductModel> productsList = new ArrayList<>();
			productsList.addAll(productCarouselComponentModel.getProducts());

			//Removing the products which are displayed in Smart Order Table	
			productsList.removeAll(productCodes);

			//Randomly collect one product for each tile
			if (!CollectionUtils.isEmpty(productsList))
			{
				final int random = new Random().nextInt(productsList.size());
				products.put(productCarouselComponentModel.getTitle(),
						productFacade.getProductForOptions(productsList.get(random), PRODUCT_OPTIONS));
			}
		}
		sessionService.removeAttribute("smartOrderPage");
		return products;
	}
}
