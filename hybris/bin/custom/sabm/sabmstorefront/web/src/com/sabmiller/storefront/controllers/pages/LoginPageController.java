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
package com.sabmiller.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractLoginPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.GuestForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.LoginForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.RegisterForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commerceservices.security.SecureToken;
import de.hybris.platform.commerceservices.security.SecureTokenService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sabmiller.commons.enumerations.LoginStatus;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.storefront.controllers.ControllerConstants;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.storefront.security.CustomerRoleChecker;


/**
 * Login Controller. Handles login and register for the account flow.
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/login")
public class LoginPageController extends AbstractLoginPageController
{
	private HttpSessionRequestCache httpSessionRequestCache;

	private static final String ASSISTANT_DEFAULT_PAGE = "/your-business";

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;
	@Resource(name = "secureTokenService")
	private SecureTokenService secureTokenService;
	
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
			final HttpServletRequest request, final HttpServletResponse response, final HttpSession session,
			@ModelAttribute("loginForm") final LoginForm loginForm) throws CMSItemNotFoundException
	{
		if (!getUserFacade().isAnonymousUser())
		{
			final List<String> belongingGroupIds = new ArrayList<String>();
			final Collection<? extends GrantedAuthority> authorities = ((org.springframework.security.core.context.SecurityContext) session
					.getAttribute("SPRING_SECURITY_CONTEXT")).getAuthentication().getAuthorities();
			for (final GrantedAuthority authority : authorities)
			{
				belongingGroupIds.add(authority.getAuthority().split("_")[1].toLowerCase());
			}

			final boolean hasOnlyRole = CustomerRoleChecker.hasOnlyRole(belongingGroupIds, CustomerRoleChecker.ROLE_ASSISTANT);
			if (hasOnlyRole)
			{
				return REDIRECT_PREFIX + SABMWebConstants.ASSISTANT_DEFAULT_PAGE_URL;
			}
			else
			{
				if (request.getParameter("targetUrl") != null){
					if(StringUtils.contains(request.getQueryString(),"&")){
						String queryString = request.getQueryString().substring(request.getQueryString().indexOf("&")+1);
						return REDIRECT_PREFIX + request.getParameter("targetUrl")+"?"+queryString;
					}
					return REDIRECT_PREFIX + request.getParameter("targetUrl");
				}
				return REDIRECT_PREFIX + ROOT;
			}
		}

		if (!loginError)
		{
			storeReferer(referer, request, response);
		}

		if (request.getParameter("targetUrl") != null)
		{
			if(StringUtils.contains(request.getQueryString(),"&")){
				if(!loginError){
				String queryString = request.getQueryString().substring(request.getQueryString().indexOf("&")+1);
				model.addAttribute("targetUrl", request.getParameter("targetUrl")+"?"+queryString);
				} else {
					String queryString = StringUtils.removeEnd(request.getQueryString(),"&error=true");
					if(queryString.indexOf("&")>0){
					model.addAttribute("targetUrl", request.getParameter("targetUrl")+"?"+StringUtils.substring(queryString,queryString.indexOf("&")+1));
					} else {
						model.addAttribute("targetUrl", request.getParameter("targetUrl"));
					}
				}
			 
			}
			else {
				model.addAttribute("targetUrl", request.getParameter("targetUrl"));
			}
			}

		// check if the cookie contains the RememberMe and use it to populate the user name input text
		final Cookie[] cookies = request.getCookies();
		if (cookies != null)
		{
			final String cookName = StringUtils.deleteWhitespace(baseSiteService.getCurrentBaseSite().getUid())
					+ SabmCoreConstants.COOKIE_REMEMBERME;
			
			
			for (final Cookie cookie : cookies)
			{
				if (cookName.equals(cookie.getName()))
				{
					final SecureToken data = secureTokenService.decryptData(cookie.getValue());
					loginForm.setJ_username(data.getData());
					model.addAttribute("remember_me", true);
					break;
				}
			}
			model.addAttribute("loginForm", loginForm);
		}

		return getDefaultLoginPage(loginError, session, model);
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

	/**
	 *
	 * Modify by yaopeng for SAB-632 Judgment display the login error message
	 *
	 * @param loginError
	 * @param session
	 * @param model
	 * @throws CMSItemNotFoundException
	 */
	@Override
	protected String getDefaultLoginPage(final boolean loginError, final HttpSession session, final Model model)
			throws CMSItemNotFoundException
	{
		model.addAttribute(new RegisterForm());
		model.addAttribute(new GuestForm());

		storeCmsPageInModel(model, getCmsPage());
		setUpMetaDataForContentPage(model, (ContentPageModel) getCmsPage());
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.INDEX_FOLLOW);

		final Breadcrumb loginBreadcrumbEntry = new Breadcrumb("#",
				getMessageSource().getMessage("header.link.login", null, "header.link.login", getI18nService().getCurrentLocale()),
				null);
		model.addAttribute("breadcrumbs", Collections.singletonList(loginBreadcrumbEntry));

		// add by SAB_632 start

		// Through the session for the login user loginDeactivatedstatus
		boolean isActive = true;
		final String isDeactivated = (String) session.getAttribute(SABMWebConstants.ACCOUNT_ISDEACTIVATED);
		if (StringUtils.isNotEmpty(isDeactivated)
				&& isDeactivated.equals(SABMWebConstants.ACCOUNT_LOGINDISABLED_STATUS_ISDEACTIVATED))
		{
			isActive = false;
			session.removeAttribute(SABMWebConstants.ACCOUNT_ISDEACTIVATED);
		}
		if (loginError)
		{
			model.addAttribute("loginError", Boolean.valueOf(loginError));
			model.addAttribute("loginAttempts",(Integer) session.getAttribute(SABMWebConstants.LOGIN_ATTEMPTS));

			final LoginStatus lockStatus = (LoginStatus) session.getAttribute(SABMWebConstants.SPRING_SECURITY_LOGINDISABLED_STATUS);
			session.removeAttribute(SABMWebConstants.SPRING_SECURITY_LOGINDISABLED_STATUS);
			//If the account isLock, display is locked message
			if (LoginStatus.CONCURRENT_LOGINS_BLOCKED.equals(lockStatus))
			{
				GlobalMessages.addErrorMessage(model, "login.error.concurrent.sessions");
			}

			else if (LoginStatus.IS_LOCK.equals(lockStatus))
			{
				GlobalMessages.addErrorMessage(model, "login.error.account.islock.title");
			}
			else if (LoginStatus.SAP_ACCOUNT_BLOCKED.equals(lockStatus))
			{
				GlobalMessages.addErrorMessage(model, "login.error.account.sap.blocked");
			}
			else if (LoginStatus.SAP_SUSPENDED.equals(lockStatus))
			{
				GlobalMessages.addErrorMessage(model, "login.error.account.sap.suspended");
			}
			else if (!isActive)
			{
				GlobalMessages.addErrorMessage(model, "login.error.account.isDeactivated.title");
			}
			//If the account unLock, display is login error message
			else
			{
				GlobalMessages.addErrorMessage(model, "login.error.account.not.found.title");
			}

		}
		// add by SAB_632 end

		return getView();
	}

	@ModelAttribute("pageType")
	protected String getPageType()
	{
		return SABMWebConstants.PageType.LOGIN.name();
	}

}

