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
package com.apb.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.controllers.ApbcommorgaddonControllerConstants;
import com.apb.core.constants.ApbCoreConstants;
import com.sabmiller.core.b2b.services.SabmB2BCustomerService;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.customer.data.RemoveCustomerJson;
import com.apb.core.util.ApbXSSEncoderUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.b2bunit.ApbB2BUnitFacade;
import com.apb.facades.product.AsahiRecommendationFacade;
import com.apb.facades.product.data.AsahiRoleData;
import com.apb.facades.user.ApbUserFacade;
import com.apb.forms.B2BCustomerForm;
import com.apb.forms.CustomerResetPasswordForm;
import com.apb.forms.validation.AsahiProfileValidator;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.apb.breadcrumb.impl.APBMyCompanyBreadcrumbBuilder;
import com.apb.core.util.AsahiCoreUtil;

/**
 * Controller defines routes to manage Users within My Company section.
 */
@RequestMapping("/my-company/organization-management/manage-users")
public class AsahiUserManagementPageController extends AsahiMyCompanyPageController
{
	private static final Logger LOG = LoggerFactory.getLogger(AsahiUserManagementPageController.class);
	public static final String PWD = "storefront.passwordPattern.";
	public static final String ENABLE_REMOVE_USER = "storefront.enable.remove.user.";
	
	private static final String ADD_NEW_PROFILE_PAGE_TITLE = "Add New Profile";
	private static final String PROFILE_DETAILS_PAGE_TITLE = "Profile Details";
	private static final String EDIT_PROFILE_PAGE_TITLE = "Edit Profile";
	private static final String UPDATE_PASSWORD_PAGE_TITLE = "Update Password";

	@Resource(name = "asahiProfileValidator")
	private AsahiProfileValidator asahiProfileValidator;

