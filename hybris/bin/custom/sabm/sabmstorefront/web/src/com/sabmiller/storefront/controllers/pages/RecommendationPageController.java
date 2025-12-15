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
package com.sabmiller.storefront.controllers.pages;


import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.core.GenericSearchConstants.LOG;
import de.hybris.platform.servicelayer.user.UserService;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sabmiller.core.enums.RecommendationStatus;
import com.sabmiller.core.enums.RecommendationType;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.model.SABMRecommendationModel;
import com.sabmiller.core.util.SabmUtils;
import com.sabmiller.facades.deal.data.DealBaseProductJson;
import com.sabmiller.facades.recommendation.SABMRecommendationFacade;
import com.sabmiller.facades.recommendation.data.RecommendationData;
import com.sabmiller.storefront.controllers.ControllerConstants;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.storefront.form.SABMAddToRecommendationForm;
import com.sabmiller.storefront.form.SABMProdToRecommendationForm;
import com.sabmiller.storefront.form.SABMUpdateRecommendationForm;
import com.sabmiller.storefront.form.SABMUpdateRecommendationsForm;
import com.sabmiller.storefront.util.RecommendationUtil;
import com.sabmiller.storefront.controllers.pages.SabmAbstractSearchPageController;
import com.apb.core.util.AsahiCoreUtil;


/**
 * Controller for recommendation page
 */
@Controller
@Scope("tenant")
@RequestMapping("/recommendation")
public class RecommendationPageController extends SabmAbstractSearchPageController
{
	private static final String RECOMMENDATION_REP_CMS_PAGE = "recommendationrep";
	private static final String RECOMMENDATION_CUS_CMS_PAGE = "recommendationcus";
	
	private static final Logger LOG = LoggerFactory.getLogger(RecommendationPageController.class);

	@Resource(name = "userService")
	private UserService userService;
	
	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	@Resource(name = "recommendationFacade")
	private SABMRecommendationFacade recommendationFacade;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;
	
	@GetMapping
	@RequireHardLogIn
	public String getRecommendations(final Model model) throws CMSItemNotFoundException
	{

		if (asahiCoreUtil.isNAPUser()) {
			return FORWARD_PREFIX + "/404";
			// throw new CMSItemNotFoundException("Not allowed access since this user is a
			// part of NAP group");

		}

		final List<RecommendationData> recommendationDetails = recommendationFacade.getRecommendations();
		model.addAttribute("recommendationData", recommendationDetails);

		RecommendationUtil.separateRecommendations(recommendationDetails,model);
	
		final ContentPageModel recommendationsCMSPage = (userService.getCurrentUser() instanceof BDECustomerModel)
				? getContentPageForLabelOrId(RECOMMENDATION_REP_CMS_PAGE)
				: getContentPageForLabelOrId(RECOMMENDATION_CUS_CMS_PAGE);
		storeCmsPageInModel(model, recommendationsCMSPage);
		setUpMetaDataForContentPage(model, recommendationsCMSPage);
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.INDEX_NOFOLLOW);
		List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs("text.recommendations.header");
		model.addAttribute("breadcrumbs", breadcrumbs);
		
		model.addAttribute("pageType", SABMWebConstants.PageType.RECOMMENDATION.name());
		model.addAttribute("requestOrigin", SabmUtils.getFormattedRequestOrigin(SabmUtils.HOME, CollectionUtils.isNotEmpty(breadcrumbs) ? breadcrumbs.get(0).getName() : ""));

