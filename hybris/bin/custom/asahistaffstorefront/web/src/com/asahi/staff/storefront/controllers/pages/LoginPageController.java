/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.asahi.staff.storefront.controllers.pages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.platform.acceleratorstorefrontcommons.forms.RegisterForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.samlsinglesignon.SSOUserService;
import de.hybris.platform.samlsinglesignon.SamlLoginService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
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

import com.asahi.staff.storefront.controllers.ControllerConstants;

/**
 * Login Controller. Handles login and register for the account flow.
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/login")
public class LoginPageController extends AbstractAsahiStaffLoginPageController
{
	private static final Logger LOG = LoggerFactory.getLogger(LoginPageController.class);

	private HttpSessionRequestCache httpSessionRequestCache;
	private static final String PORTAL_LOGIN_CMS_PAGE = "portalLogin";
	private static final String PORTAL_LOAD_SSO_LOGIN_PAGE = "asahistaffstorefront.storefront.login.sso";

	@Resource
	private UserService userService;

	@Resource
	private ConfigurationService configurationService;

	@Resource
	private SSOUserService ssoUserService;

	@Resource
	private SamlLoginService samlLoginService;


	@Override
	protected String getView()
	{
		final boolean isLoadSSOLoginPage = configurationService.getConfiguration()
				.getBoolean(LoginPageController.PORTAL_LOAD_SSO_LOGIN_PAGE, false);
		LOG.info("Load SSO Login Page: " + Boolean.valueOf(isLoadSSOLoginPage));

		if (isLoadSSOLoginPage)
		{
			return ControllerConstants.Views.Pages.Account.AccountStaffLoginPage;
		}
		else
		{
			return ControllerConstants.Views.Pages.Account.AccountLoginPage;
		}
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
		return getContentPageForLabelOrId(PORTAL_LOGIN_CMS_PAGE);
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
		if (!loginError)
		{
			storeReferer(referer, request, response);
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
	 * Store SSO login token and create cookie for ALB Domain since redirection from AD during SSO is on CUB domain..
	 *
	 * @param token
	 *           the token
	 * @param model
	 *           the model
	 * @param request
	 *           the request
	 * @param response
	 *           the response
	 * @return the string
	 */
	@GetMapping("/sso")
	public String storeSSOLoginToken(@RequestParam(value = "token", required = true)
	final String token, final Model model, final HttpServletRequest request, final HttpServletResponse response) throws JsonProcessingException, IOException {

		final Decoder decoder = Base64.getDecoder();
		final byte[] bytes = decoder.decode(token);
		final String tokenInfo = new String(bytes);

        final ObjectMapper mapper = new ObjectMapper();
        //final JSONObject object = new JSONObject(tokenInfo);
        final JsonNode object = mapper.readTree(tokenInfo);
		final String userId = object.path("user_id").asText();
		final String userName = object.path("user_name").asText();
		final String rolesByDelimiter = object.path("roles").asText();
		final List<String> roles = StringUtils.isNotBlank(rolesByDelimiter)
				? Stream.of(rolesByDelimiter.split(",", -1)).collect(Collectors.toList())
				: Collections.EMPTY_LIST;
		final String language = object.path("language").asText();

		if (CollectionUtils.isEmpty(roles))
		{
			LOG.error("User does not have access to ALB Staff Portal");
			return "redirect:/ssoError";
		}
		else
		{
			final String adRole = roles.stream()
					.filter(role -> role.equalsIgnoreCase(configurationService.getConfiguration().getString("sso.alb.ad.role")))
					.findAny().orElse(null);

			if (StringUtils.isBlank(adRole))
			{
				LOG.error("User does not have access to ALB Staff Portal");
				return "redirect:/ssoError";
			}
		}

		//Store Login Cookie
		response.setHeader("isALBStaffPortalLogin", String.valueOf(Boolean.TRUE));
		try
		{
			this.samlLoginService.storeLoginToken(response,
					this.ssoUserService.getOrCreateSSOUser(userId, userName, roles), language);
			return "redirect:/customer-search";
		}
		catch (final Exception var8)
		{

			LOG.error("Can't map user properly", var8);
			throw new AccessDeniedException("Can't map user properly", var8);
		}

	}

	/**
	 * @return the samlLoginService
	 */
	public SamlLoginService getSamlLoginService()
	{
		return samlLoginService;
	}

	/**
	 * @param samlLoginService
	 *           the samlLoginService to set
	 */
	public void setSamlLoginService(final SamlLoginService samlLoginService)
	{
		this.samlLoginService = samlLoginService;
	}

	/**
	 * @return the userService
	 */
	public SSOUserService getSSOUserService()
	{
		return ssoUserService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setSSOUserService(final SSOUserService ssoUserService)
	{
		this.ssoUserService = ssoUserService;
	}
}
