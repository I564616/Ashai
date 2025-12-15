package com.apb.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.media.MediaIOException;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.contactus.ApbContactUsFacade;
import com.apb.facades.contactust.data.ApbContactUsData;
import com.apb.facades.contactust.data.AsahiContactUsSaleRepData;
import com.apb.facades.contactust.data.ContactUsQueryTypeData;
import com.apb.storefront.constant.ApbStoreFrontContants;
import com.apb.storefront.controllers.ControllerConstants;
import com.apb.storefront.forms.ApbContactUsForm;
import com.apb.storefront.util.ApbContactUsUtil;
import com.apb.storefront.validators.ApbContactUsValidator;
import com.apb.storefront.validators.ImportRequestRegistrationPDFFormValidator;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import com.apb.core.service.config.AsahiConfigurationService;
import org.springframework.validation.FieldError;

/**
 * @author C5252631
 *
 *         ApbContactUsPageController implementation of {@link ApbAbstractPageController}
 *
 *         ContactWhen customer of existing unit. User can change data and send email to customer
 *
 */
@Controller
@RequestMapping(value = "/contactus")
public class ApbContactUsPageController extends ApbAbstractPageController
{

	private static final Logger LOG = LoggerFactory.getLogger(ApbContactUsPageController.class);

	@Resource(name = "apbContactUsValidator")
	private ApbContactUsValidator apbContactUsValidator;

	@Autowired
	private ApbContactUsFacade apbContactUsFacade;

	@Resource(name = "b2bCustomerFacade")
	private SABMCustomerFacade sabmCustomerFacade;

	@Resource(name = "apbContactUsUtil")
	private ApbContactUsUtil apbContactUsUtil;

	@Resource(name = "importRequestRegistrationPDFFormValidator")
	private ImportRequestRegistrationPDFFormValidator importRequestRegistrationPDFFormValidator;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	UserService userService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	private static final String NEW_USER_REGISTER_REQUEST = "no";

	/**
	 * @param contactUsForm
	 * @param model
	 * @param bindingResult
	 * @param redirectModel
	 * @param request
	 * @return contactUsPage jsp
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping
	public String company(@RequestParam(value = "register", defaultValue = "") final String registerRequest,
			final ApbContactUsForm contactUsForm, final Model model, final BindingResult bindingResult,
			final RedirectAttributes redirectModel, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		model.addAttribute(new ApbContactUsForm());
		storeCmsPageInModel(model, getContentPageForLabelOrId(ApbStoreFrontContants.APB_CONTACT_US));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ApbStoreFrontContants.APB_CONTACT_US));
		updatePageTitle(model, getContentPageForLabelOrId(ApbStoreFrontContants.APB_CONTACT_US));
		final Breadcrumb contactUsBreadcrumb = new Breadcrumb("#",
				getMessageSource().getMessage("header.link.contactus", null, getI18nService().getCurrentLocale()), null);
		model.addAttribute("breadcrumbs", Collections.singletonList(contactUsBreadcrumb));

		//SGA contact us default subject select for anonymous user start.

		final UserModel user = userService.getCurrentUser();
		if (asahiSiteUtil.isSga() && StringUtils.isNotEmpty(registerRequest)
				&& registerRequest.equalsIgnoreCase(NEW_USER_REGISTER_REQUEST))
		{
			setDefaultContactUsSubject(contactUsForm);
		}
		//SGA contact us default subject select for anonymous user end.



		final ApbContactUsData apbContactUsData = sabmCustomerFacade.getLogedInB2BCustomer();

		model.addAttribute("contactusUpdateAvailable",this.asahiConfigurationService.getBoolean("sga.contactus.update.available", false));
		model.addAttribute("enquiriesPageAvailable",this.asahiConfigurationService.getBoolean("sga.enquiries.page.available", false));
		model.addAttribute("requestCallbackAvailable",this.asahiConfigurationService.getBoolean("sga.request.callback.available", false));
		

		if (apbContactUsData != null && (!userService.isAnonymousUser(user)))
		{
			/* Database value set in ContactUsForm */
			apbContactUsUtil.convert(apbContactUsData, contactUsForm);
			
