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
package com.sabmiller.storefront.interceptors.beforeview;

import de.hybris.platform.acceleratorservices.config.HostConfigService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.acceleratorstorefrontcommons.interceptors.BeforeViewHandler;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.ModelAndView;


/**
 * Filter to load Google Maps API Keys into the model.
 *
 */
public class GoogleMapsBeforeViewHandler implements BeforeViewHandler
{

	private static final String GOOGLE_API_KEY_ID = "googleApiKey";
	private static final String GOOGLE_API_VERSION = "googleApiVersion";

	@Resource(name = "hostConfigService")
	private HostConfigService hostConfigService;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Override
	public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView)
			throws Exception
	{
		modelAndView.addObject("googleApiVersion", configurationService.getConfiguration().getString(GOOGLE_API_VERSION));
		final String googleApiKey = hostConfigService.getProperty(GOOGLE_API_KEY_ID, request.getServerName());
		if (StringUtils.isNotEmpty(googleApiKey))
		{
			modelAndView.addObject("googleApiKey", googleApiKey);
		}
	}

}
