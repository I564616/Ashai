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
package com.sabmiller.staff.storefront.interceptors.beforeview;

import de.hybris.platform.acceleratorservices.storefront.data.MetaElementData;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;

import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sabmiller.staff.storefront.interceptors.BeforeViewHandler;


/**
 * Adds meta tags to help guide the device for the current UI Experience.
 */
public class UiExperienceMetadataViewHandler implements BeforeViewHandler
{

	@Resource(name = "uiExperienceService")
	private UiExperienceService uiExperienceService;

	@Override
	public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView)
			throws Exception
	{

		if (modelAndView != null && modelAndView.getModel().containsKey("metatags"))
		{
			final List<MetaElementData> metaelements = ((List<MetaElementData>) modelAndView.getModel().get("metatags"));
			// Provide some hints to mobile browser even though this is not the mobile site -->
			metaelements.add(createMetaElement("viewport", "width=device-width, initial-scale=1"));
		}

	}

	protected MetaElementData createMetaElement(final String name, final String content)
	{
		final MetaElementData element = new MetaElementData();
		element.setName(name);
		element.setContent(content);
		return element;
	}
}
