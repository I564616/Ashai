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


import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.commercefacades.product.data.AsahiSearchProductData;
import de.hybris.platform.commercefacades.product.data.AsahiSearchResultsData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.FacetRefinement;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.apb.core.constants.ApbCoreConstants;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.user.ApbUserFacade;
import com.apb.storefront.constant.ApbStoreFrontContants;


/**
 * Controller for a category page
 */
@Controller
@RequestMapping(value = "/{path:.*}/c")
public class CategoryPageController extends AsahiAbstractCategoryPageController
{

	@Resource(name = "asahiSearchProductConverter")
	private Converter<ProductData, AsahiSearchProductData> asahiSearchProductConverter;

	@Resource(name = "apbUserFacade")
	private ApbUserFacade userFacade;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource(name = "userService")
	private UserService userService;
	
	@Resource(name = "assistedServiceFacade")
	private AssistedServiceFacade assistedServiceFacade;

	private static final String SHOW_BASE_PRICE_ON_PLP_FOR_SITE = "plp.show.base.price.";

	private static final String GET_PLP_PRICE_FROM_EXTERNAL_SYSTEM = "fetch.plp.price.from.service";

	private static final String DISPLAY_TEXT_WHEN_NO_PRICE_FETCHED = "display.text.plp.service.no.price";

	private static final Logger LOGGER = LoggerFactory.getLogger("CategoryPageController");



	@GetMapping(CATEGORY_CODE_PATH_VARIABLE_PATTERN)
	public String category(@PathVariable("categoryCode") final String categoryCode, // NOSONAR
			@RequestParam(value = "q", required = false) final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final Model model,
			final HttpServletRequest request, final HttpServletResponse response) throws UnsupportedEncodingException
	{
		//setting below attribute for Clear All functionality
		model.addAttribute("searchQuery", searchQuery);
		model.addAttribute("clearAllUrl", request.getRequestURL());
		
		model.addAttribute("isNAPGroup",asahiCoreUtil.isNAPUser());
		
		final boolean isApprovalPending = asahiCoreUtil.isSAMAccessApprovalPending();
		final String accessType = asahiCoreUtil.getCurrentUserAccessType();
		model.addAttribute("isApprovalPending",isApprovalPending);
		if(isApprovalPending)
		{
			model.addAttribute("approvalEmailId",null != asahiCoreUtil.getDefaultB2BUnit() 
					&& null != asahiCoreUtil.getDefaultB2BUnit().getPayerAccount()?
					asahiCoreUtil.getDefaultB2BUnit().getPayerAccount().getEmailAddress(): StringUtils.EMPTY);
		}
		model.addAttribute("isAccessDenied",asahiCoreUtil.isSAMAccessDenied());
		
		model.addAttribute("sgaAccessType",accessType);

		final CategoryModel category = getCommerceCategoryService().getCategoryForCode(categoryCode);

		final CategoryPageModel categoryPage = getCategoryPage(category);
		
		if(asahiSiteUtil.isApb() && assistedServiceFacade.isAssistedServiceModeLaunched()){
			if (null != assistedServiceFacade.getAsmSession().getAgent()
					&& assistedServiceFacade.getAsmSession().getAgent().getAllGroups().stream().anyMatch(groupItem -> groupItem.getUid().equals(ApbStoreFrontContants.ASAHISALESREP))) {
				model.addAttribute("asmMode", true);
			} else {
				model.addAttribute("asmMode", false);
			}
		}else{
			model.addAttribute("asmMode", false);
		}
		
		if (asahiSiteUtil.isBDECustomer()) {
			model.addAttribute("isBdeUser", true);
		}

		if ((categoryPage).getBackgroundImage() != null)
		{
			model.addAttribute("media", (categoryPage).getBackgroundImage().getURL());
		}

		showPriceOnListingPage(model);
		model.addAttribute("accessType",accessType);
		if (isLicenseRequired() && !asahiSiteUtil.isSga())
		{
			if (checkSuperUser())
			{
				GlobalMessages.addErrorMessage(model, "pdp.plp.liquore.license.required.super.user.msg");
			}
			else
			{
				GlobalMessages.addErrorMessage(model, "pdp.plp.liquore.license.required.msg");
			}
		}
		return performSearchAndGetResultsPage(categoryCode, searchQuery, page, showMode, sortCode, model, request, response);
	}

