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

import de.hybris.platform.acceleratorfacades.ordergridform.OrderGridFormFacade;
import de.hybris.platform.acceleratorfacades.product.data.ReadOnlyOrderGridData;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController.ShowMode;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdateEmailForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdatePasswordForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.AddressValidator;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.EmailValidator;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.PasswordValidator;
import de.hybris.platform.acceleratorstorefrontcommons.forms.verification.AddressVerificationResultHandler;
import de.hybris.platform.acceleratorstorefrontcommons.util.AddressDataUtil;
import de.hybris.platform.b2bacceleratorfacades.customer.exception.InvalidPasswordException;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.address.AddressVerificationFacade;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.util.ResponsiveUtils;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.util.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.ws.rs.Produces;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.ApbCustomerAccountService;
import com.apb.storefront.constant.ApbStoreFrontContants;
import com.apb.storefront.controllers.ControllerConstants;
import com.apb.storefront.forms.ApbCompanyDeliveryAddressForm;
import com.apb.storefront.forms.ApbUpdateProfileForm;
import com.apb.storefront.forms.AsahiNotificationPrefForm;
import com.apb.storefront.forms.PlanogramUpdateForm;
import com.apb.storefront.forms.ProductRecommendationForm;
import com.apb.storefront.validators.ApbProfileValidator;
import com.apb.storefront.validators.ImportRequestRegistrationPDFFormValidator;
import com.asahi.facades.notification.AsahiNotificationFacade;
import com.asahi.facades.notifications.data.AsahiNotificationData;
import com.asahi.facades.notifications.data.AsahiNotificationPreferenceData;
import com.asahi.facades.planograms.PlanogramData;
import com.google.gson.Gson;
import de.hybris.platform.util.Base64;
import com.sabmiller.core.b2b.services.SabmB2BCustomerService;
import com.sabmiller.core.model.AsahiSAMInvoiceModel;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.model.PlanogramModel;
import com.sabmiller.core.search.restriction.SabmSearchRestrictionService;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.order.SABMOrderFacade;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.apb.facades.user.ApbUserFacade;
import com.apb.integration.data.AsahiInvoiceDownloadResponse;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.apb.facades.contactust.data.ApbContactUsData;
import com.sabmiller.facades.customer.impl.DefaultSABMCustomerFacade;
import com.apb.core.util.AsahiCoreUtil;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import java.text.SimpleDateFormat;

/**
 * Controller for home page
 */
@Controller
@RequestMapping("/my-account")
public class AccountPageController extends AbstractSearchPageController
{
	private static final String TEXT_ACCOUNT_ADDRESS_BOOK = "text.account.addressBook";
	private static final String BREADCRUMBS_ATTR = "breadcrumbs";
	private static final String IS_DEFAULT_ADDRESS_ATTR = "isDefaultAddress";
	private static final String COUNTRY_DATA_ATTR = "countryData";
	private static final String ADDRESS_BOOK_EMPTY_ATTR = "addressBookEmpty";
	private static final String TITLE_DATA_ATTR = "titleData";
	private static final String FORM_GLOBAL_ERROR = "form.global.error";
	private static final String FORM_PASSWORD_ERROR = "form.password.error";
	private static final String PROFILE_CURRENT_PASSWORD_INVALID = "profile.currentPassword.invalid";
	private static final String PROFILE_UPDATE_PASSWORD_INVALID = "updatePwd.pwd.invalid";
	private static final String TEXT_ACCOUNT_PROFILE = "text.account.profile";
	private static final String ADDRESS_DATA_ATTR = "addressData";
	private static final String ADDRESS_FORM_ATTR = "addressForm";
	private static final String COUNTRY_ATTR = "country";
	private static final String REGIONS_ATTR = "regions";
	private static final String MY_ACCOUNT_ADDRESS_BOOK_URL = "/my-account/address-book";
	// Internal Redirects
	private static final String REDIRECT_TO_ADDRESS_BOOK_PAGE = REDIRECT_PREFIX + MY_ACCOUNT_ADDRESS_BOOK_URL;
	private static final String REDIRECT_TO_PAYMENT_INFO_PAGE = REDIRECT_PREFIX + "/my-account/payment-details";
	private static final String REDIRECT_TO_EDIT_ADDRESS_PAGE = REDIRECT_PREFIX + "/my-account/edit-address/";
	private static final String REDIRECT_TO_UPDATE_EMAIL_PAGE = REDIRECT_PREFIX + "/my-account/update-email";
	private static final String REDIRECT_TO_UPDATE_PROFILE = REDIRECT_PREFIX + "/my-account/update-profile";
	private static final String REDIRECT_TO_PASSWORD_UPDATE_PAGE = REDIRECT_PREFIX + "/my-account/update-password";
	private static final String REDIRECT_TO_ORDER_HISTORY_PAGE = REDIRECT_PREFIX + "/my-account/orders";
	private static final String REDIRECT_TO_PASSWORD_UPDATE_SUCCESS_PAGE = REDIRECT_PREFIX + "/my-account/update-password/success";
	private static final String PAGINATION_NUMBER_OF_RESULTS_COUNT = "pagination.number.results.count";

	/**
	 * We use this suffix pattern because of an issue with Spring 3.1 where a Uri value is incorrectly extracted if it
	 * contains on or more '.' characters. Please see https://jira.springsource.org/browse/SPR-6164 for a discussion on
	 * the issue and future resolution.
	 */
	private static final String ORDER_CODE_PATH_VARIABLE_PATTERN = "{orderCode:.*}";
	private static final String ADDRESS_CODE_PATH_VARIABLE_PATTERN = "{addressCode:.*}";

	// CMS Pages
	private static final String ACCOUNT_CMS_PAGE = "account";
	private static final String PROFILE_CMS_PAGE = "profile";
	private static final String UPDATE_PASSWORD_CMS_PAGE = "updatePassword";
	private static final String UPDATE_PASSWORD_SUCCESS_CMS_PAGE = "updatePasswordSuccess";
	private static final String UPDATE_PROFILE_CMS_PAGE = "update-profile";
	private static final String UPDATE_EMAIL_CMS_PAGE = "update-email";
	private static final String ADDRESS_BOOK_CMS_PAGE = "address-book";
	private static final String ADD_EDIT_ADDRESS_CMS_PAGE = "add-edit-address";
	private static final String PAYMENT_DETAILS_CMS_PAGE = "payment-details";
	private static final String ENQUIRY_HISTORY_CMS_PAGE = "enquiries";
	private static final String ORDER_HISTORY_CMS_PAGE = "orders";
	private static final String ORDER_DETAIL_CMS_PAGE = "order";

	private static final String ORDER_HISTORY_LIST_SIZE = "order.history.list.size.apb";
	private static final String ENQUIRY_HISTORY_LIST_SIZE = "enquiry.history.list.size.sga";

