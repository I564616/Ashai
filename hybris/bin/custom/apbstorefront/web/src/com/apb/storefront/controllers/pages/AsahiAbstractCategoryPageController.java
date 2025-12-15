package com.apb.storefront.controllers.pages;

import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorservices.data.RequestContextData;
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
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.services.ApbCustomerAccountService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.facades.customer.SABMCustomerFacade;


/**
 * @see Asahi Category Page controller
 */
public class AsahiAbstractCategoryPageController extends AbstractCategoryPageController
{


	@Resource(name = "asahiSiteUtil")
	private AsahiSiteUtil asahiSiteUtil;

	private static final String CATEGORY_FACET = "category";
		
	@Resource(name= "sessionService")
	private SessionService sessionService;
	
	@Resource(name = "sabmCustomerFacade")
	private SABMCustomerFacade sabmCustomerFacade;

	@Override
	protected String performSearchAndGetResultsPage(final String categoryCode, final String searchQuery, final int page, // NOSONAR
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

		final CategorySearchEvaluator categorySearch = new CategorySearchEvaluator(categoryCode, searchQuery, page, showMode,
				sortCode, categoryPage);

		ProductCategorySearchPageData<SearchStateData, ProductData, CategoryData> searchPageData = null;
		try
		{
			categorySearch.doSearch();
			searchPageData = categorySearch.getSearchPageData();

		}
		catch (final ConversionException e) // NOSONAR
		{
			searchPageData = createEmptySearchResult(categoryCode);
		}
		
		/** Set Search Results as empty for restricted categories to handle scenario where a category may 
		  be restricted in backoffice but not in product inclusion list from ECC **/
		
		if (asahiSiteUtil.isSga()) {
			sabmCustomerFacade.setRestrictedCategoriesInSession();	
			if (null != sessionService.getAttribute(ApbCoreConstants.ALB_CUSTOMER_SESSION_EXCLUDED_CATEGORIES) 
					&& ((Set<String>) sessionService.getAttribute(ApbCoreConstants.ALB_CUSTOMER_SESSION_EXCLUDED_CATEGORIES)).contains(categoryCode)) 
			{
				searchPageData = createEmptySearchResult(categoryCode);
			}
		}
		
		final boolean showCategoriesOnly = categorySearch.isShowCategoriesOnly();

		storeCmsPageInModel(model, categorySearch.getCategoryPage());
		storeContinueUrl(request);

		/* Allow only level 1 category in facets---SGA Specific. start */

		displayLevelOneCategoryOnlyInFacet(categoryCode, category, searchPageData);

		/* Allow only level 1 category in facets---SGA Specific. end */

		populateModel(model, searchPageData, showMode);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, getSearchBreadcrumbBuilder().getBreadcrumbs(categoryCode, searchPageData));
		model.addAttribute("showCategoriesOnly", Boolean.valueOf(showCategoriesOnly));
		model.addAttribute("categoryName", category.getName());
		model.addAttribute("pageType", PageType.CATEGORY.name());
		model.addAttribute("userLocation", getCustomerLocationService().getUserLocation());

		updatePageTitle(category, model);

		final RequestContextData requestContextData = getRequestContextData(request);
		requestContextData.setCategory(category);
		requestContextData.setSearch(searchPageData);

		if (searchQuery != null)
		{
			model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_FOLLOW);
		}

		//Need to fix as part of ALB/APB code merge with CUB
		//final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(category.getKeywords());
		
		List<KeywordModel> categoryKeyWords = category.getKeywords();
		List<String> keyWords = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(categoryKeyWords))
		{
			for(KeywordModel keyword : categoryKeyWords)
			{
				if(StringUtils.isNotEmpty(keyword.getKeyword()))
				{
					keyWords.add(keyword.getKeyword());
				}
			}
		}
		
		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(keyWords);
		final String metaDescription = MetaSanitizerUtil.sanitizeDescription(category.getDescription());
		setUpMetaData(model, metaKeywords, metaDescription);

		return getViewPage(categorySearch.getCategoryPage());

	}

	/**
	 * @param categoryCode
	 * @param category
	 * @param searchPageData
	 */
	private void displayLevelOneCategoryOnlyInFacet(final String categoryCode, final CategoryModel category,
			final ProductCategorySearchPageData<SearchStateData, ProductData, CategoryData> searchPageData)
	{
		if (asahiSiteUtil.isSga() && category.isLevelOne() && CollectionUtils.isNotEmpty(searchPageData.getFacets()))
		{

			final List<FacetData<SearchStateData>> facetList = searchPageData.getFacets();

			for (final FacetData<SearchStateData> facetValueList : facetList)
			{
				if (facetValueList.getName().equalsIgnoreCase(CATEGORY_FACET))
				{
					final List<FacetValueData<SearchStateData>> categoryList = facetValueList.getValues();
					final List<FacetValueData<SearchStateData>> result = categoryList.stream()
							.filter(line -> categoryCode.equals(line.getCode())).collect(Collectors.toList());
					facetValueList.setValues(result);
				}
			}

		}
	}


}
