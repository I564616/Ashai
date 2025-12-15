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


import com.sabmiller.core.b2b.services.SABMProductExclusionService;
import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCategoryPageController;
import de.hybris.platform.acceleratorstorefrontcommons.util.MetaSanitizerUtil;
import de.hybris.platform.catalog.model.KeywordModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.FacetRefinement;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sabmiller.core.util.SabmUtils;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.storefront.filters.XSSFilterUtil;
import com.sabmiller.storefront.controllers.pages.SabmAbstractCategoryPageController;


/**
 * Controller for a category page
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/{path:.*}/c")
public class CategoryPageController extends SabmAbstractCategoryPageController
{
	protected static final Logger LOG = Logger.getLogger(CategoryPageController.class);

	@Resource(name = "categoryConverter")
	private Converter<CategoryModel, CategoryData> categoryConverter;

	@Resource(name = "sabmProductExclusionService")
	private SABMProductExclusionService sabmProductExclusionService;

	@GetMapping(CATEGORY_CODE_PATH_VARIABLE_PATTERN)
	public String category(@PathVariable("categoryCode") String categoryCode,
			@RequestParam(value = "q", required = false) String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) String sortCode, final Model model, final HttpServletRequest request,
			final HttpServletResponse response) throws UnsupportedEncodingException
	{
		categoryCode = XSSFilterUtil.filter(categoryCode);
		searchQuery = XSSFilterUtil.filter(searchQuery);
		sortCode = XSSFilterUtil.filter(sortCode);

		final String result = performSearchAndGetResultsPage(categoryCode, searchQuery, page, showMode, sortCode, model, request,
				response);
		final RequestContextData requestContextData = getRequestContextData(request);
		if (requestContextData.getCategory() != null)
		{

			final CategoryData categoryData = categoryConverter.convert(requestContextData.getCategory());
			model.addAttribute("categoryData", categoryData);

		}

		return result;
	}

	/**
	 * overriding the AbstractCategoryPageController.performSearchAndGetResultsPage method get rid of the
	 * XSSFilterUtil.filter
	 *
	 */
	@Override
	protected String performSearchAndGetResultsPage(final String categoryCode, final String searchQuery, final int page,
			final ShowMode showMode, final String sortCode, final Model model, final HttpServletRequest request,
			final HttpServletResponse response) throws UnsupportedEncodingException
	{
		final CategoryModel category = getCommerceCategoryService().getCategoryForCode(categoryCode);

		final String redirection = checkRequestUrl(request, response, getCategoryModelUrlResolver().resolve(category));
		if (StringUtils.isNotEmpty(redirection))
		{
			return redirection;
		}

		final CategoryPageModel categoryPage = getCategoryPage(category);

		final String modifiedSearchQuery = (searchQuery == null) ? StringUtils.EMPTY : searchQuery;

		final CategorySearchEvaluator categorySearch = new CategorySearchEvaluator(categoryCode, modifiedSearchQuery, page,
				showMode, sortCode, categoryPage);

		sabmProductExclusionService.getAndSetSessionEanProductExclusion();
		categorySearch.doSearch();

		final ProductCategorySearchPageData<SearchStateData, ProductData, CategoryData> searchPageData = categorySearch
				.getSearchPageData();
		final boolean showCategoriesOnly = categorySearch.isShowCategoriesOnly();

		storeCmsPageInModel(model, categorySearch.getCategoryPage());
		storeContinueUrl(request);

		populateModel(model, searchPageData, showMode);
		List<Breadcrumb> breadCrumbs = getSearchBreadcrumbBuilder().getBreadcrumbs(categoryCode, searchPageData);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, breadCrumbs);
		model.addAttribute("showCategoriesOnly", Boolean.valueOf(showCategoriesOnly));
		model.addAttribute("categoryName", category.getName());
		//model.addAttribute("pageType", PageType.Category);
		model.addAttribute("pageType", PageType.CATEGORY.name());
		model.addAttribute("userLocation", getCustomerLocationService().getUserLocation());
		model.addAttribute("requestOrigin", SabmUtils.getRequestOrigin(CollectionUtils.isEmpty(breadCrumbs) ? null : breadCrumbs.get(0).getUrl(), SabmUtils.HOME));

		updatePageTitle(category, model);

		final RequestContextData requestContextData = getRequestContextData(request);
		requestContextData.setCategory(category);
		requestContextData.setSearch(searchPageData);

		if (searchQuery != null)
		{
			model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.INDEX_NOFOLLOW);
		}

		Collection<String> keywords=new ArrayList<String>();
		for(KeywordModel keywordModel:category.getKeywords()){
			keywords.add(keywordModel.getKeyword());
		}
		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(keywords);
		final String metaDescription = MetaSanitizerUtil.sanitizeDescription(category.getDescription());
		setUpMetaData(model, metaKeywords, metaDescription);

		return getViewPage(categorySearch.getCategoryPage());

	}

	@ResponseBody
	@GetMapping(CATEGORY_CODE_PATH_VARIABLE_PATTERN + "/facets")
	public FacetRefinement<SearchStateData> getFacets(@PathVariable("categoryCode") final String categoryCode,
			@RequestParam(value = "q", required = false) String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) String sortCode) throws UnsupportedEncodingException
	{

		searchQuery = XSSFilterUtil.filter(searchQuery);
		sortCode = XSSFilterUtil.filter(sortCode);
		return performSearchAndGetFacets(categoryCode, searchQuery, page, showMode, sortCode);
	}

	@ResponseBody
	@GetMapping(CATEGORY_CODE_PATH_VARIABLE_PATTERN + "/results")
	public SearchResultsData<ProductData> getResults(@PathVariable("categoryCode") final String categoryCode,
			@RequestParam(value = "q", required = false) String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) String sortCode) throws UnsupportedEncodingException
	{
		searchQuery = XSSFilterUtil.filter(searchQuery);
		sortCode = XSSFilterUtil.filter(sortCode);
		sabmProductExclusionService.getAndSetSessionEanProductExclusion();
		return performSearchAndGetResultsData(categoryCode, searchQuery, page, showMode, sortCode);
	}


	@ModelAttribute("cupRefreshInProgress")
	public boolean isCupRefreshInProgress()
	{
		final SABMCustomerFacade customerFacade = (SABMCustomerFacade) getCustomerFacade();

		return (customerFacade.isCupRefreshInProgress());
	}
	
	@ModelAttribute("requestOrigin")
	protected String populateRequestOrigin(HttpServletRequest request) {
		return SabmUtils.getRequestOrigin(request.getHeader(SabmUtils.REFERER_KEY), SabmUtils.HOME);
	}
}
