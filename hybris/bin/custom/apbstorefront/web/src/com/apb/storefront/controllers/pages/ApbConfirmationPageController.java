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

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;

import java.util.Collections;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.storefront.constant.ApbStoreFrontContants;
import com.apb.storefront.controllers.ControllerConstants;


/**
 * Controller for home page
 */
@Controller
@RequestMapping(value = "/{path:.*}/confirmation")
public class ApbConfirmationPageController extends ApbAbstractPageController
{
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	/**
	 * @param firstName
	 * @param model
	 * @param redirectModel
	 * @param request
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping
	public String registerConfirmationPage(@RequestParam("firstName") final String firstName, final Model model,
			final HttpServletRequest request) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(ApbStoreFrontContants.CONFIRMATION));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ApbStoreFrontContants.CONFIRMATION));
		updatePageTitle(model, getContentPageForLabelOrId(ApbStoreFrontContants.CONFIRMATION));
		final Breadcrumb companyDetails = new Breadcrumb("#",
				getMessageSource().getMessage("header.link.register.confirmation", null, getI18nService().getCurrentLocale()), null);
		model.addAttribute("breadcrumbs", Collections.singletonList(companyDetails));
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		String accessType = StringUtils.EMPTY;
		if(asahiSiteUtil.isSga() && request.getSession().getAttribute("accessType") == null) {
			accessType = asahiCoreUtil.getCurrentUserAccessType();
			request.getSession().setAttribute("accessType",accessType);
		}
		accessType =null != request.getSession().getAttribute("accessType")? (String)request.getSession().getAttribute("accessType") : StringUtils.EMPTY;
		if(accessType.equalsIgnoreCase(ApbCoreConstants.PAY_AND_ORDER_ACCESS)) {
			model.addAttribute("approvalEmailId",null != asahiCoreUtil.getDefaultB2BUnit() 
					&& null != asahiCoreUtil.getDefaultB2BUnit().getPayerAccount()?
					asahiCoreUtil.getDefaultB2BUnit().getPayerAccount().getEmailAddress(): StringUtils.EMPTY);
		} else {
			model.addAttribute("approvalEmailId",asahiCoreUtil.getDefaultB2BUnit()!=null?asahiCoreUtil.getDefaultB2BUnit().getEmailAddress(): StringUtils.EMPTY);
		}
		model.addAttribute("firstName", firstName);
		model.addAttribute("tradingName",asahiCoreUtil.getDefaultB2BUnit()!=null?asahiCoreUtil.getDefaultB2BUnit().getLocName(): StringUtils.EMPTY);
		request.getSession().setAttribute("multiAccountSelfRegistration", false);
		/*if (userService.getCurrentUser().getUid().equals(ApbStoreFrontContants.ANONYMOUS))
		{
			return REDIRECT_PREFIX + "/";
		}*/
		return ControllerConstants.Views.Pages.Account.ConfirmationPage;
	}

	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveHomePageTitle(cmsPage.getTitle()));
	}

}
