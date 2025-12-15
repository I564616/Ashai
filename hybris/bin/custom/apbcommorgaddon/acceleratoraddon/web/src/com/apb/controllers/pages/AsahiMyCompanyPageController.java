/**
 *
 */
package com.apb.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceorgaddon.controllers.pages.MyCompanyPageController;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.impl.UniqueAttributesInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.breadcrumb.impl.APBMyCompanyBreadcrumbBuilder;
import com.apb.controllers.ApbcommorgaddonControllerConstants;
import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.exception.AsahiBusinessException;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.b2bunit.ApbB2BUnitFacade;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.apb.facades.product.data.AsahiRoleData;
import com.apb.facades.user.ApbUserFacade;
import com.apb.forms.B2BCustomerForm;
import com.apb.service.b2bunit.ApbB2BUnitService;



/**
 * @author Ashish.Monga
 *
 */
public class AsahiMyCompanyPageController extends MyCompanyPageController
{
	private static final Logger LOG = LoggerFactory.getLogger(AsahiMyCompanyPageController.class);
	
	@Resource(name = "asahiConfigurationService")
	protected AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "apbUserFacade")
	private ApbUserFacade apbUserFacade;
	
	@Resource(name = "asahiSiteUtil")
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;
	
	@Resource
	private ApbB2BUnitService apbB2BUnitService;
	
	@Resource(name = "b2bCustomerFacade")
	private SABMCustomerFacade sabmCustomerFacade;
	
	@Resource
	private ApbB2BUnitFacade apbB2BUnitFacade;
	
	@Resource(name = "apbMyCompanyBreadcrumbBuilder")
	protected APBMyCompanyBreadcrumbBuilder apbMyCompanyBreadcrumbBuilder;
	
	private static final String DEFAULTROLECODE = "OTHER";
	private static final String DEFAULTPARENTUNIT = "";
	
	private static final String MANAGE_USER_CREATE_URL = "/my-company/organization-management/manage-users/create";
	private static final String MANAGE_USER_URL = "/my-company/organization-management/manage-users/";
	protected static final String REDIRECT_TO_MANAGE_USER = REDIRECT_PREFIX + MANAGE_USER_URL;
	private static final String REDIRECT_TO_USER_CREATE = REDIRECT_PREFIX + MANAGE_USER_CREATE_URL;

	/**
	 * @return parentB2Bunit
	 */
	@ModelAttribute("parentB2Bunit")
	public String getParentB2Bunit()
	{
		if (CollectionUtils.isNotEmpty(b2bUnitFacade.getAllActiveUnitsOfOrganization()))
		{
			return b2bUnitFacade.getAllActiveUnitsOfOrganization().get(0);
		}
		else
		{
			return DEFAULTPARENTUNIT;
		}
	}

	@Override
	protected String createUser(final Model model) throws CMSItemNotFoundException
	{
		if (!model.containsAttribute("b2BCustomerForm"))
		{
			final B2BCustomerForm b2bCustomerForm = new B2BCustomerForm();
			b2bCustomerForm.setParentB2BUnit(b2bUnitFacade.getParentUnit().getUid());
			b2bCustomerForm.setSamAccess(ApbCoreConstants.ORDER_ACCESS);

			// Add the b2bcustomergroup role by default
			b2bCustomerForm.setRole("b2bcustomergroup");

			//Setting the Other Asahi Role by default acp-152
			b2bCustomerForm.setAsahiCustomRoles(Collections.singletonList(DEFAULTROLECODE));

			model.addAttribute(b2bCustomerForm);
		}
		
		model.addAttribute("approvalEmailId",null != asahiCoreUtil.getDefaultB2BUnit() 
				&& null != asahiCoreUtil.getDefaultB2BUnit().getPayerAccount()?
				asahiCoreUtil.getDefaultB2BUnit().getPayerAccount().getEmailAddress(): StringUtils.EMPTY);
		
		model.addAttribute("titleData", getUserFacade().getTitles());
		model.addAttribute("roles", populateRolesChkBoxes(b2bUserGroupFacade.getUserGroups()));
		model.addAttribute("asahiCustomRoles", populateAsahiRolesChkBoxes(apbUserFacade.getAsahiRole()));

		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUserBreadcrumb();
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-users/create", getMessageSource()
				.getMessage("text.company.organizationManagement.createuser", null, getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return ApbcommorgaddonControllerConstants.Views.Pages.MyCompany.MyCompanyManageUserAddEditFormPage;
	}

	protected String createUser(final B2BCustomerForm b2BCustomerForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "form.global.error");
			model.addAttribute(b2BCustomerForm);
			return createUser(model);
		}

		try {
			if(asahiSiteUtil.isSga()) {
				sabmCustomerFacade.blockRegBasedOnCustomerType(b2BCustomerForm.getSamAccess(), asahiCoreUtil.getDefaultB2BUnit());
			}
		}catch(AsahiBusinessException ex) {
			LOG.error("Exception occurred while creating user profile " + ex.getMessage());
			GlobalMessages.addErrorMessage(model,ex.getMessage());
			model.addAttribute(b2BCustomerForm);
			return createUser(model);
		}
		
		final CustomerData b2bCustomerData = new CustomerData();
		if (StringUtils.isNotBlank(b2BCustomerForm.getTitleCode())) {
			b2bCustomerData.setTitleCode(b2BCustomerForm.getTitleCode());
		}
		b2bCustomerData.setFirstName(b2BCustomerForm.getFirstName());
		b2bCustomerData.setLastName(b2BCustomerForm.getLastName());
		b2bCustomerData.setEmail(b2BCustomerForm.getEmail());
		b2bCustomerData.setDisplayUid(b2BCustomerForm.getEmail());
		b2bCustomerData.setUnit(b2bUnitFacade.getUnitForUid(b2BCustomerForm.getParentB2BUnit()));
		b2bCustomerData.setSamAccess(b2BCustomerForm.getSamAccess());
		b2bCustomerData.setContactNumber(b2BCustomerForm.getMobileNumber());
		model.addAttribute(b2BCustomerForm);
		model.addAttribute("titleData", getUserFacade().getTitles());

		/* Check for unlimited access and also add standard access if unlimited is selected acp-152 */
		//checkForUnlimitedAccess(b2BCustomerForm);

		//b2bCustomerData.setRoles(b2BCustomerForm.getRoles() != null ? b2BCustomerForm.getRoles() : Collections.<String> emptyList());

        if(StringUtils.isNotEmpty(b2BCustomerForm.getRole())){
            if(b2BCustomerForm.getRole().equalsIgnoreCase(B2BConstants.B2BADMINGROUP))
                b2bCustomerData.setIsAdminUser(true);
            else
                b2bCustomerData.setIsAdminUser(false);
        }

		/* set the custom asahi user role acp-152 */
		updateAsahiRole(b2BCustomerForm, b2bCustomerData);
		model.addAttribute("roles", populateRolesChkBoxes(b2bUserGroupFacade.getUserGroups()));

		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/",
				getMessageSource().getMessage("text.company.organizationManagement", null, getI18nService().getCurrentLocale()),
				null));
		breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-user",
				getMessageSource().getMessage("text.company.manageUsers", null, getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);

		try
		{
			b2bUserFacade.updateCustomer(b2bCustomerData);
			b2bCustomerData.setUid(b2BCustomerForm.getEmail().toLowerCase());
			b2BCustomerForm.setUid(b2bCustomerData.getUid());
			if(asahiSiteUtil.isApb()){
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "text.confirmation.user.added",
						new Object[]
								{ asahiSiteUtil.getCurrentSite().getUid() });
			}
			if(asahiSiteUtil.isSga()){
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "text.confirmation.user.added2",
						new Object[]
								{ b2bCustomerData.getFirstName() + " " + b2bCustomerData.getLastName() });
				
			}
		}
		catch (final ModelSavingException e) //NOSONAR
		{
			if (e.getCause() instanceof InterceptorException
					&& ((InterceptorException) e.getCause()).getInterceptor().getClass().equals(UniqueAttributesInterceptor.class))
			{
				LOG.error("The uid of the model being stored already exists, could not save. Edit user instead.");
				bindingResult.rejectValue("email", "text.manageuser.error.email.exists.title");
				GlobalMessages.addErrorMessage(model, "form.global.error");
				model.addAttribute("b2BCustomerForm", b2BCustomerForm);
				return createUser(model);
			}
			else
			{
				LOG.error("Failed to create user.");
			}
		}
		if (asahiSiteUtil.isSga()) {
			return String.format(REDIRECT_TO_MANAGE_USER);
		} else {
			return String.format(REDIRECT_TO_USER_CREATE);
		}
	}

	/* set the custom asahi user role acp-152 */
	private void updateAsahiRole(final B2BCustomerForm b2BCustomerForm, final CustomerData b2bCustomerData)
	{
		final AsahiRoleData asahiRoleData = new AsahiRoleData();
		if (CollectionUtils.isNotEmpty(b2BCustomerForm.getAsahiCustomRoles()))
		{

			final ArrayList<String> asahiRoleDetails = new ArrayList<>(b2BCustomerForm.getAsahiCustomRoles());
			asahiRoleData.setCode(asahiRoleDetails.get(0).toString());
			asahiRoleData.setName(asahiRoleDetails.get(0).toString().toLowerCase());
			b2bCustomerData.setAsahiRole(asahiRoleData);
		}
	}

	@Override
	public String editUser(final String user, final Model model) throws CMSItemNotFoundException
	{
		List<Breadcrumb> breadcrumbs = apbMyCompanyBreadcrumbBuilder.createManageUserDetailsBreadcrumb(user);
		final AsahiB2BUnitModel currentUnit = apbB2BUnitService.getCurrentB2BUnit();
		if (!model.containsAttribute("b2BCustomerForm"))
		{
			final CustomerData customerData = b2bUserFacade.getCustomerForUid(user);
			final B2BCustomerForm b2bCustomerForm = new B2BCustomerForm();
			b2bCustomerForm.setUid(customerData.getUid());
			b2bCustomerForm.setTitleCode(customerData.getTitleCode());
			b2bCustomerForm.setFirstName(customerData.getFirstName());
			b2bCustomerForm.setLastName(customerData.getLastName());
			b2bCustomerForm.setEmail(customerData.getDisplayUid());
			b2bCustomerForm.setParentB2BUnit(b2bUserFacade.getParentUnitForCustomer(customerData.getUid()).getUid());
			b2bCustomerForm.setActive(customerData.isActive());
			b2bCustomerForm.setApproverGroups(customerData.getApproverGroups());
			b2bCustomerForm.setApprovers(customerData.getApprovers());
			b2bCustomerForm.setMobileNumber(customerData.getMobileNumber());

			if(customerData.getIsAdminUser()) {
				b2bCustomerForm.setRole(B2BConstants.B2BADMINGROUP);
			}
			else
			{
				b2bCustomerForm.setRole(B2BConstants.B2BCUSTOMERGROUP);
			}

			b2bCustomerForm.setAsahiRole(customerData.getAsahiRole());

			/* Show only b2badmingroup acp-152 end */

			if (StringUtils.isEmpty(customerData.getAsahiRole().getCode()))
			{
				b2bCustomerForm.setAsahiCustomRoles(Collections.singletonList(DEFAULTROLECODE));
			}
			else
			{
				/* set the default asahi role as Other */
				b2bCustomerForm.setAsahiCustomRoles(new ArrayList<>(Arrays.asList(customerData.getAsahiRole().getCode())));
			}
			model.addAttribute(b2bCustomerForm);
		}
		
		if(asahiSiteUtil.isSga()) {
			final String  samAccess =(null !=currentUnit.getPayerAccount() && StringUtils.isNotEmpty(user)? 
					apbB2BUnitFacade.getSamAccessType(user, currentUnit.getPayerAccount().getUid()) : ApbCoreConstants.ORDER_ACCESS);
			final Boolean accessDenied =(null !=currentUnit.getPayerAccount() && StringUtils.isNotEmpty(user)? 
					apbB2BUnitFacade.isSamAccessDenied(user, currentUnit.getPayerAccount().getUid()) : Boolean.FALSE);
			final Boolean pendingApproval = (null !=currentUnit.getPayerAccount() && StringUtils.isNotEmpty(user)? 
					apbB2BUnitFacade.isSamAccessApprovalPending(user, currentUnit.getPayerAccount().getUid()) : Boolean.FALSE);
			
			String selectedRadio = StringUtils.EMPTY;
			if(accessDenied) {
				if(samAccess.equalsIgnoreCase(ApbCoreConstants.PAY_AND_ORDER_ACCESS) || samAccess.equalsIgnoreCase(ApbCoreConstants.ORDER_ACCESS)) {
					selectedRadio = ApbCoreConstants.ORDER_ACCESS;
				}else {
					selectedRadio = "none";
				}
			}else if(samAccess.equalsIgnoreCase(ApbCoreConstants.ORDER_ACCESS)) {
				selectedRadio = ApbCoreConstants.ORDER_ACCESS;
			} else if(samAccess.equalsIgnoreCase(ApbCoreConstants.PAY_ACCESS)) {
				selectedRadio =  ApbCoreConstants.PAY_ACCESS;
				if(pendingApproval) {
					model.addAttribute("showPayPendingMessage",Boolean.TRUE);
				}
			} else if(samAccess.equalsIgnoreCase(ApbCoreConstants.PAY_AND_ORDER_ACCESS)) {
				selectedRadio = ApbCoreConstants.PAY_AND_ORDER_ACCESS;
				if(pendingApproval) {
					model.addAttribute("showOrderPayPendingMessage",Boolean.TRUE);
				}
			}
			
			model.addAttribute("selectedRadio", selectedRadio);
			
			model.addAttribute("approvalEmailId",null != asahiCoreUtil.getDefaultB2BUnit() 
					&& null != asahiCoreUtil.getDefaultB2BUnit().getPayerAccount()?
					asahiCoreUtil.getDefaultB2BUnit().getPayerAccount().getEmailAddress(): StringUtils.EMPTY);
		}

		model.addAttribute("titleData", getUserFacade().getTitles());
		model.addAttribute("roles", populateRolesChkBoxes(b2bUserGroupFacade.getUserGroups()));
		model.addAttribute("asahiCustomRoles", populateAsahiRolesChkBoxes(apbUserFacade.getAsahiRole()));

		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));

		if (model.containsAttribute("newUserEdit"))
		{
			breadcrumbs = myCompanyBreadcrumbBuilder.createManageUserBreadcrumb();
			breadcrumbs.add(new Breadcrumb("/my-company/organization-management/manage-users/create", getMessageSource()
					.getMessage("text.company.organizationManagement.createuser", null, getI18nService().getCurrentLocale()), null));
		}
		else
		{
			breadcrumbs.add(
					new Breadcrumb(String.format("/my-company/organization-management/manage-users/edit?user=%s", urlEncode(user)),
							getMessageSource().getMessage("text.company.manageusers.edit", new Object[]
							{ user }, "Edit {0} User", getI18nService().getCurrentLocale()), null));
		}
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return ApbcommorgaddonControllerConstants.Views.Pages.MyCompany.MyCompanyManageUserAddEditFormPage;
	}

	protected String editUser(final String user, final B2BCustomerForm b2BCustomerForm, final BindingResult bindingResult,
			final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "form.global.error");
			model.addAttribute(b2BCustomerForm);
			return editUser(b2BCustomerForm.getUid(), model);
		}

		// A B2B Admin should not be able to downgrade their roles, they must at lest belong to B2B Administrator role
		if (customerFacade.getCurrentCustomer().getUid().equals(b2BCustomerForm.getUid()))
		{
			final String role = b2BCustomerForm.getRole();
			if (StringUtils.isNotBlank(role) && !role.equalsIgnoreCase(B2BConstants.B2BADMINGROUP))
			{
				GlobalMessages.addErrorMessage(model, "form.b2bcustomer.adminrole.error");
				b2BCustomerForm.setRole(B2BConstants.B2BADMINGROUP);
				model.addAttribute(b2BCustomerForm);
				return editUser(b2BCustomerForm.getUid(), model);
			}
			else
			{
				// A session user can't modify their own parent unit.
				final B2BUnitData parentUnit = b2bUnitFacade.getParentUnit();
				if (!parentUnit.getUid().equals(b2BCustomerForm.getParentB2BUnit()))
				{
					GlobalMessages.addErrorMessage(model, "form.b2bcustomer.parentunit.error");
					b2BCustomerForm.setParentB2BUnit(parentUnit.getUid());
					model.addAttribute(b2BCustomerForm);
					return editUser(b2BCustomerForm.getUid(), model);
				}
			}
		}

		try {
			if(asahiSiteUtil.isSga() && StringUtils.isNotBlank(b2BCustomerForm.getSamAccess())) {
				sabmCustomerFacade.blockRegBasedOnCustomerType(b2BCustomerForm.getSamAccess(), asahiCoreUtil.getDefaultB2BUnit());
			}
		}catch(AsahiBusinessException ex) {
			LOG.error("Exception occurred while creating user profile " + ex.getMessage());
			GlobalMessages.addErrorMessage(model,ex.getMessage());
			model.addAttribute(b2BCustomerForm);
			model.addAttribute("newUserEdit", "YES");
			return editUser(b2BCustomerForm.getUid(), model);
		}
		
		final CustomerData b2bCustomerData = new CustomerData();
		b2bCustomerData.setUid(b2BCustomerForm.getUid());
		if (StringUtils.isNotBlank(b2BCustomerForm.getTitleCode())) {
			b2bCustomerData.setTitleCode(b2BCustomerForm.getTitleCode());
		}
		b2bCustomerData.setFirstName(b2BCustomerForm.getFirstName());
		b2bCustomerData.setLastName(b2BCustomerForm.getLastName());
		b2bCustomerData.setContactNumber(b2BCustomerForm.getMobileNumber());
		b2bCustomerData.setEmail(b2BCustomerForm.getEmail());
		b2bCustomerData.setDisplayUid(b2BCustomerForm.getEmail());
		b2bCustomerData.setUnit(b2bUnitFacade.getUnitForUid(b2BCustomerForm.getParentB2BUnit()));
		//checkForUnlimitedAccess(b2BCustomerForm);
		//b2bCustomerData.setRoles(b2BCustomerForm.getRoles() != null ? b2BCustomerForm.getRoles() : Collections.<String> emptyList());
		if (StringUtils.isNotEmpty(b2BCustomerForm.getRole())) {
			b2bCustomerData.setIsAdminUser(b2BCustomerForm.getRole().equalsIgnoreCase(B2BConstants.B2BADMINGROUP));
		} else {
			b2bCustomerData.setIsAdminUser(Boolean.FALSE);
		}
		
        b2bCustomerData.setSamAccess(b2BCustomerForm.getSamAccess());

		/* set the custom asahi user role acp-152 */
		updateAsahiRole(b2BCustomerForm, b2bCustomerData);


		model.addAttribute(b2BCustomerForm);
		model.addAttribute("titleData", getUserFacade().getTitles());
		model.addAttribute("roles", populateRolesChkBoxes(b2bUserGroupFacade.getUserGroups()));

		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUserDetailsBreadcrumb(user);
		model.addAttribute("breadcrumbs", breadcrumbs);

		try
		{
			b2bUserFacade.updateCustomer(b2bCustomerData);
			b2bCustomerData.setUid(b2BCustomerForm.getEmail().toLowerCase());
			if (asahiSiteUtil.isApb()) {
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "text.confirmation.user.edited");
			} else {
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "text.confirmation.alb.user.edited");
			}
		}
		catch (final ModelSavingException e) //NOSONAR
		{
			LOG.error("Failed to edit user." + e);
			GlobalMessages.addErrorMessage(model, "form.global.error");
			model.addAttribute("b2BCustomerForm", b2BCustomerForm);
			return editUser(b2BCustomerForm.getUid(), model);

		}
		return String.format(REDIRECT_TO_USER_DETAILS, urlEncode(b2bCustomerData.getUid()));
	}

