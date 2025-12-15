package com.apb.controllers.pages;

import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.apb.controllers.B2bacceleratoraddonControllerConstants;
import com.apb.storefront.controllers.pages.AbstractLoginPageController;


/**
 * Checkout Login Controller. Handles login and register for the checkout flow.
 */
@RequestMapping(value = "/login/checkout")
public class CheckoutLoginController extends AbstractLoginPageController
{
	@Resource(name = "checkoutFlowFacade")
	private CheckoutFlowFacade checkoutFlowFacade;

	@Resource(name = "guidCookieStrategy")
	private GUIDCookieStrategy guidCookieStrategy;

	@Resource(name = "authenticationManager")
	private AuthenticationManager authenticationManager;

	private static final Logger LOG = LoggerFactory.getLogger(CheckoutLoginController.class);

	@Override
	protected AbstractPageModel getCmsPage() throws CMSItemNotFoundException
	{
		return getContentPageForLabelOrId("checkout-login");
	}

	/**
	 * @param loginError
	 * @param session
	 * @param model
	 * @param request
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping
	public String doCheckoutLogin(@RequestParam(value = "error", defaultValue = "false") final boolean loginError,
			final HttpSession session, final Model model, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		// Express Checkout is not available yet in B2B.
		LOG.info("Inside our own custom apbb2baccaddon...");
		model.addAttribute("expressCheckoutAllowed", Boolean.FALSE);
		return getDefaultLoginPage(loginError, session, model, request);
	}

	@Override
	protected String getView()
	{
		return B2bacceleratoraddonControllerConstants.Views.Pages.Checkout.CheckoutLoginPage;
	}

	@Override
	protected String getSuccessRedirect(final HttpServletRequest request, final HttpServletResponse response)
	{
		if (hasItemsInCart())
		{
			return getCheckoutUrl();
		}
		// Redirect to the main checkout controller to handle checkout.
		return "/checkout";
	}

	/**
	 * Checks if there are any items in the cart.
	 *
	 * @return returns true if items found in cart.
	 */
	protected boolean hasItemsInCart()
	{
		final CartData cartData = getCheckoutFlowFacade().getCheckoutCart();

		return (cartData.getEntries() != null && !cartData.getEntries().isEmpty());
	}

	protected String getCheckoutUrl()
	{
		// Default to the multi-step checkout
		return "/checkout/multi";
	}

	protected CheckoutFlowFacade getCheckoutFlowFacade()
	{
		return checkoutFlowFacade;
	}

	@Override
	protected GUIDCookieStrategy getGuidCookieStrategy()
	{
		return guidCookieStrategy;
	}

	protected AuthenticationManager getAuthenticationManager()
	{
		return authenticationManager;
	}
}
