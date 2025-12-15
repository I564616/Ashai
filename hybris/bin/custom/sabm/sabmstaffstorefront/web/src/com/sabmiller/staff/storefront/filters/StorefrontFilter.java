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
package com.sabmiller.staff.storefront.filters;

import de.hybris.platform.acceleratorstorefrontcommons.history.BrowseHistory;
import de.hybris.platform.acceleratorstorefrontcommons.history.BrowseHistoryEntry;
import de.hybris.platform.cms2.misc.CMSFilter;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.CookieGenerator;


/**
 * Filter that initializes the session for the sabmstaffstorefront. This is a spring configured filter that is
 * executed by the PlatformFilterChain.
 */
public class StorefrontFilter extends OncePerRequestFilter
{
	public static final String AJAX_REQUEST_HEADER_NAME = "X-Requested-With";
	public static final String ORIGINAL_REFERER = "originalReferer";

	private StoreSessionFacade storeSessionFacade;
	private BrowseHistory browseHistory;
	private CookieGenerator cookieGenerator;

	@Override
	public void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws IOException, ServletException
	{
		final HttpSession session = request.getSession();
		final String queryString = request.getQueryString();

		if (isSessionNotInitialized(session, queryString))
		{
			initDefaults(request);

			markSessionInitialized(session);
		}

		// For secure requests ensure that the JSESSIONID cookie is visible to insecure requests
		if (isRequestSecure(request))
		{
			fixSecureHttpJSessionIdCookie(request, response);
		}

		if (isGetMethod(request))
		{
			if (StringUtils.isBlank(request.getHeader(AJAX_REQUEST_HEADER_NAME)))
			{
				String requestURL = request.getRequestURL().toString();
				session.setAttribute(ORIGINAL_REFERER, StringUtils.isNotBlank(queryString) ? requestURL + "?"
						+ queryString : requestURL);
			}

			getBrowseHistory().addBrowseHistoryEntry(new BrowseHistoryEntry(request.getRequestURI(), null));
		}

		filterChain.doFilter(request, response);
	}

	protected boolean isGetMethod(final HttpServletRequest httpRequest)
	{
		return "GET".equalsIgnoreCase(httpRequest.getMethod());
	}

	protected boolean isRequestSecure(final HttpServletRequest httpRequest)
	{
		return httpRequest.isSecure();
	}

	protected boolean isSessionNotInitialized(final HttpSession session, final String queryString)
	{
		return session.isNew() || StringUtils.contains(queryString, CMSFilter.CLEAR_CMSSITE_PARAM)
				|| !isSessionInitialized(session);
	}

	public void setStoreSessionFacade(final StoreSessionFacade storeSessionFacade)
	{
		this.storeSessionFacade = storeSessionFacade;
	}

	public void setBrowseHistory(final BrowseHistory browseHistory)
	{
		this.browseHistory = browseHistory;
	}

	protected void initDefaults(final HttpServletRequest request)
	{
		final StoreSessionFacade storeSessionFacade = getStoreSessionFacade();

		storeSessionFacade.initializeSession(Collections.list(request.getLocales()));
	}

	protected StoreSessionFacade getStoreSessionFacade()
	{
		return storeSessionFacade;
	}

	protected BrowseHistory getBrowseHistory()
	{
		return browseHistory;
	}


	protected boolean isSessionInitialized(final HttpSession session)
	{
		return session.getAttribute(this.getClass().getName()) != null;
	}

	protected void markSessionInitialized(final HttpSession session)
	{
		session.setAttribute(this.getClass().getName(), "initialized");
	}

	protected void fixSecureHttpJSessionIdCookie(final HttpServletRequest httpServletRequest,
			final HttpServletResponse httpServletResponse)
	{
		final HttpSession session = httpServletRequest.getSession(false);
		if (session != null)
		{
			getCookieGenerator().addCookie(httpServletResponse, session.getId());
		}

	}


	protected CookieGenerator getCookieGenerator()
	{
		return cookieGenerator;
	}

	public void setCookieGenerator(final CookieGenerator cookieGenerator)
	{
		this.cookieGenerator = cookieGenerator;
	}
}
