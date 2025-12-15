package com.apb.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController.SelectOption;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractRegisterPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commons.renderer.exceptions.RendererException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaIOException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.core.exception.AsahiBusinessException;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.product.data.AsahiRoleData;
import com.apb.facades.user.ApbUserFacade;
import com.apb.storefront.constant.ApbStoreFrontContants;
import com.apb.storefront.data.ApbRegisterData;
import com.apb.storefront.forms.ApbRegisterForm;
import com.apb.storefront.forms.ApbRequestRegisterForm;
import com.apb.storefront.util.ApbReguestRegistrationUtil;
import com.sabmiller.facades.customer.SABMCustomerFacade;


/**
 * @author C5252631
 *
 *         ApbAbstractRegisterPageController implementation of {@link of AbstractRegisterPageController}
 *
 *         When customer of existing unit would be created. An only normal user could be created or new Aashi account
 *         could be requested to Asashi customer care.
 *
 */
public abstract class ApbAbstractRegisterPageController extends AbstractRegisterPageController
{
	private static final Logger LOG = LoggerFactory.getLogger(ApbAbstractRegisterPageController.class);

	private static final String REDIRECT_CONFIRMATION_PAGE = "redirect:/register/confirmation/?firstName=";
	private static final String REDIRECT_MULTI_ACCOUNT_PAGE = "redirect:/multiAccount";

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "apbRegistrationValidator")
	private Validator apbRegistrationValidator;

	@Resource(name = "apbRequestRegistrationValidator")
	private Validator apbRequestRegistrationValidator;

	@Resource(name = "b2bCustomerFacade")
	private SABMCustomerFacade sabmCustomerFacade;

	@Resource(name = "apbUserFacade")
	private ApbUserFacade apbUserFacade;

	@Resource(name = "apbReguestRegistrationUtil")
	private ApbReguestRegistrationUtil apbReguestRegistrationUtil;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	/*
	 * request User Registration
	 */
	protected String processRequestRegisterUserRequest(final Object object, final ApbRequestRegisterForm form,
			final BindingResult bindingResult, final Model model, final HttpServletRequest request,
			final HttpServletResponse response, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException, UnknownIdentifierException, DuplicateUidException, RendererException, MediaIOException,
			IllegalArgumentException, IOException
	{
		LOG.trace("processSelfRegisterUserRequest");
		model.addAttribute("pdfFileMaxSize", asahiConfigurationService.getString(
				ApbStoreFrontContants.IMPORT_PDF_FILE_MAX_SIZE_BYTES_KEY + getCmsSiteService().getCurrentSite().getUid(), "0"));
		model.addAttribute(new ApbRegisterForm());
		model.addAttribute("requestCustomer", form.isRequestCustomerType());
		model.addAttribute("customer", form.getCustomerType());
		model.addAttribute("addAnother", form.isAddAnother());
		if (bindingResult.hasErrors())
		{
			model.addAttribute(form);
			GlobalMessages.addErrorMessage(model, ApbStoreFrontContants.FORM_GLOBAL_ERROR);
			model.addAttribute("errors", bindingResult.getFieldErrors());
			return handleRegistrationError(model);
		}
		try
		{
			sabmCustomerFacade.sendRequestRegisterEmail(apbReguestRegistrationUtil.setRequestRegistrationData(form));
		}
		catch (final RendererException r)
		{
			LOG.error("Renderer Exception " + r.getMessage());
			/*
			 * GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
			 * "request.registration.reference.number.message_" + r.getMessage());
			 */
		}
		catch (final ModelNotFoundException mnf)
		{
			LOG.error("Validation failed: " + mnf.getMessage());
			model.addAttribute(form);
			bindingResult.rejectValue("liquorLicensenumber", "register.liquorLicense.invalid");
			GlobalMessages.addErrorMessage(model, ApbStoreFrontContants.FORM_GLOBAL_ERROR);
			return handleRegistrationError(model);
		}
		request.getSession().setAttribute("selfRegistration", false);
		return REDIRECT_CONFIRMATION_PAGE + form.getAbn();
	}

	/**
	 * @param model
	 */
	protected void setMaximunSize(final Model model)
	{
		model.addAttribute("inputMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.REGISTER_INPUT_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "75"));
		model.addAttribute("dateMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.DATE_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "10"));
		model.addAttribute("phoneMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.PHONE_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "15"));
		model.addAttribute("abnMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.REGISTER_ABN_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "15"));
		model.addAttribute("acnMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.REGISTER_ACN_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "15"));
		model.addAttribute("llMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.REGISTER_LL_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "15"));
		model.addAttribute("emailMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.EMAIL_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "255"));
		model.addAttribute("inputAddressMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.ADDRESS_INPUT_NAME_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "50"));
	  	model.addAttribute("accountNoMaxSize", asahiConfigurationService
				.getString(ApbStoreFrontContants.REGISTER_ACCOUNT_NO_MAX_LIMIT + getCmsSiteService().getCurrentSite().getUid(), "15"));
		
	}

	/*
	 * Self User Registration First time
	 */
	protected String processSelfRegisterUserRequest(final Object object, final ApbRegisterForm form,
			final BindingResult bindingResult, final Model model, final HttpServletRequest request,
			final HttpServletResponse response, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException, UnknownIdentifierException
	{
		LOG.trace("processSelfRegisterUserRequest");
		model.addAttribute(new ApbRequestRegisterForm());
		model.addAttribute("customer", form.getCustomerType());
		if (bindingResult.hasErrors())
		{			
			model.addAttribute(form);
			GlobalMessages.addErrorMessage(model, ApbStoreFrontContants.FORM_GLOBAL_ERROR);
			return handleRegistrationError(model);
		}
		final ApbRegisterData data = new ApbRegisterData();
		data.setFirstName(form.getFirstName());
		data.setLastName(form.getLastName());
		data.setLogin(form.getEmail());
		data.setPassword(form.getPwd());
		data.setTitleCode(form.getTitleCode());
		data.setPhone(form.getPhone());
		data.setAbnAccountId(form.getAbnAccountId());
		data.setAbnNumber(form.getAbnNumber());
		data.setRoleOther(form.getRoleOther());
		data.setLiquorLicense(form.getLiquorLicensenumber());
		data.setRole(form.getRole());
		data.setEmail(form.getEmail());
		data.setSamAccess(form.getSamAccess());
		data.setAlbCompanyInfoData(form.getAlbCompanyInfoData());
		try
		{
			getSABMCustomerFacade().register(data,bindingResult);
			if (bindingResult.hasErrors())
			{			
				model.addAttribute(form);
				GlobalMessages.addErrorMessage(model, ApbStoreFrontContants.FORM_GLOBAL_ERROR);
				return handleRegistrationError(model);
			}
			getAutoLoginStrategy().login(form.getEmail().toLowerCase(), form.getPwd(), request, response);
			request.getSession().setAttribute("selfRegistration", true);
			if(asahiSiteUtil.isSga()) {
				if(form.getAlbCompanyInfoData().size() > 1 || asahiCoreUtil.checkIfUserHasMultipleUnits(data.getEmail())) {
					request.getSession().setAttribute("multiAccountSelfRegistration", true);
					asahiCoreUtil.setMultiAccountDisplayLink(request);
				}
			}else {
				request.getSession().setAttribute("accessType", form.getSamAccess());
			}

			//GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
			//		"registration.confirmation.message.title"); ACP-1404
		}
		catch (final ModelNotFoundException mnf)
		{
			LOG.error("Validation failed: " + mnf.getMessage());
			model.addAttribute(form);
			bindingResult.rejectValue("liquorLicensenumber", "register.liquorLicense.invalid");
			GlobalMessages.addErrorMessage(model, ApbStoreFrontContants.FORM_GLOBAL_ERROR);
			return handleRegistrationError(model);
		}
		catch (final UnknownIdentifierException uie)
		{
			LOG.error("Validation failed: " + uie.getMessage());
			model.addAttribute(form);
			bindingResult.rejectValue("abnNumber", "register.check.abn.invalid");
			if (asahiSiteUtil.isSga())
			{
				bindingResult.rejectValue("abnAccountId", "register.check.sga.account.id.invalid");
			}
			else
			{
				bindingResult.rejectValue("abnAccountId", "register.check.apb.account.id.invalid");
			}
			GlobalMessages.addErrorMessage(model, ApbStoreFrontContants.FORM_GLOBAL_ERROR);
			return handleRegistrationError(model);
		}
		catch (final DuplicateUidException due)
		{
			LOG.error("Registration failed: " + due.getMessage());
			model.addAttribute(form);
			bindingResult.rejectValue("email", "registration.error.account.exists.title");
			GlobalMessages.addErrorMessage(model, ApbStoreFrontContants.FORM_GLOBAL_ERROR);
			return handleRegistrationError(model);
		}
		catch (final AsahiBusinessException asahiBusinessException)
		{
			LOG.error("Customer Account Validation failed: " + asahiBusinessException.getMessage());
			model.addAttribute(form);
			GlobalMessages.addErrorMessage(model, asahiBusinessException.getMessage() );
			return handleRegistrationError(model);
		}
		if(asahiSiteUtil.isSga() && (form.getAlbCompanyInfoData().size() > 1 || asahiCoreUtil.checkIfUserHasMultipleUnits(data.getEmail()))) {
			return REDIRECT_MULTI_ACCOUNT_PAGE;
		}
		return REDIRECT_CONFIRMATION_PAGE + form.getFirstName();
	}


	@Override
	protected String getDefaultRegistrationPage(final Model model) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getCmsPage());
		setUpMetaDataForContentPage(model, (ContentPageModel) getCmsPage());

		if (((ContentPageModel) getCmsPage()).getBackgroundImage() != null)
		{
			model.addAttribute("media", ((ContentPageModel) getCmsPage()).getBackgroundImage().getURL());
		}

		final Breadcrumb loginBreadcrumbEntry = new Breadcrumb("#",
				getMessageSource().getMessage("header.link.registration", null, getI18nService().getCurrentLocale()), null);
		model.addAttribute("breadcrumbs", Collections.singletonList(loginBreadcrumbEntry));
		model.addAttribute(new ApbRegisterForm());
		model.addAttribute(new ApbRequestRegisterForm());
		model.addAttribute("customerCareNo", asahiConfigurationService.getString(ApbStoreFrontContants.CUSTOMER_CARE_NUMBER, ""));
		model.addAttribute("pdfFileMaxSize", asahiConfigurationService.getString(
				ApbStoreFrontContants.IMPORT_PDF_FILE_MAX_SIZE_BYTES_KEY + getCmsSiteService().getCurrentSite().getUid(), "0"));
		return getView();
	}


	/**
	 * Type of entity of application section
	 *
	 * @return entityList
	 */

	@ModelAttribute("typeofEntity")
	public List<SelectOption> getTypeOfEntity()
	{
		final String typeOfEntityProp = asahiConfigurationService.getString(
				ApbStoreFrontContants.REQUEST_REGISTER_TYPE_OF_ENTITY_LIST + getCmsSiteService().getCurrentSite().getUid(), "");
		final List<SelectOption> selectOptionList = new ArrayList<SelectOption>();
		if (StringUtils.isNotEmpty(typeOfEntityProp))
		{
			apbReguestRegistrationUtil.getOptionList(typeOfEntityProp, selectOptionList);
		}
		return selectOptionList;
	}


	/**
	 * Type Of Business
	 *
	 * @return businessList
	 */
	@ModelAttribute("typeofBusiness")
	public List<SelectOption> getTypeOfBusiness()
	{
		final String typeOfBusinessProp = asahiConfigurationService.getString(
				ApbStoreFrontContants.REQUEST_REGISTER_TYPE_OF_BUSINESS_LIST + getCmsSiteService().getCurrentSite().getUid(), "");
		final List<SelectOption> selectOptionList = new ArrayList<SelectOption>();
		if (StringUtils.isNotEmpty(typeOfBusinessProp))
		{
			apbReguestRegistrationUtil.getOptionList(typeOfBusinessProp, selectOptionList);
		}
		return selectOptionList;
	}

	/**
	 * Customer Type Not Used Fixed ACP- 1404
	 *
	 * @return customerList
	 */
	@ModelAttribute("customerType")
	public List<SelectOption> getCustomerType()
	{
		final List<SelectOption> customerList = new ArrayList<SelectOption>();
		final SelectOption entity = new SelectOption("Retail", "Retail");
		customerList.add(entity);
		return customerList;
	}

	/**
	 * @return role
	 */
	@ModelAttribute("asahiRole")
	public List<AsahiRoleData> getAsahiRole()
	{
		return apbUserFacade.getAsahiRole();
	}

	/**
	 * @return role
	 */
	@ModelAttribute("region")
	public List<RegionData> getStates()
	{
		return apbUserFacade.getStates(getCmsSiteService().getCurrentSite());
	}

	/**
	 * @return
	 */
	public Validator getApbRegistrationValidator()
	{
		return apbRegistrationValidator;
	}

	/**
	 * @return the asahiCustomerFacade
	 */
	public SABMCustomerFacade getSABMCustomerFacade()
	{
		return sabmCustomerFacade;
	}

	/**
	 * @param sabmCustomerFacade
	 *           the asahiCustomerFacade to set
	 */
	public void setSABMCustomerFacade(final SABMCustomerFacade sabmCustomerFacade)
	{
		this.sabmCustomerFacade = sabmCustomerFacade;
	}

	/**
	 * @param apbRegistrationValidator
	 *           the apbRegistrationValidator to set
	 */
	public void setApbRegistrationValidator(final Validator apbRegistrationValidator)
	{
		this.apbRegistrationValidator = apbRegistrationValidator;
	}

	/**
	 * @return the apbRequestRegistrationValidator
	 */
	public Validator getApbRequestRegistrationValidator()
	{
		return apbRequestRegistrationValidator;
	}
}