			if(asahiSiteUtil.isSga()){
				model.addAttribute("salesRepName", apbContactUsData.getSalesRepName());
				model.addAttribute("salesRepEmailID", apbContactUsData.getSalesRepEmailID());
				model.addAttribute("salesRepPhone", apbContactUsData.getSalesRepPhone());
				model.addAttribute("payAccess", apbContactUsData.isPayAccess());
			}

			else{
				model.addAttribute("asahiSalesRepData", apbContactUsData.getAsahiContactUsSaleRepData());
			}
		}
		model.addAttribute(ApbStoreFrontContants.APB_CONTACT_US_FORM, contactUsForm);
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		return ControllerConstants.Views.Pages.Account.ContactUsPage;
	}

	private void setDefaultContactUsSubject(final ApbContactUsForm contactUsForm)
	{

		final String subjectCode = apbContactUsFacade.getDefaultContactUsSubjectCode(getCmsSiteService().getCurrentSite());
		if (StringUtils.isNotEmpty(subjectCode))
		{
			contactUsForm.setSubject(subjectCode);
		}

	}

	/**
	 * Send Contact Us Query to ACC
	 *
	 * @param contactUsForm
	 *
	 * @param model
	 * @param bindingResult
	 * @param redirectModel
	 * @param request
	 *
	 * @return the string
	 * @throws CMSItemNotFoundException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws MediaIOException
	 */
	@PostMapping("/send")
public String companyDetails(final ApbContactUsForm contactUsForm, final Model model, final BindingResult bindingResult,
							 final RedirectAttributes redirectModel, final HttpServletRequest request)
		throws CMSItemNotFoundException, MediaIOException, IllegalArgumentException, IOException
{
	storeCmsPageInModel(model, getContentPageForLabelOrId(ApbStoreFrontContants.APB_CONTACT_US));
	setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ApbStoreFrontContants.APB_CONTACT_US));
	updatePageTitle(model, getContentPageForLabelOrId(ApbStoreFrontContants.APB_CONTACT_US_CONFIRMATION));
	final Breadcrumb bredcrumb = new Breadcrumb("#",
			getMessageSource().getMessage("header.link.contactus", null, getI18nService().getCurrentLocale()), null);
	model.addAttribute("breadcrumbs", Collections.singletonList(bredcrumb));
	apbContactUsValidator.validate(contactUsForm, bindingResult);
	importRequestRegistrationPDFFormValidator.validate(contactUsForm, bindingResult);
	Boolean isUpdatedContactus = this.asahiConfigurationService.getBoolean("sga.contactus.update.available", false);
	model.addAttribute("contactusUpdateAvailable",isUpdatedContactus);
	model.addAttribute("enquiriesPageAvailable",this.asahiConfigurationService.getBoolean("sga.enquiries.page.available", false));
	model.addAttribute("requestCallbackAvailable",this.asahiConfigurationService.getBoolean("sga.request.callback.available", false));
	
	
	if (bindingResult.hasErrors())
	{
		setSalesRepData(contactUsForm, model);
		model.addAttribute(contactUsForm);
		GlobalMessages.addErrorMessage(model, ApbStoreFrontContants.FORM_GLOBAL_ERROR);
		model.addAttribute("errors", bindingResult.getFieldErrors());

		for(FieldError error : bindingResult.getFieldErrors())
		{
			if("discrepancyRowError".equals(error.getField()))
			{
				boolean discrepancyRowError = true;
				model.addAttribute("discrepancyRowError", discrepancyRowError);
			}
		}

		if(isUpdatedContactus && ApbStoreFrontContants.REQUEST_CALLBACK_ENQUIRY.equalsIgnoreCase(contactUsForm.getEnquiryType()) && asahiSiteUtil.isSga()){
			return bindingResult.getFieldErrors().toString();
		}
		return ControllerConstants.Views.Pages.Account.ContactUsPage;
	}

	String enquiryId = apbContactUsFacade.sendContactUsQueryEmail(apbContactUsUtil.setContactUsData(contactUsForm));

	model.addAttribute("enquiryId", enquiryId);

	if(isUpdatedContactus && asahiSiteUtil.isSga()){

		if(ApbStoreFrontContants.REQUEST_CALLBACK_ENQUIRY.equalsIgnoreCase(contactUsForm.getEnquiryType()) && asahiSiteUtil.isSga()){
			return "success";
		}
		return ControllerConstants.Views.Pages.Account.ContactUsConfirmationPage;

	}
	return REDIRECT_PREFIX + "/contactUsConfirmation";
}
	
	
	/**
	 * Send Contact Us Query to ACC
	 *
	 * @param contactUsForm
	 *
	 * @param model
	 * @param bindingResult
	 * @param redirectModel
	 * @param request
	 *
	 * @return the string
	 * @throws CMSItemNotFoundException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws MediaIOException
	 */
	@GetMapping("/requestCallBack")
	@ResponseBody