	@ResponseBody
	@GetMapping(CATEGORY_CODE_PATH_VARIABLE_PATTERN + "/facets")
	public FacetRefinement<SearchStateData> getFacets(@PathVariable("categoryCode") final String categoryCode,
			@RequestParam(value = "q", required = false) final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode) throws UnsupportedEncodingException
	{
		return performSearchAndGetFacets(categoryCode, searchQuery, page, showMode, sortCode);
	}

	@ResponseBody
	@GetMapping(CATEGORY_CODE_PATH_VARIABLE_PATTERN + "/results")
	public AsahiSearchResultsData getResults(@PathVariable("categoryCode") final String categoryCode,
			@RequestParam(value = "q", required = false) final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode) throws UnsupportedEncodingException
	{
		/*
		 * In case user comes back from checkout page without making payment
		 */
		asahiCoreUtil.removeSessionCheckoutFlag();

		return getAsahiSearchResultsData(performSearchAndGetResultsData(categoryCode, searchQuery, page, showMode, sortCode));

	}

	private boolean isLicenseRequired()
	{
		boolean isLicenseRequired = false;
		final UserModel user = userFacade.getCurrentUser();
		if (null != user && user instanceof B2BCustomerModel && !userFacade.isAnonymousUser())
		{
			final B2BCustomerModel customer = (B2BCustomerModel) user;
			final B2BUnitModel b2bUnit = customer.getDefaultB2BUnit();
			if (b2bUnit instanceof AsahiB2BUnitModel)
			{
				final AsahiB2BUnitModel asahiB2BUnitModel = (AsahiB2BUnitModel) b2bUnit;
				if (StringUtils.isEmpty(asahiB2BUnitModel.getLiquorLicensenumber()))
				{
					isLicenseRequired = true;
				}

			}
		}
		return isLicenseRequired;
	}

	private AsahiSearchResultsData getAsahiSearchResultsData(final SearchResultsData<ProductData> searchResultsData)
	{

		final AsahiSearchResultsData asahiSearchResultsData = new AsahiSearchResultsData();
		final List<ProductData> productDatas = searchResultsData.getResults();
		final PaginationData paginationData = searchResultsData.getPagination();
		List<AsahiSearchProductData> asahiSearchProductDatas;
		if (CollectionUtils.isNotEmpty(productDatas))
		{
			asahiSearchProductDatas = new ArrayList<>();
			for (final ProductData productData : productDatas)
			{
				asahiSearchProductDatas.add(asahiSearchProductConverter.convert(productData));
			}
			asahiSearchResultsData.setResults(asahiSearchProductDatas);
		}
		if (null != paginationData)
		{
			asahiSearchResultsData.setPagination(paginationData);
		}

		asahiSearchResultsData.setIsAnonymousUser(userFacade.isAnonymousUser());
		final String accessType = asahiCoreUtil.getCurrentUserAccessType();
		if (asahiSiteUtil.isSga() && accessType.equalsIgnoreCase(ApbCoreConstants.PAY_ACCESS) ) {
			asahiSearchResultsData.setIsAnonymousUser(true);
		}
		
		LOGGER.info("asahiSearchResultsData " + asahiSearchResultsData);

		return asahiSearchResultsData;
	}

	private void showPriceOnListingPage(final Model model)
	{
		model.addAttribute("showBasePrice", asahiConfigurationService
				.getString(SHOW_BASE_PRICE_ON_PLP_FOR_SITE + getCmsSiteService().getCurrentSite().getUid(), "false"));
		model.addAttribute("isPriceFetch",
				Boolean.parseBoolean(asahiConfigurationService.getString(GET_PLP_PRICE_FROM_EXTERNAL_SYSTEM, "true")));
		model.addAttribute("textForNoPrice",
				asahiSiteUtil.isSga() ? asahiConfigurationService.getString(ApbStoreFrontContants.PRICE_ERROR_CODE, "NA")
						: asahiConfigurationService.getString(DISPLAY_TEXT_WHEN_NO_PRICE_FETCHED, "----"));
	}

	public boolean checkSuperUser()
	{
		final UserModel userModel = userFacade.getCurrentUser();
		if (userModel instanceof B2BCustomerModel)
		{
			return userModel.getGroups().stream().anyMatch(entry -> CollectionUtils.isNotEmpty(entry.getGroups())
					&& entry.getUid().equals(ApbStoreFrontContants.B2B_ADMIN_GROUP));
		}
		return false;
	}
}
