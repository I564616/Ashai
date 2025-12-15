/**
 *
 */
package com.sabmiller.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sabmiller.core.b2b.services.SabmB2BCustomerService;
import com.sabmiller.facades.pa.search.PersonalAssistanceSearchFacade;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.storefront.form.PersonalAssistanceForm;
import com.sabmiller.storefront.security.ImpersonateUserLoginStrategy;
import com.sabmiller.storefront.controllers.pages.SabmAbstractPageController;


/**
 * @author dale.bryan.a.mercado
 *
 */
@Controller
@RequestMapping(value = "/paSearch")
public class PersonalAssistancePageController extends SabmAbstractPageController
{
	private static final Logger LOG = LoggerFactory.getLogger(PersonalAssistancePageController.class.getName());

	private static final String PERSONAL_ASSISTANCE_SEARCH_PAGE = "personalAssistanceSearch";
	private static final String PERSONAL_ASSISTANCE_SEARCH_RESULTS_PAGE = "personalAssistanceSearchResult";
	private static final String REQUEST_TYPE_ACCOUNT = "account";
	private static final String REQUEST_TYPE_CUSTOMER = "customer";
	private static final String REQUEST_TYPE_USER = "user";

	private static final int FIELDS_MIN_LENGTH = 3;

	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder homeBreadcrumbBuilder;

	@Resource(name = "personalAssistanceSearchFacade")
	private PersonalAssistanceSearchFacade personalAssistanceSearchFacade;

	@Resource(name = "sabmB2BCustomerService")
	private SabmB2BCustomerService b2bCustomerService;

	@Resource(name = "impersonateUserLoginStrategy")
	private ImpersonateUserLoginStrategy impersonateUserLoginStrategy;

	@Resource(name = "userService")
	private UserService userService;

	@RequireHardLogIn
	@GetMapping
	public String paSearchPage(final Model model) throws CMSItemNotFoundException
	{

		final UserModel currentUser = userService.getCurrentUser();
		if (!(currentUser instanceof EmployeeModel))
		{
			return REDIRECT_PREFIX + "/";
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(PERSONAL_ASSISTANCE_SEARCH_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PERSONAL_ASSISTANCE_SEARCH_PAGE));
		model.addAttribute("breadcrumbs", homeBreadcrumbBuilder.getBreadcrumbs("text.personal.assistance.search"));
		return getViewForPage(model);
	}

	@RequireHardLogIn
	@GetMapping("/results")
	public String paSearchResults(final Model model) throws CMSItemNotFoundException
	{
		return REDIRECT_PREFIX + "/";
	}

