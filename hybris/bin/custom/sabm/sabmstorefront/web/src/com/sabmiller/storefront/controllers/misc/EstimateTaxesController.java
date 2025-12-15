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
package com.sabmiller.storefront.controllers.misc;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;

import jakarta.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sabmiller.storefront.filters.XSSFilterUtil;


/**
 * Controller to handle the tax estimation on a cart.
 */
@Controller
@Scope("tenant")
public class EstimateTaxesController extends AbstractController
{
	protected static final Logger LOG = Logger.getLogger(EstimateTaxesController.class);
	@Resource(name = "cartFacade")
	private CartFacade cartFacade;


	@GetMapping(value = "/cart/estimate", produces = "application/json")
	public @ResponseBody
	CartData addToCart(@RequestParam("zipCode") final String zipCode, @RequestParam("isocode") final String countryIsoCode,
			final Model model)
	{
		final CartData estimatedCart = cartFacade.estimateExternalTaxes(zipCode, XSSFilterUtil.filter(countryIsoCode));
		return estimatedCart;
	}
}