public RequestCallBackResponse requestCallBack(final ApbContactUsForm contactUsForm, final Model model, final BindingResult bindingResult,
							 final RedirectAttributes redirectModel, final HttpServletRequest request)
		throws CMSItemNotFoundException, MediaIOException, IllegalArgumentException, IOException
{
		
		RequestCallBackResponse requestCallBackResponse = new RequestCallBackResponse();
		Map<String,String> errors = new HashMap<>();
		Boolean success = true;
	if (StringUtils.isEmpty(contactUsForm.getName()))
	{
		errors.put("name", "contactus.name.invalid");
		success=false;
	}
	
	if (StringUtils.isEmpty(contactUsForm.getContactNumber())
			|| !(apbContactUsValidator.validateMobilePattern(contactUsForm.getContactNumber())))
	{
		errors.put("contactNumber", "contactus.contact.number.invalid");
		success=false;
	}
	
	if(!success)
	{
		requestCallBackResponse.setErrors(errors);
		requestCallBackResponse.setSuccess(success);
		return requestCallBackResponse;
	}
	
	apbContactUsFacade.sendContactUsQueryEmail(apbContactUsUtil.setContactUsData(contactUsForm));

		/*
		 * Boolean isUpdatedContactus =
		 * this.asahiConfigurationService.getBoolean("sga.contactus.update.available",
		 * false);
		 * 
		 * if(isUpdatedContactus && asahiSiteUtil.isSga()){
		 * 
		 * if(contactUsForm.getEnquiryType().equalsIgnoreCase("REQUEST_CALLBACK") &&
		 * asahiSiteUtil.isSga()){ return "success"; }
		 * model.addAttribute("enquiriesPageAvailable",this.asahiConfigurationService.
		 * getBoolean("sga.enquiries.page.available", false)); return
		 * ControllerConstants.Views.Pages.Account.ContactUsConfirmationPage;
		 * 
		 * }
		 */
	
	requestCallBackResponse.setSuccess(success);
	return requestCallBackResponse;
}

	/**
	 * @param contactUsForm
	 * @param model
	 */
	private void setSalesRepData(final ApbContactUsForm contactUsForm, final Model model)
	{
		final AsahiContactUsSaleRepData asahiContactUsSaleRepData = new AsahiContactUsSaleRepData();
		asahiContactUsSaleRepData.setShowSalesRep(contactUsForm.isShowSalesRep());
		asahiContactUsSaleRepData.setActiveSalesRep(contactUsForm.isActiveSalesRep());

		if (StringUtils.isNotEmpty(contactUsForm.getAsahiSalesRepEmail()))
		{
			asahiContactUsSaleRepData.setEmailAddress(contactUsForm.getAsahiSalesRepEmail());
		}
		if (StringUtils.isNotEmpty(contactUsForm.getAsahiSalesRepName()))
		{
			asahiContactUsSaleRepData.setName(contactUsForm.getAsahiSalesRepName());
		}
		model.addAttribute("asahiSalesRepData", asahiContactUsSaleRepData);
	}

	/**
	 * get all subjects from db with ascending order
	 *
	 * @return role
	 */
	@ModelAttribute("subjects")
	public List<ContactUsQueryTypeData> getSubject()
	{
		return apbContactUsFacade.getSubject(getCmsSiteService().getCurrentSite());
	}

	/**
	 * Page Title of cms page
	 *
	 * @param model
	 * @param cmsPage
	 */
	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model, asahiSiteUtil.isSga()? getPageTitleResolver().resolveContentPageTitle(cmsPage.getTitle()) : getPageTitleResolver().resolveHomePageTitle(cmsPage.getTitle()));
	}

}
