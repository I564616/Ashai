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
package com.apb.storefront.interceptors;

import de.hybris.platform.acceleratorstorefrontcommons.interceptors.BeforeControllerHandler;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * A postHandle HandlerInterceptor that runs a number of BeforeViewHandlers before the view is rendered.
 */
public class BeforeControllerHandlerInterceptor implements HandlerInterceptor
{
	private static final String INTERCEPTOR_ONCE_KEY = BeforeControllerHandlerInterceptor.class.getName();

	private List<BeforeControllerHandler> beforeControllerHandlers;

	protected List<BeforeControllerHandler> getBeforeControllerHandlers()
	{
		return beforeControllerHandlers;
	}

	public void setBeforeControllerHandlers(final List<BeforeControllerHandler> beforeControllerHandlers)
	{
		this.beforeControllerHandlers = beforeControllerHandlers;
	}

	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception
	{
		if (request.getAttribute(INTERCEPTOR_ONCE_KEY) == null)
		{
			// Set the flag so that we are not executed multiple times
			request.setAttribute(INTERCEPTOR_ONCE_KEY, Boolean.TRUE);

			final HandlerMethod handlerMethod = (HandlerMethod) handler;

			// Call the pre handler once for the request
			for (final BeforeControllerHandler beforeControllerHandler : getBeforeControllerHandlers())
			{
				if(!(beforeControllerHandler.getClass().equals(Class.forName("de.hybris.platform.b2bacceleratoraddon.interceptors.SecurePortalBeforeControllerHandler")))) {
					if (!beforeControllerHandler.beforeController(request, response, handlerMethod))
					{
						// Return false immediately if a handler returns false
						return false;
					}
				}
			}
		}

		return true;
	}
}
