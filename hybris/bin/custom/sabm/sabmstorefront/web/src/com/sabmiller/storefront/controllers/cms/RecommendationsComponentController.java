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
package com.sabmiller.storefront.controllers.cms;

import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sabmiller.facades.recommendation.SABMRecommendationFacade;
import com.sabmiller.facades.recommendation.data.RecommendationData;
import com.sabmiller.storefront.controllers.ControllerConstants;
import com.sabmiller.storefront.util.RecommendationUtil;

import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;


/**
 * Controller for CMS RecommendationsComponent.
 */
@Controller("RecommendationsComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.RecommendationsComponent)
public class RecommendationsComponentController extends ProductCarouselComponentController
{

	protected static final int PRODUCTLIST_SIZE = 20;

	@Resource(name = "recommendationFacade")
	private SABMRecommendationFacade recommendationFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final ProductCarouselComponentModel component)
	{
		final List<RecommendationData> recommendationDetails = recommendationFacade.getRecommendations();
		model.addAttribute("recommendationData", recommendationDetails);
		RecommendationUtil.separateRecommendations(recommendationDetails, model);
	}

}
