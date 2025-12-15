package com.apb.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

import java.util.Collections;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.facades.user.ApbUserFacade;
import com.apb.facades.user.data.ApbCompanyData;
import com.apb.storefront.constant.ApbStoreFrontContants;
import com.apb.storefront.controllers.ControllerConstants;
import com.apb.storefront.forms.ApbCompanyDetailsForm;
import com.apb.storefront.util.ApbCompanyDataUtil;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.core.service.config.AsahiConfigurationService;


/**
 * @author C5252631
 *
 *         ApbCompanyDetailsPageController implementation of {@link ApbAbstractPageController}
 *
 *         When customer of existing unit. User can change data and send email to customer
 *
 */
@Controller
@RequestMapping(value = "/company")
public class ApbCompanyDetailsPageController extends ApbAbstractPageController
{

	private static final Logger LOG = LoggerFactory.getLogger(ApbCompanyDetailsPageController.class);

	@Resource(name = "apbCompanyDetailsValidator")
	private Validator apbCompanyDetailsValidator;

	@Resource(name = "b2bCustomerFacade")
	private SABMCustomerFacade sabmCustomerFacade;

	@Resource(name = "apbUserFacade")
	private ApbUserFacade apbUserFacade;

	@Resource(name = "apbCompanyDataUtil")
	private ApbCompanyDataUtil apbCompanyDataUtil;
	
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Autowired
	private CMSSiteService cmsSiteService;
	
	@Resource(name = "asahiSiteUtil")
	private com.apb.core.util.AsahiSiteUtil asahiSiteUtil;

	/** The mobile validation pattern. */
	private static final String MOBILE_VALIDATION_KEY = "customer.mobile.validation.pattern.";

	private static final String EMAIL_VALIDATION_KEY = "customer.email.validation.";
	
	/**
	 * ABN number validation pattern
	 */
	private static final String ABN_VALIDATION_KEY = "customer.abn.validation.pattern.";
	/**
	 * Default ABN number validation pattern from ConfigurationItem
	 */
	private static final String DEFAULT_ABN_VALIDATION_KEY = "^\\d{11,11}$";
	
	private static final String DEFAULT_MOBILE_VALIDATION_KEY = "^(?:\\+?(61))? ?(?:\\((?=.*\\)))?(0?[2-57-8])\\)? ?(\\d\\d(?:[- ](?=\\d{3})|(?!\\d\\d[- ]?\\d[- ]))\\d\\d[- ]?\\d[- ]?\\d{3})$";
	
	private static final String DEFAULT_EMAIL_VALIDATION_KEY = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";

	private static final String CUSTOMER_EMAIL_SPLIT = "customer.email.split";

