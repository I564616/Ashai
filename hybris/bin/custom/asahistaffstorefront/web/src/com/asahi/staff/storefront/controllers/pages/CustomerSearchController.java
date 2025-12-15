/**
 *
 */
package com.asahi.staff.storefront.controllers.pages;


import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.asahi.staff.storefront.form.AsahiCustomerSearchForm;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.facades.constants.SabmFacadesConstants;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;
import com.sabmiller.facades.deal.SABMRepDrivenDealConditionStatusFacade;
import com.sabmiller.facades.search.data.CustomerSearchPageData;
import com.sabmiller.facades.search.solrfacetsearch.SABMCustomerSearchFacade;


/**
 * CustomerSearchController
 */
@Controller
@Scope("tenant")
public class CustomerSearchController extends AbstractAsahiStaffSearchPageController
{
	//CMS Page
	private static final String CUSTOMER_SEARCH_CMS_PAGE = "customerSearch";
	private static final String CUSTOMER_SEARCH_RESULTS_CMS_PAGE = "customerSearchResults";

	private static final String CONFIRM_ENABLED_DEALS_CMS_PAGE = "confirmRep-DrivenDeals";

	private static final String BREADCRUMBS_KEY = "breadcrumbs";
	private static final String BREADCRUMBS_CUSTOMER_SEARCH_TEXT_KEY = "text.staff.portal.customer.search.breadcrumb";
	private static final String BREADCRUMBS_SEARCH_RESULT_TEXT_KEY = "staff.portal.customer.searchResults.breadcrumb";
	private static final String BREADCRUMBS_DEALS = "staff.portal.customer.deals.breadcrumb";
	private static final String BREADCRUMBS_CONFIRM_CHANGES = "text.staff.portal.confirm.enabled.deals.breadcrumb";


	private static final String SEPARATOR = ":";
	public static final String SESSION_CUSTOMER_SEARCH_RESULTS_DATA = "customerSearchResultsData";
	public static final String SESSION_CUSTOMER_SEARCH_FORM = "customerSearchForm";
	private static final int LIST_SIZE = 1;
	private static final String SESSION_CHANGED_DEAL_STATUS_DATA = "changedDealStatusData";
	public static final int MAX_PAGE_LIMIT = 100; // should be configured
	private static final String PAGINATION_NUMBER_OF_RESULTS_COUNT = "pagination.number.results.count";
	private static final String NO_RESULTS_CMS_PAGE_ID = "searchEmpty";
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(CustomerSearchController.class);
	private static final String CUST_NAME=":customername-asc";

	@Resource(name = "sabmCustomerSearchFacade")
	private SABMCustomerSearchFacade<CustomerData> sabmCustomerSearchFacade;

	@Resource(name = "sabmDealsSearchFacade")
	private SABMDealsSearchFacade sabmDealsSearchFacade;

	@Resource(name = "customerFacade")
	private SABMCustomerFacade customerFacade;