	private static final String ORDER_HISTORY_EXPORT_LIST_SIZE = "order.history.export.list.size.sga";
	
	
	public static final String VIEW_ALL_PRODUCTS_QUANTITY = "checkout.cart.summary.view.all.quantity";
	
	private static final String ADD_SURCHARGE = "isAddSurcharge";
	private static final String ACCOUNT_PROFILE_UPDATE_TITLE = "text.account.profile.update.title";
	
	private static final String PLANOGRAM_PAGE = "planograms";
	private static final String REDIRECT_TO_PLANOGRAM_PAGE = REDIRECT_PREFIX + "/my-account/planograms";

	/** The Constant INVOICE_APPLICATION_TYPE. */
	private static final String PDF_APPLICATION_TYPE = "application/pdf";

	/** The Constant INVOICE_PDF_CACHE_CONTROL. */
	private static final String PDF_CACHE_CONTROL = "must-revalidate, post-check=0, pre-check=0";
	
	public static final String APPLICATION_PDF_CONTENT_TYPE = "application/pdf";
	public static final String APP_IMAGE_JPEG_CONTENT_TYPE = "image/jpeg";
	public static final String APP_IMAGE_JPG_CONTENT_TYPE = "image/jpg";
	public static final String APP_IMAGE_PNG_CONTENT_TYPE = "image/png";
	public static final String PDF_FILE_EXTENSION = ".pdf";
	public static final String JPEG_FILE_EXTENSION = ".jpeg";
	public static final String PNG_FILE_EXTENSION = ".png";
	public static final String JPG_FILE_EXTENSION = ".jpg";
	private static final Logger LOG = LoggerFactory.getLogger(AccountPageController.class);
	
	@Resource
	private UserService userService;

	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;

