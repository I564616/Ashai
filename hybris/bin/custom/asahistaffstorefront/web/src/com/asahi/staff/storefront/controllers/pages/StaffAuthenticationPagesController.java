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
package com.asahi.staff.storefront.controllers.pages;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * Logout Page Controller. Handles logout redirect page after account logs out.
 */
@Controller
@Scope("tenant")
public class StaffAuthenticationPagesController extends AbstractAsahiStaffPageController
{
	private static final Logger LOG = LoggerFactory.getLogger(StaffAuthenticationPagesController.class);

	private static final String PORTAL_LOGOUT_REDIRECT = "portalLogoutRedirect";
	private static final String PORTAL_SSO_ERROR = "portalSSOError";
	private static final String PORTAL_LOGIN = "/login";

	@GetMapping("/logoutRedirect")
	public String logoutRedirect(final Model model) throws CMSItemNotFoundException
	{
		LOG.info("Load Logout Redirect Page... ");

		return REDIRECT_PREFIX + PORTAL_LOGIN;
	}

	@GetMapping("/ssoError")
	public String ssoErrorHandler(final Model model) throws CMSItemNotFoundException
	{
		LOG.error("SSO login encountered an error.");

		storeCmsPageInModel(model, getContentPageForLabelOrId(PORTAL_SSO_ERROR));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PORTAL_SSO_ERROR));
		updatePageTitle(model, getContentPageForLabelOrId(PORTAL_SSO_ERROR));

		return getViewForPage(model);
	}

	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveHomePageTitle(cmsPage.getTitle()));
	}

}