/*	*//* Check for unlimited access and also add standard access if unlimited is selected acp-152 *//*
	private void checkForUnlimitedAccess(final B2BCustomerForm b2bCustomerForm)
	{

		final ArrayList<String> roles = new ArrayList<>(b2bCustomerForm.getRoles());
		if (CollectionUtils.isNotEmpty(roles) && roles.contains(B2BConstants.B2BADMINGROUP))
		{

			roles.add(B2BConstants.B2BCUSTOMERGROUP);
			b2bCustomerForm.setRoles(roles);

		}

	}*/

	@Override
	protected String manageUserDetail(final String user, final Model model) throws CMSItemNotFoundException
	{
		final CustomerData customerData = b2bUserFacade.getCustomerForUid(user);
		final AsahiB2BUnitModel currentUnit = apbB2BUnitService.getCurrentB2BUnit();
		final Collection<String> disabledUser = currentUnit.getDisabledUser();
		if(asahiSiteUtil.isSga()) {
			customerData.setSamAccess(null !=currentUnit.getPayerAccount() ? 
					apbB2BUnitFacade.getSamAccessType(user, currentUnit.getPayerAccount().getUid()) : ApbCoreConstants.ORDER_ACCESS);
			customerData.setAccessDenied(null !=currentUnit.getPayerAccount() ? 
					apbB2BUnitFacade.isSamAccessDenied(user, currentUnit.getPayerAccount().getUid()) : Boolean.FALSE);
			customerData.setPendingApproval(null !=currentUnit.getPayerAccount() ? 
					apbB2BUnitFacade.isSamAccessApprovalPending(user, currentUnit.getPayerAccount().getUid()) : Boolean.FALSE);
		}
		
		if(CollectionUtils.isNotEmpty(disabledUser) && disabledUser.contains(user)) {
			customerData.setActive(Boolean.FALSE);
		}
		
		model.addAttribute("customerData", customerData);
		model.addAttribute("accountId", currentUnit.getUid());
		model.addAttribute("accountName", currentUnit.getLocName());
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORGANIZATION_MANAGEMENT_CMS_PAGE));
		final List<Breadcrumb> breadcrumbs = myCompanyBreadcrumbBuilder.createManageUserDetailsBreadcrumb(user);
		model.addAttribute("breadcrumbs", breadcrumbs);

		if (!customerData.getUnit().isActive())
		{
			GlobalMessages.addInfoMessage(model, "text.parentunit.disabled.warning");
		}
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return ApbcommorgaddonControllerConstants.Views.Pages.MyCompany.MyCompanyManageUserDetailPage;
	}

	/**
	 * Data class used to hold a drop down select option value. Holds the code identifier as well as the display name.
	 */
	public static class SelectOption
	{
		private final String code;
		private final String name;

		public SelectOption(final String code, final String name)
		{
			this.code = code;
			this.name = name;
		}

		public String getCode()
		{
			return code;
		}

		public String getName()
		{
			return name;
		}
	}

	protected List<SelectOption> populateRolesChkBoxes(final List<String> roles)
	{
		final List<SelectOption> selectBoxList = new ArrayList<SelectOption>();

		for (final String data : roles)
		{
			if (data.equals("b2bcustomergroup") || data.equals("b2badmingroup"))
			{
				selectBoxList.add(new SelectOption(data, getMessageSource().getMessage(String.format("b2busergroup.%s.name", data),
						null, getI18nService().getCurrentLocale())));
			}
		}

		return selectBoxList;
	}

	/* populate custom asahi roles acp-152 */
	protected List<SelectOption> populateAsahiRolesChkBoxes(final List<AsahiRoleData> roles)
	{
		final List<String> asahiRoles = new ArrayList<>();

		final List<SelectOption> selectBoxList = new ArrayList<SelectOption>();

		for (final AsahiRoleData data : roles)
		{
			asahiRoles.add(data.getCode());
		}


		for (final String data : asahiRoles)
		{

			selectBoxList.add(new SelectOption(data, getMessageSource().getMessage(String.format("b2busergroup.%s.name", data), null,
					getI18nService().getCurrentLocale())));

		}

		return selectBoxList;
	}


	@Override
	protected int getSearchPageSize()
	{
		return Integer.parseInt(this.asahiConfigurationService.getString("apbcommorgaddon.manageUser.pageSize", "10"));
	}


}
