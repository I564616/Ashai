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

import de.hybris.platform.acceleratorcms.model.components.SearchBoxComponentModel;
import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorservices.customer.CustomerLocationService;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.SearchBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.util.MetaSanitizerUtil;
import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.commercefacades.product.data.AsahiSearchProductData;
import de.hybris.platform.commercefacades.product.data.AsahiSearchResultsData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.AutocompleteResultData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetRefinement;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

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
import com.sap.security.core.server.csi.XSSEncoder;


@Controller
@RequestMapping("/search")
public class SearchPageController extends AbstractSearchPageController
{
	private static final String SEARCH_META_DESCRIPTION_ON = "search.meta.description.on";
	private static final String SEARCH_META_DESCRIPTION_RESULTS = "search.meta.description.results";
	private static final String HEADER_B2BUNIT_DROPDOWN_SHOW_APB = "header.b2bunit.dropdown.show.apb";

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(SearchPageController.class);

	private static final String COMPONENT_UID_PATH_VARIABLE_PATTERN = "{componentUid:.*}";
	private static final String FACET_SEPARATOR = ":";

	private static final String SEARCH_CMS_PAGE_ID = "search";
	private static final String NO_RESULTS_CMS_PAGE_ID = "searchEmpty";

	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> productSearchFacade;

	@Resource(name = "searchBreadcrumbBuilder")
	private SearchBreadcrumbBuilder searchBreadcrumbBuilder;

	@Resource(name = "customerLocationService")
	private CustomerLocationService customerLocationService;

