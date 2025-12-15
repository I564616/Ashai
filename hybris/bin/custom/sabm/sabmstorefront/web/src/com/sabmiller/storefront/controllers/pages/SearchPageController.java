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
import de.hybris.platform.acceleratorcms.model.components.SearchBoxComponentModel;
import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorservices.customer.CustomerLocationService;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.SearchBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.util.MetaSanitizerUtil;
import de.hybris.platform.acceleratorstorefrontcommons.util.XSSFilterUtil;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.AutocompleteResultData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetRefinement;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sap.security.core.server.csi.XSSEncoder;

import com.sabmiller.storefront.controllers.pages.SabmAbstractSearchPageController;

@Controller
@Scope("tenant")
@RequestMapping("/search")
public class SearchPageController extends SabmAbstractSearchPageController
{
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

	@Resource(name = "sabmProductExclusionService")
	private SABMProductExclusionService sabmProductExclusionService;

	@GetMapping( params = "!q")
	public String textSearch(@RequestParam(value = "text", defaultValue = "") final String searchText,
			final HttpServletRequest request, final Model model) throws CMSItemNotFoundException, UnsupportedEncodingException
	{
		String searchingText = "";
		if (StringUtils.isNotBlank(searchText))
		{
			final PageableData pageableData = createPageableData(0, getSearchPageSize(), null, ShowMode.Page);
			searchingText = escapeRE(searchText);
			//			final String encodedSearchText = XSSEncoder.encodeHTML(XSSFilterUtil.filter(searchingText));
			final String encodedSearchText = Encode.forHtml(searchingText);
			final String encodedText = WordUtils.uncapitalize(encodedSearchText);
			final SearchStateData searchState = new SearchStateData();
			final SearchQueryData searchQueryData = new SearchQueryData();
			searchQueryData.setValue(encodedSearchText);
			searchState.setQuery(searchQueryData);

			sabmProductExclusionService.getAndSetSessionEanProductExclusion(); //prefill product exclusion
			ProductSearchPageData<SearchStateData, ProductData> searchPageData = productSearchFacade.textSearch(searchState,
					pageableData);
			searchPageData = encodeSearchPageData(searchPageData);
			if (searchPageData == null)
			{
				storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
			}
			else if (searchPageData.getKeywordRedirectUrl() != null)
			{
				searchPageData.setFreeTextSearch(encodedText);
				// if the search engine returns a redirect, just
				return "redirect:" + searchPageData.getKeywordRedirectUrl();
			}
			else if (searchPageData.getPagination().getTotalNumberOfResults() == 0)
			{
				searchPageData.setFreeTextSearch(encodedText);
				model.addAttribute("searchPageData", searchPageData);
				storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
				updatePageTitle(encodedText, model);
			}
			else
			{
				searchPageData.setFreeTextSearch(encodedText);
				storeContinueUrl(request);
				populateModel(model, searchPageData, ShowMode.Page);
				storeCmsPageInModel(model, getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID));
				updatePageTitle(encodedText, model);
			}
			model.addAttribute("userLocation", customerLocationService.getUserLocation());
			getRequestContextData(request).setSearch(searchPageData);
			if (searchPageData != null)
			{
				model.addAttribute(WebConstants.BREADCRUMBS_KEY, searchBreadcrumbBuilder.getBreadcrumbs(null,
						searchPageData.getFreeTextSearch(), CollectionUtils.isEmpty(searchPageData.getBreadcrumbs())));
			}
		}
		else
		{
			storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
		}
		//		final String encodedSearchText = XSSEncoder.encodeHTML(XSSFilterUtil.filter(searchingText));
		final String encodedSearchText = Encode.forHtml(searchingText);
		final String encodedText = WordUtils.uncapitalize(encodedSearchText);
		model.addAttribute("searchText", encodedSearchText);
		model.addAttribute("pageType", PageType.PRODUCTSEARCH.name());
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.INDEX_NOFOLLOW);

		final String metaDescription = MetaSanitizerUtil
				.sanitizeDescription(getMessageSource().getMessage("search.meta.description.results", null,
						"search.meta.description.results", getI18nService().getCurrentLocale()) + " " + encodedText + " "
						+ getMessageSource().getMessage("search.meta.description.on", null, "search.meta.description.on",
								getI18nService().getCurrentLocale())
						+ " " + getSiteName());
		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(encodedText);
		setUpMetaData(model, metaKeywords, metaDescription);

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
		final String encodedSearchText = XSSFilterUtil.filter(searchQuery);
		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = performSearch(encodedSearchText, page, showMode,
				sortCode, getSearchPageSize());
		searchPageData.setFreeTextSearch(searchQuery);
		populateModel(model, searchPageData, showMode);
		model.addAttribute("userLocation", customerLocationService.getUserLocation());

		if (searchPageData.getPagination().getTotalNumberOfResults() == 0)
		{
			updatePageTitle(searchQuery, model);
			storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
		}
		else
		{
			storeContinueUrl(request);
			updatePageTitle(searchQuery, model);
			storeCmsPageInModel(model, getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID));
		}
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				searchBreadcrumbBuilder.getBreadcrumbs(null, searchQuery, CollectionUtils.isEmpty(searchPageData.getBreadcrumbs())));
		model.addAttribute("pageType", PageType.PRODUCTSEARCH.name());
		model.addAttribute("searchText", searchText);

		final String metaDescription = MetaSanitizerUtil
				.sanitizeDescription(getMessageSource().getMessage("search.meta.description.results", null,
						"search.meta.description.results", getI18nService().getCurrentLocale()) + " " + searchQuery + " "
						+ getMessageSource().getMessage("search.meta.description.on", null, "search.meta.description.on",
								getI18nService().getCurrentLocale())
						+ " " + getSiteName());

		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(searchText);
		setUpMetaData(model, metaKeywords, metaDescription);

		return getViewForPage(model);
	}

	protected ProductSearchPageData<SearchStateData, ProductData> performSearch(final String searchQuery, final int page,
			final ShowMode showMode, final String sortCode, final int pageSize)
	{
		final PageableData pageableData = createPageableData(page, pageSize, sortCode, showMode);

		final SearchStateData searchState = new SearchStateData();
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(searchQuery);
		searchState.setQuery(searchQueryData);

		sabmProductExclusionService.getAndSetSessionEanProductExclusion();//prefill product exclusion session
		return encodeSearchPageData(productSearchFacade.textSearch(searchState, pageableData));
	}

	@ResponseBody
	@GetMapping("/results")
	public SearchResultsData<ProductData> jsonSearchResults(@RequestParam("q") final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode) throws CMSItemNotFoundException
	{
		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = performSearch(XSSFilterUtil.filter(searchQuery), page, showMode,
				sortCode, getSearchPageSize());
		final SearchResultsData<ProductData> searchResultsData = new SearchResultsData<>();
		searchResultsData.setResults(searchPageData.getResults());
		searchResultsData.setPagination(searchPageData.getPagination());
		return searchResultsData;
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
		searchQueryData.setValue(XSSFilterUtil.filter(searchQuery));
		searchState.setQuery(searchQueryData);

		sabmProductExclusionService.getAndSetSessionEanProductExclusion(); //prefill product exclusions
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
		final String searchterm = escapeRE(term);
		if (component.isDisplaySuggestions())
		{
			resultData.setSuggestions(
					subList(productSearchFacade.getAutocompleteSuggestions(searchterm), component.getMaxSuggestions()));
		}

		if (component.isDisplayProducts())
		{
			sabmProductExclusionService.getAndSetSessionEanProductExclusion();//prefill product exclusions if still not available
			resultData.setProducts(subList(productSearchFacade.textSearch(searchterm).getResults(), component.getMaxProducts()));
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

	@Override
	protected void populateModel(final Model model, final SearchPageData<?> searchPageData, final ShowMode showMode)
	{
		super.populateModel(model, searchPageData, showMode);
	}

	protected ProductSearchPageData<SearchStateData, ProductData> encodeSearchPageData(
			final ProductSearchPageData<SearchStateData, ProductData> searchPageData)
	{
		final SearchStateData currentQuery = searchPageData.getCurrentQuery();

		if (currentQuery != null)
		{
			final SearchQueryData query = currentQuery.getQuery();
			final String encodedQueryValue = encodeQueryValue(query.getValue());
			query.setValue(encodedQueryValue);
			LOG.info("The query Value is [{}]", StringUtils.trimToEmpty(query.getValue()));
			currentQuery.setQuery(query);
			searchPageData.setCurrentQuery(currentQuery);
			searchPageData.setFreeTextSearch(encodeQueryValue(searchPageData.getFreeTextSearch()));

			final List<FacetData<SearchStateData>> facets = searchPageData.getFacets();
			if (CollectionUtils.isNotEmpty(facets))
			{
				encodeFacetData(facets);
			}
		}
		return searchPageData;
	}

	// encode the facet query url
	private void encodeFacetData(final List<FacetData<SearchStateData>> facets)
	{

		for (final FacetData<SearchStateData> facetData : facets)
		{
			final List<FacetValueData<SearchStateData>> facetValueDatas = facetData.getValues();
			if (CollectionUtils.isNotEmpty(facetValueDatas))
			{
				for (final FacetValueData<SearchStateData> facetValueData : facetValueDatas)
				{
					final SearchStateData facetQuery = facetValueData.getQuery();
					final SearchQueryData queryData = facetQuery.getQuery();
					final String queryValue = queryData.getValue();
					queryData.setValue(encodeQueryValue(queryValue));
				}
			}

			final List<FacetValueData<SearchStateData>> topFacetValueDatas = facetData.getTopValues();
			if (CollectionUtils.isNotEmpty(topFacetValueDatas))
			{
				for (final FacetValueData<SearchStateData> topFacetValueData : topFacetValueDatas)
				{
					final SearchStateData facetQuery = topFacetValueData.getQuery();
					final SearchQueryData queryData = facetQuery.getQuery();
					final String queryValue = queryData.getValue();
					queryData.setValue(encodeQueryValue(queryValue));
				}
			}
		}

	}

	// encode the query url
	private String encodeQueryValue(final String queryValue)
	{
		if (StringUtils.isNotBlank(queryValue))
		{
			final StringBuilder queryValueBuilder = new StringBuilder();
			try
			{
				final String[] queryValues = queryValue.split(FACET_SEPARATOR);
				queryValueBuilder.append(XSSEncoder.encodeHTML(queryValues[0]));
				for (int i = 1; i < queryValues.length; i++)
				{
					queryValueBuilder.append(FACET_SEPARATOR).append(queryValues[i]);
				}
			}
			catch (final UnsupportedEncodingException e)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Error occured during Encoding the Search Page data values", e);
				}
				return null;
			}
			return queryValueBuilder.toString();
		}
		return null;
	}

	public String escapeRE(final String str)
	{
		final Pattern escaper = Pattern.compile("([^a-zA-z0-9 ])");
		return escaper.matcher(WordUtils.capitalize(str)).replaceAll("");
	}
}
