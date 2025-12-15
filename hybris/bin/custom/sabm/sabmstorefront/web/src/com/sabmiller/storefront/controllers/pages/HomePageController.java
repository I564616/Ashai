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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.sabmiller.core.model.AsahiB2BUnitModel;

import com.sabmiller.commons.constants.SabmcommonsConstants;
import com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.order.SABMOrderFacade;
import com.sabmiller.facades.order.data.TrackOrderData;
import com.sabmiller.facades.order.json.OrderHistoryJson;
import com.sabmiller.facades.user.NotificationData;
import com.sabmiller.facades.util.SabmFeatureUtil;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.storefront.security.CustomerRoleChecker;
import com.sabmiller.storefront.controllers.pages.SabmAbstractPageController;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import com.apb.core.util.AsahiCoreUtil;

/**
 * Controller for home page
 */
@Controller
@Scope("tenant")
@RequestMapping("/")
public class HomePageController extends SabmAbstractPageController
{
	@Resource(name = "orderFacade")
	private SABMOrderFacade orderFacade;

	@Resource(name = "b2bCommerceUnitFacade")
	private SabmB2BCommerceUnitFacade b2bUnitFacade;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "customerFacade")
	private SABMCustomerFacade customerFacade;

	@Resource(name = "sabmFeatureUtil")
	private SabmFeatureUtil sabmFeatureUtil;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;


	private static final Logger LOG = LoggerFactory.getLogger(HomePageController.class);

	private static final String MESSAGE_CODE_PATH_VARIABLE_PATTERN = "{messageCode:.*}";

	@SuppressWarnings("boxing")
	@GetMapping
	public String home(@RequestParam(value = "logout", defaultValue = "false") final boolean logout, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (logout)
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER, "account.confirmation.signout.title");
			return REDIRECT_PREFIX + ROOT;
		}

		if (customerFacade.isEmployeeUser(userService.getCurrentUser()))
		{
			return REDIRECT_PREFIX + SABMWebConstants.EMPOYEE_USER_SEARCH_URL;
		}

		/*
		 * Check if current user has only assistant role then redirect to the /your-business as default.
		 */
		final List<String> belongingGroupIds = new ArrayList<String>();
		final Set<PrincipalGroupModel> groups = userService.getCurrentUser().getGroups();
		for (final PrincipalGroupModel group : groups)
		{
			if(group instanceof AsahiB2BUnitModel)
			{
				continue;
			}
			else
			{
				belongingGroupIds.add(group.getUid());
			}
		}
		final boolean hasOnlyAssistantRole = CustomerRoleChecker.hasOnlyRole(belongingGroupIds, CustomerRoleChecker.ROLE_ASSISTANT);
		if (hasOnlyAssistantRole)
		{
			return REDIRECT_PREFIX + SABMWebConstants.ASSISTANT_DEFAULT_PAGE_URL;
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(null));

		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));

		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.INDEX_NOFOLLOW);

		updatePageTitle(model, getContentPageForLabelOrId(null));

		final int MAX_ORDERHISTROY_COUNT = getSiteConfigService().getInt(SABMWebConstants.MAX_ORDERHISTROY_COUNT, 5);
		// by SAB-581 get the list OrderHistoryJson

		final List<OrderHistoryJson> orderHistoryList = orderFacade.getTopOrderHistory(MAX_ORDERHISTROY_COUNT);

		model.addAttribute("maxOrderHistoryCount", MAX_ORDERHISTROY_COUNT);
		model.addAttribute("orderHistoryList", orderHistoryList);

		//get top 5 order template for home page
		model.addAttribute("maxOrderTemplateCount", getSiteConfigService().getLong(SABMWebConstants.MAX_ORDERTEMPLATE_COUNT, 5));

		model.addAttribute("orderTemplates", b2bUnitFacade.getB2BUnitOrderTemplates());

		try{

			List<NotificationData> notifications = ((SABMCustomerFacade) getCustomerFacade()).getUnreadSiteNotification();
			model.addAttribute("notifications",notifications);
		}
		catch(Exception e){
			LOG.error("Exception while fetching notifications");
		}


		model.addAttribute("pageType", SABMWebConstants.PageType.HOME.name());
		final boolean isTrackDeliveryOrderFeatureEnabled = sabmFeatureUtil.isFeatureEnabled(SabmcommonsConstants.TRACK_DELIVERY_ORDER);

		model.addAttribute("isTrackDeliveryOrderFeatureEnabled", isTrackDeliveryOrderFeatureEnabled);

		//to avoid risk of referencing null value in frontend. added this
		final List<TrackOrderData>  trackOrderDataList = isTrackDeliveryOrderFeatureEnabled?orderFacade.getActiveOrderByB2BUnit():Collections.emptyList();

		model.addAttribute("trackOrderDataList", trackOrderDataList);

		model.addAttribute("isNAPGroup",asahiCoreUtil.isNAPUserForSite());

		return getViewForPage(model);
	}

	@SuppressWarnings("boxing")
	@ResponseBody
	@GetMapping("/notification/hide/"+MESSAGE_CODE_PATH_VARIABLE_PATTERN)
	public boolean hideNotification(@PathVariable(value = "messageCode") final String messageCode) throws CMSItemNotFoundException
	{
		((SABMCustomerFacade) getCustomerFacade()).markSiteNotificationAsRead(messageCode);
	return true;
	}
	
	
	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveHomePageTitle(cmsPage.getTitle()));
	}

	@ModelAttribute("cupRefreshInProgress")
	public boolean isCupRefreshInProgress()
	{
		final UserModel userModel = userService.getCurrentUser();
		if (userModel instanceof B2BCustomerModel)
		{
			return ((SABMCustomerFacade) getCustomerFacade()).isCupRefreshInProgress();
		}
		return false;
	}

}
