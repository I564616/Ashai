package com.apb.storefront.controllers.pages;

import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.acceleratorservices.storefront.data.MetaElementData;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.strategies.B2BUserGroupsLookUpStrategy;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.UserModel;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.product.AsahiRecommendationFacade;
import com.apb.storefront.constant.ApbStoreFrontContants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import org.springframework.web.bind.annotation.ModelAttribute;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;


public class ApbAbstractPageController extends AbstractSearchPageController
{
	private static final Logger LOG = LoggerFactory.getLogger(ApbAbstractPageController.class);
	
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "b2bUserGroupsLookUpStrategy")
	private B2BUserGroupsLookUpStrategy b2bUserGroupsLookUpStrategy;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;
	
	@Resource(name = "asahiRecommendationFacade")
	private AsahiRecommendationFacade asahiRecommendationFacade;
	
	@Resource(name = "asahiSiteUtil")
	private AsahiSiteUtil asahiSiteUtil;


	private static final String SHOW_BASE_PRICE_ON_PLP_FOR_SITE = "plp.show.base.price.";
	

	@Override
	protected MetaElementData createMetaElement(final String name, final String content)
	{
		final MetaElementData element = new MetaElementData();
		element.setName(name);
		element.setContent(content);
		return element;
	}

	@Override
	protected void setUpMetaDataForContentPage(final Model model, final ContentPageModel contentPage)
	{
		setUpMetaData(model, contentPage.getKeywords(), contentPage.getDescription());
		addBackgroundImage(model, contentPage);
		model.addAttribute("showB2BUnitDropDown", this.asahiConfigurationService.getString(
				ApbStoreFrontContants.HEADER_B2BUNIT_DROPDOWN_SHOW_APB + getCmsSiteService().getCurrentSite().getUid(), "false"));
		model.addAttribute(ApbStoreFrontContants.EXIST_SUPERUSER, checkSuperUser());
		setMaximunSize(model);
		setShowBasePriceFlag(model);
	}


	private void addBackgroundImage(final Model model, final ContentPageModel contentPage)
	{
		if (contentPage.getBackgroundImage() != null)
		{
			model.addAttribute("media", contentPage.getBackgroundImage().getURL());
		}
	}

	/**
	 * @param model
	 * @return
	 *//*
	   * public void checkSuperUser(final Model model) { final UserModel userModel = userService.getCurrentUser(); if
	   * (userModel instanceof B2BCustomerModel) { userModel.getGroups().forEach(entry -> { if
	   * (CollectionUtils.isNotEmpty(entry.getGroups()) && entry.getUid().equals(ApbStoreFrontContants.B2B_ADMIN_GROUP))
	   * { model.addAttribute(ApbStoreFrontContants.EXIST_SUPERUSER, Boolean.TRUE); } }); } }
	   */

	/**
	 * @return
	 */
	public boolean checkSuperUser()
	{
		/*final UserModel userModel = userService.getCurrentUser();
		if (userModel instanceof B2BCustomerModel)
		{
			return userModel.getGroups().stream().anyMatch(entry -> CollectionUtils.isNotEmpty(entry.getGroups())
					&& entry.getUid().equals(ApbStoreFrontContants.B2B_ADMIN_GROUP));
		}
		return false;*/
		return asahiCoreUtil.adminRightExists();
	}

	@Override
	protected RequestContextData getRequestContextData(final HttpServletRequest request)
	{
		return getBean(request, "requestContextData", RequestContextData.class);
	}

	/**
	 * @param model
	 */
	public void setMaximunSize(final Model model)
	{
		model.addAttribute("inputMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.COMPANY_INPUT_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "75"));
		model.addAttribute("phoneMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.PHONE_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "15"));
		model.addAttribute("abnMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.COMPANY_ABN_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "15"));
		model.addAttribute("delInstMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.COMPANY__DEL_INST_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "100"));
		model.addAttribute("emailMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.EMAIL_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "255"));
		model.addAttribute("llMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.COMPANY_LL_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "255"));
		model.addAttribute("calMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.CALENDAR_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "60"));
		model.addAttribute("otherMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.OTHER_SUBJECT_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "25"));
		final String pdfUploadSize = asahiConfigurationService.getString(
				ApbStoreFrontContants.IMPORT_PDF_FILE_MAX_SIZE_BYTES_KEY + getCmsSiteService().getCurrentSite().getUid(), "0");
		model.addAttribute("pdfFileMaxSize", Long.parseLong(pdfUploadSize));
		final String furtherMaxSize = asahiConfigurationService
				.getString(ApbStoreFrontContants.FURTHER_DETAIL_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "2000");
		model.addAttribute("furtherDetailsMaxSize", Long.parseLong(furtherMaxSize));
		// ContactUs Page
		model.addAttribute("inputACCNoMaxSize", asahiConfigurationService.getString(
				ApbStoreFrontContants.ACCOUNT_INPUT_ACC_NO_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "75"));
		model.addAttribute("inputCompNameMaxSize", asahiConfigurationService.getString(
				ApbStoreFrontContants.ACCOUNT_INPUT_COMP_NAME_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "15"));
		model.addAttribute("inputNameMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.ACCOUNT_INPUT_NAME_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "50"));
		model.addAttribute("inputAddressMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.ADDRESS_INPUT_NAME_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "50"));
		model.addAttribute("inputCommentsMaxSize", asahiConfigurationService.getString(
				ApbStoreFrontContants.KEGRETURN_COMMENTS_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "200"));
	}


	public void setShowBasePriceFlag(final Model model)
	{
		model.addAttribute("showBasePrice", asahiConfigurationService
				.getString(SHOW_BASE_PRICE_ON_PLP_FOR_SITE + getCmsSiteService().getCurrentSite().getUid(), "false"));
	}
	
	protected void storeCmsPageInModel(final Model model, final AbstractPageModel cmsPage)
	{
		if (model != null && cmsPage != null)
		{
			model.addAttribute(CMS_PAGE_MODEL, cmsPage);
			if (cmsPage instanceof ContentPageModel)
			{
				storeContentPageTitleInModel(model, getPageTitleResolver().resolveContentPageTitle(cmsPage.getTitle()));
			}
		}
		
		if(asahiSiteUtil.isSga())
		{
			model.addAttribute("isNAPGroup",asahiCoreUtil.isNAPUser());
			
			final boolean isApprovalPending = asahiCoreUtil.isSAMAccessApprovalPending();
			final String accessType = asahiCoreUtil.getCurrentUserAccessType();
			model.addAttribute("isApprovalPending",isApprovalPending);
			model.addAttribute("isAccessDenied",asahiCoreUtil.isSAMAccessDenied());
			
			model.addAttribute("sgaAccessType",accessType);
			if(isApprovalPending)
			{
				model.addAttribute("approvalEmailId",null != asahiCoreUtil.getDefaultB2BUnit() 
						&& null != asahiCoreUtil.getDefaultB2BUnit().getPayerAccount()?
						asahiCoreUtil.getDefaultB2BUnit().getPayerAccount().getEmailAddress(): StringUtils.EMPTY);
			}
		}
	}
	
	@ModelAttribute("recommendationsCount")
	public String getRepRecommendedProductCount()
	{
		if (asahiSiteUtil.isBDECustomer()) {
			Integer totalProductCount = asahiRecommendationFacade.getTotalRepRecommendedProducts();
			return String.valueOf(totalProductCount);
		}
		return StringUtils.EMPTY;
	}
	
	@Override
	@ModelAttribute("user")
	public CustomerData getUser()
	{
		try
		{
			return getCustomerFacade().getCurrentCustomer();
		}
		catch(Exception ex)
		{
			LOG.error("Error getting the current customer information in ApbAbstractPageController");
		}
		return null;
	}
}
