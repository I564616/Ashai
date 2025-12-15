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

import com.sabmiller.facades.notification.SABMNotificationFacade;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractRegisterPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.RegisterForm;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sabmiller.core.b2b.services.SabmB2BCustomerService;
import com.sabmiller.core.model.SABMNotificationModel;
import com.sabmiller.core.model.SABMNotificationPrefModel;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade;
import com.sabmiller.facades.customer.CustomerJson;
import com.sabmiller.facades.customer.CustomerJsonResponse;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.registrationrequest.data.RegistrationRequestForm;
import com.sabmiller.storefront.controllers.ControllerConstants;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.storefront.form.SABMCreateUserForm;

import org.apache.commons.lang3.StringUtils;


/**
 * Register Controller for mobile. Handles login and register for the account flow.
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/register")
public class RegisterPageController extends AbstractRegisterPageController
{
	private static final Logger LOG = LoggerFactory.getLogger(RegisterPageController.class);
	private static final String CUSTOMER_UID_PATH_VARIABLE_PATTERN = "/{uId:.*}";
	private static final String REDIRECT_GETUSER = "redirect:/register/getUser/";

	// CMS Pages
	private static final String CREATEUSER_CMS_PAGE = "createUser";
	private static final String REGISTRATIONREQUESTS_CMS_PAGE = "registrationRequests";
	private static final String EDITUSER_CMS_PAGE = "editUser";
	private static final String FORGOTTEN_PWD_EMAIL_SENT_CMS_PAGE = "forgottenPasswordEmailSent";

	private HttpSessionRequestCache httpSessionRequestCache;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	@Resource(name = "customerFacade")
	private SABMCustomerFacade customerFacade;

	@Resource(name = "b2bCommerceUnitFacade")
	private SabmB2BCommerceUnitFacade b2bCommerceUnitFacade;

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "customerJsonValidator")
	private Validator customerJsonValidator;

	@Resource(name = "registrationRequestValidator")
	private Validator registrationRequestValidator;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource(name = "sabmB2BCustomerService")
	private SabmB2BCustomerService sabmB2BCustomerService;
	
	@Resource(name = "notificationFacade")
	private SABMNotificationFacade notificationFacade;
	
	@Override
	protected AbstractPageModel getCmsPage() throws CMSItemNotFoundException
	{
		return getContentPageForLabelOrId("register");
	}

	@Override
	protected String getSuccessRedirect(final HttpServletRequest request, final HttpServletResponse response)
	{
		if (httpSessionRequestCache.getRequest(request, response) != null)
		{
			return httpSessionRequestCache.getRequest(request, response).getRedirectUrl();
		}
		return "/";
	}

	@Override
	protected String getView()
	{
		return ControllerConstants.Views.Pages.Account.AccountNewCustomerLogin;
	}

	@Resource(name = "httpSessionRequestCache")
	public void setHttpSessionRequestCache(final HttpSessionRequestCache accHttpSessionRequestCache)
	{
		this.httpSessionRequestCache = accHttpSessionRequestCache;
	}

	@GetMapping
	public String doRegister(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getDefaultRegistrationPage(model);
	}

	@PostMapping("/newcustomer")
	public String doRegister(final RegisterForm form, final BindingResult bindingResult, final Model model,
			final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		getRegistrationValidator().validate(form, bindingResult);
		return processRegisterUserRequest(null, form, bindingResult, model, request, response, redirectModel);
	}

	/**
	 * @author yuxiao.wang
	 * @param model
	 * @return AccountNewCustomerLogin
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/createUser")
	public String createUser(final Model model) throws CMSItemNotFoundException
	{
		return setCreateUserData(model, null);
	}

	@RequestMapping(value = "/createUser/{b2bUnitid:.*}")
	public String createUser(@PathVariable("b2bUnitid") final String b2bUnitid, final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("b2bUnitid", b2bUnitid);
		return setCreateUserData(model, b2bUnitid);
	}

	/**
	 * @param model
	 * @return Create user page
	 * @throws CMSItemNotFoundException
	 */
	private String setCreateUserData(final Model model, final String b2bUnitId) throws CMSItemNotFoundException
	{
		final de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData b2bUnitData = b2bCommerceUnitFacade.getTopLevelB2BUnit();
		final CurrencyModel currentCurrency = getCommonI18NService().getCurrentCurrency();

		model.addAttribute("sabmCreateUserForm", new SABMCreateUserForm());
		model.addAttribute("businessUnit", b2bUnitData);
		model.addAttribute("customerJson", customerFacade.getCurrentCustomerJsonStates());
		model.addAttribute("currentCurrency", currentCurrency);
		storeCmsPageInModel(model, getContentPageForLabelOrId(CREATEUSER_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CREATEUSER_CMS_PAGE));

		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb("/your-business/businessunits",
				getMessageSource().getMessage("text.yourBusiness.businessUnits", null, getI18nService().getCurrentLocale()), null));
		if (StringUtils.isNotEmpty(b2bUnitId))
		{
			breadcrumbs.add(new Breadcrumb("/your-business/unitsdetails/" + b2bUnitId,
					getMessageSource().getMessage("text.yourBusiness.businessUnitDetails", null, getI18nService().getCurrentLocale()),
					null));
		}
		breadcrumbs.add(new Breadcrumb("#",
				getMessageSource().getMessage("register.new.customer", null, getI18nService().getCurrentLocale()), null));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		model.addAttribute("breadcrumbs", breadcrumbs);
		return getViewForPage(model);
	}

	/**
	 * @author yuxiao.wang
	 * @param form
	 * @param bindingResult
	 * @param model
	 * @return AccountNewCustomerLogin
	 * @throws CMSItemNotFoundException
	 */
	@PostMapping("/saveUser")
	@ResponseBody
	public CustomerJsonResponse saveUser(@RequestBody final CustomerJson form, final BindingResult bindingResult,
			final Model model) throws CMSItemNotFoundException
	{
		/*
		 * SABMC-1014
		 */
		final String email = form.getEmail();
		final CustomerModel currentUser = (CustomerModel) userService.getCurrentUser();
		if (currentUser.getUid().equals(email))
		{
			if (currentUser.getPrimaryAdmin() != null && !currentUser.getPrimaryAdmin())
			{
				LOG.warn("Illegally update the logged in user profile");
				return null;
			}
		}

		customerJsonValidator.validate(form, bindingResult);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("In saveUser(). form is : {} ", ReflectionToStringBuilder.toString(form));
		}
		final CustomerJsonResponse jsonResponse = new CustomerJsonResponse();
		if (bindingResult.hasErrors())
		{
			jsonResponse.setMessageType("bad");
			jsonResponse
					.setMessage(getMessageSource().getMessage("register.createUser.error", null, getI18nService().getCurrentLocale()));
			GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, "register.createUser.error", null);
			LOG.warn("Create user fail. the uid: [{}]", form.getEmail());
		}
		else
		{
			final CustomerData customerData = customerFacade.saveUser(form);
			if (null != customerData)
			{
				jsonResponse.setMessageType("good");
				jsonResponse.setMessage(
						getMessageSource().getMessage("register.createUser.success", null, getI18nService().getCurrentLocale()));
				GlobalMessages.addMessage(model, GlobalMessages.INFO_MESSAGES_HOLDER, "register.createUser.success", null);
			}
			else
			{
				jsonResponse.setMessageType("bad");
				jsonResponse.setMessage(
						getMessageSource().getMessage("register.createUser.error", null, getI18nService().getCurrentLocale()));
				LOG.warn("Create user fail. the uid: [{}]", form.getEmail());
				GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, "register.createUser.error", null);
			}

			if (StringUtils.isNotEmpty(form.getB2bUnitId()))
			{
				jsonResponse.setRedirectUrl("your-business/unitsdetails/" + form.getB2bUnitId());
			}
			else
			{
				jsonResponse.setRedirectUrl("your-business/businessunits");
			}
		}

		return jsonResponse;
	}

	/**
	 * Check user according to input email
	 *
	 * @author yuxiao.wang
	 * @param email
	 * @return Map object
	 */
	@ResponseBody
	@GetMapping("/checkUser")
	public CustomerJson isExistUserMail(final String email)
	{
		return customerFacade.isExistUserMail(StringUtils.trimToEmpty(email));
	}

	/**
	 * @author yuxiao.wang
	 * @param uId
	 * @param model
	 * @return AccountEditUserPage
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping("/getUser" + CUSTOMER_UID_PATH_VARIABLE_PATTERN)
	public String getUser(@PathVariable("uId") final String uId,
			@RequestParam(value = "businessUnitId", required = false) final String businessUnitId, final Model model)
			throws CMSItemNotFoundException
	{
		if (!b2bCommerceUnitFacade.isCurrentB2BUnitExistOfUid(StringUtils.trimToEmpty(uId)))
		{
			return FORWARD_PREFIX + "/404";
		}

		final CustomerData customerData = customerFacade.getCustomerForUid(StringUtils.trimToEmpty(uId));
		model.addAttribute("customerData", customerData);
		final de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData  b2bUnitData = b2bCommerceUnitFacade.getTopLevelB2BUnit();
		model.addAttribute("businessUnit", b2bUnitData);
		model.addAttribute("customerJson", customerFacade.getCustomerJsonByUid(StringUtils.trimToEmpty(uId)));
		final CurrencyModel currentCurrency = getCommonI18NService().getCurrentCurrency();
		model.addAttribute("currentCurrency", currentCurrency);

		final String b2bUnitId = StringUtils.trimToEmpty(businessUnitId);

		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb("/your-business/businessunits",
				getMessageSource().getMessage("text.yourBusiness.businessUnits", null, getI18nService().getCurrentLocale()), null));

		if (StringUtils.isNotEmpty(b2bUnitId))
		{
			breadcrumbs.add(new Breadcrumb("/your-business/unitsdetails/" + b2bUnitId,
					getMessageSource().getMessage("text.yourBusiness.businessUnitDetails", null, getI18nService().getCurrentLocale()),
					null));
		}

		model.addAttribute("uId", uId);
		storeCmsPageInModel(model, getContentPageForLabelOrId(EDITUSER_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(EDITUSER_CMS_PAGE));
		breadcrumbs.add(new Breadcrumb("#",
				getMessageSource().getMessage("register.customer.edit", null, getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("b2bUnitid", b2bUnitId);
		List<SABMNotificationModel> notificationModels = notificationFacade.getNotificationForAllUnits(uId);
		if(notificationFacade != null && CollectionUtils.isNotEmpty(notificationModels)) {
			boolean smsPrefs = notificationModels.stream()
				    .flatMap(notificationModel -> notificationModel.getNotificationPreferences().stream())
				    .anyMatch(SABMNotificationPrefModel::getSmsEnabled);
			if(smsPrefs) {
				model.addAttribute("notifications", "notifications");
			}
   	}
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	/**
	 * @author yuxiao.wang
	 * @param form
	 * @param bindingResult
	 * @param model
	 * @return AccountEditUserPage
	 * @throws CMSItemNotFoundException
	 */
	@PostMapping("/editUser")
	@ResponseBody
	public CustomerJsonResponse editUser(@RequestBody final CustomerJson form, final BindingResult bindingResult,
			final Model model) throws CMSItemNotFoundException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("In editUser(). form is : {} ", ReflectionToStringBuilder.toString(form));
		}
		final CustomerJsonResponse jsonResponse = new CustomerJsonResponse();
		if (bindingResult.hasErrors())
		{
			jsonResponse.setMessageType("bad");
			jsonResponse
					.setMessage(getMessageSource().getMessage("register.createUser.error", null, getI18nService().getCurrentLocale()));
			GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, "register.createUser.error", null);
			LOG.warn("Edit user fail. the uid: [{}]", form.getEmail());
		}
		else
		{
			 CustomerData customerData =null;
			 boolean emailIdExists=false;
			try{
			 customerData = customerFacade.editUser(form);
			}
			catch(DuplicateUidException e){
				emailIdExists=true;
			}
			if (null != customerData)
			{
				jsonResponse.setMessageType("good");
				jsonResponse.setMessage(
						getMessageSource().getMessage("register.EditUser.success", null, getI18nService().getCurrentLocale()));
				//Any change made, treat user as a new user, sent to the current email address for the user to set a password.
				//customerFacade.sendWelcomeEmailMessage(form.getEmail());
				GlobalMessages.addMessage(model, GlobalMessages.CONF_MESSAGES_HOLDER, "register.EditUser.success", null);
			}
			else
			{
				
				jsonResponse.setMessageType("bad");
				if(emailIdExists){
					jsonResponse.setMessage(
							getMessageSource().getMessage("register.EditUser.emailId.exists.error", null, getI18nService().getCurrentLocale()));
						
				} else {
				jsonResponse.setMessage(
						getMessageSource().getMessage("register.createUser.error", null, getI18nService().getCurrentLocale()));
				}
				LOG.warn("Edit user fail. the uid: [{}]", form.getEmail());
				GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, "register.createUser.error", null);
			}

			if (StringUtils.isNotEmpty(form.getB2bUnitId()))
			{
				jsonResponse.setRedirectUrl("your-business/unitsdetails/" + form.getB2bUnitId());
			}
			else
			{
				jsonResponse.setRedirectUrl("your-business/businessunits");
			}
		}

		return jsonResponse;
	}

	@GetMapping("/updatePwd" + CUSTOMER_UID_PATH_VARIABLE_PATTERN)
	public String forgotPasswordRequest(@PathVariable("uId") final String uId, final Model model) throws CMSItemNotFoundException
	{
		try
		{
			customerFacade.forgottenPassword(uId);
		}
		catch (final UnknownIdentifierException unknownIdentifierException)
		{
			LOG.warn("Email: [{}] does not exist.", uId, unknownIdentifierException);
		}
		model.addAttribute("email", uId);
		storeCmsPageInModel(model, getContentPageForLabelOrId(FORGOTTEN_PWD_EMAIL_SENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(FORGOTTEN_PWD_EMAIL_SENT_CMS_PAGE));
		final Breadcrumb loginBreadcrumbEntry = new Breadcrumb("#",
				getMessageSource().getMessage("text.account.email.sent.breadcrumb", null, getI18nService().getCurrentLocale()), null);
		model.addAttribute("breadcrumbs", Collections.singletonList(loginBreadcrumbEntry));
		return getViewForPage(model);
	}

	/**
	 *
	 * @param uId
	 * @return Redirect URL
	 * @throws CMSItemNotFoundException
	 */
	@PostMapping("/sendWelcomeEmail" + CUSTOMER_UID_PATH_VARIABLE_PATTERN)
	public String sendWelcomeEmail(@PathVariable("uId") final String uId, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		try
		{
			customerFacade.sendWelcomeEmailMessage(uId);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "register.editUser.sendWelcomeEmail");
		}
		catch (final Exception e)
		{
			LOG.error("Send Welcome Email fail.", e);
		}

		return REDIRECT_GETUSER + uId;
	}

	@GetMapping("/registration-form")
	public String registrationRequests(final Model model) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(REGISTRATIONREQUESTS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(REGISTRATIONREQUESTS_CMS_PAGE));
		return getViewForPage(model);
	}

	@PostMapping(value = "/registration-form", consumes = "application/json")
	@ResponseBody
	public String sendRegistrationRequests(@RequestBody final RegistrationRequestForm registrationRequestForm,
			final BindingResult bindingResult, final Model model) throws CMSItemNotFoundException
	{
		
		registrationRequestValidator.validate(registrationRequestForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			return "NOOK";
		}

		final boolean isSucess = customerFacade.sendRegistrationRequestsMessage(registrationRequestForm);

		return isSucess ? "OK" : "NOOK";
	}

	@GetMapping("/isExistingUser" + "/{emailId:.*}")
	@ResponseBody
	public String checkIfExistingUser(@PathVariable("emailId") final String emailId, @RequestParam(value ="createUser", defaultValue = "false") final String createUser) throws CMSItemNotFoundException
	{
		final UserModel user = asahiCoreUtil.checkIfUserExists(emailId);
		if (null != user && sabmB2BCustomerService.checkIfUserRegisteredForOtherSites(user, BooleanUtils.toBoolean(createUser))) {
			return "TRUE";
		}
		return "FALSE";
	
	}

	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the httpSessionRequestCache
	 */
	public HttpSessionRequestCache getHttpSessionRequestCache()
	{
		return httpSessionRequestCache;
	}
	
	@ModelAttribute("pageType")
	protected String getPageType()
	{
		return SABMWebConstants.PageType.REGISTER.name();
	}
}
