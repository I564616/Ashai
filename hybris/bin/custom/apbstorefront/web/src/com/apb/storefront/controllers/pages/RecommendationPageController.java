package com.apb.storefront.controllers.pages;

import java.io.UnsupportedEncodingException;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.product.AsahiRecommendationFacade;
import com.apb.storefront.controllers.ControllerConstants;
import com.apb.storefront.forms.AsahiUpdateRecommendationsForm;
import com.apb.storefront.forms.ProductRecommendationForm;
import com.sabmiller.facades.recommendation.data.RecommendationData;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;

@Controller
@Scope("tenant")
@RequestMapping("/recommendation")
public class RecommendationPageController extends ApbAbstractPageController {

	private static final String REP_RECOMMENDATION_CMS_PAGE = "repRecommendation";
	private static final String BREADCRUMBS_ATTR = "breadcrumbs";
	private static final String UPDATE = "UPDATE";
	private static final String REMOVE = "REMOVE";
	private static final String REMOVE_ALL = "REMOVE_ALL";
	private static final String PRODUCTS_PAGINATION_NUMBER_OF_RESULTS_COUNT = "recommendation.pagination.number.results.count";
	private static final String BY_RECOMMENDEDDATE = "byRecommendedDate";

	@Resource(name = "asahiRecommendationFacade")
	private AsahiRecommendationFacade asahiRecommendationFacade;
	
	@Resource(name = "recommendationBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder recommendationBreadcrumbBuilder;	

	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;
	


	@GetMapping
	@RequireHardLogIn
	public String getRecommendations(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") ShowMode showMode,
			@RequestParam(value = "sort", defaultValue = BY_RECOMMENDEDDATE) final String sortCode,final Model model) throws CMSItemNotFoundException
	{
		if (asahiSiteUtil.isBDECustomer()) {
			
			final int numberPagesShown = getSiteConfigService().getInt(PRODUCTS_PAGINATION_NUMBER_OF_RESULTS_COUNT, 10);
			final SearchPageData searchData = prepareSearchPageData(page, numberPagesShown, showMode, sortCode);
			final SearchPageData<RecommendationData> searchPageData = asahiRecommendationFacade.getAsahiProductRecommendations(searchData, sortCode);
			populateRecommendationData(searchPageData, page, showMode, sortCode, model);
			model.addAttribute("recommendationResults", searchPageData);
			model.addAttribute("customerName", (null != asahiCoreUtil.getCurrentB2BCustomer().getDefaultB2BUnit()) 
					? asahiCoreUtil.getCurrentB2BCustomer().getDefaultB2BUnit().getLocName() : StringUtils.EMPTY);
			storeCmsPageInModel(model, getContentPageForLabelOrId(REP_RECOMMENDATION_CMS_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(REP_RECOMMENDATION_CMS_PAGE));
			
			if ((getContentPageForLabelOrId(REP_RECOMMENDATION_CMS_PAGE)).getBackgroundImage() != null)
			{
				model.addAttribute("media", (getContentPageForLabelOrId(REP_RECOMMENDATION_CMS_PAGE)).getBackgroundImage().getURL());
			}
			model.addAttribute(BREADCRUMBS_ATTR, recommendationBreadcrumbBuilder.getBreadcrumbs(null));
			updatePageTitle(model, getContentPageForLabelOrId(REP_RECOMMENDATION_CMS_PAGE));
			return getViewForPage(model);
		}
		else {
			return REDIRECT_PREFIX + ROOT;
		}

	}

	private void populateRecommendationData(SearchPageData<RecommendationData> searchPageData, int page,
			ShowMode showMode, String sortCode, Model model) {
		if (searchPageData == null || searchPageData.getResults().size() == 0)
		{
			model.addAttribute("searchPageData", searchPageData);
		}
		else
		{
			final List<RecommendationData> recommendations = searchPageData.getResults();

			model.addAttribute("recommendationData", recommendations);
			final int numberPagesShown = getSiteConfigService().getInt(PRODUCTS_PAGINATION_NUMBER_OF_RESULTS_COUNT, 10);

			model.addAttribute("numberPagesShown", Integer.valueOf(numberPagesShown));
			model.addAttribute("searchPageData", searchPageData);
			model.addAttribute("isShowAllAllowed", calculateShowAll(searchPageData, ShowMode.Page));
			model.addAttribute("isShowPageAllowed", calculateShowPaged(searchPageData, ShowMode.Page));
		}
		
	}

	@PostMapping(value = "/add", produces = "application/json")
	@RequireHardLogIn
	public String addRecommendation(@RequestBody @Valid final ProductRecommendationForm form, final Model model)
			throws CMSItemNotFoundException {

		if (StringUtils.isNotBlank(form.getProductCode()) && form.getQty() > 0) {
			asahiRecommendationFacade.saveProductRecommendations(form.getProductCode(), Integer.valueOf(form.getQty()));
			model.addAttribute("isRecommendationAdded", true);

		} else {
			model.addAttribute("isRecommendationAdded", false);
		}		
	  Integer totalProductCount = asahiRecommendationFacade.getTotalRepRecommendedProducts();
	  model.addAttribute("recommendationsCount", String.valueOf(totalProductCount));
	  return ControllerConstants.Views.Fragments.Recommendation.addToRecommendation;
	}

	@PostMapping("/updateRecommendations")
	@RequireHardLogIn
	@ResponseBody
	public String updateRecommendations(@RequestBody @Valid final AsahiUpdateRecommendationsForm form,
			final BindingResult bindingResult,Model model) throws UnsupportedEncodingException
	{
		if (bindingResult.hasErrors())
		{
			return Boolean.FALSE.toString();
		}

		if (form != null)
		{
			
			boolean success = this.updateAndRemoveRecommendation(form);
			if (!success) {
				return Boolean.FALSE.toString();
			}
			
		}

		Integer totalProductCount = asahiRecommendationFacade.getTotalRepRecommendedProducts();
		return String.valueOf(totalProductCount);
	}
	
	private boolean updateAndRemoveRecommendation(final AsahiUpdateRecommendationsForm form)
	{
		boolean success = true;
		switch  (form.getAction()) {
				case UPDATE : 
					if (form.getQuantity() <= 0 || StringUtils.isBlank(form.getProductCode())) {
						return Boolean.FALSE;
					}
					success = asahiRecommendationFacade.updateProductRecommendation(form.getProductCode(), form.getQuantity());
					break;
				case REMOVE :
					if (StringUtils.isBlank(form.getProductCode())) {
						return Boolean.FALSE;
					}
					success = asahiRecommendationFacade.deleteRecommendationByProductId(form.getProductCode());
					break;
				case REMOVE_ALL:
					success = asahiRecommendationFacade.deleteAllRecommendations();
					break;
			}
		
		return success;
	}

	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage) {
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveContentPageTitle(cmsPage.getTitle()));
	}
	