	@Resource(name = "apbUserFacade")
	private ApbUserFacade apbUserFacade;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "asahiSiteUtil")
	private AsahiSiteUtil asahiSiteUtil;

	@Autowired
	private CMSSiteService cmsSiteService;

	@Resource
	private ApbB2BUnitService apbB2BUnitService;

	@Resource
	private ApbB2BUnitFacade apbB2BUnitFacade;

	@Resource(name="apbMyCompanyBreadcrumbBuilder")
	private APBMyCompanyBreadcrumbBuilder apbMyCompanyBreadcrumbBuilder;
	
	@Resource(name = "asahiRecommendationFacade")
	private AsahiRecommendationFacade asahiRecommendationFacade;
	
	@Resource(name = "asahiCoreUtil")
	private AsahiCoreUtil asahiCoreUtil;
	
	@Resource(name = "sabmB2BCustomerService")
	private SabmB2BCustomerService sabmB2BCustomerService;


	@GetMapping
	@RequireHardLogIn
	public String manageUsers(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") ShowMode showMode,
			@RequestParam(value = "sort", defaultValue = B2BCustomerModel.NAME) final String sortCode, final Model model)
					throws CMSItemNotFoundException
	{
		// Handle paged search results
		final PageableData pageableData = createPageableData(page, getSearchPageSize(), sortCode, showMode);
		final SearchPageData<CustomerData> searchPageData = b2bUserFacade.getPagedCustomers(pageableData);
		final AsahiB2BUnitModel currentUnit = apbB2BUnitService.getCurrentB2BUnit();
		final Collection<String> disabledUser = currentUnit.getDisabledUser();

		searchPageData.getResults().stream().forEach(customer -> {
			if(asahiSiteUtil.isSga()) {
				customer.setSamAccess(null !=currentUnit.getPayerAccount() ? 
						apbB2BUnitFacade.getSamAccessType(customer.getUid(), currentUnit.getPayerAccount().getUid()) : ApbCoreConstants.ORDER_ACCESS);
				customer.setAccessDenied(null !=currentUnit.getPayerAccount() ? 
						apbB2BUnitFacade.isSamAccessDenied(customer.getUid(), currentUnit.getPayerAccount().getUid()) : Boolean.FALSE);
				customer.setPendingApproval(null !=currentUnit.getPayerAccount() ? 
						apbB2BUnitFacade.isSamAccessApprovalPending(customer.getUid(), currentUnit.getPayerAccount().getUid()) : Boolean.FALSE);
				
				if (apbUserFacade.getCurrentUser() instanceof BDECustomerModel 
						&&  apbUserFacade.isUserEligibleToReceiveWelcomeEmail(customer.getUid(), ApbCoreConstants.SGA_SITE_ID)) {
				
					customer.setDisplayWelcomeEmailLink(Boolean.TRUE);
				}
			}
			if (CollectionUtils.isNotEmpty(disabledUser) && disabledUser.contains(customer.getUid()))
			{
				customer.setActive(Boolean.FALSE);
			}
		});

		if (apbUserFacade.getCurrentUser() instanceof BDECustomerModel) {
			model.addAttribute("displayWelcomeEmailLinks", true);
		}
		apbUserFacade.sortCustomers(searchPageData);

		populateModel(model, searchPageData, showMode);
		storeCmsPageInModel(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-users",
				getMessageSource().getMessage("text.company.manageUsers", null, getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		model.addAttribute("removeUserEnabled",isUserRemovalEnabled());
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		
		if (asahiSiteUtil.isBDECustomer()) {
			Integer totalProductCount = asahiRecommendationFacade.getTotalRepRecommendedProducts();
			model.addAttribute("recommendationsCount", String.valueOf(totalProductCount));
		}
		return ApbcommorgaddonControllerConstants.Views.Pages.MyCompany.MyCompanyManageUsersPage;
	}

	@Override
	@GetMapping("/details")
	@RequireHardLogIn
	public String manageUserDetail(@RequestParam("user") final String user, final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("action", "manageUsers");
		model.addAttribute("removeUserEnabled",isUserRemovalEnabled());
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		final String landingPage = super.manageUserDetail(user, model);
		updatePageTitle(model, PROFILE_DETAILS_PAGE_TITLE);
		return landingPage;
	}

	@Override
	@GetMapping("/edit")
	@RequireHardLogIn
	public String editUser(@RequestParam("user") final String user, final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("action", "manageUsers");
		model.addAttribute("asahiRoles", getApbRoles());
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		final String landingPage = super.editUser(user, model);
		updatePageTitle(model, EDIT_PROFILE_PAGE_TITLE);
		return landingPage;
	}

	@GetMapping("/edit-approver")
	@RequireHardLogIn
	public String editUsersApprover(@RequestParam("user") final String user, @RequestParam("approver") final String approver,
			final Model model, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		model.addAttribute("cancelUrl", getCancelUrl(MANAGE_USER_DETAILS_URL, request.getContextPath(), user));
		model.addAttribute("saveUrl",
				String.format("%s/my-company/organization-management/manage-users/edit-approver?user=%s&approver=%s",
						request.getContextPath(), urlEncode(user), urlEncode(approver)));
		final String editUserUrl = super.editUser(approver, model);
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUserDetailsBreadcrumb(user);
		breadcrumbs
		.add(new Breadcrumb(
				String.format("/my-company/organization-management/manage-units/edit-approver?user=%s&approver=%s",
						urlEncode(user), urlEncode(approver)),
				getMessageSource().getMessage("text.company.manageusers.edit", new Object[]
						{ approver }, "Edit {0} User", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		return editUserUrl;
	}

	@PostMapping("/edit-approver")
	@RequireHardLogIn
	public String editUsersApprover(@RequestParam("user") final String user, @RequestParam("approver") final String approver,
			@Valid final B2BCustomerForm b2BCustomerForm, final BindingResult bindingResult, final Model model,
			final HttpServletRequest request, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		model.addAttribute("cancelUrl", getCancelUrl(MANAGE_USER_DETAILS_URL, request.getContextPath(), user));
		model.addAttribute("saveUrl",
				String.format("%s/my-company/organization-management/manage-users/edit-approver?user=%s&approver=%s",
						request.getContextPath(), urlEncode(user), urlEncode(approver)));

		final String editUserUrl = super.editUser(user, b2BCustomerForm, bindingResult, model, redirectModel);
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUserDetailsBreadcrumb(user);
		breadcrumbs
		.add(new Breadcrumb(
				String.format("/my-company/organization-management/manage-units/edit-approver?user=%s&approver=%s",
						urlEncode(user), urlEncode(approver)),
				getMessageSource().getMessage("text.company.manageusers.edit", new Object[]
						{ approver }, "Edit {0} User", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		if (bindingResult.hasErrors() || model.containsAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER))
		{
			return editUserUrl;
		}
		else
		{
			return String.format(REDIRECT_TO_USER_DETAILS, urlEncode(user));
		}
	}

	@RequestMapping(value = "/approvers/remove", method =
		{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public String removeApproverFromCustomer(@RequestParam("user") final String user,
			@RequestParam("approver") final String approver, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		b2bApproverFacade.removeApproverFromCustomer(user, approver);
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "text.confirmation.approver.removed");
		return String.format(REDIRECT_TO_USER_DETAILS, urlEncode(user));
	}

	@Override
	@PostMapping("/edit")
	@RequireHardLogIn
	public String editUser(@RequestParam("user") final String user, @Valid final B2BCustomerForm b2BCustomerForm,
			final BindingResult bindingResult, final Model model, final RedirectAttributes redirectModel)
					throws CMSItemNotFoundException
	{
		asahiProfileValidator.validate(b2BCustomerForm, bindingResult);

		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		return super.editUser(user, b2BCustomerForm, bindingResult, model, redirectModel);
	}

	@Override
	@GetMapping("/create")
	@RequireHardLogIn
	public String createUser(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("action", "manageUsers");
		model.addAttribute("asahiRoles", getApbRoles());
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		final String landingPage = super.createUser(model);
		updatePageTitle(model, ADD_NEW_PROFILE_PAGE_TITLE);
		return landingPage;
	}

	@Override
	@PostMapping("/create")
	@RequireHardLogIn
	public String createUser(@Valid final B2BCustomerForm b2BCustomerForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{

		asahiProfileValidator.validate(b2BCustomerForm, bindingResult);
		model.addAttribute("action", "manageUsers");
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		return super.createUser(b2BCustomerForm, bindingResult, model, redirectModel);
	}

	@GetMapping("/disable")
	@RequireHardLogIn
	public String disableUserConfirmation(@RequestParam("user") final String user, final Model model)
			throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(user);
		breadcrumbs.add(
				new Breadcrumb(String.format("/my-company/organization-management/manage-users/disable?user=%s", urlEncode(user)),
						getMessageSource().getMessage("text.company.manage.units.disable.breadcrumb", new Object[]
								{ user }, "Disable {0} Customer", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);

		final CustomerData customerData = b2bUserFacade.getCustomerForUid(user);
		model.addAttribute("customerData", customerData);
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		return ApbcommorgaddonControllerConstants.Views.Pages.MyCompany.MyCompanyManageUserDisbaleConfirmPage;
	}

	@PostMapping("/disable")
	@RequireHardLogIn
	public String disableUser(@RequestParam("user") final String user, final Model model, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUnitsDetailsBreadcrumbs(user);
		breadcrumbs.add(
				new Breadcrumb(String.format("/my-company/organization-management/manage-users/disable?user=%s", urlEncode(user)),
						getMessageSource().getMessage("text.company.manageusers.disable.breadcrumb", new Object[]
								{ user }, "Disable {0}  Customer ", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);

		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}

		if (checkForSelfUser(user))
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "form.b2bcustomer.adminrole.error");
		}
		else
		{
			b2bUserFacade.disableCustomer(user);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "text.confirmation.user.disable",
					new Object[]
							{ user , asahiSiteUtil.getCurrentSite().getName()});
		}

		return String.format(REDIRECT_TO_USER_DETAILS, urlEncode(user));
	}

	@PostMapping("/enable")
	@RequireHardLogIn
	public String enableUser(@RequestParam("user") final String user, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		b2bUserFacade.enableCustomer(user);
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "text.confirmation.user.enable",
				new Object[]
						{ user, asahiSiteUtil.getCurrentSite().getName() });
		return String.format(REDIRECT_TO_USER_DETAILS, urlEncode(user));
	}

	@GetMapping("/resetpassword")
	@RequireHardLogIn
	public String updatePassword(@RequestParam("user") final String user, final Model model) throws CMSItemNotFoundException
	{
		if (!model.containsAttribute("customerResetPasswordForm"))
		{
			final CustomerResetPasswordForm customerResetPasswordForm = new CustomerResetPasswordForm();
			customerResetPasswordForm.setUid(user);
			model.addAttribute("customerResetPasswordForm", customerResetPasswordForm);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = apbMyCompanyBreadcrumbBuilder.createManageUserDetailsBreadcrumb(user);
		breadcrumbs.add(new Breadcrumb(
				String.format("/my-company/organization-management/manage-users/restpassword?user=%s", urlEncode(user)),
				getMessageSource().getMessage("text.company.manageusers.restpassword.breadcrumb", new Object[]
						{ user }, "Reset Password {0}  User ", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		updatePageTitle(model, UPDATE_PASSWORD_PAGE_TITLE);

		return ApbcommorgaddonControllerConstants.Views.Pages.MyCompany.MyCompanyManageUserResetPasswordPage;
	}

	@PostMapping("/resetpassword")
	@RequireHardLogIn
	public String updatePassword(@RequestParam("user") final String user,
			@Valid final CustomerResetPasswordForm customerResetPasswordForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		validatePassword(bindingResult, customerResetPasswordForm.getNewPassword(),
				customerResetPasswordForm.getCheckNewPassword());
		if (bindingResult.hasErrors())
		{
			model.addAttribute(customerResetPasswordForm);
			GlobalMessages.addErrorMessage(model, "form.global.error");
			return updatePassword(customerResetPasswordForm.getUid(), model);
		}

		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}

		if (customerResetPasswordForm.getNewPassword().equals(customerResetPasswordForm.getCheckNewPassword()))
		{

			b2bUserFacade.resetCustomerPassword(customerResetPasswordForm.getUid(), customerResetPasswordForm.getNewPassword());
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "text.confirmation.password.updated");
		}
		else
		{
			model.addAttribute(customerResetPasswordForm);
			bindingResult.rejectValue("checkNewPassword", "validation.checkPwd.equals", new Object[] {},
					"validation.checkPwd.equals");
			GlobalMessages.addErrorMessage(model, "form.global.error");
			return updatePassword(customerResetPasswordForm.getUid(), model);
		}

		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder
				.createManageUnitsDetailsBreadcrumbs(customerResetPasswordForm.getUid());
		breadcrumbs.add(new Breadcrumb(
				String.format("/my-company/organization-management/manage-users/restpassword?user=%s",
						urlEncode(customerResetPasswordForm.getUid())),
				getMessageSource().getMessage("text.company.manageusers.restpassword.breadcrumb", new Object[]
						{ customerResetPasswordForm.getUid() }, "Reset Password {0}  Customer ", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		return String.format(REDIRECT_TO_USER_DETAILS, urlEncode(customerResetPasswordForm.getUid()));
	}

	@GetMapping("/approvers")
	@RequireHardLogIn
	public String getPagedApproversForCustomer(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final AbstractSearchPageController.ShowMode showMode,
			@RequestParam(value = "sort", defaultValue = UserModel.NAME) final String sortCode,
			@RequestParam("user") final String user, final Model model, final HttpServletRequest request)
					throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));

		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUserDetailsBreadcrumb(user);
		breadcrumbs.add(
				new Breadcrumb(String.format("/my-company/organization-management/manage-users/approvers?user=%s", urlEncode(user)),
						getMessageSource().getMessage("text.company.manageUsers.approvers.breadcrumb", new Object[]
								{ user }, "Customer {0} Approvers", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		final B2BUnitData unit = b2bUnitFacade.getUnitForUid(user);
		model.addAttribute("unit", unit);

		// Handle paged search results
		final PageableData pageableData = createPageableData(page, getSearchPageSize(), sortCode, showMode);
		final SearchPageData<CustomerData> searchPageData = b2bApproverFacade.getPagedApproversForCustomer(pageableData, user);
		populateModel(model, searchPageData, showMode);
		model.addAttribute("action", "approvers");
		model.addAttribute("baseUrl", "/my-company/organization-management/manage-users");
		model.addAttribute("cancelUrl", getCancelUrl(MANAGE_USER_DETAILS_URL, request.getContextPath(), user));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		return ApbcommorgaddonControllerConstants.Views.Pages.MyCompany.MyCompanyManageUserCustomersPage;
	}

	@ResponseBody
	@RequestMapping(value = "/approvers/select", method =
{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public B2BSelectionData selectApproverForCustomer(@RequestParam("user") final String user,
			@RequestParam("approver") final String approver) throws CMSItemNotFoundException
	{
		return populateDisplayNamesForRoles(b2bApproverFacade.addApproverForCustomer(user, approver));
	}

	@ResponseBody
	@RequestMapping(value = "/approvers/deselect", method =
{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public B2BSelectionData deselectApproverForCustomer(@RequestParam("user") final String user,
			@RequestParam("approver") final String approver) throws CMSItemNotFoundException
	{
		return populateDisplayNamesForRoles(b2bApproverFacade.removeApproverFromCustomer(user, approver));
	}

	@GetMapping("/permissions")
	@RequireHardLogIn
	public String getPagedPermissionsForCustomer(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final AbstractSearchPageController.ShowMode showMode,
			@RequestParam(value = "sort", defaultValue = UserModel.NAME) final String sortCode,
			@RequestParam("user") final String user, final Model model, final HttpServletRequest request)
					throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));

		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUserDetailsBreadcrumb(user);
		breadcrumbs.add(
				new Breadcrumb(String.format("/my-company/organization-management/manage-users/permissions?user=%s", urlEncode(user)),
						getMessageSource().getMessage("text.company.manage.units.permissions.breadcrumb", new Object[]
								{ user }, "Customer {0} Permissions", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);

		// Handle paged search results
		final PageableData pageableData = createPageableData(page, getSearchPageSize(), sortCode, showMode);
		final SearchPageData<B2BPermissionData> searchPageData = b2bPermissionFacade.getPagedPermissionsForCustomer(pageableData,
				user);
		populateModel(model, searchPageData, showMode);
		model.addAttribute("action", "permissions");
		model.addAttribute("baseUrl", "/my-company/organization-management/manage-users");
		model.addAttribute("cancelUrl", getCancelUrl(MANAGE_USER_DETAILS_URL, request.getContextPath(), user));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		return ApbcommorgaddonControllerConstants.Views.Pages.MyCompany.MyCompanyManageUserPermissionsPage;
	}

	@ResponseBody
	@RequestMapping(value = "/permissions/select", method =
{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public B2BSelectionData selectPermissionForCustomer(@RequestParam("user") final String user,
			@RequestParam("permission") final String permission) throws CMSItemNotFoundException
	{
		return b2bPermissionFacade.addPermissionToCustomer(user, permission);
	}

	@ResponseBody
	@RequestMapping(value = "/permissions/deselect", method =
{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public B2BSelectionData deselectPermissionForCustomer(@RequestParam("user") final String user,
			@RequestParam("permission") final String permission) throws CMSItemNotFoundException
	{
		return b2bPermissionFacade.removePermissionFromCustomer(user, permission);
	}

	@RequestMapping(value = "/permissions/remove", method =
		{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public String removeCustomersPermission(@RequestParam("user") final String user,
			@RequestParam("permission") final String permission, final RedirectAttributes redirectModel)
					throws CMSItemNotFoundException
	{
		b2bPermissionFacade.removePermissionFromCustomer(user, permission);
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "text.confirmation.permission.removed");
		return String.format(REDIRECT_TO_USER_DETAILS, urlEncode(user));
	}

	@GetMapping("/permissions/confirm/remove")
	@RequireHardLogIn
	public String confirmRemovePermissionFromUser(@RequestParam("user") final String user,
			@RequestParam("permission") final String permission, final Model model, final HttpServletRequest request)
					throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUserDetailsBreadcrumb(user);
		breadcrumbs.add(
				new Breadcrumb("#", getMessageSource().getMessage("text.company.users.remove.permission.confirmation", new Object[]
						{ permission }, "Remove Permission {0}", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("arguments", String.format("%s, %s", permission, user));
		model.addAttribute("page", "users");
		model.addAttribute("role", "permission");
		model.addAttribute("disableUrl",
				String.format(
						request.getContextPath()
						+ "/my-company/organization-management/manage-users/permissions/remove/?user=%s&permission=%s",
						urlEncode(user), urlEncode(permission)));
		model.addAttribute("cancelUrl", getCancelUrl(MANAGE_USER_DETAILS_URL, request.getContextPath(), user));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		return ApbcommorgaddonControllerConstants.Views.Pages.MyCompany.MyCompanyRemoveDisableConfirmationPage;
	}

	@GetMapping("/approvers/confirm/remove")
	@RequireHardLogIn
	public String confirmRemoveApproverFromUser(@RequestParam("user") final String user,
			@RequestParam("approver") final String approver, final Model model, final HttpServletRequest request)
					throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));

		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUserDetailsBreadcrumb(user);
		breadcrumbs.add(new Breadcrumb("#", getMessageSource()
				.getMessage(String.format("text.company.users.remove.%s.confirmation", B2BConstants.B2BAPPROVERGROUP), new Object[]
						{ approver }, "Remove B2B Approver {0}", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("arguments", String.format("%s, %s", approver, user));
		model.addAttribute("page", "users");
		model.addAttribute("role", B2BConstants.B2BAPPROVERGROUP);
		model.addAttribute("disableUrl",
				String.format(
						request.getContextPath()
						+ "/my-company/organization-management/manage-users/approvers/remove/?user=%s&approver=%s",
						urlEncode(user), urlEncode(approver)));
		model.addAttribute("cancelUrl", getCancelUrl(MANAGE_USER_DETAILS_URL, request.getContextPath(), user));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		return ApbcommorgaddonControllerConstants.Views.Pages.MyCompany.MyCompanyRemoveDisableConfirmationPage;
	}

	@GetMapping("/usergroups")
	@RequireHardLogIn
	public String getPagedB2BUserGroupsForCustomer(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final AbstractSearchPageController.ShowMode showMode,
			@RequestParam(value = "sort", defaultValue = UserModel.NAME) final String sortCode,
			@RequestParam("user") final String user, final Model model, final HttpServletRequest request)
					throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MY_COMPANY_CMS_PAGE));

		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUserDetailsBreadcrumb(user);
		breadcrumbs.add(
				new Breadcrumb(String.format("/my-company/organization-management/manage-users/usergroups?user=%s", urlEncode(user)),
						getMessageSource().getMessage("text.company.manageUsers.usergroups.breadcrumb", new Object[]
								{ user }, "Customer {0} User Groups", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);

		// Handle paged search results
		final PageableData pageableData = createPageableData(page, getSearchPageSize(), sortCode, showMode);
		final SearchPageData<B2BUserGroupData> searchPageData = b2bUserFacade.getPagedB2BUserGroupsForCustomer(pageableData, user);
		populateModel(model, searchPageData, showMode);
		model.addAttribute("action", "usergroups");
		model.addAttribute("baseUrl", "/my-company/organization-management/manage-users");
		model.addAttribute("cancelUrl", getCancelUrl(MANAGE_USER_DETAILS_URL, request.getContextPath(), user));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		return ApbcommorgaddonControllerConstants.Views.Pages.MyCompany.MyCompanyManageUserB2BUserGroupsPage;
	}

	@ResponseBody
	@RequestMapping(value = "/usergroups/select", method =
{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public B2BSelectionData selectB2BUserGroupForCustomer(@RequestParam("user") final String user,
			@RequestParam("usergroup") final String usergroup) throws CMSItemNotFoundException
	{
		return b2bUserFacade.addB2BUserGroupToCustomer(user, usergroup);
	}

	@ResponseBody
	@RequestMapping(value = "/usergroups/deselect", method =
{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public B2BSelectionData deselectB2BUserGroupForCustomer(@RequestParam("user") final String user,
			@RequestParam("usergroup") final String usergroup) throws CMSItemNotFoundException
	{
		return b2bUserFacade.deselectB2BUserGroupFromCustomer(user, usergroup);
	}

	@GetMapping("/usergroups/confirm/remove")
	@RequireHardLogIn
	public String confirmRemoveUserGroupFromUser(@RequestParam("user") final String user,
			@RequestParam("usergroup") final String usergroup, final Model model, final HttpServletRequest request)
					throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUserDetailsBreadcrumb(user);
		breadcrumbs.add(
				new Breadcrumb("#", getMessageSource().getMessage("text.company.users.remove.usergroup.confirmation", new Object[]
						{ usergroup }, "Remove User group {0}", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute("arguments", String.format("%s, %s", user, usergroup));
		model.addAttribute("page", "users");
		model.addAttribute("role", "usergroup");
		model.addAttribute("disableUrl",
				String.format(
						request.getContextPath()
						+ "/my-company/organization-management/manage-users/usergroups/remove/?user=%s&usergroup=%s",
						urlEncode(user), urlEncode(usergroup)));
		model.addAttribute("cancelUrl", getCancelUrl(MANAGE_USER_DETAILS_URL, request.getContextPath(), user));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		return ApbcommorgaddonControllerConstants.Views.Pages.MyCompany.MyCompanyRemoveDisableConfirmationPage;
	}

	@RequestMapping(value = "/usergroups/remove", method =
		{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public String removeCustomersUserGroup(@RequestParam("user") final String user,
			@RequestParam("usergroup") final String usergroup, final RedirectAttributes redirectModel)
					throws CMSItemNotFoundException
	{
		try
		{
			b2bUserFacade.removeB2BUserGroupFromCustomerGroups(user, usergroup);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
					"text.confirmation.usergroup.removed");
		}
		catch (final UnknownIdentifierException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("can not remove b2b user '" + ApbXSSEncoderUtil.encodeValue(user) + "' from group '"
						+ ApbXSSEncoderUtil.encodeValue(usergroup) + "' due to, " + e.getMessage(), e);
			}
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "usergroup.notfound");
		}
		return String.format(REDIRECT_TO_USER_DETAILS, urlEncode(user));
	}

	@PostMapping("/remove_customer")
	@RequireHardLogIn
	public String removeCustomerFromB2BUnit(@RequestParam("user") final String user, final Model model, final RedirectAttributes redirectModel) {
		if(isUserRemovalEnabled()) {
			final AsahiB2BUnitModel currentUnit = apbB2BUnitService.getCurrentB2BUnit();
			if (apbUserFacade.removeCustomerFromB2bUnit(user, currentUnit)) {
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "text.user.removed.from.account.success", new Object[] {user});
			} else {
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "text.user.removed.from.account.failed", new Object[] {user});
			}
		} else {
			if(LOG.isWarnEnabled()) {
				LOG.warn("Remove user feature is currently disabled");
			}
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "text.remove.user.disabled");
		}
		return REDIRECT_TO_MANAGE_USER;
	}
	
	@RequestMapping(value = "/sendWelcomeEmail", method = { RequestMethod.GET,RequestMethod.POST})
	@RequireHardLogIn
	public ResponseEntity<String> sendWelcomeEmail(@RequestParam("user") final String user, final Model model, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
			boolean mailSent = apbUserFacade.sendWelcomeEmail(user);
			
			if (mailSent) {
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "text.confirmation.user.sent.welcome.email",
						new Object[]
						{ user });
				String confirmationMessage = getMessageSource().getMessage("text.confirmation.user.sent.welcome.email.success", new Object[]
						{ user }, getI18nService().getCurrentLocale());
				return new ResponseEntity<String>(confirmationMessage,HttpStatus.OK);
			}	
			return new ResponseEntity<String>(getMessageSource().getMessage("text.confirmation.user.sent.welcome.email.error", new Object[]
					{ user }, getI18nService().getCurrentLocale()),HttpStatus.EXPECTATION_FAILED);
		
		
	}

	private List<AsahiRoleData> getApbRoles()
	{

		return apbUserFacade.getAsahiRole();
	}


	private boolean checkForSelfUser(final String uid)
	{

		boolean selfUser = false;

		if (customerFacade.getCurrentCustomer().getUid().equals(uid))
		{
			selfUser = true;
		}
		return selfUser;
	}

	protected void validatePassword(final Errors errors, final String newPassword, final String checkPassword)
	{
		boolean regexMatch = Boolean.FALSE;
		if (StringUtils.isEmpty(newPassword))
		{
			errors.rejectValue("newPassword", "updatePwd.pwd.invalid");
		}
		if (StringUtils.isEmpty(checkPassword))
		{
			errors.rejectValue("checkNewPassword", "updatePwd.pwd.invalid");
		}
		if (StringUtils.isNotEmpty(newPassword) && !validatePasswordPattern(newPassword))
		{
			errors.rejectValue("newPassword", "updatePwd.pwd.invalid");
			regexMatch = Boolean.TRUE;
		}
		if (StringUtils.isNotEmpty(checkPassword) && !validatePasswordPattern(newPassword))
		{
			errors.rejectValue("checkNewPassword", "updatePwd.pwd.invalid");
		}
		if (!regexMatch && StringUtils.isNotEmpty(newPassword) && StringUtils.isNotEmpty(checkPassword)
				&& !StringUtils.equals(newPassword, checkPassword))
		{
			errors.rejectValue("checkNewPassword", "validation.checkPwd.equals");
		}
	}

	protected boolean validatePasswordPattern(final String pwd)
	{
		final String pwdPattern = this.asahiConfigurationService.getString(PWD + cmsSiteService.getCurrentSite().getUid(), "");
		final Pattern pattern = Pattern.compile(pwdPattern);
		final Matcher matcher = pattern.matcher(pwd);
		return matcher.matches();
	}

	/**
	 * @param model
	 * @param pageTitle
	 *           Method to update page Title
	 */
	protected void updatePageTitle(final Model model, final String pageTitle)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveContentPageTitle(pageTitle));
	}

	protected boolean isUserRemovalEnabled() {
		if(asahiSiteUtil.isBDECustomer()) {
			return this.asahiConfigurationService.getBoolean(ENABLE_REMOVE_USER + cmsSiteService.getCurrentSite().getUid(), false);
		} else {
			return false;
		}
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