	@Resource(name = "cmsComponentService")
	private CMSComponentService cmsComponentService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "asahiSearchProductConverter")
	private Converter<ProductData, AsahiSearchProductData> asahiSearchProductConverter;

	@Resource(name = "apbUserFacade")
	private ApbUserFacade userFacade;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource(name = "assistedServiceFacade")
	private AssistedServiceFacade assistedServiceFacade;

	private static final String SHOW_BASE_PRICE_ON_PLP_FOR_SITE = "plp.show.base.price.";

	private static final String GET_PLP_PRICE_FROM_EXTERNAL_SYSTEM = "fetch.plp.price.from.service";


	@GetMapping( params = "!q")
	public String textSearch(@RequestParam(value = "text", defaultValue = "") final String searchText,
			final HttpServletRequest request, final Model model) throws CMSItemNotFoundException
	{
		/*
		 * In case user comes back from checkout page without making payment
		 */
		asahiCoreUtil.removeSessionCheckoutFlag();

		if (StringUtils.isNotBlank(searchText))
		{
			final PageableData pageableData = createPageableData(0, getSearchPageSize(), null, ShowMode.Page);

			final SearchStateData searchState = new SearchStateData();
			final SearchQueryData searchQueryData = new SearchQueryData();
			searchQueryData.setValue(searchText);
			searchState.setQuery(searchQueryData);

			ProductSearchPageData<SearchStateData, ProductData> searchPageData = null;
			try
			{
				searchPageData = encodeSearchPageData(productSearchFacade.textSearch(searchState, pageableData));
			}
			catch (final ConversionException e) // NOSONAR
			{
				// nothing to do - the exception is logged in SearchSolrQueryPopulator
			}

			if (searchPageData == null)
			{
				storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
				if ((getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID)).getBackgroundImage() != null)
				{
					model.addAttribute("media", (getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID)).getBackgroundImage().getURL());
				}

			}
			else if (searchPageData.getKeywordRedirectUrl() != null)
			{
				// if the search engine returns a redirect, just
				return "redirect:" + searchPageData.getKeywordRedirectUrl();
			}
			else if (searchPageData.getPagination().getTotalNumberOfResults() == 0)
			{
				model.addAttribute("searchPageData", searchPageData);
				storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
				if ((getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID)).getBackgroundImage() != null)
				{
					model.addAttribute("media", (getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID)).getBackgroundImage().getURL());
				}


				updatePageTitle(searchText, model);
			}
			else
			{
				storeContinueUrl(request);
				populateModel(model, searchPageData, ShowMode.Page);
				storeCmsPageInModel(model, getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID));
				if ((getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID)).getBackgroundImage() != null)
				{
					model.addAttribute("media", (getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID)).getBackgroundImage().getURL());
				}

				updatePageTitle(searchText, model);
			}
			model.addAttribute("userLocation", customerLocationService.getUserLocation());
			getRequestContextData(request).setSearch(searchPageData);
			if (searchPageData != null)
			{
				model.addAttribute(WebConstants.BREADCRUMBS_KEY, searchBreadcrumbBuilder.getBreadcrumbs(null, searchText,
						CollectionUtils.isEmpty(searchPageData.getBreadcrumbs())));
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
			}
		}
		else
		{
			storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
			if ((getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID)).getBackgroundImage() != null)
			{
				model.addAttribute("media", (getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID)).getBackgroundImage().getURL());
			}

		}
		model.addAttribute("pageType", PageType.PRODUCTSEARCH.name());
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_FOLLOW);
		showPriceOnListingPage(model);
		final String metaDescription = MetaSanitizerUtil
				.sanitizeDescription(getMessageSource().getMessage(SEARCH_META_DESCRIPTION_RESULTS, null,
						SEARCH_META_DESCRIPTION_RESULTS, getI18nService().getCurrentLocale()) + " " + searchText + " "
						+ getMessageSource().getMessage(SEARCH_META_DESCRIPTION_ON, null, SEARCH_META_DESCRIPTION_ON,
								getI18nService().getCurrentLocale())
						+ " " + getSiteName());
		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(searchText);
		setUpMetaData(model, metaKeywords, metaDescription);
		model.addAttribute("showB2BUnitDropDown",
				this.asahiConfigurationService.getString(HEADER_B2BUNIT_DROPDOWN_SHOW_APB, "false"));

		if (asahiSiteUtil.isApb() && assistedServiceFacade.isAssistedServiceModeLaunched())
		{
			if (null != assistedServiceFacade.getAsmSession().getAgent()
					&& assistedServiceFacade.getAsmSession().getAgent().getAllGroups().stream().anyMatch(groupItem -> groupItem.getUid().equals(ApbStoreFrontContants.ASAHISALESREP))) {
				model.addAttribute("asmMode", true);
			} else {
				model.addAttribute("asmMode", false);
			}
		}
		else
		{
			model.addAttribute("asmMode", false);
		}
		
		if (asahiSiteUtil.isBDECustomer()) {
			model.addAttribute("isBdeUser", true);
		}
		setAdditionalInfoForSga(model);

		return getViewForPage(model);
	}

	@GetMapping( params = "q")
	public String refineSearch(@RequestParam("q") final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode,
			@RequestParam(value = "text", required = false) final String searchText, final HttpServletRequest request,
			final Model model) throws CMSItemNotFoundException
	{
		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = performSearch(searchQuery, page, showMode,
				sortCode, getSearchPageSize());

		populateModel(model, searchPageData, showMode);
		model.addAttribute("userLocation", customerLocationService.getUserLocation());

		if (searchPageData.getPagination().getTotalNumberOfResults() == 0)
		{
			updatePageTitle(searchPageData.getFreeTextSearch(), model);
			storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
			if ((getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID)).getBackgroundImage() != null)
			{
				model.addAttribute("media", (getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID)).getBackgroundImage().getURL());
			}

		}
		else
		{
			storeContinueUrl(request);
			updatePageTitle(searchPageData.getFreeTextSearch(), model);
			storeCmsPageInModel(model, getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID));
			if ((getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID)).getBackgroundImage() != null)
			{
				model.addAttribute("media", (getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID)).getBackgroundImage().getURL());
			}


		}
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, searchBreadcrumbBuilder.getBreadcrumbs(null, searchPageData));
		model.addAttribute("pageType", PageType.PRODUCTSEARCH.name());

		final String metaDescription = MetaSanitizerUtil
				.sanitizeDescription(getMessageSource().getMessage(SEARCH_META_DESCRIPTION_RESULTS, null,
						SEARCH_META_DESCRIPTION_RESULTS, getI18nService().getCurrentLocale()) + " " + searchText + " "
						+ getMessageSource().getMessage(SEARCH_META_DESCRIPTION_ON, null, SEARCH_META_DESCRIPTION_ON,
								getI18nService().getCurrentLocale())
						+ " " + getSiteName());

		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(searchText);
		setUpMetaData(model, metaKeywords, metaDescription);
		showPriceOnListingPage(model);

		//setting below attribute for Clear All functionality
		model.addAttribute("searchQuery", searchQuery);
		model.addAttribute("clearAllUrl", this.generateClearAllUrl(request));
		model.addAttribute("accessType",asahiCoreUtil.getCurrentUserAccessType());
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
		
		if (asahiSiteUtil.isBDECustomer()) {
			model.addAttribute("isBdeUser", true);
		}
		
		setAdditionalInfoForSga(model);
		return getViewForPage(model);
	}

	/**
	 * Generate clear all url.
	 *
	 * @param request
	 *           the request
	 * @return the object
	 */
	private Object generateClearAllUrl(final HttpServletRequest request)
	{
		final StringBuffer clearAllUrl = new StringBuffer(request.getRequestURL());
		if (null != request.getQueryString())
		{
			final String[] queryString = request.getQueryString().split("q=");
			if (queryString.length > 1)
			{
				clearAllUrl.append("?q=").append(queryString[1].split("%")[0]);
			}
		}
		return clearAllUrl;
	}

	protected ProductSearchPageData<SearchStateData, ProductData> performSearch(final String searchQuery, final int page,
			final ShowMode showMode, final String sortCode, final int pageSize)
	{
		final PageableData pageableData = createPageableData(page, pageSize, sortCode, showMode);

		final SearchStateData searchState = new SearchStateData();
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(searchQuery);
		searchState.setQuery(searchQueryData);

		return encodeSearchPageData(productSearchFacade.textSearch(searchState, pageableData));
	}

	@ResponseBody
	@GetMapping("/results")
	public AsahiSearchResultsData jsonSearchResults(@RequestParam("q") final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode) throws CMSItemNotFoundException
	{
		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = performSearch(searchQuery, page, showMode,
				sortCode, getSearchPageSize());

		return getAsahiSearchResultsData(searchPageData);

	}

	private AsahiSearchResultsData getAsahiSearchResultsData(
			final ProductSearchPageData<SearchStateData, ProductData> searchPageData)
	{
		final List<ProductData> productDatas = searchPageData.getResults();
		final PaginationData paginationData = searchPageData.getPagination();
		final AsahiSearchResultsData asahiSearchResultsData = new AsahiSearchResultsData();
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
		
		return asahiSearchResultsData;
	}

	@ResponseBody
	@GetMapping("/facets")
	public FacetRefinement<SearchStateData> getFacets(@RequestParam("q") final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode) throws CMSItemNotFoundException
	{
		final SearchStateData searchState = new SearchStateData();
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(searchQuery);
		searchState.setQuery(searchQueryData);

		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = productSearchFacade.textSearch(searchState,
				createPageableData(page, getSearchPageSize(), sortCode, showMode));
		final List<FacetData<SearchStateData>> facets = refineFacets(searchPageData.getFacets(),
				convertBreadcrumbsToFacets(searchPageData.getBreadcrumbs()));
		final FacetRefinement<SearchStateData> refinement = new FacetRefinement<>();
		refinement.setFacets(facets);
		refinement.setCount(searchPageData.getPagination().getTotalNumberOfResults());
		refinement.setBreadcrumbs(searchPageData.getBreadcrumbs());
		return refinement;
	}

	@ResponseBody
	@GetMapping("/autocomplete/" + COMPONENT_UID_PATH_VARIABLE_PATTERN)
	public AutocompleteResultData getAutocompleteSuggestions(@PathVariable final String componentUid,
			@RequestParam("term") final String term) throws CMSItemNotFoundException
	{
		final AutocompleteResultData resultData = new AutocompleteResultData();

		final SearchBoxComponentModel component = (SearchBoxComponentModel) cmsComponentService.getSimpleCMSComponent(componentUid);

		if (component.isDisplaySuggestions())
		{
			resultData.setSuggestions(subList(productSearchFacade.getAutocompleteSuggestions(term), component.getMaxSuggestions()));
		}

		if (component.isDisplayProducts())
		{
			resultData.setProducts(subList(productSearchFacade.textSearch(term, SearchQueryContext.SUGGESTIONS).getResults(),
					component.getMaxProducts()));
		}

		return resultData;
	}

	protected <E> List<E> subList(final List<E> list, final int maxElements)
	{
		if (CollectionUtils.isEmpty(list))
		{
			return Collections.emptyList();
		}

		if (list.size() > maxElements)
		{
			return list.subList(0, maxElements);
		}

		return list;
	}

	protected void updatePageTitle(final String searchText, final Model model)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveContentPageTitle(
				getMessageSource().getMessage("search.meta.title", null, "search.meta.title", getI18nService().getCurrentLocale())
						+ " " + searchText));
	}


	protected ProductSearchPageData<SearchStateData, ProductData> encodeSearchPageData(
			final ProductSearchPageData<SearchStateData, ProductData> searchPageData)
	{
		final SearchStateData currentQuery = searchPageData.getCurrentQuery();

		if (currentQuery != null)
		{
			try
			{
				final SearchQueryData query = currentQuery.getQuery();
				final String encodedQueryValue = XSSEncoder.encodeHTML(query.getValue());
				query.setValue(encodedQueryValue);
				currentQuery.setQuery(query);
				searchPageData.setCurrentQuery(currentQuery);
				searchPageData.setFreeTextSearch(XSSEncoder.encodeHTML(searchPageData.getFreeTextSearch()));

				final List<FacetData<SearchStateData>> facets = searchPageData.getFacets();
				if (CollectionUtils.isNotEmpty(facets))
				{
					processFacetData(facets);
				}
			}
			catch (final UnsupportedEncodingException e)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Error occured during Encoding the Search Page data values", e);
				}
			}
		}
		return searchPageData;
	}

	protected void processFacetData(final List<FacetData<SearchStateData>> facets) throws UnsupportedEncodingException
	{
		for (final FacetData<SearchStateData> facetData : facets)
		{
			final List<FacetValueData<SearchStateData>> topFacetValueDatas = facetData.getTopValues();
			if (CollectionUtils.isNotEmpty(topFacetValueDatas))
			{
				processFacetDatas(topFacetValueDatas);
			}
			final List<FacetValueData<SearchStateData>> facetValueDatas = facetData.getValues();
			if (CollectionUtils.isNotEmpty(facetValueDatas))
			{
				processFacetDatas(facetValueDatas);
			}
		}
	}

	protected void processFacetDatas(final List<FacetValueData<SearchStateData>> facetValueDatas)
			throws UnsupportedEncodingException
	{
		for (final FacetValueData<SearchStateData> facetValueData : facetValueDatas)
		{
			final SearchStateData facetQuery = facetValueData.getQuery();
			final SearchQueryData queryData = facetQuery.getQuery();
			final String queryValue = queryData.getValue();
			if (StringUtils.isNotBlank(queryValue))
			{
				final String[] queryValues = queryValue.split(FACET_SEPARATOR);
				final StringBuilder queryValueBuilder = new StringBuilder();
				queryValueBuilder.append(XSSEncoder.encodeHTML(queryValues[0]));
				for (int i = 1; i < queryValues.length; i++)
				{
					queryValueBuilder.append(FACET_SEPARATOR).append(queryValues[i]);
				}
				queryData.setValue(queryValueBuilder.toString());
			}
		}
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

	private void showPriceOnListingPage(final Model model)
	{
		model.addAttribute("showBasePrice", asahiConfigurationService
				.getString(SHOW_BASE_PRICE_ON_PLP_FOR_SITE + getCmsSiteService().getCurrentSite().getUid(), "false"));
		model.addAttribute("isPriceFetch",
				Boolean.parseBoolean(asahiConfigurationService.getString(GET_PLP_PRICE_FROM_EXTERNAL_SYSTEM, "true")));
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
	
	private void setAdditionalInfoForSga(final Model model)
	{
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
}