	@GetMapping("/getTotalCount")
	@ResponseBody
	public String getRepRecommendedTotalProductCount()
	{
		if (asahiSiteUtil.isBDECustomer()) {
			Integer totalProductCount = asahiRecommendationFacade.getTotalRepRecommendedProducts();
			return String.valueOf(totalProductCount);
		}
		return StringUtils.EMPTY;
	}
	
	
	protected SearchPageData prepareSearchPageData(int page, int numberPagesShown, ShowMode showMode, String sortCode)
	{
		final SearchPageData<?> searchPageData = new SearchPageData();
		final PaginationData pagination = new PaginationData();
		pagination.setCurrentPage(page);
		if (ShowMode.All == showMode)
		{
			pagination.setPageSize(MAX_PAGE_LIMIT);
		} else {
			pagination.setPageSize(numberPagesShown);
		}
		pagination.setNeedsTotal(true);
		searchPageData.setPagination(pagination);

		return searchPageData;
	}


	protected Boolean calculateShowAll(final SearchPageData<?> searchPageData, final ShowMode showMode)
	{
		return Boolean.valueOf((showMode != ShowMode.All && //
				searchPageData.getPagination().getTotalNumberOfResults() > searchPageData.getPagination().getPageSize())
				&& isShowAllAllowed(searchPageData));
	}

	protected Boolean calculateShowPaged(final SearchPageData<?> searchPageData, final ShowMode showMode)
	{
		return Boolean.valueOf(showMode == ShowMode.All && (searchPageData.getPagination().getNumberOfPages() > 1
				|| searchPageData.getPagination().getPageSize() == getMaxSearchPageSize()));
	}
	
	protected boolean isShowAllAllowed(final SearchPageData<?> searchPageData)
	{
		return searchPageData.getPagination().getNumberOfPages() > 1
				&& searchPageData.getPagination().getTotalNumberOfResults() < MAX_PAGE_LIMIT;
	}

}