	@Resource(name = "sabmRepDrivenDealConditionStatusFacade")
	private SABMRepDrivenDealConditionStatusFacade dealConditionStatusFacade;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	/**
	 * Navigate to the customer search page.
	 *
	 * @param model
	 * @return page path
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping("/customer-search")
	@RequireHardLogIn
	public String get(final Model model) throws CMSItemNotFoundException
	{
		final Breadcrumb breadcrumbEntry = new Breadcrumb("#",
				getMessageSource().getMessage(BREADCRUMBS_CUSTOMER_SEARCH_TEXT_KEY, null, getI18nService().getCurrentLocale()), null);

		model.addAttribute(BREADCRUMBS_KEY, Collections.singletonList(breadcrumbEntry));
		model.addAttribute("asahiCustomerSearchForm", new AsahiCustomerSearchForm());

		storeCmsPageInModel(model, getContentPageForLabelOrId(CUSTOMER_SEARCH_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CUSTOMER_SEARCH_CMS_PAGE));
		return getViewForPage(model);
	}

	/**
	 * Get customer list through invoke the method to query solr result and return to page. If the customer size more
	 * than 1 return customer list. Otherwise return null
	 *
	 * @param form
	 * @param bindingResult
	 * @param model
	 * @return page path
	 * @throws CMSItemNotFoundException
	 */
	@PostMapping("/doCustomerSearch")
	@RequireHardLogIn
	public String doCustomerSearch(final AsahiCustomerSearchForm form, final BindingResult bindingResult, final Model model)
			throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			LOG.warn("Customer Search fail.");
		}
		final ContentPageModel noResultPage = getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID);
		
		String customername_sort = StringUtils.isNotBlank(form.getSort())?form.getSort().substring(1):CUST_NAME.substring(1);
		
		final PageableData pageableData = createPageableData(form.getPage(), getSearchPageSize(), customername_sort, ShowMode.Page);
		// search customer
		final SearchStateData searchState = new SearchStateData();
		searchState.setQuery(getSearchQuery(form));


		final CustomerSearchPageData<SearchStateData, CustomerData> searchPageData = sabmCustomerSearchFacade
				.textSearch(searchState, pageableData);


		if (searchPageData == null)
		{
			storeCmsPageInModel(model, noResultPage);
		}
		if (searchPageData.getPagination().getTotalNumberOfResults() == 0)
		{
			model.addAttribute("searchPageData", searchPageData);
			storeCmsPageInModel(model, noResultPage);
		}
		else
		{
			final List<CustomerData> customerDataList = searchPageData.getResults();

			model.addAttribute("customerDatas", customerDataList);
			final int numberPagesShown = getSiteConfigService().getInt(PAGINATION_NUMBER_OF_RESULTS_COUNT, 5);

			model.addAttribute("numberPagesShown", Integer.valueOf(numberPagesShown));
			model.addAttribute("searchPageData", searchPageData);
			model.addAttribute("isShowAllAllowed", calculateShowAll(searchPageData, ShowMode.Page));
			model.addAttribute("isShowPageAllowed", calculateShowPaged(searchPageData, ShowMode.Page));
			model.addAttribute("form", form);
		}



		//add go back link for mobile
		model.addAttribute("backUrl", "/customer-search");

		/*
		 * Breadcrumbs
		 */
		createSearchResultPageBreadcrumb(model);

		getSessionService().setAttribute(SESSION_CUSTOMER_SEARCH_FORM, form);
		getSessionService().setAttribute(SESSION_CUSTOMER_SEARCH_RESULTS_DATA, searchPageData);

		//If the search only returns one result, this page should be skipped and the user is taken directly to the deals page
		//		if (searchPageData.getPagination().getTotalNumberOfResults() == LIST_SIZE)
		//		{
		//			return REDIRECT_PREFIX + "/deals/specific/" + customerDataList.get(0).getUid();
		//		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(CUSTOMER_SEARCH_RESULTS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CUSTOMER_SEARCH_RESULTS_CMS_PAGE));
		return getViewForPage(model);
	}


	//	/**
	//	 * Confirm changes and send email page.
	//	 *
	//	 * @param model
	//	 * @return String
	//	 * @throws CMSItemNotFoundException
	//	 */
	//	@RequestMapping(value = "/confirm-changes", method = RequestMethod.GET)
	//	@RequireHardLogIn
	//	public String getConfirmAndSendEmailsPage(final Model model) throws CMSItemNotFoundException
	//	{
	//		final Object changedDeals = getSessionService().getAttribute(SESSION_CHANGED_DEAL_STATUS_DATA);
	//		if (!(changedDeals instanceof SABMChangesDealForm))
	//		{
	//			return FORWARD_PREFIX + "/backToCustomerSearchResults";
	//		}
	//
	//		final SABMChangesDealForm changedDealsFrom = (SABMChangesDealForm) changedDeals;
	//		final String b2bUnitId = changedDealsFrom.getUid();
	//		if (StringUtils.isEmpty(b2bUnitId))
	//		{
	//			LOG.info("Missing b2bUnitId info");
	//			return FORWARD_PREFIX + "/404";
	//		}
	//		final B2BUnitModel b2bnit = customerFacade.getB2BUnitForId(b2bUnitId);
	//		if (null == b2bnit)
	//		{
	//			return FORWARD_PREFIX + "/404";
	//		}
	//		final B2BUnitData b2bunitData = customerFacade.getB2BUnitForUnitModel(b2bnit);
	//		model.addAttribute("b2bunitData", b2bunitData);
	//
	//		final String primaryAdminStatus = b2bnit.getB2BUnitStatus().getCode();
	//		model.addAttribute("primaryAdminStatus", primaryAdminStatus);
	//
	//		final Map<String, List<String>> changedDealsTitle = sabmDealsSearchFacade
	//				.getChangedDealsTitleForCurrentUser(changedDealsFrom.getConditions());
	//
	//		if (changedDealsTitle != null)
	//		{
	//			if (changedDealsTitle.containsKey(SabmCoreConstants.ACTIVATED_DEAL_KEY))
	//			{
	//				model.addAttribute("activatedDealList", changedDealsTitle.get(SabmCoreConstants.ACTIVATED_DEAL_KEY));
	//			}
	//			if (changedDealsTitle.containsKey(SabmCoreConstants.DEACTIVATED_DEAL_KEY))
	//			{
	//				model.addAttribute("deactivatedDealList", changedDealsTitle.get(SabmCoreConstants.DEACTIVATED_DEAL_KEY));
	//			}
	//		}
	//
	//		model.addAttribute("otherCustomerUid", sabmDealsSearchFacade.getOtherSelectCustomers(b2bnit));
	//		final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
	//		breadcrumbs.add(new Breadcrumb("/customer-search",
	//				getMessageSource().getMessage(BREADCRUMBS_CUSTOMER_SEARCH_TEXT_KEY, null, getI18nService().getCurrentLocale()),
	//				null));
	//		breadcrumbs.add(new Breadcrumb("/backToCustomerSearchResults",
	//				getMessageSource().getMessage(BREADCRUMBS_SEARCH_RESULT_TEXT_KEY, null, getI18nService().getCurrentLocale()), null));
	//		breadcrumbs.add(new Breadcrumb("/deals/specific/" + b2bUnitId,
	//				getMessageSource().getMessage(BREADCRUMBS_DEALS, null, getI18nService().getCurrentLocale()), null));
	//		breadcrumbs.add(new Breadcrumb("#",
	//				getMessageSource().getMessage(BREADCRUMBS_CONFIRM_CHANGES, null, getI18nService().getCurrentLocale()), null));
	//		model.addAttribute(BREADCRUMBS_KEY, breadcrumbs);
	//
	//
	//		//add go back link for mobile
	//		model.addAttribute("backUrl", "/deals/specific/" + b2bunitData.getUid());
	//
	//		storeCmsPageInModel(model, getContentPageForLabelOrId(CONFIRM_ENABLED_DEALS_CMS_PAGE));
	//		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CONFIRM_ENABLED_DEALS_CMS_PAGE));
	//		return getViewForPage(model);
	//	}
	//
	//	/**
	//	 * Set the deals messages to model
	//	 *
	//	 * @param toEmails
	//	 * @param sendToMe
	//	 * @param behaviour
	//	 * @return String
	//	 *
	//	 */
	//	@RequestMapping(value = "/confirm-send", method = RequestMethod.POST)
	//	@RequireHardLogIn
	//	public String confirmAndSendEmails(@RequestParam(value = "emails", required = false) final List<String> toEmails,
	//			@RequestParam(value = "sendToMe", required = false, defaultValue = "false") final String sendToMe,
	//			@RequestParam(value = "behaviour", required = false, defaultValue = "") final String behaviour)
	//			throws CMSItemNotFoundException
	//	{
	//		final Object changedDeals = getSessionService().getAttribute(SESSION_CHANGED_DEAL_STATUS_DATA);
	//		if (!(changedDeals instanceof SABMChangesDealForm))
	//		{
	//			return FORWARD_PREFIX + "/backToCustomerSearchResults";
	//		}
	//		final SABMChangesDealForm changedDealsForm = (SABMChangesDealForm) changedDeals;
	//		final String b2bUnitId = changedDealsForm.getUid();
	//
	//		Boolean isSendToMe = null;
	//		if ("false".equals(sendToMe))
	//		{
	//			isSendToMe = Boolean.FALSE;
	//		}
	//		else
	//		{
	//			isSendToMe = Boolean.TRUE;
	//		}
	//
	//		dealConditionStatusFacade.saveRepDrivenDealConditionStatus(changedDealsForm.getUid(), changedDealsForm.getConditions());
	//
	//		sabmDealsSearchFacade.sendConfirmEnableDealEmail(b2bUnitId, changedDealsForm.getConditions(), behaviour, isSendToMe,
	//				toEmails == null ? new ArrayList<String>() : toEmails);
	//
	//		getSessionService().removeAttribute(SESSION_CHANGED_DEAL_STATUS_DATA);
	//		return REDIRECT_PREFIX + "/confirmationSent";
	//	}


	@GetMapping("/backToCustomerSearchResults")
	@RequireHardLogIn
	public String backToCustomerSearchResults(final Model model) throws CMSItemNotFoundException
	{
		final CustomerSearchPageData<SearchStateData, CustomerData> searchPageData = getSessionService()
				.getAttribute(SESSION_CUSTOMER_SEARCH_RESULTS_DATA);

		if (searchPageData == null)
		{
			return REDIRECT_PREFIX + "/customer-search";
		}

		final List<CustomerData> customerDataList = searchPageData.getResults();

		final AsahiCustomerSearchForm form = getSessionService().getAttribute(SESSION_CUSTOMER_SEARCH_FORM);

		model.addAttribute("customerDatas", customerDataList);

		model.addAttribute("form", form);

		createSearchResultPageBreadcrumb(model);
		//final Breadcrumb breadcrumbEntry = new Breadcrumb("#", getMessageSource().getMessage("staff.portal.customer.searchResults.breadcrumb", null, getI18nService().getCurrentLocale()), null);
		//model.addAttribute(BREADCRUMBS_KEY, Collections.singletonList(breadcrumbEntry));

		//If the search only returns one result, this page should be skipped and the user is taken directly to the deals page

		//		if (searchPageData.getPagination().getTotalNumberOfResults() == LIST_SIZE)
		//		{
		//			return REDIRECT_PREFIX + "/deals/specific/" + customerDataList.get(0).getUid();
		//		}

		getSessionService().setAttribute(SESSION_CUSTOMER_SEARCH_FORM, form);
		getSessionService().setAttribute(SESSION_CUSTOMER_SEARCH_RESULTS_DATA, searchPageData);

		//add go back link for mobile
		model.addAttribute("backUrl", "/customer-search");

		storeCmsPageInModel(model, getContentPageForLabelOrId(CUSTOMER_SEARCH_RESULTS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CUSTOMER_SEARCH_RESULTS_CMS_PAGE));
		return getViewForPage(model);
	}


	/**
	 * the method is set SearchQueryData from Front page
	 *
	 * @param form
	 * @return SearchQueryData
	 */
	protected SearchQueryData getSearchQuery(final AsahiCustomerSearchForm form)
	{
		validateParameterNotNull(form, "AsahiCustomerSearchForm cannot be null");
		final SearchQueryData searchQueryData = new SearchQueryData();
		String queryValue = StringUtils.isNotEmpty(form.getSort())?form.getSort():CUST_NAME;
		if (StringUtils.isNotEmpty(form.getCustomerName().trim()))
		{
			queryValue = queryValue + SEPARATOR + SabmFacadesConstants.CUSTOMER_NAME + SEPARATOR + form.getCustomerName().trim();
		}
		if (StringUtils.isNotEmpty(form.getAddress().trim()))
		{
			queryValue = queryValue + SEPARATOR + SabmFacadesConstants.CUSTOMER_ARR_STREET + SEPARATOR + form.getAddress().trim();
		}
		if (StringUtils.isNotEmpty(form.getPostcode().trim()))
		{
			queryValue = queryValue + SEPARATOR + SabmFacadesConstants.CUSTOMER_ARR_POSTCODE + SEPARATOR + form.getPostcode().trim();
		}
		if (StringUtils.isNotEmpty(form.getSuburb().trim()))
		{
			queryValue = queryValue + SEPARATOR + SabmFacadesConstants.CUSTOMER_ARR_SUBURB + SEPARATOR + form.getSuburb().trim();
		}
		if (StringUtils.isNotEmpty(form.getExpiryDateMonth()))
		{
			queryValue = queryValue + SEPARATOR + SabmFacadesConstants.CUSTOMER_ARR_ISOCODE + SEPARATOR
					+ form.getExpiryDateMonth().trim();
		}
		if (StringUtils.isNotEmpty(form.getAccountPayerNumber().trim()))
		{
			queryValue = queryValue + SEPARATOR + SabmFacadesConstants.CUSTOMER_ACC_OR_PAY_NUMBER + SEPARATOR
					+ form.getAccountPayerNumber().trim();
		}

		if (StringUtils.isNotEmpty(StringUtils.trim(form.getEmail())))
		{
			queryValue = queryValue + SEPARATOR + SabmFacadesConstants.CUSTOMER_EMAIL + SEPARATOR
					+ StringUtils.trim(form.getEmail()).toLowerCase();
		}
		LOG.info(queryValue);
		searchQueryData.setValue(queryValue);
		
		return searchQueryData;
	}



	/**
	 * Create Breadcrubms for search Result page.
	 *
	 * @param model
	 */
	protected void createSearchResultPageBreadcrumb(final Model model)
	{
		final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
		final Breadcrumb breadcrumbSearch = new Breadcrumb("/customer-search",
				getMessageSource().getMessage(BREADCRUMBS_CUSTOMER_SEARCH_TEXT_KEY, null, getI18nService().getCurrentLocale()), null);
		final Breadcrumb breadcrumbSearchResult = new Breadcrumb("#",
				getMessageSource().getMessage(BREADCRUMBS_SEARCH_RESULT_TEXT_KEY, null, getI18nService().getCurrentLocale()), null);
		breadcrumbs.add(breadcrumbSearch);
		breadcrumbs.add(breadcrumbSearchResult);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, breadcrumbs);
	}
}
