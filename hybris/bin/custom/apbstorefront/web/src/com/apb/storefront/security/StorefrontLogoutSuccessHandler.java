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
package com.apb.storefront.security;

import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import com.apb.core.constants.ApbCoreConstants;


public class StorefrontLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler
{
	private GUIDCookieStrategy guidCookieStrategy;
	private List<String> restrictedPages;
	private SessionService sessionService;

	protected GUIDCookieStrategy getGuidCookieStrategy()
	{
		return guidCookieStrategy;
	}

	public void setGuidCookieStrategy(final GUIDCookieStrategy guidCookieStrategy)
	{
		this.guidCookieStrategy = guidCookieStrategy;
	}

	protected List<String> getRestrictedPages()
	{
		return restrictedPages;
	}

	public void setRestrictedPages(final List<String> restrictedPages)
	{
		this.restrictedPages = restrictedPages;
	}

	@Override
	public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException
	{
		getGuidCookieStrategy().deleteCookie(request, response);

		// Delegate to default redirect behaviour
		super.onLogoutSuccess(request, response, authentication);
	}

	@Override
	protected String determineTargetUrl(final HttpServletRequest request, final HttpServletResponse response)
	{
		String targetUrl = super.determineTargetUrl(request, response);

		for (final String restrictedPage : getRestrictedPages())
		{
			// When logging out from a restricted page, return user to homepage.
			if (targetUrl.contains(restrictedPage))
			{
				targetUrl = super.getDefaultTargetUrl();
			}
		}

		getSessionService().removeAttribute(ApbCoreConstants.CUSTOMER_SESSION_INCLUSION_LIST);
		getSessionService().removeAttribute(ApbCoreConstants.IS_CUSTOMER_SESSION_CREDIT_BLOCK);
		getSessionService().removeAttribute(ApbCoreConstants.IS_SHOW_PRODUCT_WITHOUT_PRICE);
		getSessionService().removeAttribute(ApbCoreConstants.INCLUSION_CHECKOUT_FLAG);
		getSessionService().removeAttribute(ApbCoreConstants.PRODUCT_BLOCK_FLAG);

		return targetUrl;
	}

	public SessionService getSessionService() {
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService) {
		this.sessionService = sessionService;
	}
}
