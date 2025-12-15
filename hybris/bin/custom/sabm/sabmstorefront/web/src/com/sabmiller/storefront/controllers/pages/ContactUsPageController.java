/**
 *
 */
package com.sabmiller.storefront.controllers.pages;

import com.sabmiller.commons.constants.SabmcommonsConstants;
import com.sabmiller.facades.util.SabmFeatureUtil;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sabm.core.model.cms.components.LiveChatComponentModel;
import com.sabmiller.facades.b2bunit.DefaultSabmB2BUnitFacade;
import com.sabmiller.facades.businessenquiry.SabmBusinessEnquiryFacade;
import com.sabmiller.facades.businessenquiry.data.SabmContactUsData;
import com.sabmiller.storefront.controllers.ControllerConstants;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.storefront.form.ContactUsForm;
import com.sabmiller.storefront.controllers.pages.SabmAbstractPageController;

/**
 * Controller Handlers for the following pages: 1. contact us page
 *
 * @author wei.yang.ng@accenture.com
 */
@Controller
@Scope("tenant")
public class ContactUsPageController extends SabmAbstractPageController
{
	private static final Logger LOG = LoggerFactory.getLogger(ContactUsPageController.class.getName());

    private static final String CONTACT_US_PAGE = "sabmSupportPage";
	private static final String CONTACT_US_SENT_PAGE_REDIRECT = "redirect:/serviceRequestEmailSent";
	private static final String CONTACT_US_PREFERRED_CONTACT_PHONE = "Phone";

	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder homeBreadcrumbBuilder;

	@Resource(name = "b2bCommerceUnitFacade")
	private DefaultSabmB2BUnitFacade sabmB2BUnitFacade;

	@Resource(name="cmsComponentService")
	DefaultCMSComponentService cmsComponentService;
	
	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Resource
	private SabmBusinessEnquiryFacade sabmBusinessEnquiryFacade;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "sabmFeatureUtil")
	private SabmFeatureUtil sabmFeatureUtil;

	/**
	 * Contoller handler to set up the Contact Us page before it is rendered.
	 *
	 * @param model
	 * @return
	 */
	@RequireHardLogIn
	@GetMapping("/serviceRequest")
	public String contactUsPage(final Model model) throws CMSItemNotFoundException
	{
		//Add customer data to model
		final CustomerData customer = getCustomerFacade().getCurrentCustomer();
		model.addAttribute("customer", customer);

		storeCmsPageInModel(model, getContentPageForLabelOrId(CONTACT_US_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CONTACT_US_PAGE));
		model.addAttribute("breadcrumbs", homeBreadcrumbBuilder.getBreadcrumbs("text.contactus.title"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

		model.addAttribute("isInvoiceDiscrepancyEnabled", sabmFeatureUtil.isFeatureEnabled(SabmcommonsConstants.INVOICEDISCREPANY));

		return getViewForPage(model);
	}

	@RequireHardLogIn
	@PostMapping("/serviceRequest")
	public String contactUsPage(@Valid final ContactUsForm contactUsForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes reDirect) throws CMSItemNotFoundException
	{
		//Check the facade layer for all b2bunits the current user belongs to.
		final String customerUid = getCustomerFacade().getCurrentCustomerUid();
		//final List<B2BUnitData> b2bUnits = sabmB2BUnitFacade.getB2BUnitsByCustomer(customerUid);
		//SABMC- 1889
		final List<de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData> b2bUnits = sabmB2BUnitFacade.getBranchesForCustomer(customerUid);
		model.addAttribute("b2bUnits", b2bUnits);

		storeCmsPageInModel(model, getContentPageForLabelOrId(CONTACT_US_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CONTACT_US_PAGE));
		model.addAttribute("breadcrumbs", homeBreadcrumbBuilder.getBreadcrumbs("text.contactus.title"));

		if (CONTACT_US_PREFERRED_CONTACT_PHONE.equals(contactUsForm.getPreferred_contact())
				&& StringUtils.isBlank(contactUsForm.getPhoneNumber()))
		{
			bindingResult.addError(new ObjectError("contactUsForm", "Phone number required."));
		}

		if (bindingResult.hasErrors())
		{
			//Add customer data to model
			final CustomerData customer = getCustomerFacade().getCurrentCustomer();
			model.addAttribute("customer", customer);
			model.addAttribute("hasErrors", true);
			return getViewForPage(model);
		}

		final SabmContactUsData contactUsData = new SabmContactUsData();
		contactUsData.setName(contactUsForm.getName());
		contactUsData.setBusinessUnit(contactUsForm.getBusiness_unit());
		contactUsData.setPreferredContactMethod(contactUsForm.getPreferred_contact());
		contactUsData.setEmailAddress(getCustomerFacade().getCurrentCustomer().getEmail());
		contactUsData.setInquiryMessage(contactUsForm.getServiceMessage());
		contactUsData.setRequestType(contactUsForm.getRequest_type());

		if (StringUtils.isBlank(contactUsForm.getPhoneNumber()))
		{
			contactUsData.setPhoneNumber(StringUtils.EMPTY);
		}
		else
		{
			contactUsData.setPhoneNumber(contactUsForm.getPhoneNumber());
		}


		sabmBusinessEnquiryFacade.sendBusinessEnquiryEmail(contactUsData);

		reDirect.addFlashAttribute("pageType", getPageType());

		return CONTACT_US_SENT_PAGE_REDIRECT;
	}


	@RequestMapping(value = "/livechatfeedback", method = { RequestMethod.GET, RequestMethod.POST })
	public String feedback(final Model model) { 

	return ControllerConstants.Views.Pages.Account.LiveChatFeedBackPage;
	
	}

	protected String getPageType()
	{
		return SABMWebConstants.PageType.CONTACT_US.name();
	}



}
