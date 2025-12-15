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

import java.io.IOException;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.storefront.controllers.ControllerConstants;
import com.apb.storefront.forms.ApbRegisterForm;
import com.apb.storefront.forms.ApbRequestRegisterForm;
import com.apb.storefront.validators.ImportRequestRegistrationPDFFormValidator;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commons.renderer.exceptions.RendererException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaIOException;


/**
 * Register Controller for mobile. Handles login register and request registration for the account flow.
 */
@Controller
@RequestMapping(value = "/register")
public class RegisterPageController extends ApbAbstractRegisterPageController
{
	@Resource(name = "importRequestRegistrationPDFFormValidator")
	private ImportRequestRegistrationPDFFormValidator importRequestRegistrationPDFFormValidator;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	private final String PASSWORD_LENGTH = "customer.password.length.";

	private HttpSessionRequestCache httpSessionRequestCache;
	
	@Resource
 	private AsahiSiteUtil asahiSiteUtil;
	

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
		return ControllerConstants.Views.Pages.Account.AccountRegisterPage;
	}

	@Resource(name = "httpSessionRequestCache")
	public void setHttpSessionRequestCache(final HttpSessionRequestCache accHttpSessionRequestCache)
	{
		this.httpSessionRequestCache = accHttpSessionRequestCache;
	}

	/**
	 * @param model
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping
	public String doRegister(final Model model) throws CMSItemNotFoundException
	{
		final String pwdLen = asahiConfigurationService.getString(PASSWORD_LENGTH + getCmsSiteService().getCurrentSite().getUid(),
				"5");
		model.addAttribute("pwdMaxLen", pwdLen);
		setMaximunSize(model);
		return getDefaultRegistrationPage(model);
	}

	/**
	 * Create new self registration
	 *
	 * @param form
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @param response
	 * @param redirectModel
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@PostMapping("/self-customer")
	public String doSelfRegister(final ApbRegisterForm form, final BindingResult bindingResult, final Model model,
			final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		setMaximunSize(model);
		replaceBlankForAbnNumber(form);
		getApbRegistrationValidator().validate(form, bindingResult);
		final String pwdLen = asahiConfigurationService.getString(PASSWORD_LENGTH + getCmsSiteService().getCurrentSite().getUid(),
				"5");
		final int length = Integer.parseInt(pwdLen);
		model.addAttribute("pwdMaxLen", pwdLen);
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		if (StringUtils.isNotEmpty(form.getPwd()) && StringUtils.length(form.getPwd()) < length)
		{
			model.addAttribute("label1", "register.pwd.max.length");
			model.addAttribute("label2", "register.pwd.max.characters");
		}
		return processSelfRegisterUserRequest(null, form, bindingResult, model, request, response, redirectModel);
	}
	
	/**
	 * metheod toreplace the blank characters from the ABN
	 * @param form
	 */
	protected void replaceBlankForAbnNumber(final ApbRegisterForm form) {
		if(asahiSiteUtil.isSga()) {
			if(CollectionUtils.isNotEmpty(form.getAlbCompanyInfoData())){
				form.getAlbCompanyInfoData().forEach(companyInfo -> companyInfo.setAbnNumber(companyInfo.getAbnNumber().replace(" ", "")));
			}
		} else {
			form.setAbnNumber(form.getAbnNumber().replace(" ", ""));
		}
	}

	/**
	 * Request User Registration
	 *
	 * @param form
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @param response
	 * @param redirectModel
	 * @return
	 * @throws CMSItemNotFoundException
	 * @throws DuplicateUidException
	 * @throws UnknownIdentifierException
	 */
	@PostMapping("/request-register")
	public String doRegister(final Model model, final ApbRequestRegisterForm form, final BindingResult bindingResult,
			final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException, UnknownIdentifierException, DuplicateUidException, MediaIOException, RendererException,
			IllegalArgumentException, IOException
	{
		setMaximunSize(model);
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		form.setAbn(form.getAbn().replace(" ", ""));
		getApbRequestRegistrationValidator().validate(form, bindingResult);
		importRequestRegistrationPDFFormValidator.validate(form, bindingResult);
		return processRequestRegisterUserRequest(null, form, bindingResult, model, request, response, redirectModel);
	}

}
