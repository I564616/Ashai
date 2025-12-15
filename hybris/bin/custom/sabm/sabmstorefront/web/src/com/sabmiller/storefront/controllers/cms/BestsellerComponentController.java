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
import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sabmiller.storefront.controllers.ControllerConstants;


/**
 * Controller for CMS BestsellerComponent.
 */
@Controller("BestsellerComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.BestsellerComponent)
public class BestsellerComponentController extends ProductCarouselComponentController
{

	protected static final int PRODUCTLIST_SIZE = 20;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final ProductCarouselComponentModel component)
	{
		List<ProductData> products = new ArrayList<>();

		products.addAll(collectLinkedProducts(component));
		products.addAll(collectSearchProducts(component));
		Collections.shuffle(products);
		//Displayed in order from 1-20.
		if (products.size() > PRODUCTLIST_SIZE)
		{
			products = products.subList(0, PRODUCTLIST_SIZE);
		}

		model.addAttribute("title", component.getTitle());
		model.addAttribute("productData", products);
	}
}
