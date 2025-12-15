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
package com.apb.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.ContentPageBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import com.apb.storefront.controllers.pages.ApbAbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import jakarta.annotation.Resource;
import com.apb.storefront.constant.ApbStoreFrontContants;
import com.apb.storefront.controllers.ControllerConstants;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Simple CMS Content Page controller. Used only to preview CMS Pages. The DefaultPageController is used to serve
 * generic content pages.
 */
@Controller
@RequestMapping(value = "/preview-content")
public class PreviewContentPageController extends ApbAbstractPageController
{

	@Resource(name = "contentPageBreadcrumbBuilder")
	private ContentPageBreadcrumbBuilder contentPageBreadcrumbBuilder;

	@GetMapping( params =
	{ "uid" })
	public String get(@RequestParam(value = "uid") final String cmsPageUid, final Model model) throws CMSItemNotFoundException
	{
		final AbstractPageModel pageForRequest = getCmsPageService().getPageForId(cmsPageUid);
		storeCmsPageInModel(model, getCmsPageService().getPageForId(cmsPageUid));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(cmsPageUid));
		
		if((getContentPageForLabelOrId(cmsPageUid)).getBackgroundImage() != null)
			model.addAttribute("media", (getContentPageForLabelOrId(cmsPageUid)).getBackgroundImage().getURL());
	
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				contentPageBreadcrumbBuilder.getBreadcrumbs((ContentPageModel) pageForRequest));
		
		if(ApbStoreFrontContants.ASAHI_TERMS_AND_LEGAL_PAGE.equalsIgnoreCase(cmsPageUid))
		{
			return ControllerConstants.Views.Pages.Account.TermsAndLegalPage;
		}
		if(ApbStoreFrontContants.ASAHI_ABOUT_US_PAGE.equalsIgnoreCase(cmsPageUid))
		{
			return ControllerConstants.Views.Pages.Account.AboutUsPage;
		}
		
		return getViewForPage(pageForRequest);
	}
}