	/**
	 * @param apbCompanyDetailsForm
	 * @param model
	 * @param bindingResult
	 * @param redirectModel
	 * @param request
	 * @return company
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping
	public String company(final ApbCompanyDetailsForm apbCompanyDetailsForm, final Model model, final BindingResult bindingResult,
			final RedirectAttributes redirectModel, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		model.addAttribute(new ApbCompanyDetailsForm());
		storeCmsPageInModel(model, getContentPageForLabelOrId(ApbStoreFrontContants.COMPANY));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ApbStoreFrontContants.COMPANY));
		updatePageTitle(model, getContentPageForLabelOrId(ApbStoreFrontContants.COMPANY));
		final Breadcrumb companyDetails = new Breadcrumb("#",
				getMessageSource().getMessage("header.link.company.details", null, getI18nService().getCurrentLocale()), null);
		model.addAttribute("breadcrumbs", Collections.singletonList(companyDetails));
		final ApbCompanyData companyData = sabmCustomerFacade.getB2BCustomerData();
		model.addAttribute(ApbStoreFrontContants.COMPANYDATA, companyData);
		apbCompanyDataUtil.convert(companyData, apbCompanyDetailsForm);
		model.addAttribute(ApbStoreFrontContants.APB_COMPANY_DETAILS_FORM, apbCompanyDetailsForm);
		addValidationConfigurations(model);
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		if (!checkSuperUser() && !asahiSiteUtil.isSga())
		{
			return REDIRECT_PREFIX + "/";
		}
		else
		{
			return ControllerConstants.Views.Pages.Account.AccountCompanyDetailsPage;
		}
	}

	private void addValidationConfigurations(Model model) 
	{
		model.addAttribute("mobileRegexPattern", getMobileValdationPattern());
		model.addAttribute("emailRegexPattern", getEmailValidationPattern());
		model.addAttribute("abnRegexPattern", getAbnValidationPattern());
		model.addAttribute("apbValidEmailSeparator", getApbEmailSeparator());
		
	}
	
	public String getApbEmailSeparator()
	{
		String emailSeparator = asahiConfigurationService.getString(CUSTOMER_EMAIL_SPLIT, ";");
		String[] separators = emailSeparator.split("");
		StringBuilder sb = new StringBuilder();
		int counter=0;
		for(String separator:separators)
		{
			counter++;
			if(separator.equals(";")) {
				sb.append("semi-colon(;)");
				if(counter==separators.length) {
					sb.append(".");
				}
				else {
					sb.append(" or ");
				}
			}
			if(separator.equals(",")) {
				sb.append("comma(,)");
				if(counter==separators.length) {
					sb.append(".");
				}
				else {
					sb.append(" or ");
				}
			}
		}
		
		return sb.toString();
	}
	
	public String getEmailValidationPattern()
	{
		return asahiConfigurationService.getString(EMAIL_VALIDATION_KEY + cmsSiteService.getCurrentSite().getUid(), DEFAULT_EMAIL_VALIDATION_KEY);
	}
	
	public String getMobileValdationPattern()
	{
		return asahiConfigurationService.getString(MOBILE_VALIDATION_KEY + cmsSiteService.getCurrentSite().getUid(), DEFAULT_MOBILE_VALIDATION_KEY);
	}
	
	public String getAbnValidationPattern()
	{
		return asahiConfigurationService.getString(ABN_VALIDATION_KEY + cmsSiteService.getCurrentSite().getUid(), DEFAULT_ABN_VALIDATION_KEY);
	}

	/**
	 * Request Changes
	 *
	 * @param apbCompanyDetailsForm
	 * @param model
	 * @param bindingResult
	 * @param redirectModel
	 * @param request
	 *
	 * @return the string
	 * @throws CMSItemNotFoundException
	 */
	@PostMapping("/company-details")
	public String companyDetails(ApbCompanyDetailsForm apbCompanyDetailsForm, final Model model, final BindingResult bindingResult,
			final RedirectAttributes redirectModel, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		if (!checkSuperUser() || asahiSiteUtil.isSga())
		{
			return REDIRECT_PREFIX + "/";
		}
		else
		{
			storeCmsPageInModel(model, getContentPageForLabelOrId(ApbStoreFrontContants.COMPANY));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ApbStoreFrontContants.COMPANY));
			updatePageTitle(model, getContentPageForLabelOrId(ApbStoreFrontContants.COMPANY));
			final Breadcrumb companyDetails = new Breadcrumb("#",
					getMessageSource().getMessage("header.link.company.details", null, getI18nService().getCurrentLocale()), null);
			model.addAttribute("breadcrumbs", Collections.singletonList(companyDetails));
			removeTrailingSpaces(apbCompanyDetailsForm);
			apbCompanyDetailsValidator.validate(apbCompanyDetailsForm, bindingResult);
			apbCompanyDetailsForm.setApbCompanyDeliveryAddressForm(apbCompanyDetailsForm.getApbCompanyDeliveryAddressForm());
			final ApbCompanyData companyData = sabmCustomerFacade.getB2BCustomerData();
			model.addAttribute(ApbStoreFrontContants.COMPANYDATA, companyData);
			if (bindingResult.hasErrors())
			{
				addValidationConfigurations(model);
				apbCompanyDataUtil.convertFromDateFrame(companyData, apbCompanyDetailsForm);
				model.addAttribute(apbCompanyDetailsForm);
				GlobalMessages.addErrorMessage(model, ApbStoreFrontContants.FORM_GLOBAL_ERROR);
				model.addAttribute("errors", bindingResult.getFieldErrors());
				return ControllerConstants.Views.Pages.Account.AccountCompanyDetailsPage;
			}
			apbCompanyDetailsForm = apbCompanyDataUtil.checkValueFromDb(apbCompanyDetailsForm);
			apbCompanyDataUtil.setFormDataValue(apbCompanyDetailsForm, companyData);
			apbCompanyDataUtil.convert(companyData, apbCompanyDetailsForm);
			apbCompanyDetailsForm.setSameasInvoiceAddress(apbCompanyDetailsForm.isSameasInvoiceAddress());

			sabmCustomerFacade.updateCompanyDetails(apbCompanyDataUtil.setUpdateCompanyDetailsData(apbCompanyDetailsForm));
			return REDIRECT_PREFIX + "/companyConfirmation";
		}
	}

	private void removeTrailingSpaces(ApbCompanyDetailsForm apbCompanyDetailsForm) 
	{
		apbCompanyDetailsForm.setAbn(apbCompanyDetailsForm.getAbn().replace(" ", ""));
		apbCompanyDetailsForm.setCompanyMobilePhone(apbCompanyDetailsForm.getCompanyMobilePhone().replace(" ", ""));
		apbCompanyDetailsForm.setCompanyPhone(apbCompanyDetailsForm.getCompanyPhone().replace(" ", ""));
		apbCompanyDetailsForm.setCompanyFax(apbCompanyDetailsForm.getCompanyFax().replace(" ", ""));	
	}

	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveContentPageTitle(cmsPage.getTitle()));
	}
}
