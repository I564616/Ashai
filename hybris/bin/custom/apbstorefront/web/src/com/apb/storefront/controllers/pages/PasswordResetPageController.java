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

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.ForgottenPwdForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.UpdatePasswordFormValidator;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.core.services.ApbCustomerAccountService;
import com.apb.core.util.ApbXSSEncoderUtil;
import com.apb.storefront.controllers.ControllerConstants;
import com.apb.storefront.forms.AsahiForgottenPwdForm;
import com.apb.storefront.forms.AsahiUpdatePwdForm;
import com.apb.storefront.validators.AsahiUpdatePasswordFormValidator;
import com.apb.core.util.AsahiSiteUtil;


/**
 * Controller for the forgotten password pages. Supports requesting a password reset email as well as changing the
 * password once you have got the token that was sent via email.
 */
@Controller
@RequestMapping(value = "/login/pw")
public class PasswordResetPageController extends ApbAbstractPageController
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(PasswordResetPageController.class);

	private static final String FORGOTTEN_PWD_TITLE = "forgottenPwd.title";
	private static final String RESET_PASSWORD = "text.manageuser.restpasswordform";
	private static final String RESET_PASSWORD_URL = "/login/pw/change";
	private static final String REDIRECT_RESET_PASSWORD_PAGE = REDIRECT_PREFIX + RESET_PASSWORD_URL;
	private static final String RESET_PWD_TITLE = "resetPwd.title";
	private static final String REDIRECT_PWD_REQ_CONF = "redirect:/login/pw/request/external/conf";
	private static final String REDIRECT_LOGIN = "redirect:/login";
	private static final String REDIRECT_PWD_CHANGE_CONF = "redirect:/login/pw/change/conf";
	private static final String REDIRECT_HOME = "redirect:/";
	private static final String UPDATE_PWD_CMS_PAGE = "updatePassword";
	private static final String INVALID_TOKEN = "tokenInvalid";
	private static final String RESET_PASSWORD_TITLE = "Reset Password";
	private static final String RESET_PASSWORD_AND_USERNAME_TITLE = "Forgotten Email or Password";
	private String resetBredcrumbMessgae = StringUtils.EMPTY;
	

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;

	@Resource(name = "updatePasswordFormValidator")
	private UpdatePasswordFormValidator updatePasswordFormValidator;

	@Resource(name = "asahiForgotPasswordFormValidator")
	private Validator asahiForgotPasswordFormValidator;

	@Resource(name = "asahiUpdatePasswordFormValidator")
	private AsahiUpdatePasswordFormValidator asahiUpdatePasswordFormValidator;
	
	@Resource(name = "customerAccountService")
	private ApbCustomerAccountService customerAccountService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

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
				LOG.warn("Email: " + ApbXSSEncoderUtil.encodeValue(form.getEmail()) + " does not exist in the database.");
			}
			return ControllerConstants.Views.Fragments.Password.ForgotPasswordValidationMessage;
		}
	}

	@GetMapping("/request/external")
	public String getExternalPasswordRequest(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("isForgetPassword", "true");
		model.addAttribute(new AsahiForgottenPwdForm());
		storeCmsPageInModel(model, getContentPageForLabelOrId(null));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));
		updatePageTitle(model, null);
		addBackgroundImage(model, null);

		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs(asahiSiteUtil.isSga()?RESET_PWD_TITLE:FORGOTTEN_PWD_TITLE));
		return ControllerConstants.Views.Pages.Password.PasswordResetRequest;
	}

	private void addBackgroundImage(final Model model, final String pageName) throws CMSItemNotFoundException
	{
		if ((getContentPageForLabelOrId(pageName)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(pageName)).getBackgroundImage().getURL());
		}
	}

	@GetMapping("/request/external/conf")
	public String getExternalPasswordRequestConf(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("isForgetPassword", "true");
		storeCmsPageInModel(model, getContentPageForLabelOrId(null));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));
		updatePageTitle(model, null);
		addBackgroundImage(model, null);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs(asahiSiteUtil.isSga()?RESET_PWD_TITLE:FORGOTTEN_PWD_TITLE));
		return ControllerConstants.Views.Pages.Password.PasswordResetRequestConfirmation;
	}

	@PostMapping("/request/external")
	public String externalPasswordRequest(final AsahiForgottenPwdForm form, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		model.addAttribute("isForgetPassword", "true");
		asahiForgotPasswordFormValidator.validate(form, bindingResult);
		storeCmsPageInModel(model, getContentPageForLabelOrId(null));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));
		updatePageTitle(model, null);
		addBackgroundImage(model, null);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs(asahiSiteUtil.isSga()?RESET_PWD_TITLE:FORGOTTEN_PWD_TITLE));
		
		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "forgottenPwd.email.error");
			return ControllerConstants.Views.Pages.Password.PasswordResetRequest;
		}
		else
		{
			try
			{
				customerFacade.forgottenPassword(form.getEmail());
			}
			catch (final UnknownIdentifierException unknownIdentifierException)
			{
				LOG.warn("Email: " + ApbXSSEncoderUtil.encodeValue(form.getEmail()) + " does not exist in the database.");
				GlobalMessages.addErrorMessage(model, "forgottenPwd.email.error");
				bindingResult.rejectValue("email", "forgottenPwd.email.notexists", new Object[]
				{ form.getEmail() }, "forgottenPwd.email.notexists");
				return ControllerConstants.Views.Pages.Password.PasswordResetRequest;
			}
			return REDIRECT_PWD_REQ_CONF;
		}
	}

	@GetMapping("/change")
	public String getChangePassword(@RequestParam(required = false) final String token, final Model model, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		model.addAttribute("isForgetPassword", "true");
		if (StringUtils.isBlank(token))
		{
			return REDIRECT_HOME;
		}
		if(!customerAccountService.checkTokenValid(token))
		{
			model.addAttribute(INVALID_TOKEN, true);
		}
		final AsahiUpdatePwdForm form = new AsahiUpdatePwdForm();
		form.setToken(token);
		model.addAttribute(form);
		storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PWD_CMS_PAGE));
		addBackgroundImage(model, UPDATE_PWD_CMS_PAGE);
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PWD_CMS_PAGE));

		if ((getContentPageForLabelOrId(UPDATE_PWD_CMS_PAGE)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(UPDATE_PWD_CMS_PAGE)).getBackgroundImage().getURL());
		}

		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				resourceBreadcrumbBuilder.getBreadcrumbs("text.manageuser.restpasswordform"));
		return ControllerConstants.Views.Pages.Password.PasswordResetChangePage;
	}

	@PostMapping("/change")
	public String changePassword(final AsahiUpdatePwdForm form, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		model.addAttribute("isForgetPassword", "true");
		asahiUpdatePasswordFormValidator.validate(form, bindingResult);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs(RESET_PASSWORD));
		addBackgroundImage(model, null);
		/* getUpdatePasswordFormValidator().validate(form, bindingResult); */
		if (bindingResult.hasErrors())
		{
			model.addAttribute("errorMessage", "true");
			prepareErrorMessage(model, UPDATE_PWD_CMS_PAGE);
			return ControllerConstants.Views.Pages.Password.PasswordResetChangePage;
		}
		if (!StringUtils.isBlank(form.getToken()))
		{
			try
			{
				resetBredcrumbMessgae = form.getToken();
				customerFacade.updatePassword(form.getToken(), form.getPwd());
			}
			catch (final TokenInvalidatedException e)
			{
				model.addAttribute(INVALID_TOKEN, true);
				return ControllerConstants.Views.Pages.Password.PasswordResetChangePage;
			}
			catch (final RuntimeException e)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("RuntimeException occurred...", e);
				}
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "updatePwd.token.invalid");
				model.addAttribute(INVALID_TOKEN, true);
				return ControllerConstants.Views.Pages.Password.PasswordResetChangePage;
			}
		}
		return REDIRECT_PWD_CHANGE_CONF;
	}

	@GetMapping("/change/conf")
	public String getPasswordChangedConf(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("isPasswordUpdated", "true");
		storeCmsPageInModel(model, getContentPageForLabelOrId(null));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));
		addBackgroundImage(model, null);
		if (StringUtils.isNotEmpty(resetBredcrumbMessgae))
		{
			model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs(RESET_PASSWORD));
		}
		else
		{
			model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs(FORGOTTEN_PWD_TITLE));
		}
		return ControllerConstants.Views.Pages.Password.PasswordChangedConfirmation;
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

		if ((getContentPageForLabelOrId(page)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(page)).getBackgroundImage().getURL());
		}

	}


	public UpdatePasswordFormValidator getUpdatePasswordFormValidator()
	{
		return updatePasswordFormValidator;
	}
	
	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		String title = asahiSiteUtil.isSga()?RESET_PASSWORD_AND_USERNAME_TITLE:RESET_PASSWORD_TITLE;
		storeContentPageTitleInModel(model,getPageTitleResolver().resolveContentPageTitle(title));
	}
}