	@Resource(name = "acceleratorCheckoutFacade")
	private CheckoutFacade checkoutFacade;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "b2bCustomerFacade")
	private DefaultSABMCustomerFacade customerFacade;

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	@Resource(name = "passwordValidator")
	private PasswordValidator passwordValidator;

	@Resource(name = "addressValidator")
	private AddressValidator addressValidator;

	@Resource(name = "apbProfileValidator")
	private ApbProfileValidator profileValidator;

	@Resource(name = "emailValidator")
	private EmailValidator emailValidator;

	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;

	@Resource(name = "addressVerificationFacade")
	private AddressVerificationFacade addressVerificationFacade;

	@Resource(name = "addressVerificationResultHandler")
	private AddressVerificationResultHandler addressVerificationResultHandler;

	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;

	@Resource(name = "orderGridFormFacade")
	private OrderGridFormFacade orderGridFormFacade;

	@Resource(name = "addressDataUtil")
	private AddressDataUtil addressDataUtil;

	/** The asahi order facade. */
	@Resource(name = "orderFacade")
	private SABMOrderFacade sabmOrderFacade;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	
	@Resource
	SABMCartFacade sabmCartFacade;

	@Resource(name = "apbUserFacade")
	private ApbUserFacade apbUserFacade;
	
	@Resource(name ="asahiNotificationFacade")
	private AsahiNotificationFacade asahiNotificationFacade;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;
	
	@Resource(name = "sabmB2BCustomerService")
	private SabmB2BCustomerService sabmB2BCustomerService;
	
	@Resource
	private ApbB2BUnitService apbB2BUnitService;
	
	@Resource(name = "sabmSearchRestrictionService")
	private SabmSearchRestrictionService sabmSearchRestrictionService;
	
	@Resource(name = "importRequestRegistrationPDFFormValidator")
	private ImportRequestRegistrationPDFFormValidator importRequestRegistrationPDFFormValidator;
	
	@Autowired
	private ApbCustomerAccountService apbCustomerAccountService;
	
	@Autowired
	private MediaService mediaService;

	/**
	 * Password pattern calling from ConfigurationItem
	 */
	public static final String PWD = "storefront.passwordPattern.";
	


	protected PasswordValidator getPasswordValidator()
	{
		return passwordValidator;
	}

	protected AddressValidator getAddressValidator()
	{
		return addressValidator;
	}

	protected ApbProfileValidator getProfileValidator()
	{
		return profileValidator;
	}

	protected EmailValidator getEmailValidator()
	{
		return emailValidator;
	}

	protected I18NFacade getI18NFacade()
	{
		return i18NFacade;
	}

	protected AddressVerificationFacade getAddressVerificationFacade()
	{
		return addressVerificationFacade;
	}

	protected AddressVerificationResultHandler getAddressVerificationResultHandler()
	{
		return addressVerificationResultHandler;
	}

	@ModelAttribute("countries")
	public Collection<CountryData> getCountries()
	{
		return checkoutFacade.getDeliveryCountries();
	}

	@ModelAttribute("titles")
	public Collection<TitleData> getTitles()
	{
		return userFacade.getTitles();
	}

	@ModelAttribute("countryDataMap")
	public Map<String, CountryData> getCountryDataMap()
	{
		final Map<String, CountryData> countryDataMap = new HashMap<>();
		for (final CountryData countryData : getCountries())
		{
			countryDataMap.put(countryData.getIsocode(), countryData);
		}
		return countryDataMap;
	}


	@GetMapping("/addressform")
	public String getCountryAddressForm(@RequestParam("addressCode") final String addressCode,
			@RequestParam("countryIsoCode") final String countryIsoCode, final Model model)
	{
		model.addAttribute("supportedCountries", getCountries());
		populateModelRegionAndCountry(model, countryIsoCode);

		final AddressForm addressForm = new AddressForm();
		model.addAttribute(ADDRESS_FORM_ATTR, addressForm);
		for (final AddressData addressData : userFacade.getAddressBook())
		{
			if (addressData.getId() != null && addressData.getId().equals(addressCode)
					&& countryIsoCode.equals(addressData.getCountry().getIsocode()))
			{
				model.addAttribute(ADDRESS_DATA_ATTR, addressData);
				addressDataUtil.convert(addressData, addressForm);
				break;
			}
		}
		return ControllerConstants.Views.Fragments.Account.CountryAddressForm;
	}

	protected void populateModelRegionAndCountry(final Model model, final String countryIsoCode)
	{
		model.addAttribute(REGIONS_ATTR, getI18NFacade().getRegionsForCountryIso(countryIsoCode));
		model.addAttribute(COUNTRY_ATTR, countryIsoCode);
	}

	@GetMapping
	@RequireHardLogIn
	public String account(final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (ResponsiveUtils.isResponsive())
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "system.error.page.not.found", null);
			return REDIRECT_PREFIX + "/";
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ACCOUNT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ACCOUNT_CMS_PAGE));
		if ((getContentPageForLabelOrId(ACCOUNT_CMS_PAGE)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(ACCOUNT_CMS_PAGE)).getBackgroundImage().getURL());
		}
		model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs(null));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		return getViewForPage(model);
	}

	@GetMapping("/enquiries")
	@RequireHardLogIn
	public String enquiries(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode,final Model model,final HttpServletRequest request) throws CMSItemNotFoundException
	{
		// Handle paged search results
		final PageableData pageableData = createPageableData(page,
				Integer.parseInt(this.asahiConfigurationService.getString(ENQUIRY_HISTORY_LIST_SIZE, "10")),sortCode,showMode);
		
		SearchPageData<ApbContactUsData> searchPageData = new SearchPageData<>();
		
		try
		{
		searchPageData = customerFacade.getAllEnquiries(pageableData);
		populateModel(model, searchPageData, showMode);
		}
		catch(ParseException ex)
		{
			searchPageData.setPagination(createEmptyPagination());
			searchPageData.setResults(Collections.EMPTY_LIST);
			searchPageData.setSorts(Collections.EMPTY_LIST);
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(ENQUIRY_HISTORY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ENQUIRY_HISTORY_CMS_PAGE));
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs("text.account.myEnquiries"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		
		HttpSession session = request.getSession();
		String zoneOffset = (String)session.getAttribute("timezone");
		request.getSession().setAttribute("timezone", zoneOffset);
		
		return getViewForPage(model);
		//return searchPageData;
	}

	@GetMapping("/orders")
	@RequireHardLogIn
	public String orders(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode,
			@RequestParam(value = "startDate", required = false) final String startDate,
			@RequestParam(value = "endDate", required = false) final String endDate,final Model model,final HttpServletRequest request) throws CMSItemNotFoundException
	{
		// Handle paged search results
		final PageableData pageableData = createPageableData(page,
				Integer.parseInt(this.asahiConfigurationService.getString(ORDER_HISTORY_LIST_SIZE, "10")),sortCode,showMode);
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		LOG.info("startdate in order is", pageableData.getStartDate());
		final UserModel user = this.userService.getCurrentUser();
		final B2BCustomerModel b2bCustModel = (B2BCustomerModel) user;
		final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel) b2bCustModel.getDefaultB2BUnit();
		final String cofoDate = b2bUnit.getCooDate();
		LOG.info("startdate in order is [{}] and cofodate is [{}]", pageableData.getStartDate(), cofoDate);

		pageableData.setStartDate(startDate);

		pageableData.setEndDate(endDate);

		SearchPageData<OrderHistoryData> searchPageData = new SearchPageData<>();

		try {
			searchPageData = this.sabmOrderFacade.getPagedOrderHistory(pageableData, cofoDate);
			populateModel(model, searchPageData, showMode);
		} catch (ParseException ex) {
			LOG.error("Error occured while parsing the start" + startDate + " or end Date " + endDate);
			searchPageData.setPagination(createEmptyPagination());
			searchPageData.setResults(Collections.EMPTY_LIST);
			searchPageData.setSorts(Collections.EMPTY_LIST);
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_HISTORY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_HISTORY_CMS_PAGE));
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderHistory"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		
		model.addAttribute("orderExportAvailable",this.asahiConfigurationService.getString("sga.order.export.available", "false"));
		
		model.addAttribute("orderRange",this.asahiConfigurationService.getString("sga.order.history.duration", "30"));
		
		HttpSession session = request.getSession();
		String zoneOffset = (String)session.getAttribute("timezone");
		request.getSession().setAttribute("timezone", zoneOffset);
		
		setAdditionalInfoForSga(model);

		
		return getViewForPage(model);
	}
	
	@GetMapping("/orders/exportcsv")
	@ResponseBody
	@RequireHardLogIn
	public String exportOrders(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode,
			@RequestParam(value = "startDate", required = false) final String startDate,
			@RequestParam(value = "endDate", required = false) final String endDate, final Model model,final HttpServletRequest request) throws CMSItemNotFoundException
	{
		// Handle paged search results
		final PageableData pageableData = createPageableData(page,
				Integer.parseInt(this.asahiConfigurationService.getString(ORDER_HISTORY_EXPORT_LIST_SIZE, "1000000")),sortCode,showMode);
		final UserModel user = this.userService.getCurrentUser();
		final B2BCustomerModel b2bCustModel = (B2BCustomerModel) user;
		final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel) b2bCustModel.getDefaultB2BUnit();
		final String cofoDate = b2bUnit.getCooDate();

		if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
			pageableData.setStartDate(startDate);
			pageableData.setEndDate(endDate);
		}

		try {
			String url = this.sabmOrderFacade.exportOrderCSV(pageableData, cofoDate);
			return url;
		} catch (ParseException ex) {
			LOG.error("Error occured while parsing the start" + startDate + " or end Date " + endDate);
		}
		return null;
	}

	@GetMapping("/order/" + ORDER_CODE_PATH_VARIABLE_PATTERN)
	@RequireHardLogIn
	public String order(@PathVariable("orderCode") final String orderCode, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		try
		{
			final OrderData orderDetails = sabmOrderFacade.getOrderDetailsForCode(orderCode);
			
			model.addAttribute("orderData", orderDetails);
			if(asahiSiteUtil.isSga() && orderDetails.getShowExclusionError() !=null && orderDetails.getShowExclusionError().booleanValue()){
				if(null != orderDetails.getAllProductExcluded() &&  orderDetails.getAllProductExcluded().booleanValue())
				{
					GlobalMessages.addErrorMessage(model, "sga.allunavailable.error.message");
				}
				else{
					GlobalMessages.addErrorMessage(model, "sga.orderdetails.exclusion.error.message");
				}
			}
			
			final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
			breadcrumbs.add(new Breadcrumb("/my-account/orders",
					getMessageSource().getMessage("text.account.orderHistory", null, getI18nService().getCurrentLocale()), null));
			breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage("text.account.order.orderBreadcrumb", new Object[]
			{ orderDetails.getCode() }, "Order {0}", getI18nService().getCurrentLocale()), null));
			model.addAttribute(BREADCRUMBS_ATTR, breadcrumbs);
			model.addAttribute("viewAllQuantity",
					Integer.getInteger(asahiConfigurationService.getString(VIEW_ALL_PRODUCTS_QUANTITY, "3"), 3));
			model.addAttribute(ADD_SURCHARGE, sabmCartFacade.isAddSurcharge());


		}
		catch (final UnknownIdentifierException e)
		{
			LOG.warn("Attempted to load a order that does not exist or is not visible", e);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "system.error.page.not.found", null);
			return REDIRECT_TO_ORDER_HISTORY_PAGE;
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));
		
		setAdditionalInfoForSga(model);
		
		return getViewForPage(model);
	}

	@GetMapping("/order/" + ORDER_CODE_PATH_VARIABLE_PATTERN
			+ "/getReadOnlyProductVariantMatrix")
	@RequireHardLogIn
	public String getProductVariantMatrixForResponsive(@PathVariable("orderCode") final String orderCode,
			@RequestParam("productCode") final String productCode, final Model model) throws CMSItemNotFoundException
	{
		final OrderData orderData = orderFacade.getOrderDetailsForCodeWithoutUser(orderCode);

		final Map<String, ReadOnlyOrderGridData> readOnlyMultiDMap = orderGridFormFacade.getReadOnlyOrderGridForProductInOrder(
				productCode, Arrays.asList(ProductOption.BASIC, ProductOption.CATEGORIES), orderData);
		model.addAttribute("readOnlyMultiDMap", readOnlyMultiDMap);

		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}

		return ControllerConstants.Views.Fragments.Checkout.ReadOnlyExpandedOrderForm;
	}

	@GetMapping("/profile")
	@RequireHardLogIn
	public String profile(final Model model) throws CMSItemNotFoundException
	{
		final List<TitleData> titles = userFacade.getTitles();

		final CustomerData customerData = customerFacade.getCurrentCustomer();
		if (customerData.getTitleCode() != null)
		{
			model.addAttribute("title", findTitleForCode(titles, customerData.getTitleCode()));
		}

		model.addAttribute("customerData", customerData);

		storeCmsPageInModel(model, getContentPageForLabelOrId(PROFILE_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PROFILE_CMS_PAGE));
		model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs(TEXT_ACCOUNT_PROFILE));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	protected TitleData findTitleForCode(final List<TitleData> titles, final String code)
	{
		if (code != null && !code.isEmpty() && titles != null && !titles.isEmpty())
		{
			for (final TitleData title : titles)
			{
				if (code.equals(title.getCode()))
				{
					return title;
				}
			}
		}
		return null;
	}

	@GetMapping("/update-email")
	@RequireHardLogIn
	public String editEmail(final Model model) throws CMSItemNotFoundException
	{
		final CustomerData customerData = customerFacade.getCurrentCustomer();
		final UpdateEmailForm updateEmailForm = new UpdateEmailForm();

		updateEmailForm.setEmail(customerData.getDisplayUid());

		model.addAttribute("updateEmailForm", updateEmailForm);

		storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_EMAIL_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_EMAIL_CMS_PAGE));
		model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs(TEXT_ACCOUNT_PROFILE));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@PostMapping("/update-email")
	@RequireHardLogIn
	public String updateEmail(final UpdateEmailForm updateEmailForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		getEmailValidator().validate(updateEmailForm, bindingResult);
		String returnAction = REDIRECT_TO_UPDATE_EMAIL_PAGE;

		if (!bindingResult.hasErrors() && !updateEmailForm.getEmail().equals(updateEmailForm.getChkEmail()))
		{
			bindingResult.rejectValue("chkEmail", "validation.checkEmail.equals", new Object[] {}, "validation.checkEmail.equals");
		}

		if (bindingResult.hasErrors())
		{
			returnAction = setErrorMessagesAndCMSPage(model, UPDATE_EMAIL_CMS_PAGE);
		}
		else
		{
			try
			{
				customerFacade.changeUid(updateEmailForm.getEmail(), updateEmailForm.getPassword());
				GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
						"text.account.profile.confirmationUpdated", null);

				// Replace the spring security authentication with the new UID
				final String newUid = customerFacade.getCurrentCustomer().getUid().toLowerCase();
				final Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
				final UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(newUid, null,
						oldAuthentication.getAuthorities());
				newAuthentication.setDetails(oldAuthentication.getDetails());
				SecurityContextHolder.getContext().setAuthentication(newAuthentication);
			}
			catch (final DuplicateUidException e)
			{
				bindingResult.rejectValue("email", "profile.email.unique");
				returnAction = setErrorMessagesAndCMSPage(model, UPDATE_EMAIL_CMS_PAGE);
			}
			catch (final PasswordMismatchException passwordMismatchException)
			{
				bindingResult.rejectValue("password", PROFILE_CURRENT_PASSWORD_INVALID);
				returnAction = setErrorMessagesAndCMSPage(model, UPDATE_EMAIL_CMS_PAGE);
			}
		}

		return returnAction;
	}

	protected String setErrorMessagesAndCMSPage(final Model model, final String cmsPageLabelOrId) throws CMSItemNotFoundException
	{
		GlobalMessages.addErrorMessage(model, FORM_GLOBAL_ERROR);
		storeCmsPageInModel(model, getContentPageForLabelOrId(cmsPageLabelOrId));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(cmsPageLabelOrId));
		model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs(TEXT_ACCOUNT_PROFILE));
		
		if(asahiSiteUtil.isSga() && UPDATE_PROFILE_CMS_PAGE.equalsIgnoreCase(cmsPageLabelOrId) ){
			AsahiNotificationData notifications = asahiNotificationFacade.getUserNotificationPreferences();
			final Gson gson = new Gson();
			model.addAttribute("notifications", gson.toJson(notifications));
			
		}
		return getViewForPage(model);
	}


	@GetMapping("/update-profile")
	@RequireHardLogIn
	public String editProfile(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute(TITLE_DATA_ATTR, userFacade.getTitles());

		final CustomerData customerData = customerFacade.getCurrentCustomer();
		final ApbUpdateProfileForm updateProfileForm = new ApbUpdateProfileForm();
		updateProfileForm.setEmailAddress(customerData.getUid());
		updateProfileForm.setMobileNumber(customerData.getContactNumber());
		updateProfileForm.setFirstName(customerData.getFirstName());
		updateProfileForm.setLastName(customerData.getLastName());
		updateProfileForm.setTitleCode(customerData.getTitleCode());
		if(asahiSiteUtil.isSga()){
			AsahiNotificationData notifications = asahiNotificationFacade.getUserNotificationPreferences();
			final Gson gson = new Gson();
			model.addAttribute("notifications", gson.toJson(notifications));
			
		}
		
		model.addAttribute("apbUpdateProfileForm", updateProfileForm);
		

		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PROFILE_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PROFILE_CMS_PAGE));

		model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs(asahiSiteUtil.isSga()?ACCOUNT_PROFILE_UPDATE_TITLE:TEXT_ACCOUNT_PROFILE));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}



	@PostMapping("/update-profile")
	@RequireHardLogIn
	public String updateProfile(final ApbUpdateProfileForm updateProfileForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		getProfileValidator().validate(updateProfileForm, bindingResult);

		String returnAction = REDIRECT_TO_UPDATE_PROFILE;
		final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
		final CustomerData customerData = new CustomerData();
		if(asahiSiteUtil.isApb()) {
			customerData.setTitleCode(updateProfileForm.getTitleCode());
		} else {
			customerData.setTitleCode(currentCustomerData.getTitleCode());
		}
		customerData.setFirstName(updateProfileForm.getFirstName());
		customerData.setLastName(updateProfileForm.getLastName());
		customerData.setUid(currentCustomerData.getUid());
		customerData.setDisplayUid(currentCustomerData.getDisplayUid());
		customerData.setContactNumber(updateProfileForm.getMobileNumber());
		
		
		model.addAttribute(TITLE_DATA_ATTR, userFacade.getTitles());

		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PROFILE_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PROFILE_CMS_PAGE));

		if (bindingResult.hasErrors())
		{
			returnAction = setErrorMessagesAndCMSPage(model, UPDATE_PROFILE_CMS_PAGE);
		}
		else
		{
			try
			{
				if(asahiSiteUtil.isSga()) {
					customerData.setDisableEmailNotification(Boolean.FALSE);
					asahiNotificationFacade.saveNotificationPreferences(populatePreferenceDataFromProfileForm(updateProfileForm.getNotificationPrefs()));
				}
				customerFacade.updateProfile(customerData);
				
				GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
						"text.account.profile.confirmationUpdated", null);

			}
			catch (final DuplicateUidException e)
			{
				bindingResult.rejectValue("email", "registration.error.account.exists.title");
				returnAction = setErrorMessagesAndCMSPage(model, UPDATE_PROFILE_CMS_PAGE);
			}
		}


		model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs(asahiSiteUtil.isSga()?ACCOUNT_PROFILE_UPDATE_TITLE:TEXT_ACCOUNT_PROFILE));
		return returnAction;
	}

	/**
	 * Populate preference data from profile form.
	 *
	 * @param notificationPrefs the notification prefs
	 * @return the list
	 */
	private List<AsahiNotificationPreferenceData> populatePreferenceDataFromProfileForm(
			List<AsahiNotificationPrefForm> notificationPrefs) {
		List<AsahiNotificationPreferenceData> preferences = new ArrayList<AsahiNotificationPreferenceData>();
		
		for(AsahiNotificationPrefForm pref:notificationPrefs) {
			AsahiNotificationPreferenceData preference = new AsahiNotificationPreferenceData();
			preference.setNotificationType(pref.getNotificationType());
			preference.setEmailEnabled(pref.getEmailEnabled());
			preferences.add(preference);
		}
	
		return preferences;
	}

	@GetMapping("/update-password")
	@RequireHardLogIn
	public String updatePassword(final Model model) throws CMSItemNotFoundException
	{
		final UpdatePasswordForm updatePasswordForm = new UpdatePasswordForm();

		model.addAttribute("updatePasswordForm", updatePasswordForm);

		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		model.addAttribute("userEmailId", getUser().getUid());
		storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));

		model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile.updatePasswordForm"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@GetMapping("/update-password/success")
	@RequireHardLogIn
	public String updatePasswordSuccess(final Model model) throws CMSItemNotFoundException
	{
		final UpdatePasswordForm updatePasswordForm = new UpdatePasswordForm();

		model.addAttribute("updatePasswordForm", updatePasswordForm);

		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PASSWORD_SUCCESS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PASSWORD_SUCCESS_CMS_PAGE));

		model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile.updatePasswordForm"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@PostMapping("/update-password")
	@RequireHardLogIn
	public String updatePassword(final UpdatePasswordForm updatePasswordForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		//getPasswordValidator().validate(updatePasswordForm, bindingResult);
		if (!bindingResult.hasErrors())
		{
			if ((StringUtils.isNotEmpty(updatePasswordForm.getNewPassword())
					&& StringUtils.isNotEmpty(updatePasswordForm.getCheckNewPassword()))
					&& (updatePasswordForm.getNewPassword().equals(updatePasswordForm.getCheckNewPassword()))
					&& validatePasswordPattern(updatePasswordForm.getNewPassword()))
			{
				try
				{
					customerFacade.changePassword(updatePasswordForm.getCurrentPassword(), updatePasswordForm.getNewPassword());
				}
				catch (final PasswordMismatchException localException)
				{
					bindingResult.rejectValue("currentPassword", PROFILE_CURRENT_PASSWORD_INVALID, new Object[] {},
							PROFILE_CURRENT_PASSWORD_INVALID);
					LOG.error("Password Mismatch Exception " + localException.getMessage());
				}
				catch (final InvalidPasswordException localException)
				{
					bindingResult.rejectValue("newPassword", PROFILE_UPDATE_PASSWORD_INVALID, new Object[] {},
							PROFILE_UPDATE_PASSWORD_INVALID);
					LOG.error("Invalid Password Exception " + localException.getMessage());
				}
			}
			else
			{
				validatePasswordForm(updatePasswordForm, bindingResult);
			}
		}

		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, FORM_PASSWORD_ERROR);
			storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));

			if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
			{
				model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
			}
			model.addAttribute("userEmailId", getUser().getUid());
			model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile.updatePasswordForm"));
			return getViewForPage(model);
		}
		else
		{
			return REDIRECT_TO_PASSWORD_UPDATE_SUCCESS_PAGE;
		}
	}

	/**
	 * @param updatePasswordForm
	 * @param bindingResult
	 */
	private void validatePasswordForm(final UpdatePasswordForm updatePasswordForm, final BindingResult bindingResult)
	{
		boolean regexMatch = Boolean.FALSE;
		if (StringUtils.isEmpty(updatePasswordForm.getCurrentPassword()))
		{
			bindingResult.rejectValue("currentPassword", PROFILE_CURRENT_PASSWORD_INVALID, new Object[] {},
					PROFILE_CURRENT_PASSWORD_INVALID);
		}
		if (StringUtils.isEmpty(updatePasswordForm.getNewPassword()))
		{
			bindingResult.rejectValue("newPassword", PROFILE_UPDATE_PASSWORD_INVALID, new Object[] {},
					PROFILE_UPDATE_PASSWORD_INVALID);
		}
		if (StringUtils.isEmpty(updatePasswordForm.getCheckNewPassword()))
		{
			bindingResult.rejectValue("checkNewPassword", PROFILE_UPDATE_PASSWORD_INVALID, new Object[] {},
					PROFILE_UPDATE_PASSWORD_INVALID);
		}
		if (StringUtils.isNotEmpty(updatePasswordForm.getNewPassword())
				&& !validatePasswordPattern(updatePasswordForm.getNewPassword()))
		{
			bindingResult.rejectValue("newPassword", PROFILE_UPDATE_PASSWORD_INVALID, new Object[] {},
					PROFILE_UPDATE_PASSWORD_INVALID);
			regexMatch = Boolean.TRUE;
		}
		if (StringUtils.isNotEmpty(updatePasswordForm.getCheckNewPassword())
				&& !validatePasswordPattern(updatePasswordForm.getNewPassword()))
		{
			bindingResult.rejectValue("checkNewPassword", PROFILE_UPDATE_PASSWORD_INVALID, new Object[] {},
					PROFILE_UPDATE_PASSWORD_INVALID);
		}
		if (!regexMatch && StringUtils.isNotEmpty(updatePasswordForm.getNewPassword())
				&& StringUtils.isNotEmpty(updatePasswordForm.getCheckNewPassword())
				&& !StringUtils.equals(updatePasswordForm.getNewPassword(), updatePasswordForm.getCheckNewPassword()))
		{
			bindingResult.rejectValue("checkNewPassword", "validation.checkPwd.equals", new Object[] {},
					"validation.checkPwd.equals");
		}

	}

	protected boolean validatePasswordPattern(final String pwd)
	{
		final String pwdPattern = this.asahiConfigurationService.getString(PWD + getCmsSiteService().getCurrentSite().getUid(), "");
		final Pattern pattern = Pattern.compile(pwdPattern);
		final Matcher matcher = pattern.matcher(pwd);
		return matcher.matches();
	}


	@GetMapping("/address-book")
	@RequireHardLogIn
	public String getAddressBook(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute(ADDRESS_DATA_ATTR, userFacade.getAddressBook());

		storeCmsPageInModel(model, getContentPageForLabelOrId(ADDRESS_BOOK_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADDRESS_BOOK_CMS_PAGE));
		model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs(TEXT_ACCOUNT_ADDRESS_BOOK));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@GetMapping("/add-address")
	@RequireHardLogIn
	public String addAddress(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute(COUNTRY_DATA_ATTR, checkoutFacade.getDeliveryCountries());
		model.addAttribute(TITLE_DATA_ATTR, userFacade.getTitles());
		final AddressForm addressForm = getPreparedAddressForm();
		model.addAttribute(ADDRESS_FORM_ATTR, addressForm);
		model.addAttribute(ADDRESS_BOOK_EMPTY_ATTR, Boolean.valueOf(userFacade.isAddressBookEmpty()));
		model.addAttribute(IS_DEFAULT_ADDRESS_ATTR, Boolean.FALSE);
		storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));

		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb(MY_ACCOUNT_ADDRESS_BOOK_URL,
				getMessageSource().getMessage(TEXT_ACCOUNT_ADDRESS_BOOK, null, getI18nService().getCurrentLocale()), null));
		breadcrumbs.add(new Breadcrumb("#",
				getMessageSource().getMessage("text.account.addressBook.addEditAddress", null, getI18nService().getCurrentLocale()),
				null));
		model.addAttribute(BREADCRUMBS_ATTR, breadcrumbs);
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	protected AddressForm getPreparedAddressForm()
	{
		final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
		final AddressForm addressForm = new AddressForm();
		addressForm.setFirstName(currentCustomerData.getFirstName());
		addressForm.setLastName(currentCustomerData.getLastName());
		addressForm.setTitleCode(currentCustomerData.getTitleCode());
		return addressForm;
	}

	@PostMapping("/add-address")
	@RequireHardLogIn
	public String addAddress(final AddressForm addressForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		getAddressValidator().validate(addressForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, FORM_GLOBAL_ERROR);
			storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
			setUpAddressFormAfterError(addressForm, model);
			return getViewForPage(model);
		}

		final AddressData newAddress = addressDataUtil.convertToVisibleAddressData(addressForm);

		if (userFacade.isAddressBookEmpty())
		{
			newAddress.setDefaultAddress(true);
		}
		else
		{
			newAddress.setDefaultAddress(addressForm.getDefaultAddress() != null && addressForm.getDefaultAddress().booleanValue());
		}

		final AddressVerificationResult<AddressVerificationDecision> verificationResult = getAddressVerificationFacade()
				.verifyAddressData(newAddress);
		final boolean addressRequiresReview = getAddressVerificationResultHandler().handleResult(verificationResult, newAddress,
				model, redirectModel, bindingResult, getAddressVerificationFacade().isCustomerAllowedToIgnoreAddressSuggestions(),
				"checkout.multi.address.added");

		populateModelRegionAndCountry(model, addressForm.getCountryIso());
		model.addAttribute("edit", Boolean.TRUE);
		model.addAttribute(IS_DEFAULT_ADDRESS_ATTR, Boolean.valueOf(isDefaultAddress(addressForm.getAddressId())));

		if (addressRequiresReview)
		{
			storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
			return getViewForPage(model);
		}

		userFacade.addAddress(newAddress);


		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.address.added",
				null);

		return REDIRECT_TO_EDIT_ADDRESS_PAGE + newAddress.getId();
	}

	protected void setUpAddressFormAfterError(final AddressForm addressForm, final Model model)
	{
		model.addAttribute(COUNTRY_DATA_ATTR, checkoutFacade.getDeliveryCountries());
		model.addAttribute(TITLE_DATA_ATTR, userFacade.getTitles());
		model.addAttribute(ADDRESS_BOOK_EMPTY_ATTR, Boolean.valueOf(userFacade.isAddressBookEmpty()));
		model.addAttribute(IS_DEFAULT_ADDRESS_ATTR, Boolean.valueOf(isDefaultAddress(addressForm.getAddressId())));
		if (addressForm.getCountryIso() != null)
		{
			populateModelRegionAndCountry(model, addressForm.getCountryIso());
		}
	}

	@GetMapping("/edit-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN)
	@RequireHardLogIn
	public String editAddress(@PathVariable("addressCode") final String addressCode, final Model model)
			throws CMSItemNotFoundException
	{
		final AddressForm addressForm = new AddressForm();
		model.addAttribute(COUNTRY_DATA_ATTR, checkoutFacade.getDeliveryCountries());
		model.addAttribute(TITLE_DATA_ATTR, userFacade.getTitles());
		model.addAttribute(ADDRESS_FORM_ATTR, addressForm);
		final List<AddressData> addressBook = userFacade.getAddressBook();
		model.addAttribute(ADDRESS_BOOK_EMPTY_ATTR, Boolean.valueOf(CollectionUtils.isEmpty(addressBook)));


		for (final AddressData addressData : addressBook)
		{
			if (addressData.getId() != null && addressData.getId().equals(addressCode))
			{
				model.addAttribute(REGIONS_ATTR, getI18NFacade().getRegionsForCountryIso(addressData.getCountry().getIsocode()));
				model.addAttribute(COUNTRY_ATTR, addressData.getCountry().getIsocode());
				model.addAttribute(ADDRESS_DATA_ATTR, addressData);
				addressDataUtil.convert(addressData, addressForm);

				if (isDefaultAddress(addressData.getId()))
				{
					addressForm.setDefaultAddress(Boolean.TRUE);
					model.addAttribute(IS_DEFAULT_ADDRESS_ATTR, Boolean.TRUE);
				}
				else
				{
					addressForm.setDefaultAddress(Boolean.FALSE);
					model.addAttribute(IS_DEFAULT_ADDRESS_ATTR, Boolean.FALSE);
				}
				break;
			}
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));

		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb(MY_ACCOUNT_ADDRESS_BOOK_URL,
				getMessageSource().getMessage(TEXT_ACCOUNT_ADDRESS_BOOK, null, getI18nService().getCurrentLocale()), null));
		breadcrumbs.add(new Breadcrumb("#",
				getMessageSource().getMessage("text.account.addressBook.addEditAddress", null, getI18nService().getCurrentLocale()),
				null));
		model.addAttribute(BREADCRUMBS_ATTR, breadcrumbs);
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		model.addAttribute("edit", Boolean.TRUE);
		return getViewForPage(model);
	}

	/**
	 * Method checks if address is set as default
	 *
	 * @param addressId
	 *           - identifier for address to check
	 * @return true if address is default, false if address is not default
	 */
	protected boolean isDefaultAddress(final String addressId)
	{
		final AddressData defaultAddress = userFacade.getDefaultAddress();
		return defaultAddress != null && defaultAddress.getId() != null && defaultAddress.getId().equals(addressId);
	}

	@PostMapping("/edit-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN)
	@RequireHardLogIn
	public String editAddress(final AddressForm addressForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		getAddressValidator().validate(addressForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, FORM_GLOBAL_ERROR);
			storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
			setUpAddressFormAfterError(addressForm, model);
			return getViewForPage(model);
		}

		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

		final AddressData newAddress = addressDataUtil.convertToVisibleAddressData(addressForm);

		if (Boolean.TRUE.equals(addressForm.getDefaultAddress()) || userFacade.getAddressBook().size() <= 1)
		{
			newAddress.setDefaultAddress(true);
		}

		final AddressVerificationResult<AddressVerificationDecision> verificationResult = getAddressVerificationFacade()
				.verifyAddressData(newAddress);
		final boolean addressRequiresReview = getAddressVerificationResultHandler().handleResult(verificationResult, newAddress,
				model, redirectModel, bindingResult, getAddressVerificationFacade().isCustomerAllowedToIgnoreAddressSuggestions(),
				"checkout.multi.address.updated");

		model.addAttribute(REGIONS_ATTR, getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso()));
		model.addAttribute(COUNTRY_ATTR, addressForm.getCountryIso());
		model.addAttribute("edit", Boolean.TRUE);
		model.addAttribute(IS_DEFAULT_ADDRESS_ATTR, Boolean.valueOf(isDefaultAddress(addressForm.getAddressId())));

		if (addressRequiresReview)
		{
			storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
			return getViewForPage(model);
		}

		userFacade.editAddress(newAddress);

		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.address.updated",
				null);
		return REDIRECT_TO_EDIT_ADDRESS_PAGE + newAddress.getId();
	}

	@PostMapping("/select-suggested-address")
	public String doSelectSuggestedAddress(final AddressForm addressForm, final RedirectAttributes redirectModel)
	{
		final Set<String> resolveCountryRegions = org.springframework.util.StringUtils
				.commaDelimitedListToSet(Config.getParameter("resolve.country.regions"));

		final AddressData selectedAddress = addressDataUtil.convertToVisibleAddressData(addressForm);

		final CountryData countryData = selectedAddress.getCountry();

		if (!resolveCountryRegions.contains(countryData.getIsocode()))
		{
			selectedAddress.setRegion(null);
		}

		if (Boolean.TRUE.equals(addressForm.getEditAddress()))
		{
			userFacade.editAddress(selectedAddress);
		}
		else
		{
			userFacade.addAddress(selectedAddress);
		}

		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.address.added");

		return REDIRECT_TO_ADDRESS_BOOK_PAGE;
	}

	@RequestMapping(value = "/remove-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN, method =
	{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public String removeAddress(@PathVariable("addressCode") final String addressCode, final RedirectAttributes redirectModel)
	{
		final AddressData addressData = new AddressData();
		addressData.setId(addressCode);
		userFacade.removeAddress(addressData);

		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.address.removed");
		return REDIRECT_TO_ADDRESS_BOOK_PAGE;
	}

	@GetMapping("/set-default-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN)
	@RequireHardLogIn
	public String setDefaultAddress(@PathVariable("addressCode") final String addressCode, final RedirectAttributes redirectModel)
	{
		final AddressData addressData = new AddressData();
		addressData.setDefaultAddress(true);
		addressData.setVisibleInAddressBook(true);
		addressData.setId(addressCode);
		userFacade.setDefaultAddress(addressData);
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
				"account.confirmation.default.address.changed");
		return REDIRECT_TO_ADDRESS_BOOK_PAGE;
	}

	@GetMapping("/payment-details")
	@RequireHardLogIn
	public String paymentDetails(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("customerData", customerFacade.getCurrentCustomer());
		model.addAttribute("paymentInfoData", userFacade.getCCPaymentInfos(true));
		storeCmsPageInModel(model, getContentPageForLabelOrId(PAYMENT_DETAILS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
		model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs("text.account.paymentDetails"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@PostMapping("/set-default-payment-details")
	@RequireHardLogIn
	public String setDefaultPaymentDetails(@RequestParam final String paymentInfoId)
	{
		CCPaymentInfoData paymentInfoData = null;
		if (StringUtils.isNotBlank(paymentInfoId))
		{
			paymentInfoData = userFacade.getCCPaymentInfoForCode(paymentInfoId);
		}
		userFacade.setDefaultPaymentInfo(paymentInfoData);
		return REDIRECT_TO_PAYMENT_INFO_PAGE;
	}

	@PostMapping("/remove-payment-method")
	@RequireHardLogIn
	public String removePaymentMethod(@RequestParam(value = "paymentInfoId") final String paymentMethodId,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		userFacade.unlinkCCPaymentInfo(paymentMethodId);
		GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
				"text.account.profile.paymentCart.removed");
		return REDIRECT_TO_PAYMENT_INFO_PAGE;
	}

	private PaginationData createPaginationData(final int pageNumber, final int pageSize)
	{
		final PaginationData pageableData = new PaginationData();
		pageableData.setCurrentPage(pageNumber);
		pageableData.setPageSize(pageSize);
		return pageableData;
	}
	
	private void setAdditionalInfoForSga(final Model model)
	{
		if(asahiSiteUtil.isSga())
		{
			model.addAttribute("isNAPGroup",asahiCoreUtil.isNAPUser());			
			final String accessType = asahiCoreUtil.getCurrentUserAccessType();
			model.addAttribute("isAccessDenied",asahiCoreUtil.isSAMAccessDenied());
			
				final boolean isApprovalPending = asahiCoreUtil.isSAMAccessApprovalPending();
				model.addAttribute("isApprovalPending",isApprovalPending);
				
				model.addAttribute("sgaAccessType",accessType);
				
				if(isApprovalPending)
				{
					model.addAttribute("approvalEmailId",null != asahiCoreUtil.getDefaultB2BUnit() 
							&& null != asahiCoreUtil.getDefaultB2BUnit().getPayerAccount()?
							asahiCoreUtil.getDefaultB2BUnit().getPayerAccount().getEmailAddress(): StringUtils.EMPTY);
				}
		}
	}

	
	
	@GetMapping("/planograms")
	@RequireHardLogIn
	public String getPlanograms(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
		
		final List<PlanogramData> additionalPlanograms = apbUserFacade.getPlanogramsForB2BUnit();
		model.addAttribute("additionalPlanograms", additionalPlanograms);
		model.addAttribute("planogramUpdateForm", new PlanogramUpdateForm());
		final String pdfUploadSize = asahiConfigurationService.getString(
				ApbStoreFrontContants.IMPORT_PDF_FILE_MAX_SIZE_BYTES_KEY + getCmsSiteService().getCurrentSite().getUid(), "0");
		model.addAttribute("pdfFileMaxSize", Long.parseLong(pdfUploadSize));
		
		/*
		 * Business User should upload only one default planogram for a customer account
		 * at CL2/3/4. As Hybris does not maintain the levels all CL associated planograms are retrived
		 */
		
		final List<PlanogramData> defaultPlanogram = apbUserFacade.getDefaultPlanogram();
		model.addAttribute("defaultPlanogram", defaultPlanogram);
		final AsahiB2BUnitModel currentUnit = apbB2BUnitService.getCurrentB2BUnit();
		if (StringUtils.isNotBlank(currentUnit.getSalesRepEmailID())) {
			model.addAttribute("repEmail", currentUnit.getSalesRepEmailID());
		}
		if (StringUtils.isNotBlank(currentUnit.getSalesRepName())) {
			model.addAttribute("repName", currentUnit.getSalesRepName());
		}
		if (StringUtils.isNotBlank(currentUnit.getSalesRepPhone())) {
			model.addAttribute("repPhone", currentUnit.getSalesRepPhone());
		}
	
		storeCmsPageInModel(model, getContentPageForLabelOrId(PLANOGRAM_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PLANOGRAM_PAGE));
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs("text.account.planograms"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
		
	}
	
	@PostMapping("/planograms/add")
	@RequireHardLogIn
	@ResponseBody
	public String addPlanogram(@Valid final PlanogramUpdateForm form, final BindingResult bindingResult,final Model model, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException {
		
			if (StringUtils.isBlank(form.getDocumentName())){
				GlobalMessages.addErrorMessage(model, "form.global.error");
				bindingResult.rejectValue("documentName", "text.planogram.documentname.mandatory.field", new Object[] {}, "text.planogram.documentname.mandatory.field");
				return "ERROR";
			}
			
			if (null == form.getFile()){
				GlobalMessages.addErrorMessage(model, "form.global.error");
				bindingResult.rejectValue("file", "text.planogram.missing.file.error", new Object[] {}, "text.planogram.missing.file.error");
				return "ERROR";
			}
			importRequestRegistrationPDFFormValidator.validate(form, bindingResult);
			if (bindingResult.hasErrors()) {
				model.addAttribute(form);
				GlobalMessages.addErrorMessage(model, ApbStoreFrontContants.FORM_GLOBAL_ERROR);
				model.addAttribute("errors", bindingResult.getFieldErrors());
				return ControllerConstants.Views.Pages.Planogram.PlanogramPage;
			}
			PlanogramData formData = new PlanogramData();
			formData.setDocumentName(form.getDocumentName());
			formData.setFile(form.getFile());
			boolean success = apbUserFacade.savePlanogram(formData);
			if (success) {
				return "SUCCESS";
			} else {
				return "ERROR";
			}
			
	}
	
	@PostMapping("/planograms/remove")
	@RequireHardLogIn
	@ResponseBody
	public String removePlanogram(@RequestBody @Valid final PlanogramUpdateForm form, final BindingResult bindingResult, final Model model)
			throws CMSItemNotFoundException {
			
		if (StringUtils.isBlank(form.getCode())){
			GlobalMessages.addErrorMessage(model, "form.global.error");
			bindingResult.rejectValue("planogramCode", "text.planogram.code.mandatory.field", new Object[] {}, "text.planogram.code.mandatory.field");
			return "ERROR";
		}
			apbUserFacade.removePlanogram(form.getCode());
			return "SUCCESS";
	}
	
	@PostMapping("/planograms/bulkRemove")
	@RequireHardLogIn
	@ResponseBody
	public String removeAllPlanograms(final Model model)
			throws CMSItemNotFoundException {
			
			apbUserFacade.removeAllPlanograms();
			return "SUCCESS";
	}
	
	@Produces("application/pdf,image/jpeg,image/png,image/jpg")
	@GetMapping("/planograms/view")
	public ResponseEntity<byte[]> viewPlanogram(@RequestParam(value = "planogramCode") final String planogramCode,final HttpServletRequest request, final HttpServletResponse response)
	{	
		
			final PlanogramModel model = apbCustomerAccountService.fetchPlanogramByCode(planogramCode);

			if (null != model && null != model.getMedia()) {
				try {
					final InputStream inputStream = mediaService.getStreamFromMedia(model.getMedia());
					final byte[] asBytes = IOUtils.toByteArray(inputStream);

					final HttpHeaders headers = new HttpHeaders();
					
					String fileContentType = model.getMedia().getMime();
					String fileName = model.getMedia().getRealFileName();
					if ((APPLICATION_PDF_CONTENT_TYPE.equalsIgnoreCase(fileContentType) && fileName.toLowerCase().endsWith(PDF_FILE_EXTENSION))){					
						headers.setContentType(MediaType.parseMediaType(PDF_APPLICATION_TYPE));
					} 
					else if  ((APP_IMAGE_JPEG_CONTENT_TYPE.equalsIgnoreCase(fileContentType)
								&& (fileName.toLowerCase().endsWith(JPEG_FILE_EXTENSION) || fileName.toLowerCase().endsWith(JPG_FILE_EXTENSION)))
								|| (APP_IMAGE_PNG_CONTENT_TYPE.equalsIgnoreCase(fileContentType) && fileName.toLowerCase().endsWith(PNG_FILE_EXTENSION))
								|| ((APP_IMAGE_JPG_CONTENT_TYPE.equalsIgnoreCase(fileContentType))
										&& (fileName.toLowerCase().endsWith(JPG_FILE_EXTENSION) || fileName.toLowerCase().endsWith(JPEG_FILE_EXTENSION))))
					{
						headers.setContentType(MediaType.parseMediaType(fileContentType));
					}
					headers.add("Access-Control-Allow-Origin", "*");
					headers.add("Access-Control-Allow-Methods", "GET, POST, PUT");
					headers.add("Access-Control-Allow-Headers", "Content-Type");
					headers.add("Cache-Control", PDF_CACHE_CONTROL);
					headers.add("Pragma", "no-cache");
					headers.add("Expires", "0");
					final ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(asBytes, headers, HttpStatus.OK);
					return responseEntity;
				} catch (final Exception ex) {
					LOG.info("Error has occured while downloading the pdf");
				}
			}

			return new ResponseEntity("No planogram found for code :" + planogramCode, HttpStatus.NOT_FOUND);


	}
	
	@GetMapping("/isExistingUser" + "/{emailId:.*}")
	@ResponseBody
	public String checkIfExistingUser(@PathVariable("emailId") final String emailId,  @RequestParam(value ="createUser", defaultValue = "false") final String createUser) throws CMSItemNotFoundException
	{
		final UserModel user = asahiCoreUtil.checkIfUserExists(emailId);
		if (null != user && sabmB2BCustomerService.checkIfUserRegisteredForOtherSites(user, BooleanUtils.toBoolean(createUser))) {
			return "TRUE";
		}
		return "FALSE";
	
	}
}
