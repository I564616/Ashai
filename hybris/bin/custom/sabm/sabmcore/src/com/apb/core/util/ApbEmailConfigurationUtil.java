package com.apb.core.util;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.model.process.ApbCompanyDetailsProcessModel;
import de.hybris.platform.commerceservices.model.process.ApbContactUsEmailProcessModel;
import de.hybris.platform.commerceservices.model.process.ApbKegReturnEmailProcessModel;
import de.hybris.platform.commerceservices.model.process.ApbRequestRegistrationProcessModel;

import java.text.MessageFormat;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.model.ApbCompanyDetailsEmailModel;
import com.apb.core.model.ApbKegReturnEmailModel;
import com.apb.core.model.ApbRequestRegisterEmailModel;
import com.apb.core.model.ContactUsQueryEmailModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.sabm.core.model.AsahiDealChangeEmailProcessModel;

/**
 * ApbEmailConfigurationUtil Implementation of get subject for email
 */
public class ApbEmailConfigurationUtil
{

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Autowired
	private CMSSiteService cmsSiteService;

	/**
	 * @param model
	 * @return subject
	 */
	public String getSubject(final Object model)
	{
		String subject = StringUtils.EMPTY;
		String configSubject = StringUtils.EMPTY;
		final String siteId = cmsSiteService.getCurrentSite().getUid();
		switch (model.getClass().getSimpleName())
		{
			/** GET SUBJECT FOR COMPANY */
			case "ApbCompanyDetailsEmailModel":
				configSubject = asahiConfigurationService.getString(ApbCoreConstants.COMPANY_DETAILS_EMAIL_SUBJECT + siteId, "");
				subject = MessageFormat.format(configSubject, ((ApbCompanyDetailsEmailModel) model).getReferenceNumber());
				break;
			case "ApbCompanyDetailsProcessModel":
				configSubject = asahiConfigurationService.getString(ApbCoreConstants.COMPANY_DETAILS_EMAIL_SUBJECT + siteId, "");
				subject = MessageFormat.format(configSubject,
						((ApbCompanyDetailsProcessModel) model).getApbCompanyDetailsEmail().getReferenceNumber());
				break;

			/** GET SUBJECT FOR CONTACT US */
			case "ContactUsQueryEmailModel":
				configSubject = asahiConfigurationService.getString(ApbCoreConstants.CONTACT_US_EMAIL_SUBJECT + siteId, "");
				subject = MessageFormat.format(configSubject, ((ContactUsQueryEmailModel) model).getReferenceNumber());
				break;
			case "ApbContactUsEmailProcessModel":
				configSubject = asahiConfigurationService.getString(ApbCoreConstants.CONTACT_US_EMAIL_SUBJECT + siteId, "");
				subject = MessageFormat.format(configSubject,
						((ApbContactUsEmailProcessModel) model).getContactUsQueryEmail().getReferenceNumber());
				break;

			/** GET SUBJECT FOR KEG RETURN */
			case "ApbKegReturnEmailModel":
				configSubject = asahiConfigurationService.getString(ApbCoreConstants.KEG_RETURN_EMAIL_SUBJECT + siteId, "");
				subject = MessageFormat.format(configSubject, ((ApbKegReturnEmailModel) model).getReferenceNumber());
				break;
			case "ApbKegReturnEmailProcessModel":
				configSubject = asahiConfigurationService.getString(ApbCoreConstants.KEG_RETURN_EMAIL_SUBJECT + siteId, "");
				subject = MessageFormat.format(configSubject,
						((ApbKegReturnEmailProcessModel) model).getApbKegReturnEmail().getReferenceNumber());
				break;

			/** GET SUBJECT FOR REQUEST REGISTRATION */
			case "ApbRequestRegisterEmailModel":
				configSubject = asahiConfigurationService.getString(ApbCoreConstants.REQUEST_REGISTER_EMAIL_SUBJECT + siteId, "");
				subject = MessageFormat.format(configSubject, ((ApbRequestRegisterEmailModel) model).getReferenceNumber());
				break;
			case "ApbRequestRegistrationProcessModel":
				configSubject = asahiConfigurationService.getString(ApbCoreConstants.REQUEST_REGISTER_EMAIL_SUBJECT + siteId, "");
				subject = MessageFormat.format(configSubject,
						((ApbRequestRegistrationProcessModel) model).getRequestRegisterEmail().getReferenceNumber());
				break;

			/** GET SUBJECT FOR SUPER USER */
			case "StoreFrontSuperCustomerProcessModel":
				subject = asahiConfigurationService.getString(ApbCoreConstants.SUPER_REGISTRATION__EMAIL_SUBJECT + siteId, "");
				break;

			/** GET SUBJECT FOR FORGET PASSWORD */
			case "ForgottenPasswordProcessModel":
				subject = asahiConfigurationService.getString(ApbCoreConstants.FORGOT_PASSWORD_EMAIL_SUBJECT + siteId, "");
				break;

				/** GET SUBJECT FOR ORDER CONFIRMATION */
			case "OrderProcessModel":
				subject = asahiConfigurationService.getString(ApbCoreConstants.ORDER_CONFIRMATION__EMAIL_SUBJECT + siteId, "");
				break;
			case "AsahiCustomerWelcomeEmailProcessModel":
				subject = asahiConfigurationService.getString(ApbCoreConstants.CUSTOMER_WELCOME_EMAIL_SUBJECT + siteId, "");
				break;
			case "SgaProfileUpdatedNoticeProcessModel":
				subject = asahiConfigurationService.getString(ApbCoreConstants.CUSTOMER_PROFILE_UPDATE_EMAIL_SUBJECT + siteId, "");
				break;
			case "AsahiDealChangeEmailProcessModel":
				AsahiDealChangeEmailProcessModel asahiDealChangeEmailProcessModel = (AsahiDealChangeEmailProcessModel) model;
				subject = asahiConfigurationService.getString(ApbCoreConstants.ASAHI_DEAL_UPDATE_EMAIL_SUBJECT + asahiDealChangeEmailProcessModel.getSite().getUid(), "");
				break;
		}

		return subject;
	}
}
