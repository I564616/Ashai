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

import static com.apb.storefront.constant.ApbStoreFrontContants.ASAHI_USER_TIMEOFFSET_COOKIE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.checkout.APBCheckoutFacade;
import com.apb.facades.deal.data.AsahiDealData;
import com.apb.storefront.constant.ApbStoreFrontContants;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Controller for home page
 */
@Controller
@RequestMapping("/")
public class HomePageController extends ApbAbstractPageController
{
	private static final Logger LOGGER = Logger.getLogger(HomePageController.class);
	private static final String HEADER_B2BUNIT_DROPDOWN_SHOW_APB = "header.b2bunit.dropdown.show.apb";
	private static final String ASAHI_SAM_ACCESS_REQUEST_PAGE_ID = "requestAccess";
	private static final String SAM_PAY_ACCESS_BREADCRUMB = "sam.pay.access.confirmation.breadcrumb";
	private static final String BREADCRUMBS_ATTR = "breadcrumbs";
	

	@Autowired
	private SABMCustomerFacade sabmCustomerFacade;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "apbCheckoutFacade")
	private APBCheckoutFacade apbCheckoutFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	
	@Resource(name = "sabmDealsSearchFacade")
	private SABMDealsSearchFacade dealsSearchFacade;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;
	
	@Resource(name = "cartFacade")
	private SABMCartFacade sabmCartFacade;
	
	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	/** The Constant ASAHI_DEVICE_TYPE_COOKIE. */
	private static final String ASAHI_DEVICE_TYPE_COOKIE = "deviceType";

	@GetMapping
	public String home(@RequestParam(value = "logout", defaultValue = "false") final boolean logout, final Model model,
			final RedirectAttributes redirectModel, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		if (logout)
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER, "account.confirmation.signout.title");
			return REDIRECT_PREFIX + ROOT;
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(null));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));

		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}


		updatePageTitle(model, getContentPageForLabelOrId(null));
		//	model.addAttribute("creditLimit", sabmCustomerFacade.getCustomerAccountCreditLimit());

		//Populating Customer B2BUnits
		final UserModel customer = this.userService.getCurrentUser();
		final Map<String, String> b2bUnits = new HashMap<String, String>();

		if (CollectionUtils.isNotEmpty(customer.getAllGroups()))
		{
			for (final PrincipalGroupModel groupModel : customer.getAllGroups())
			{
				if (groupModel instanceof AsahiB2BUnitModel)
				{
					b2bUnits.put(groupModel.getUid(), groupModel.getName());
				}
			}
		}
		
		asahiCoreUtil.setMultiAccountDisplayLink(request);
		final Boolean isUnitBelongToCurrentSite = sessionService.getAttribute(ApbStoreFrontContants.IS_DEFAULT_UNIT_BELONGS_TO_CURRENT_SITE);
		if(null != isUnitBelongToCurrentSite && !isUnitBelongToCurrentSite ) {
			sessionService.setAttribute(ApbStoreFrontContants.IS_DEFAULT_UNIT_BELONGS_TO_CURRENT_SITE,Boolean.TRUE);
			sabmCartFacade.removeSessionCart();
		}
		model.addAttribute("isApprovalPending",asahiCoreUtil.isSAMAccessApprovalPending());
		model.addAttribute("isAccessDenied",asahiCoreUtil.isSAMAccessDenied());
		
		final String accessType = asahiCoreUtil.getCurrentUserAccessType();
		model.addAttribute("accessType",accessType);
		
		model.addAttribute("approvalEmailId",null != asahiCoreUtil.getDefaultB2BUnit() 
					&& null != asahiCoreUtil.getDefaultB2BUnit().getPayerAccount()?
					asahiCoreUtil.getDefaultB2BUnit().getPayerAccount().getEmailAddress(): StringUtils.EMPTY);
		
		if (null != request.getSession())
		{
			request.getSession().setAttribute("b2bUnits", b2bUnits.get(asahiSiteUtil.getCurrentSite().getUid()));
			request.getSession().setAttribute("multiAccountSelfRegistration", false);
		}
		model.addAttribute("showB2BUnitDropDown",
				this.asahiConfigurationService.getString(HEADER_B2BUNIT_DROPDOWN_SHOW_APB, "false"));
		setUserTimeInSession(request);
		setDeviceTypeInSession(request);
		if(asahiSiteUtil.isSga()) {
			model.addAttribute("asahiDeals",getAsahiDeals());
		}
		if((!userService.isAnonymousUser(customer))){
			//Update... if the user is first time logged in....
			sabmCustomerFacade.updateCustomerLoggedIn(customer);

			//set the indicator to permit the logic to show cc info popup
            final Boolean  showPopup = asahiCoreUtil.getCurrentB2BCustomer().getShowCCInfoPopup();
            model.addAttribute("showAlbCCPopup", null != showPopup ? showPopup : true);
		}
		return getViewForPage(model);
	}

	/**
	 * @param request
	 *           <p>
	 * 			 This method will set the device type in session. Which can be used at any required place from session.
	 *           </p>
	 *
	 */
	private void setDeviceTypeInSession(final HttpServletRequest request)
	{
		if (null != request.getCookies())
		{
			for (final Cookie cookie : request.getCookies())
			{
				if (null != cookie && null != cookie.getName() && ASAHI_DEVICE_TYPE_COOKIE.equals(cookie.getName()))
				{
					sessionService.setAttribute("userDeviceType", cookie.getValue());
					break;
				}
			}
		}

	}

	/**
	 * Sets the user time in session.
	 *
	 * @param request the new user time in session
	 */
	private void setUserTimeInSession(final HttpServletRequest request)
	{
		if (request.getCookies() != null)
		{

			for (final Cookie cookie : request.getCookies())
			{
				if (null != cookie && null != cookie.getName() && ASAHI_USER_TIMEOFFSET_COOKIE.equals(cookie.getName()))
				{
					sessionService.setAttribute(ASAHI_USER_TIMEOFFSET_COOKIE, cookie.getValue());
					break;
				}
			}
		}
	}

	/**
	 * get credit check response.
	 *
	 * @return the string
	 */
	@ResponseBody
	@GetMapping("/creditcheck")
	public boolean getCreditCheck()
	{
		return sabmCustomerFacade.getCustomerAccountCreditLimit();
	}

	/**
	 * check access type for payer
	 *
	 * @return boolean
	 */
	@ResponseBody
	@GetMapping(value = "/validateForPayerAccess", produces = "application/json")
	public boolean validateForPayerAccess(@RequestParam("code") final String code)
	{
		return sabmCustomerFacade.requestOrderORPayAccess(code);
	}
	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model,asahiSiteUtil.isSga()? getPageTitleResolver().resolveContentPageTitle(cmsPage.getTitle()) : getPageTitleResolver().resolveHomePageTitle(cmsPage.getTitle()));
	}
	
	@GetMapping("/access")
	public String grantAccess(@RequestParam(value = "code", defaultValue = "false") final String code,
			@RequestParam(value = "type", defaultValue = "false") final String type,
			final Model model,final RedirectAttributes redirectModel,final HttpServletRequest request,HttpServletResponse response) throws CMSItemNotFoundException
	{
		String access = sabmCustomerFacade.approveORRejectPayAccess(type, code);
		redirectModel.addFlashAttribute("accessMessege", access != null ? access : "error");
		storeCmsPageInModel(model, getContentPageForLabelOrId(null));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));

		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		updatePageTitle(model, getContentPageForLabelOrId(null));
		return REDIRECT_PREFIX + "/";
	}
	
	@GetMapping("/requestAccess")
	public String grantAccess(@RequestParam(value = "code", defaultValue = "false") final String code,
			final Model model,final RedirectAttributes redirectModel,final HttpServletRequest request,
			HttpServletResponse response) throws CMSItemNotFoundException
	{

		final UserModel user = this.userService.getCurrentUser();
		if(userService.isAnonymousUser(user)){
			return REDIRECT_PREFIX + "/";
		}
		
		model.addAttribute("accessType", code);
		if(user instanceof B2BCustomerModel){
			B2BCustomerModel customer = (B2BCustomerModel)user;
			final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel) customer.getDefaultB2BUnit();
			if(null!=b2bUnit.getPayerAccount()){
				model.addAttribute("emailID", b2bUnit.getPayerAccount().getEmailAddress());
				model.addAttribute("tradingName", b2bUnit.getPayerAccount().getLocName());
			}
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ASAHI_SAM_ACCESS_REQUEST_PAGE_ID));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ASAHI_SAM_ACCESS_REQUEST_PAGE_ID));
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(ASAHI_SAM_ACCESS_REQUEST_PAGE_ID)).getBackgroundImage().getURL());
		}
		
		model.addAttribute(BREADCRUMBS_ATTR,
				accountBreadcrumbBuilder.getBreadcrumbs(asahiConfigurationService.getString(SAM_PAY_ACCESS_BREADCRUMB, "Payer Access Request Confirmation")));
		updatePageTitle(model, getContentPageForLabelOrId(ASAHI_SAM_ACCESS_REQUEST_PAGE_ID));
		return getViewForPage(model);
	}

	@PostMapping("/cc-info-popup/disable")
	public @ResponseBody void saveSgaUserResponseOnPopup(){
        B2BCustomerModel customer = asahiCoreUtil.getCurrentB2BCustomer();
        if(null != customer){
            try {
                customer.setShowCCInfoPopup(false);
                Consumer<B2BCustomerModel> consumer = asahiCoreUtil::updateB2BCustomer;
                consumer.accept(customer);
            }catch (ModelSavingException mse) {
                LOGGER.error("Could not save model while tried to set popup false", mse);
            }
        }
    }
	
	
	protected List<AsahiDealData> getAsahiDeals()
	{
		if(!asahiCoreUtil.isNAPUser()) {
			B2BCustomerModel customer = asahiCoreUtil.getCurrentB2BCustomer();
			List<AsahiDealData> customerDealProducts = new ArrayList<AsahiDealData>();
			if(null != customer && customer.getDefaultB2BUnit() instanceof AsahiB2BUnitModel){
				final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel) customer.getDefaultB2BUnit();
				customerDealProducts= dealsSearchFacade.getCustomerSpecificDeals(b2bUnit);
			}
			return customerDealProducts;
		} else {
			return null;
		}
	}
}
