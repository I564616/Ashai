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
package com.sabmiller.storefront.security;

import de.hybris.platform.acceleratorservices.urlencoder.UrlEncoderService;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commerceservices.security.SecureTokenService;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import com.sabmiller.storefront.web.wrappers.RemoveEncodingHttpServletRequestWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;


public class AcceleratorRememberMeServices extends TokenBasedRememberMeServices
{
	private UserService userService;
	private CustomerFacade customerFacade;
	private CheckoutCustomerStrategy checkoutCustomerStrategy;
	private StoreSessionFacade storeSessionFacade;
	private CommonI18NService commonI18NService;
	private UrlEncoderService urlEncoderService;
	private SecureTokenService secureTokenService;

	public static final String SPRING_SECURITY_LAST_USERNAME_KEY = "SPRING_SECURITY_LAST_USERNAME";

	public AcceleratorRememberMeServices(final String key, final UserDetailsService userDetailsService)
	{
		super(key, userDetailsService);
	}

	@Override
	protected void setCookie(final String[] tokens, final int maxAge, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		if (!getCheckoutCustomerStrategy().isAnonymousCheckout())
		{
			super.setCookie(tokens, maxAge, new RemoveEncodingHttpServletRequestWrapper(request, getUrlEncodingPattern(request)),
					response);
		}
	}

	@Override
	public void logout(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication)
	{
		super.logout(new RemoveEncodingHttpServletRequestWrapper(request, getUrlEncodingPattern(request)), response,
				authentication);
	}

	/**
	 * Modify by xiaowu for SAB-377 add remember me logic
	 *
	 * @param request
	 * @param user
	 */
	@Override
	protected Authentication createSuccessfulAuthentication(final HttpServletRequest request, final UserDetails user)
	{
		getUserService().setCurrentUser(getUserService().getUserForUID(user.getUsername()));

		if (StringUtils.isNotEmpty(getUrlEncoderService().getUrlEncodingPattern()))
		{
			getCustomerFacade().rememberMeLoginSuccessWithUrlEncoding(getUrlEncoderService().isLanguageEncodingEnabled(),
					getUrlEncoderService().isCurrencyEncodingEnabled());
		}
		else
		{
			getCustomerFacade().loginSuccess();
		}

		// add by xiaowu start, if the username is null set the username to current user
		if (request.getSession().getAttribute(SPRING_SECURITY_LAST_USERNAME_KEY) == null)
		{
			request.getSession().setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, user.getUsername());
		}

		final RememberMeAuthenticationToken auth = new RememberMeAuthenticationToken(getKey(), user, user.getAuthorities());
		auth.setDetails(getAuthenticationDetailsSource().buildDetails(request));
		return auth;
	}

	@Override
	protected String retrievePassword(final Authentication authentication)
	{
		return getUserService().getUserForUID(authentication.getPrincipal().toString()).getEncodedPassword();
	}

	protected String getUrlEncodingPattern(final HttpServletRequest request)
	{
		final String encodingAttributes = (String) request.getAttribute(WebConstants.URL_ENCODING_ATTRIBUTES);
		return StringUtils.defaultString(encodingAttributes);
	}

	protected UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected CustomerFacade getCustomerFacade()
	{
		return customerFacade;
	}

	public void setCustomerFacade(final CustomerFacade customerFacade)
	{
		this.customerFacade = customerFacade;
	}

	protected CheckoutCustomerStrategy getCheckoutCustomerStrategy()
	{
		return checkoutCustomerStrategy;
	}

	public void setCheckoutCustomerStrategy(final CheckoutCustomerStrategy checkoutCustomerStrategy)
	{
		this.checkoutCustomerStrategy = checkoutCustomerStrategy;
	}

	protected UrlEncoderService getUrlEncoderService()
	{
		return urlEncoderService;
	}

	public void setUrlEncoderService(final UrlEncoderService urlEncoderService)
	{
		this.urlEncoderService = urlEncoderService;
	}


	protected StoreSessionFacade getStoreSessionFacade()
	{
		return storeSessionFacade;
	}

	public void setStoreSessionFacade(final StoreSessionFacade storeSessionFacade)
	{
		this.storeSessionFacade = storeSessionFacade;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected SecureTokenService getSecureTokenService()
	{
		return secureTokenService;
	}

	public void setSecureTokenService(final SecureTokenService secureTokenService)
	{
		this.secureTokenService = secureTokenService;
	}
}
