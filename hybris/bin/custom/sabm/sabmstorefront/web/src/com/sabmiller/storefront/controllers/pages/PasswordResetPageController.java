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

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.ForgottenPwdForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdatePwdForm;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sabmiller.core.b2b.services.impl.DefaultSabmB2BCustomerServiceImpl;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.storefront.controllers.ControllerConstants;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.storefront.form.validation.SABMPasswordSecurityValidator;
import com.sabmiller.storefront.controllers.pages.SabmAbstractPageController;


/**
 * Controller for the forgotten password pages. Supports requesting a password reset email as well as changing the
 * password once you have got the token that was sent via email.
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/login/pw")
public class PasswordResetPageController extends SabmAbstractPageController
{

	private static final Logger LOG = LoggerFactory.getLogger(AccountPageController.class);

	private static final String REDIRECT_PWD_REQ_CONF = "redirect:/login/pw/request/external/conf";
	//	modified for SAB-381
	private static final String REDIRECT_HOME = "redirect:/";
	private static final String REDIRECT_HOME_LOGIN = "redirect:/login";
	private static final String REDIRECT_HOME_LOGIN_NEW_USER = "redirect:/login?newUser=true&targetUrl=/your-business";
	private static final String UPDATE_PWD_CMS_PAGE = "updatePassword";
	private static final String FORGOTTEN_PWD_EMAIL_SENT_CMS_PAGE = "forgottenPasswordEmailSent";

	@Resource(name = "customerFacade")
	private SABMCustomerFacade customerFacade;

	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;

	@Resource(name = "sabmPasswordSecurityValidator")
	private SABMPasswordSecurityValidator sabmPasswordSecurityValidator;


	@Resource(name = "defaultSabmB2BCustomerService")
	DefaultSabmB2BCustomerServiceImpl defaultSabmB2BCustomerServiceImpl;


	@GetMapping("/request")
	public String getPasswordRequest(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute(new ForgottenPwdForm());
		return ControllerConstants.Views.Fragments.Password.PasswordResetRequestPopup;
	}

	@PostMapping("/request")
	public String passwordRequest(@Valid final ForgottenPwdForm form, final BindingResult bindingResult, final Model model)
			throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			return ControllerConstants.Views.Fragments.Password.PasswordResetRequestPopup;
		}
		else
		{
			try
			{
				customerFacade.forgottenPassword(form.getEmail());
			}
			catch (final UnknownIdentifierException unknownIdentifierException)
			{
				LOG.warn("Email: [{}] does not exist.", form.getEmail(), unknownIdentifierException);
			}
			return ControllerConstants.Views.Fragments.Password.ForgotPasswordValidationMessage;
		}
	}

	/**
	 * Receive a request for forget a password link
	 *
	 * @author yuxiao.wang
	 * @param model
	 * @return forgotPasswordRequestPageant
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping("/forgot/request")
	public String getForgotPasswordRequest(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute(new ForgottenPwdForm());
		storeCmsPageInModel(model, getContentPageForLabelOrId(null));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("forgottenPwd.title"));
		return ControllerConstants.Views.Pages.Password.ForgotPasswordRequestPage;
	}

	/**
	 * Receive a request for email validation. This will check if email is owned by a customer.
	 *
	 * @param model
	 * @param email
	 *           the email address to be checked
	 * @return isValid value is VALID if there is a customer who owns the email, otherwise INVALID.
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping("/forgot/validateEmail")
	public @ResponseBody String isEmailValid(@RequestParam(value = "email") final String email, final Model model)
			throws CMSItemNotFoundException
	{
		final List<B2BCustomerModel> customers = getDefaultSabmB2BCustomerServiceImpl().searchCustomerByEmail(email);
		//		Commenting as per Remediation from Pentest Results: should not show invalid message.
		//		return CollectionUtils.isNotEmpty(customers)? "VALID":"INVALID";
		return CollectionUtils.isNotEmpty(customers) ? "VALID" : "VALID";
	}

	/**
	 * Form post request
	 *
	 * @author yuxiao.wang
	 * @param form
	 * @param bindingResult
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@PostMapping("/forgot/request")
	public String forgotPasswordRequest(@Valid final ForgottenPwdForm form, final BindingResult bindingResult, final Model model)
			throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			return ControllerConstants.Views.Pages.Password.ForgotPasswordRequestPage;
		}
		else
		{
			try
			{
				customerFacade.forgottenPassword(form.getEmail());
			}
			catch (final UnknownIdentifierException unknownIdentifierException)
			{
				LOG.warn("Email: [{}] does not exist.", form.getEmail(), unknownIdentifierException);
			}
			model.addAttribute("email", form.getEmail());
			storeCmsPageInModel(model, getContentPageForLabelOrId(FORGOTTEN_PWD_EMAIL_SENT_CMS_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(FORGOTTEN_PWD_EMAIL_SENT_CMS_PAGE));
			final Breadcrumb loginBreadcrumbEntry = new Breadcrumb("#",
					getMessageSource().getMessage("text.account.email.sent.breadcrumb", null, getI18nService().getCurrentLocale()),
					null);
			model.addAttribute("breadcrumbs", Collections.singletonList(loginBreadcrumbEntry));
			return getViewForPage(model);
		}
	}

	@GetMapping("/request/external")
	public String getExternalPasswordRequest(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute(new ForgottenPwdForm());
		storeCmsPageInModel(model, getContentPageForLabelOrId(null));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("forgottenPwd.title"));
		return ControllerConstants.Views.Pages.Password.PasswordResetRequest;
	}

	@GetMapping("/request/external/conf")
	public String getExternalPasswordRequestConf(final Model model) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(null));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("forgottenPwd.title"));
		return ControllerConstants.Views.Pages.Password.PasswordResetRequestConfirmation;
	}

	@PostMapping("/request/external")
	public String externalPasswordRequest(@Valid final ForgottenPwdForm form, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(null));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("forgottenPwd.title"));

		if (bindingResult.hasErrors())
		{
			return ControllerConstants.Views.Pages.Password.PasswordResetRequest;
		}
		else
		{
			try
			{
				customerFacade.forgottenPassword(form.getEmail());
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
						"account.confirmation.forgotten.password.link.sent");
			}
			catch (final UnknownIdentifierException unknownIdentifierException)
			{
				LOG.warn("Email: [{}] does not exist.", form.getEmail(), unknownIdentifierException);
			}
			return REDIRECT_PWD_REQ_CONF;
		}
	}

	/**
	 *
	 * @param token
	 * @param model
	 * @return PasswordResetChangePage
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping("/change")
	public String getChangePassword(@RequestParam(required = false) final String token,
			@RequestParam(value = "newUser", required = false) final String newUser, final Model model)
			throws CMSItemNotFoundException
	{
		if (StringUtils.isBlank(token))
		{
			return REDIRECT_HOME;
		}

		if (BooleanUtils.toBoolean(newUser) && ((SABMCustomerFacade) getCustomerFacade()).isPasswordSet(token))
		{
			return REDIRECT_HOME;
		}

		final UpdatePwdForm form = new UpdatePwdForm();
		form.setToken(token);
		model.addAttribute(form);
		storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PWD_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PWD_CMS_PAGE));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("updatePwd.title"));
		return ControllerConstants.Views.Pages.Password.PasswordResetChangePage;
	}

	/**
	 * update by yuxiao.wang by SAB-381
	 *
	 * @param form
	 * @param bindingResult
	 * @param model
	 * @param redirectModel
	 * @return homepage
	 * @throws CMSItemNotFoundException
	 */
	@PostMapping("/change")
	public String changePassword(@Valid final UpdatePwdForm form, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel, @RequestParam(value = "newUser", required = false) final boolean newUser)
			throws CMSItemNotFoundException
	{
		//update by yuxiao.wang for SAB-381 -validate Password security rules
		if (bindingResult.hasErrors() || !sabmPasswordSecurityValidator.validatePassword(form.getPwd()))
		{
			prepareErrorMessage(model, UPDATE_PWD_CMS_PAGE);
			return ControllerConstants.Views.Pages.Password.PasswordResetChangePage;
		}
		if (!StringUtils.isBlank(form.getToken()))
		{
			try
			{
				customerFacade.updatePassword(form.getToken(), form.getPwd());
				//update the status by token
				customerFacade.updateB2BUnitStatus(form.getToken(), Boolean.FALSE, Boolean.TRUE);
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
						"account.confirmation.password.updated");
			}
			catch (final IllegalArgumentException e)
			{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "updatePwd.token.invalid");
			}
			catch (final TokenInvalidatedException e)
			{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "updatePwd.token.invalidated");
			}
			catch (final RuntimeException e)
			{
				LOG.warn("The link used to access the update page was invalid.", e);
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "updatePwd.token.invalid");
			}

		}
		if (newUser)
		{
			return REDIRECT_HOME_LOGIN_NEW_USER;
		}
		else
		{
			//taken to the homepage screen
			return REDIRECT_HOME_LOGIN;
		}
	}

	/**
	 * Prepares the view to display an error message
	 *
	 * @throws CMSItemNotFoundException
	 */
	protected void prepareErrorMessage(final Model model, final String page) throws CMSItemNotFoundException
	{
		GlobalMessages.addErrorMessage(model, "form.global.error");
		storeCmsPageInModel(model, getContentPageForLabelOrId(page));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(page));
	}


	public DefaultSabmB2BCustomerServiceImpl getDefaultSabmB2BCustomerServiceImpl()
	{
		return defaultSabmB2BCustomerServiceImpl;
	}

	public void setDefaultSabmB2BCustomerServiceImpl(final DefaultSabmB2BCustomerServiceImpl defaultSabmB2BCustomerServiceImpl)
	{
		this.defaultSabmB2BCustomerServiceImpl = defaultSabmB2BCustomerServiceImpl;
	}

	@ModelAttribute("pageType")
	protected String getPageType()
	{
		return SABMWebConstants.PageType.PASSWORD_RESET.name();
	}

}
