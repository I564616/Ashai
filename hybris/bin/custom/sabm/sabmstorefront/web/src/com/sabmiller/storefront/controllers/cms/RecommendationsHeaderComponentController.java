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


import com.sabm.core.model.cms.components.RecommendationsHeaderComponentModel;
import com.sabmiller.facades.recommendation.SABMRecommendationFacade;
import com.sabmiller.storefront.controllers.ControllerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;


/**
 * Controller for CMS RecommendationsComponent.
 */
@Controller("RecommendationsHeaderComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.RecommendationsHeaderComponent)
public class RecommendationsHeaderComponentController extends AbstractCMSComponentController<RecommendationsHeaderComponentModel>
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(RecommendationsHeaderComponentController.class);

	protected static final int PRODUCTLIST_SIZE = 20;

	@Resource(name = "recommendationFacade")
	private SABMRecommendationFacade recommendationFacade;


	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, RecommendationsHeaderComponentModel component) {
		String recommendationCount = "0";
		try {
			recommendationCount = String.valueOf(recommendationFacade.getTotalRecommendations());
		} catch (Exception e) {
			LOG.warn("Exception while getting recommendationCount");
		}

		model.addAttribute("recommendationsCount", recommendationCount);
	}



}
