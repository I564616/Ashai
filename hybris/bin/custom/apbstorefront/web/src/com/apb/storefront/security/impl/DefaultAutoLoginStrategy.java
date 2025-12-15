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
package com.apb.storefront.security.impl;

import de.hybris.platform.acceleratorstorefrontcommons.security.AutoLoginStrategy;
import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.commercefacades.customer.CustomerFacade;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetails;


/**
 * Default implementation of {@link AutoLoginStrategy}
 */
public class DefaultAutoLoginStrategy implements AutoLoginStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultAutoLoginStrategy.class);

	private AuthenticationManager authenticationManager;
	private CustomerFacade customerFacade;
	private GUIDCookieStrategy guidCookieStrategy;
	private RememberMeServices rememberMeServices;

	@Override
	public void login(final String username, final String password, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
		token.setDetails(new WebAuthenticationDetails(request));
		try
		{
			final Authentication authentication = getAuthenticationManager().authenticate(token);
			SecurityContext securityContext = SecurityContextHolder.getContext();
			securityContext.setAuthentication(authentication);
			getCustomerFacade().loginSuccess();
			getGuidCookieStrategy().setCookie(request, response);
			getRememberMeServices().loginSuccess(request, response, token);
			
			HttpSession session = request.getSession(true);
		    session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
		}
		catch (final Exception e)
		{
			SecurityContextHolder.getContext().setAuthentication(null);
			LOG.error("Failure during autoLogin", e);
		}
	}

	protected AuthenticationManager getAuthenticationManager()
	{
		return authenticationManager;
	}

	public void setAuthenticationManager(final AuthenticationManager authenticationManager)
	{
		this.authenticationManager = authenticationManager;
	}

	protected CustomerFacade getCustomerFacade()
	{
		return customerFacade;
	}

	public void setCustomerFacade(final CustomerFacade customerFacade)
	{
		this.customerFacade = customerFacade;
	}

	protected GUIDCookieStrategy getGuidCookieStrategy()
	{
		return guidCookieStrategy;
	}

	public void setGuidCookieStrategy(final GUIDCookieStrategy guidCookieStrategy)
	{
		this.guidCookieStrategy = guidCookieStrategy;
	}

	protected RememberMeServices getRememberMeServices()
	{
		return rememberMeServices;
	}

	public void setRememberMeServices(final RememberMeServices rememberMeServices)
	{
		this.rememberMeServices = rememberMeServices;
	}
}
