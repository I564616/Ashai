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

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import de.hybris.platform.acceleratorstorefrontcommons.interceptors.BeforeViewHandler;
//import de.hybris.platform.b2bacceleratoraddon.interceptors.SecurePortalBeforeViewHandler;

/**
 * A postHandle HandlerInterceptor that runs a number of BeforeViewHandlers before the view is rendered.
 */
public class BeforeViewHandlerInterceptor implements HandlerInterceptor
{
	private List<BeforeViewHandler> beforeViewHandlers;

	protected List<BeforeViewHandler> getBeforeViewHandlers()
	{
		return beforeViewHandlers;
	}

	public void setBeforeViewHandlers(final List<BeforeViewHandler> beforeViewHandlers)
	{
		this.beforeViewHandlers = beforeViewHandlers;
	}

	@Override
	public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView) throws Exception
	{
		if (modelAndView != null && !isIncludeRequest(request) && isSupportedView(modelAndView))
		{
			for (final BeforeViewHandler beforeViewHandler : getBeforeViewHandlers())
			{
				if(!(beforeViewHandler.getClass().equals(Class.forName("de.hybris.platform.b2bacceleratoraddon.interceptors.SecurePortalBeforeViewHandler")))) {

					beforeViewHandler.beforeView(request, response, modelAndView);
				}
			}
		}
	}

	protected boolean isIncludeRequest(final HttpServletRequest request)
	{
		return request.getAttribute("jakarta.servlet.include.request_uri") != null;
	}

	protected boolean isSupportedView(final ModelAndView modelAndView)
	{
		return modelAndView.getViewName() != null && !isRedirectView(modelAndView);
	}

	protected boolean isRedirectView(final ModelAndView modelAndView)
	{
		final String viewName = modelAndView.getViewName();
		return viewName != null && viewName.startsWith("redirect:");
	}
}
