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
package com.apb.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.forms.RegisterForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.apb.storefront.controllers.ControllerConstants;
import com.apb.storefront.data.LoginValidateInclusionData;


/**
 * Login Controller. Handles login and register for the account flow.
 */
@Controller
@RequestMapping(value = "/login")
public class LoginPageController extends AbstractLoginPageController
{
	private HttpSessionRequestCache httpSessionRequestCache;

	@Resource(name = "sabmCustomerFacade")
	private SABMCustomerFacade sabmCustomerFacade;

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private UserService userService;

	@Override
	protected String getView()
	{
		return ControllerConstants.Views.Pages.Account.AccountLoginPage;
	}

	@Override
	protected String getSuccessRedirect(final HttpServletRequest request, final HttpServletResponse response)
	{
		if (httpSessionRequestCache.getRequest(request, response) != null)
		{
			return httpSessionRequestCache.getRequest(request, response).getRedirectUrl();
		}
		return "/";
	}

	@Override
	protected AbstractPageModel getCmsPage() throws CMSItemNotFoundException
	{
		return getContentPageForLabelOrId("login");
	}


	@Resource(name = "httpSessionRequestCache")
	public void setHttpSessionRequestCache(final HttpSessionRequestCache accHttpSessionRequestCache)
	{
		this.httpSessionRequestCache = accHttpSessionRequestCache;
	}

	@GetMapping
	public String doLogin(@RequestHeader(value = "referer", required = false) final String referer,
			@RequestParam(value = "error", defaultValue = "false") final boolean loginError, final Model model,
			final HttpServletRequest request, final HttpServletResponse response, final HttpSession session)
			throws CMSItemNotFoundException
	{
		final UserModel user = userService.getCurrentUser();
		if (!loginError)
		{
			storeReferer(referer, request, response);
		}

		//redirect to home page if the user is already signed in.

		if (!userService.isAnonymousUser(user))
		{
			return REDIRECT_PREFIX + ROOT;
		}
		return getDefaultLoginPage(loginError, session, model, request);
	}

	protected void storeReferer(final String referer, final HttpServletRequest request, final HttpServletResponse response)
	{
		if (StringUtils.isNotBlank(referer) && !StringUtils.endsWith(referer, "/login")
				&& StringUtils.contains(referer, request.getServerName()))
		{
			httpSessionRequestCache.saveRequest(request, response);
		}
	}

	@PostMapping("/register")
	public String doRegister(@RequestHeader(value = "referer", required = false) final String referer, final RegisterForm form,
			final BindingResult bindingResult, final Model model, final HttpServletRequest request,
			final HttpServletResponse response, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		getRegistrationValidator().validate(form, bindingResult);
		return processRegisterUserRequest(referer, form, bindingResult, model, request, response, redirectModel);
	}

	@GetMapping(value = "/validateCustomerCreditAndInclusionList", produces = "application/json")
	@RequireHardLogIn
	public String validateCustomerCreditAndInclusionList(final Model model,final HttpServletRequest request)
	{
		if (asahiSiteUtil.isSga())
		{
			final LoginValidateInclusionData response = sabmCustomerFacade.setCustomerCreditAndInclusionInSession();
			/*
			 * Updating cart totals after setting the inclusion list along with prices in the session
			 */
			cartFacade.getSessionCart();
			model.addAttribute("response", response);
			request.getSession().setAttribute("wasCheckoutInterfce", false);
			request.getSession().setAttribute("makeLoginCall", false);
		}
		return "fragments/account/loginInclusionResponse";
	}
}