		return getViewForPage(model);
	}


	@PostMapping(value = "/add", produces = "application/json")
	@RequireHardLogIn
	public String addRecommendation(@RequestBody @Valid final SABMProdToRecommendationForm form,  final Model model)
			throws CMSItemNotFoundException
	{

		if(userService.getCurrentUser() instanceof BDECustomerModel && form.getQty() >0){
			recommendationFacade.saveProductAsRecommendation(form.getProductCodePost(), Integer.valueOf(form.getQty()),form.getUnit());
			model.addAttribute("isRecommendationAdded", true);

		} else {
			model.addAttribute("isRecommendationAdded", false);
		}

		final List<RecommendationData> recommendationDetails = recommendationFacade.getRecommendations();
		model.addAttribute("recommendationsCount", String.valueOf(recommendationDetails.size()));


		return ControllerConstants.Views.Fragments.Recommendation.AddToRecommendationPopup;

	}

	@PostMapping("/add/deal")
	@RequireHardLogIn
	public String addDealToRecommendation(final Model model, @Valid @RequestBody final SABMAddToRecommendationForm form,
								final BindingResult bindingErrors, final HttpServletRequest request)
	{
		if(userService.getCurrentUser() instanceof BDECustomerModel) {
			
			LOG.debug("Adding deal to recommendation with products size : " + form.getBaseProducts().size());
			List<DealBaseProductJson> dealProductsList = new ArrayList<DealBaseProductJson>();
			for(SABMProdToRecommendationForm prodForm : form.getBaseProducts()){
				DealBaseProductJson dealProductData =new DealBaseProductJson();
				dealProductData.setProductCode(prodForm.getProductCodePost());
				dealProductData.setQty(prodForm.getQty());
				dealProductData.setUomCode(prodForm.getUnit());
				dealProductsList.add(dealProductData);
			}
			recommendationFacade.addDealAsRecommendation(form.getDealCode(),dealProductsList);
			model.addAttribute("isRecommendationAdded", true);
		} else {
			model.addAttribute("isRecommendationAdded", false);
		}

		final List<RecommendationData> recommendationDetails = recommendationFacade.getRecommendations();

		model.addAttribute("recommendationsCount", String.valueOf(recommendationDetails.size()));

		return ControllerConstants.Views.Fragments.Recommendation.AddToRecommendationPopup;
	}

	@PostMapping("/update")
	@RequireHardLogIn
	@ResponseBody
	public String updateRecommendation(@RequestBody final SABMUpdateRecommendationsForm form, final Model model) {
		
		if (CollectionUtils.isNotEmpty(form.getRecommendations())) {
			final SABMUpdateRecommendationForm recommendationForm = form.getRecommendations().get(0);
			
			try {
				recommendationFacade.updateRecommendation(recommendationForm.getRecommendationId(), RecommendationStatus.valueOf(recommendationForm.getStatus().toUpperCase()));
			} catch (Exception e) {
				LOG.error("Failed to update recommendation ", e);
			}
		}
		
		final List<RecommendationData> recommendationDetails = recommendationFacade.getRecommendations();
		return String.valueOf(recommendationDetails.size());
	}
	
	@PostMapping("/updateRecommendations")
	@RequireHardLogIn
	@ResponseBody
	public String updateRecommendations(@RequestBody @Valid final SABMUpdateRecommendationsForm form,
			final BindingResult bindingResult,Model model) throws UnsupportedEncodingException
	{
		if (bindingResult.hasErrors())
		{
			return Boolean.FALSE.toString();
		}

		if (form != null)
		{
			try {
				this.updateAndRemoveRecommendation(form);
			} catch (Exception e) {
				LOG.error("Failed to update recommendations ", e);
			}
		}

		final List<RecommendationData> recommendationDetails = recommendationFacade.getRecommendations();
		return String.valueOf(recommendationDetails.size());
	}
	
	private Boolean updateAndRemoveRecommendation(final SABMUpdateRecommendationsForm form)
	{
		Boolean success = true;
		for (final SABMUpdateRecommendationForm recommendationForm : form.getRecommendations())
		{
			switch  (RecommendationType.valueOf(recommendationForm.getRecommendationType())) {
				case PRODUCT : 
					success = recommendationFacade.updateProductRecommendation(recommendationForm.getRecommendationId(), recommendationForm.getQuantity(),
							recommendationForm.getUnit());
					break;
					
				case DEAL :
					SABMRecommendationModel recommendationModel = recommendationFacade.getRecommendationByID(recommendationForm.getRecommendationId());

					List<DealBaseProductJson> dealProductsList = new ArrayList<DealBaseProductJson>();
					for(SABMProdToRecommendationForm prodForm : recommendationForm.getBaseProducts()){
						DealBaseProductJson dealProductData =new DealBaseProductJson();
						dealProductData.setProductCode(prodForm.getProductCodePost());
						dealProductData.setQty(prodForm.getQty());
						dealProductData.setUomCode(prodForm.getUnit());
						dealProductsList.add(dealProductData);
					}
					recommendationFacade.saveDealAsRecommendation(recommendationModel.getDealCode(),dealProductsList);
					success = true;
			}
		}
		
		if (success && StringUtils.isNotEmpty(form.getRecommendationsToDelete()))
		{
			final String[] recommendationIdsArray = form.getRecommendationsToDelete().split(",");
			//sort the ids and remove them.
			Collections.sort(Arrays.asList(recommendationIdsArray));
			Collections.reverse(Arrays.asList(recommendationIdsArray));
			for (final String recommendationIdToRemove : recommendationIdsArray)
			{
				if (StringUtils.isNotEmpty(recommendationIdToRemove))
				{
					recommendationFacade.deleteRecommendationByID(recommendationIdToRemove);
				}
			}
		}
		
		return success;
	}
	
}