	@RequireHardLogIn
	@PostMapping("/results")
	public String resultsPage(@Valid final PersonalAssistanceForm personalAssistanceForm, final BindingResult bindingResult,
			final Model model, final HttpServletRequest request, final HttpServletResponse response)
			throws CMSItemNotFoundException, UnsupportedEncodingException
	{
		LOG.info("Entering personal assistance search...");

		final String requestType = personalAssistanceForm.getRequest_type();
		final String accountNumber = personalAssistanceForm.getAccount_no();
		final String customerNumber = personalAssistanceForm.getCustomer_no();
		final String customerName = personalAssistanceForm.getCustomer_name();
		final String email = personalAssistanceForm.getUser_email();

		//		final String encodedAccountNumber = XSSEncoder.encodeHTML(XSSFilterUtil.filter(accountNumber));
		final String encodedAccountNumber = Encode.forHtml(accountNumber);
		//		final String encodedCustomerNumber = XSSEncoder.encodeHTML(XSSFilterUtil.filter(customerNumber));
		final String encodedCustomerNumber = Encode.forHtml(customerNumber);
		//		final String encodedCustomerName = XSSEncoder.encodeHTML(XSSFilterUtil.filter(customerName));
		final String encodedCustomerName = Encode.forHtml(customerName);
		//		final String encodedEmail = XSSEncoder.encodeHTML(XSSFilterUtil.filter(email));
		final String encodedEmail = Encode.forHtml(email);

		model.addAttribute("requestType", requestType);
		if (REQUEST_TYPE_ACCOUNT.equals(requestType)
				&& (StringUtils.isBlank(encodedAccountNumber) || encodedAccountNumber.length() < FIELDS_MIN_LENGTH))
		{
			bindingResult.addError(new ObjectError("personalAssistanceForm", "Account number invalid."));
		}

		if (REQUEST_TYPE_CUSTOMER.equals(requestType)
				&& ((StringUtils.isBlank(encodedCustomerNumber) || encodedCustomerNumber.length() < FIELDS_MIN_LENGTH)
						&& (StringUtils.isBlank(encodedCustomerName) || encodedCustomerName.length() < FIELDS_MIN_LENGTH)))
		{
			bindingResult.addError(new ObjectError("personalAssistanceForm", "Customer number/name invalid."));
		}

		if (REQUEST_TYPE_USER.equals(requestType)
				&& (StringUtils.isBlank(encodedEmail) || encodedEmail.length() < FIELDS_MIN_LENGTH))
		{
			bindingResult.addError(new ObjectError("personalAssistanceForm", "Email invalid."));
		}

		if (bindingResult.hasErrors())
		{
			storeCmsPageInModel(model, getContentPageForLabelOrId(PERSONAL_ASSISTANCE_SEARCH_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PERSONAL_ASSISTANCE_SEARCH_PAGE));
			model.addAttribute("breadcrumbs", homeBreadcrumbBuilder.getBreadcrumbs("text.personal.assistance.search"));

			//Add customer data to model
			final CustomerData customer = getCustomerFacade().getCurrentCustomer();
			model.addAttribute("customer", customer);
			model.addAttribute("hasErrors", true);
			return getViewForPage(model);
		}

		if (REQUEST_TYPE_USER.equals(requestType))
		{
			final List<B2BCustomerModel> searchB2BCustomerByEmail = b2bCustomerService.searchCustomerByEmail(encodedEmail);
			if (searchB2BCustomerByEmail.size() == 1)
			{
				// redirect to impersonate page
				LOG.info("There is only one result. Now logging in as impersonated user");
				impersonateUserLoginStrategy.loginAsCustomer(searchB2BCustomerByEmail.get(0).getUid(), null, request, response);
				return REDIRECT_PREFIX + "/";
			}
			else
			{
				model.addAttribute("email", encodedEmail);
				model.addAttribute("b2bUnits", personalAssistanceSearchFacade.searchB2BUnitByUser(searchB2BCustomerByEmail));
			}
		}
		else if (REQUEST_TYPE_CUSTOMER.equals(requestType))
		{
			model.addAttribute("customerNumber", encodedCustomerNumber);
			model.addAttribute("customerName", encodedCustomerName);
			model.addAttribute("b2bUnits",
					personalAssistanceSearchFacade.searchB2BUnitByCustomer(encodedCustomerNumber, encodedCustomerName));
		}
		else if (REQUEST_TYPE_ACCOUNT.equals(requestType))
		{
			model.addAttribute("accountNumber", encodedAccountNumber);
			model.addAttribute("b2bUnits", personalAssistanceSearchFacade.searchB2BUnitByAccount(encodedAccountNumber));
		}

		// TODO: PLEASE CHANGE THIS TO PA RESULTS PAGE
		storeCmsPageInModel(model, getContentPageForLabelOrId(PERSONAL_ASSISTANCE_SEARCH_RESULTS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PERSONAL_ASSISTANCE_SEARCH_RESULTS_PAGE));
		final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
		final Breadcrumb breadcrumbSearch = new Breadcrumb("/paSearch",
				getMessageSource().getMessage("text.personal.assistance.search", null, getI18nService().getCurrentLocale()), null);
		final Breadcrumb breadcrumbSearchResult = new Breadcrumb("#",
				getMessageSource().getMessage("text.personal.assistance.search.results", null, getI18nService().getCurrentLocale()),
				null);
		breadcrumbs.add(breadcrumbSearch);
		breadcrumbs.add(breadcrumbSearchResult);
		model.addAttribute("breadcrumbs", breadcrumbs);

		return getViewForPage(model);

	}

	public String escapeRE(final String str)
	{
		final Pattern escaper = Pattern.compile("([^a-zA-z0-9])");
		return escaper.matcher(str).toString();
	}

	@ModelAttribute("pageType")
	protected String getPageType()
	{
		return SABMWebConstants.PageType.PERSONAL_ASSISTANCE.name();
	}

}
