package com.apb.core.email.impl;

import de.hybris.platform.acceleratorservices.email.impl.DefaultEmailGenerationService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageTemplateModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.model.process.ApbCompanyDetailsProcessModel;
import de.hybris.platform.commerceservices.model.process.ApbContactUsEmailProcessModel;
import de.hybris.platform.commerceservices.model.process.ApbKegReturnEmailProcessModel;
import de.hybris.platform.commerceservices.model.process.ApbRequestRegistrationProcessModel;
import de.hybris.platform.commerceservices.model.process.AsahiCustomerNotifyProcessModel;
import de.hybris.platform.commerceservices.model.process.AsahiCustomerWelcomeEmailProcessModel;
import de.hybris.platform.commerceservices.model.process.AsahiPayerAccessProcessModel;
import de.hybris.platform.commerceservices.model.process.AsahiPaymentConfirmationProcessModel;
import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.commerceservices.model.process.SgaProfileUpdatedNoticeProcessModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontSuperCustomerProcessModel;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.customer.dao.AsahiCustomerAccountDao;
import com.apb.core.email.ApbEmailGenerationService;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.ApbEmailConfigurationUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.sabm.core.model.AsahiDealChangeEmailProcessModel;
import com.sabm.core.model.BusinessEnquiryEmailProcessModel;
import com.sabm.core.model.ConfirmEnabledDealProcessModel;
import com.sabmiller.commons.email.commons.EmailContextErrorException;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.notification.service.NotificationService;


/**
 * @author C5252631
 *
 *         ApbEmailGenerationServiceImpl implementation of {@link DefaultEmailGenerationService}
 *
 */
public class ApbEmailGenerationServiceImpl extends DefaultEmailGenerationService implements ApbEmailGenerationService
{
	private static final Logger LOG = LoggerFactory.getLogger(ApbEmailGenerationServiceImpl.class);

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	@Autowired
	private ModelService modelService;

	@Autowired
	private MediaService mediaService;

	@Autowired
	private CMSSiteService cmsSiteService;

	@Autowired
	private UserService userService;

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	@Autowired
	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2BCustomerService;

	/** The asahi customer account dao. */
	@Resource(name = "asahiCustomerAccountDao")
	private AsahiCustomerAccountDao asahiCustomerAccountDao;

	@Resource(name = "apbEmailConfigurationUtil")
	private ApbEmailConfigurationUtil apbEmailConfigurationUtil;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;


	@Resource(name = "notificationService")
	private NotificationService notificationService;

	public static final String SUPER_ADMIN_GROUP = "self.registration.super.admin.group.";

	private static final String SEND_EMAIL_TO_ADMIN_USER = "send.email.to.admin.user.";

	@SuppressWarnings("finally")
	public EmailMessageModel createSuperEmailMessage(String emailSubject, final String emailBody,
			final EmailAddressModel fromAdrdess, final BusinessProcessModel businessProcessModel, final String replyToAddress,
			final String emailPage)
	{
		String configFromAddress = "";
		String displayName = "";
		final String siteId = cmsSiteService.getCurrentSite().getUid();
		final List<EmailAttachmentModel> attachments = new ArrayList<EmailAttachmentModel>();
		final List<EmailAddressModel> toEmails = new ArrayList<EmailAddressModel>();
		final EmailAddressModel emailAddressModel = new EmailAddressModel();
		EmailAddressModel fromAddress = new EmailAddressModel();
		final List<EmailAddressModel> tocc = new ArrayList<>();

		/** Configure Super User */

		if (businessProcessModel instanceof StoreFrontSuperCustomerProcessModel)
		{
			final StoreFrontSuperCustomerProcessModel storeFrontSuperCustomerProcessModel = (StoreFrontSuperCustomerProcessModel) businessProcessModel;
			final CustomerModel customerModel = storeFrontSuperCustomerProcessModel.getCustomer();
			AsahiB2BUnitModel unit = null;
			if (customerModel instanceof B2BCustomerModel)
			{
				final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) customerModel;
				unit = asahiSiteUtil.isSga()?storeFrontSuperCustomerProcessModel.getUnit():(AsahiB2BUnitModel) b2bCustomerModel.getDefaultB2BUnit();
				emailAddressModel.setEmailAddress(unit.getEmailAddress());
				toEmails.add(emailAddressModel);
				/* send email to all admin users of default b2bunit of customer start */
				if (asahiSiteUtil.isSga() && asahiConfigurationService.getBoolean(SEND_EMAIL_TO_ADMIN_USER, true))
				{
					final List<EmailAddressModel> adminEmailList = getSuperAdminEmailList(siteId, unit);
					if (CollectionUtils.isNotEmpty(adminEmailList))
					{
						toEmails.addAll(adminEmailList);
					}
					else if (LOG.isDebugEnabled())
					{
						LOG.debug("No Admin User Exist for DefaultB2BUnit " + unit.getUid());
					}
				}
				/* send email to all admin users of default b2bunit of customer end */
			}
			else
			{
				LOG.warn("B2B Unit doesn't eamil Address, Email not sent!");
			}
			configFromAddress = asahiConfigurationService.getString(ApbCoreConstants.SUPER_REGISTRATION_FROM_EMAIL + siteId, "");
			displayName = asahiConfigurationService.getString(ApbCoreConstants.SUPER_REGISTRATION_FROM_EMAIL + siteId, "");
			emailAddressModel.setDisplayName(displayName+ThreadLocalRandom.current().nextDouble());
			fromAddress = getEmailService().getOrCreateEmailAddressForEmail(configFromAddress, displayName);
			fromAddress.setDisplayName(displayName);
			emailSubject = apbEmailConfigurationUtil.getSubject(storeFrontSuperCustomerProcessModel);
		}

		/** Configure Request Registration Page */

		else if (businessProcessModel instanceof ApbRequestRegistrationProcessModel)
		{
			final String sendReqRegEmail = asahiConfigurationService.getString(ApbCoreConstants.REQUEST_REGISTER_TO_EMAIL + siteId,
					"");
			final ApbRequestRegistrationProcessModel reqRegProcessModel = (ApbRequestRegistrationProcessModel) businessProcessModel;
			emailAddressModel.setEmailAddress(sendReqRegEmail);
			toEmails.add(emailAddressModel);

			final ApbRequestRegistrationProcessModel requestRegistrationProcessModel = (ApbRequestRegistrationProcessModel) businessProcessModel;
			emailSubject = apbEmailConfigurationUtil.getSubject(requestRegistrationProcessModel);
			requestRegisterMediaAttachment(attachments, reqRegProcessModel);
			configFromAddress = asahiConfigurationService.getString(ApbCoreConstants.REQUEST_REGISTER_FROM_EMAIL + siteId, "");
			displayName = asahiConfigurationService.getString(ApbCoreConstants.REQUEST_REGISTER_EMAIL_NAME + siteId, "");
			emailAddressModel.setDisplayName(displayName);
			fromAddress = getEmailService().getOrCreateEmailAddressForEmail(configFromAddress, displayName);
			fromAddress.setDisplayName(displayName);
		}

		/** Configure Company Detail Page */

		else if (businessProcessModel instanceof ApbCompanyDetailsProcessModel)
		{
			configFromAddress = asahiConfigurationService.getString(ApbCoreConstants.COMPANY_DETAILS_FROM_EMAIL + siteId, "");
			displayName = asahiConfigurationService.getString(ApbCoreConstants.COMPANY_DETAILS_EMAIL_NAME + siteId, "");

			final ApbCompanyDetailsProcessModel apbCompanyDetailsProcessModel = (ApbCompanyDetailsProcessModel) businessProcessModel;
			final String sendCompanyDetailsEmail = asahiConfigurationService.getString(ApbCoreConstants.COMPANY_DETAILS_TO_EMAIL
					+ siteId, "");
			emailAddressModel.setEmailAddress(sendCompanyDetailsEmail);
			toEmails.add(emailAddressModel);
			final EmailAddressModel emailAddressModelToCC = modelService.create(EmailAddressModel.class);
			emailAddressModelToCC.setEmailAddress(apbCompanyDetailsProcessModel.getCustomer().getContactEmail());
			emailAddressModelToCC.setDisplayName(apbCompanyDetailsProcessModel.getCustomer().getName());
			try
			{
				final List<EmailAddressModel> emailAddressModels2 = flexibleSearchService.getModelsByExample(emailAddressModelToCC);
				modelService.clearTransactionsSettings();
				modelService.removeAll(emailAddressModels2);
			}
			catch (final ModelNotFoundException mnf)
			{
				LOG.error("Email Address Model Not Found " + mnf.getMessage());
			}
			emailAddressModelToCC.setEmailAddress(apbCompanyDetailsProcessModel.getCustomer().getContactEmail());
			tocc.add(emailAddressModelToCC);
			emailAddressModelToCC.setDisplayName(apbCompanyDetailsProcessModel.getCustomer().getName());
			emailSubject = apbEmailConfigurationUtil.getSubject(apbCompanyDetailsProcessModel);
			emailAddressModel.setDisplayName(displayName);
			fromAddress = getEmailService().getOrCreateEmailAddressForEmail(configFromAddress, displayName);
			fromAddress.setDisplayName(displayName);
		}

		/** Configure Contact Us Page Email */
		else if (businessProcessModel instanceof ApbContactUsEmailProcessModel)
		{
			configFromAddress = asahiConfigurationService.getString(ApbCoreConstants.CONTACT_US_FROM_EMAIL + siteId, "");
			displayName = asahiConfigurationService.getString(ApbCoreConstants.CONTACT_US_EMAIL_NAME + siteId, "");

			final ApbContactUsEmailProcessModel apbContactUsEmailProcessModel = (ApbContactUsEmailProcessModel) businessProcessModel;
			String sendCompanyDetailsEmail= "";
			if(asahiSiteUtil.isSga() && asahiConfigurationService.getBoolean("sga.contactus.update.available", false))
			{
				if(StringUtils.isNotEmpty(apbContactUsEmailProcessModel.getContactUsQueryEmail().getEnquirySubType()))
				{
					sendCompanyDetailsEmail = asahiConfigurationService.getString(
							ApbCoreConstants.CONTACT_US_TO_EMAIL + siteId + "." + apbContactUsEmailProcessModel.getContactUsQueryEmail().getEnquiryType() + "." + apbContactUsEmailProcessModel.getContactUsQueryEmail().getEnquirySubType(), "");
				}
				else
				{
					sendCompanyDetailsEmail = asahiConfigurationService.getString(
							ApbCoreConstants.CONTACT_US_TO_EMAIL + siteId + "." + apbContactUsEmailProcessModel.getContactUsQueryEmail().getEnquiryType(), "");
				}
			}
			else {
					sendCompanyDetailsEmail = asahiConfigurationService.getString(
						ApbCoreConstants.CONTACT_US_TO_EMAIL + siteId, "");
			}
			emailAddressModel.setEmailAddress(sendCompanyDetailsEmail);
			toEmails.add(emailAddressModel);
			contactUsMediaAttachment(attachments, apbContactUsEmailProcessModel);
			final EmailAddressModel emadm = new EmailAddressModel();
			emadm.setEmailAddress(apbContactUsEmailProcessModel.getContactUsQueryEmail().getEmailAddress());
			emadm.setDisplayName(apbContactUsEmailProcessModel.getContactUsQueryEmail().getName());
			try
			{
				final List<EmailAddressModel> emailAddressModels2 = flexibleSearchService.getModelsByExample(emadm);
				modelService.clearTransactionsSettings();
				modelService.removeAll(emailAddressModels2);
			}
			catch (final ModelNotFoundException mnf)
			{
				LOG.error("Email Address Model Not Found " + mnf.getMessage());
			}
			emadm.setEmailAddress(apbContactUsEmailProcessModel.getContactUsQueryEmail().getEmailAddress());


			//Do not send ContactUs Notification if user entered in the ContactUsForm has opted-out
			boolean sendCCEmail = true;
			if (asahiSiteUtil.isApb()) {
				sendCCEmail = true;
			} else {
				if (null != apbContactUsEmailProcessModel.getCustomer() && apbContactUsEmailProcessModel.getCustomer().getUid().equals("anonymous")) {
					sendCCEmail = true;
				} else {
					UserModel userModel = apbContactUsEmailProcessModel.getCustomer();
					if (null != userModel && userModel instanceof B2BCustomerModel) {
   					final B2BUnitModel currentB2BUnit = ((B2BCustomerModel)userModel).getDefaultB2BUnit();
   					if (!userModel.getUid().equalsIgnoreCase(emadm.getEmailAddress())){
   						try {
   							userModel = userService.getUserForUID(emadm.getEmailAddress());
   							sendCCEmail = notificationService.getEmailPreferenceForNotificationType(NotificationType.CONTACT_US, (B2BCustomerModel)userModel, currentB2BUnit);
   						} catch (final Exception ex) {
   							sendCCEmail = true;
   						}
   					} else {
   						sendCCEmail = notificationService.getEmailPreferenceForNotificationType(NotificationType.CONTACT_US, (B2BCustomerModel)userModel, currentB2BUnit);
   					}
					}
				}
			}

			if (sendCCEmail) {
				tocc.add(emadm);
			}

			emadm.setDisplayName(apbContactUsEmailProcessModel.getContactUsQueryEmail().getName());
			emailSubject = apbEmailConfigurationUtil.getSubject(apbContactUsEmailProcessModel);
			emailAddressModel.setDisplayName(displayName);
			fromAddress = getEmailService().getOrCreateEmailAddressForEmail(configFromAddress, displayName);
			fromAddress.setDisplayName(displayName);
		}

		/** Configure Keg Return Or Checkout Page Email */
		else if (businessProcessModel instanceof ApbKegReturnEmailProcessModel)
		{
			configFromAddress = asahiConfigurationService.getString(ApbCoreConstants.KEG_RETURN_FROM_EMAIL + siteId, "");
			displayName = asahiConfigurationService.getString(ApbCoreConstants.KEG_RETURN_EMAIL_NAME + siteId, "");

			final ApbKegReturnEmailProcessModel kegReturnEmailProcessModel = (ApbKegReturnEmailProcessModel) businessProcessModel;
			final String sendKegDetailsEmail = asahiConfigurationService
					.getString(ApbCoreConstants.KEG_RETURN_TO_EMAIL + siteId, "");
			emailAddressModel.setEmailAddress(kegReturnEmailProcessModel.getApbKegReturnEmail().getEmailAddress());
			emailAddressModel.setDisplayName(displayName);
			toEmails.add(emailAddressModel);
			final EmailAddressModel emadm = new EmailAddressModel();
			emadm.setEmailAddress(kegReturnEmailProcessModel.getApbKegReturnEmail().getEmailAddress());
			emadm.setDisplayName(displayName);
			try
			{
				final List<EmailAddressModel> emailAddressModels2 = flexibleSearchService.getModelsByExample(emadm);
				modelService.clearTransactionsSettings();
				modelService.removeAll(emailAddressModels2);
			}
			catch (final ModelNotFoundException mnf)
			{
				LOG.error("Email Address Model Not Found " + mnf.getMessage());
			}
			emailSubject = apbEmailConfigurationUtil.getSubject(kegReturnEmailProcessModel);
			emadm.setEmailAddress(sendKegDetailsEmail);
			tocc.add(emadm);

			emailAddressModel.setDisplayName(kegReturnEmailProcessModel.getApbKegReturnEmail().getContactName());
			fromAddress = getEmailService().getOrCreateEmailAddressForEmail(configFromAddress, displayName);
			fromAddress.setDisplayName(displayName);
			try
			{
				final EmailAddressModel emailAddressModelTocc = asahiCustomerAccountDao.getEmailAddressModel(tocc.get(0)
						.getDisplayName(), tocc.get(0).getEmailAddress());
				if (emailAddressModelTocc != null)
				{
					modelService.remove(emailAddressModelTocc);
				}
			}
			catch (final ModelNotFoundException mnf)
			{
				LOG.error("Email Address Model Not Found ", mnf);
			}

		}
		else if (businessProcessModel instanceof OrderProcessModel
				&& BooleanUtils.isTrue(((OrderProcessModel) businessProcessModel).getOrder().getBdeOrder()))
		{
			final OrderProcessModel orderProcessModel = (OrderProcessModel) businessProcessModel;
			if (CollectionUtils.isNotEmpty(((OrderProcessModel) businessProcessModel).getToEmails()))
			{
				for (final String toEmail : ((OrderProcessModel) businessProcessModel).getToEmails())
				{
					final EmailAddressModel toEmailAddresse = new EmailAddressModel();
					toEmailAddresse.setEmailAddress(toEmail);
					toEmailAddresse.setDisplayName(getDisplayNameForBDEFromEmialAddress(toEmail));
					toEmails.add(toEmailAddresse);
					emailAddressModel.setEmailAddress(toEmail);
				}

				configFromAddress = asahiConfigurationService.getString(ApbCoreConstants.ORDER_CONFIRMATION_FROM_EMAIL + siteId, "");
				displayName = asahiConfigurationService.getString(ApbCoreConstants.ORDER_CONFIRMATION_EMAIL_NAME + siteId, "");
				emailAddressModel.setDisplayName(displayName);
				fromAddress = getEmailService().getOrCreateEmailAddressForEmail(configFromAddress, displayName);
				fromAddress.setDisplayName(displayName);
				emailSubject = apbEmailConfigurationUtil.getSubject(orderProcessModel);
			}
		}
		// configure the parameters for order confirmation email
		else if (businessProcessModel instanceof OrderProcessModel)
		{
			final OrderProcessModel orderProcessModel = (OrderProcessModel) businessProcessModel;
			final OrderModel orderModel = orderProcessModel.getOrder();

			if (null != orderModel && null != orderModel.getUser())
			{
				emailAddressModel.setEmailAddress(orderModel.getUser().getUid());
				toEmails.add(emailAddressModel);
			}
			else
			{
				LOG.warn("Order Not Found for the customer Order confirmation email not sent.");
			}

			configFromAddress = asahiConfigurationService.getString(ApbCoreConstants.ORDER_CONFIRMATION_FROM_EMAIL + siteId, "");
			displayName = asahiConfigurationService.getString(ApbCoreConstants.ORDER_CONFIRMATION_EMAIL_NAME + siteId, "");
			emailAddressModel.setDisplayName(displayName);
			fromAddress = getEmailService().getOrCreateEmailAddressForEmail(configFromAddress, displayName);
			fromAddress.setDisplayName(displayName);
			emailSubject = apbEmailConfigurationUtil.getSubject(orderProcessModel);
		}

		/** Configure Customer Notify Email */
		else if (businessProcessModel instanceof AsahiCustomerNotifyProcessModel)
		{
			final AsahiCustomerNotifyProcessModel asahiCustomerNotifyProcessModel = (AsahiCustomerNotifyProcessModel) businessProcessModel;

			final String notifyType = getNotifyTypeFromEmailPage(emailPage).toLowerCase();

			configFromAddress = asahiConfigurationService.getString(ApbCoreConstants.NOTIFY_FROM_EMAIL + notifyType, "");

			displayName = asahiConfigurationService.getString(ApbCoreConstants.NOTIFY_EMAIL_NAME + notifyType, "");

			emailSubject = asahiConfigurationService.getString(ApbCoreConstants.NOTIFY_EMAIL_SUBJECT + notifyType, "subject");

			emailAddressModel.setEmailAddress(asahiCustomerNotifyProcessModel.getCustomer().getUid());
			toEmails.add(emailAddressModel);

			emailAddressModel.setDisplayName(displayName);
			fromAddress = getEmailService().getOrCreateEmailAddressForEmail(configFromAddress, displayName);
			fromAddress.setDisplayName(displayName);
		}
		/** Configure Payment Confirmation Email */
		else if (businessProcessModel instanceof AsahiPaymentConfirmationProcessModel)
		{
			final AsahiPaymentConfirmationProcessModel asahiPaymentConfirmationProcessModel = (AsahiPaymentConfirmationProcessModel) businessProcessModel;

			configFromAddress = asahiConfigurationService.getString(ApbCoreConstants.PAYMENT_CONFIRMATION_FROM_EMAIL, "");

			displayName = asahiConfigurationService.getString(ApbCoreConstants.PAYMENT_CONFIRMATION_EMAIL_NAME, "");

			emailSubject = asahiConfigurationService.getString(ApbCoreConstants.PAYMENT_CONFIRMATION_EMAIL_SUBJECT, "");

			emailAddressModel.setEmailAddress(asahiPaymentConfirmationProcessModel.getCustomer().getUid());
			toEmails.add(emailAddressModel);

			emailAddressModel.setDisplayName(displayName);
			fromAddress = getEmailService().getOrCreateEmailAddressForEmail(configFromAddress, displayName);
			fromAddress.setDisplayName(displayName);
		}
		/** Configure Forget Password Page Email */
		if (businessProcessModel instanceof ForgottenPasswordProcessModel)
		{
			final ForgottenPasswordProcessModel forgottenPasswordProcessModel = (ForgottenPasswordProcessModel) businessProcessModel;
			configFromAddress = asahiConfigurationService.getString(ApbCoreConstants.FORGOT_PASSWORD_FROM_EMAIL + siteId, "");
			displayName = asahiConfigurationService.getString(ApbCoreConstants.FORGOT_PASSWORD_EMAIL_NAME + siteId, "");

			if (asahiSiteUtil.isSga()
					&& null != forgottenPasswordProcessModel.getProcessDefinitionName()
					&& forgottenPasswordProcessModel.getProcessDefinitionName().equalsIgnoreCase(
							"assistedForgottenPasswordEmailProcess"))
			{
				emailSubject = asahiConfigurationService.getString(ApbCoreConstants.ASSISTED_CUSTOMER_REGISTRATION_EMAIL_SUBJECT
						+ siteId, "");

			}
			else if (asahiSiteUtil.isSga()
					&& null != forgottenPasswordProcessModel.getProcessDefinitionName()
					&& forgottenPasswordProcessModel.getProcessDefinitionName().equalsIgnoreCase(
							"asahiPasswordResetEmailProcess"))
			{
				emailSubject = asahiConfigurationService.getString(ApbCoreConstants.STAFF_FLOW_CUSTOMER_REGISTRATION_EMAIL_SUBJECT
						+ siteId, "");

			}
			else
			{
				emailSubject = apbEmailConfigurationUtil.getSubject(forgottenPasswordProcessModel);
			}
			emailAddressModel.setEmailAddress(forgottenPasswordProcessModel.getCustomer().getContactEmail());
			toEmails.add(emailAddressModel);

			emailAddressModel.setDisplayName(displayName);
			fromAddress = getEmailService().getOrCreateEmailAddressForEmail(configFromAddress, displayName);
			fromAddress.setDisplayName(displayName);
		}
		/** Configure Resend Welcome Email */
		if (businessProcessModel instanceof AsahiCustomerWelcomeEmailProcessModel)
		{
			final AsahiCustomerWelcomeEmailProcessModel customerWelcomeEmailProcess = (AsahiCustomerWelcomeEmailProcessModel) businessProcessModel;
			configFromAddress = asahiConfigurationService.getString(ApbCoreConstants.CUSTOMER_WELCOME_FROM_EMAIL + siteId, "");
			displayName = asahiConfigurationService.getString(ApbCoreConstants.CUSTOMER_WELCOME_EMAIL_NAME + siteId, "");
			emailSubject = apbEmailConfigurationUtil.getSubject(customerWelcomeEmailProcess);
			emailAddressModel.setEmailAddress(customerWelcomeEmailProcess.getCustomer().getContactEmail());
			toEmails.add(emailAddressModel);
			emailAddressModel.setDisplayName(displayName);
			fromAddress = getEmailService().getOrCreateEmailAddressForEmail(configFromAddress, displayName);
			fromAddress.setDisplayName(displayName);
		}

		/** Email for customer profile removal for sga */
		if (businessProcessModel instanceof SgaProfileUpdatedNoticeProcessModel)
		{
			final SgaProfileUpdatedNoticeProcessModel sgaProfileUpdatedNoticeProcessModel = (SgaProfileUpdatedNoticeProcessModel) businessProcessModel;
			configFromAddress = asahiConfigurationService.getString(ApbCoreConstants.CUSTOMER_PROFILE_UPDATE_FROM_EMAIL + siteId,
					"");
			displayName = asahiConfigurationService.getString(ApbCoreConstants.CUSTOMER_PROFILE_UPDATE_EMAIL_NAME + siteId, "");
			emailSubject = apbEmailConfigurationUtil.getSubject(sgaProfileUpdatedNoticeProcessModel);
			emailAddressModel.setEmailAddress(sgaProfileUpdatedNoticeProcessModel.getCustomer().getContactEmail());
			toEmails.add(emailAddressModel);
			emailAddressModel.setDisplayName(displayName);
			fromAddress = getEmailService().getOrCreateEmailAddressForEmail(configFromAddress, displayName);
			fromAddress.setDisplayName(displayName);
		}
		
		/** Email for customer profile removal for sga */
		if (businessProcessModel instanceof AsahiDealChangeEmailProcessModel)
		{
			final AsahiDealChangeEmailProcessModel asahiDealChangeEmailProcessModel = (AsahiDealChangeEmailProcessModel) businessProcessModel;
			if (CollectionUtils.isNotEmpty(asahiDealChangeEmailProcessModel.getToEmails()))
			{
				for (final String toEmail : (asahiDealChangeEmailProcessModel.getToEmails()))
				{
					final EmailAddressModel toEmailAddresse = new EmailAddressModel();
					toEmailAddresse.setEmailAddress(toEmail);
					toEmailAddresse.setDisplayName(getDisplayNameForBDEFromEmialAddress(toEmail));
					toEmails.add(toEmailAddresse);
					emailAddressModel.setEmailAddress(toEmail);
				}
				configFromAddress = asahiConfigurationService.getString(ApbCoreConstants.ASAHI_DEAL_UPDATE_FROM_EMAIL + asahiDealChangeEmailProcessModel.getSite().getUid(),
						"");
				displayName = asahiConfigurationService.getString(ApbCoreConstants.ASAHI_DEAL_UPDATE_EMAIL_NAME + asahiDealChangeEmailProcessModel.getSite().getUid(), "");
				emailSubject = apbEmailConfigurationUtil.getSubject(asahiDealChangeEmailProcessModel);
				emailAddressModel.setDisplayName(displayName);
				fromAddress = getEmailService().getOrCreateEmailAddressForEmail(configFromAddress, displayName);
				fromAddress.setDisplayName(displayName);
			}
		}

		removeEmailAddressModel(businessProcessModel, emailAddressModel);
		/* Remove the Email Models from list of to emails start */
		if (asahiSiteUtil.isSga())
		{

			for (final EmailAddressModel email : toEmails)
			{
				removeEmailAddressModel(businessProcessModel, email);
			}
		}
		/* Remove the Email Models from list of to emails end */


		return getEmailService().createEmailMessage(toEmails, tocc, new ArrayList<EmailAddressModel>(), fromAddress,
				replyToAddress, emailSubject, emailBody, attachments);


	}

	private String getNotifyTypeFromEmailPage(final String emailPage)
	{
		String notifyType = StringUtils.EMPTY;
		if (emailPage.equalsIgnoreCase(ApbCoreConstants.NO_DELIVERY_TEMPLATE))
		{
			notifyType = asahiConfigurationService.getString(ApbCoreConstants.NO_DELIVERY, "NODEL");
		}
		else if (emailPage.equalsIgnoreCase(ApbCoreConstants.ALT_CALLDAY_DELIVERY_TEMPLATE))
		{
			notifyType = asahiConfigurationService.getString(ApbCoreConstants.ALT_CALLDAY_DELIVERY, "ALTCALL");
		}
		else if (emailPage.equalsIgnoreCase(ApbCoreConstants.ALT_DELDATE_DELIVERY_TEMPLATE))
		{
			notifyType = asahiConfigurationService.getString(ApbCoreConstants.ALT_DELDATE_DELIVERY, "ALTDEL");
		}

		return notifyType;
	}

	/**
	 * @param siteId
	 * @param b2bCustomerModel
	 * @return List<EmailAddressModel> Get the list of admin users email address for a customer related to
	 *         defaultB2Bunit.
	 */
	private List<EmailAddressModel> getSuperAdminEmailList(final String siteId, final AsahiB2BUnitModel b2bUnitModel)
	{
		final Set<PrincipalModel> memberList = b2bUnitModel.getMembers();
		final List<EmailAddressModel> adminEmailList = new ArrayList<EmailAddressModel>();

		if (CollectionUtils.isNotEmpty(memberList))
		{
			for (final PrincipalModel member : memberList)
			{
				if (member instanceof B2BCustomerModel)
				{
					final Set<PrincipalGroupModel> groupList = member.getAllGroups();
					for (final PrincipalGroupModel group : groupList)
					{
						if (group.getUid().equalsIgnoreCase(
								asahiConfigurationService.getString(SUPER_ADMIN_GROUP + siteId, "sgaSuperAdmin")))
						{
							final EmailAddressModel emailAddress = new EmailAddressModel();
							emailAddress.setEmailAddress(member.getUid());
							emailAddress.setDisplayName(member.getUid()+ThreadLocalRandom.current().nextDouble());
							adminEmailList.add(emailAddress);
							break;
						}
					}
				}
			}
		}
		return adminEmailList;
	}

	/**
	 * @param businessProcessModel
	 * @param emailAddressModel
	 *           Get and Remove the already existing emailAddressModel if any before saving.
	 */
	private void removeEmailAddressModel(final BusinessProcessModel businessProcessModel, final EmailAddressModel emailAddressModel)
	{
		try
		{
			final List<EmailAddressModel> emailAddressModels = flexibleSearchService.getModelsByExample(emailAddressModel);
			modelService.removeAll(emailAddressModels);
		}


		catch (final ModelNotFoundException mnf)
		{
			LOG.error("Email Address Model Not Found " + mnf.getMessage());
		}
		finally
		{
			LOG.warn("Email Processing for businessProcess [" + businessProcessModel + "]");
		}
	}

	/**
	 * file attaching and sending in email in request registration page
	 *
	 * @param attachments
	 * @param reqRegProcessModel
	 */
	private void requestRegisterMediaAttachment(final List<EmailAttachmentModel> attachments,
			final ApbRequestRegistrationProcessModel reqRegProcessModel)
	{
		final MediaModel mediaModel = reqRegProcessModel.getRequestRegisterEmail().getUploadFile();
		if (mediaModel != null)
		{
			EmailAttachmentModel emailAttachmentModel = new EmailAttachmentModel();
			emailAttachmentModel = getEmailService().createEmailAttachment(mediaService.getDataStreamFromMedia(mediaModel),
					mediaModel.getRealFileName(), mediaModel.getMime());
			attachments.add(emailAttachmentModel);
		}
	}


	/**
	 * file attaching and sending in email for contact us page
	 *
	 * @param attachments
	 * @param apbContactUsEmailProcessModel
	 */
	private void contactUsMediaAttachment(final List<EmailAttachmentModel> attachments,
			final ApbContactUsEmailProcessModel apbContactUsEmailProcessModel)
	{
		final MediaModel mediaModel = apbContactUsEmailProcessModel.getContactUsQueryEmail().getUploadFile();
		if (mediaModel != null)
		{
			EmailAttachmentModel emailAttachmentModel = new EmailAttachmentModel();
			emailAttachmentModel = getEmailService().createEmailAttachment(mediaService.getDataStreamFromMedia(mediaModel),
					mediaModel.getRealFileName(), mediaModel.getMime());
			attachments.add(emailAttachmentModel);
		}
	}


	@Override
	public EmailMessageModel generate(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel)
	{
		if(!asahiSiteUtil.isCub())
		{
		ServicesUtil.validateParameterNotNull(emailPageModel, "EmailPageModel cannot be null");
		Assert.isInstanceOf(EmailPageTemplateModel.class, emailPageModel.getMasterTemplate(),
				"MasterTemplate associated with EmailPageModel should be EmailPageTemplate");

		final EmailPageTemplateModel emailPageTemplateModel = (EmailPageTemplateModel) emailPageModel.getMasterTemplate();
		final RendererTemplateModel bodyRenderTemplate = emailPageTemplateModel.getHtmlTemplate();
		Assert.notNull(bodyRenderTemplate, "HtmlTemplate associated with MasterTemplate of EmailPageModel cannot be null");
		final RendererTemplateModel subjectRenderTemplate = emailPageTemplateModel.getSubject();
		Assert.notNull(subjectRenderTemplate, "Subject associated with MasterTemplate of EmailPageModel cannot be null");

		EmailMessageModel emailMessageModel = new EmailMessageModel();
		//This call creates the context to be used for rendering of subject and body templates.
		final AbstractEmailContext<BusinessProcessModel> emailContext = getEmailContextFactory().create(businessProcessModel,
				emailPageModel, bodyRenderTemplate);

		if (emailContext == null)
		{
			LOG.error("Failed to create email context for businessProcess [" + businessProcessModel + "]");
			throw new IllegalStateException("Failed to create email context for businessProcess [" + businessProcessModel + "]");
		}
		else
		{
			if (!validate(emailContext))
			{
				LOG.error("Email context for businessProcess [" + businessProcessModel + "] is not valid: "
						+ ReflectionToStringBuilder.toString(emailContext));
				throw new IllegalStateException("Email context for businessProcess [" + businessProcessModel + "] is not valid: "
						+ ReflectionToStringBuilder.toString(emailContext));
			}

			if (asahiSiteUtil.isSga() && businessProcessModel instanceof StoreFrontSuperCustomerProcessModel) {
				final StoreFrontSuperCustomerProcessModel storeFrontSuperCustomerProcessModel = (StoreFrontSuperCustomerProcessModel) businessProcessModel;
				emailContext.put("unitName", storeFrontSuperCustomerProcessModel.getUnit().getLocName());
			}
			final StringWriter subject = new StringWriter();
			getRendererService().render(subjectRenderTemplate, emailContext, subject);
			final StringWriter body = new StringWriter();
			getRendererService().render(bodyRenderTemplate, emailContext, body);

			System.out.println("emailbody*******" +body.toString());
			/* Custom Email Process Model */
			if (businessProcessModel instanceof StoreFrontSuperCustomerProcessModel
					|| businessProcessModel instanceof ApbRequestRegistrationProcessModel
					|| businessProcessModel instanceof ApbCompanyDetailsProcessModel
					|| businessProcessModel instanceof ApbContactUsEmailProcessModel
					|| businessProcessModel instanceof ApbKegReturnEmailProcessModel
					|| businessProcessModel instanceof ForgottenPasswordProcessModel
					|| businessProcessModel instanceof OrderProcessModel
					|| businessProcessModel instanceof SgaProfileUpdatedNoticeProcessModel
					|| businessProcessModel instanceof AsahiDealChangeEmailProcessModel)
			{
				emailMessageModel = createSuperEmailMessage(emailMessageModel.getSubject(), body.toString(),
						emailMessageModel.getFromAddress(), businessProcessModel, emailMessageModel.getReplyToAddress(), null);
			}
			else if (businessProcessModel instanceof AsahiCustomerNotifyProcessModel
					|| businessProcessModel instanceof AsahiPaymentConfirmationProcessModel)
			{
				emailMessageModel = createSuperEmailMessage(emailMessageModel.getSubject(), body.toString(),
						emailMessageModel.getFromAddress(), businessProcessModel, emailMessageModel.getReplyToAddress(),
						emailPageModel.getUid());
			}
			else if (businessProcessModel instanceof AsahiPayerAccessProcessModel)
			{
				final String configEmailSubject = asahiConfigurationService.getString(ApbCoreConstants.PAYER_ACCESS_EMAIL_SUBJECT
						+ cmsSiteService.getCurrentSite().getUid(), "");
				emailMessageModel = createEmailMessage(subject.toString(), body.toString(), emailContext);
			}
			else
			{
				/* OOTB */
				/* Configure Registration Page */
				final String configEmailSubject = asahiConfigurationService.getString(
						ApbCoreConstants.SELF_REGISTRATION__EMAIL_SUBJECT + cmsSiteService.getCurrentSite().getUid(), "");
				emailMessageModel = createEmailMessage(configEmailSubject, body.toString(), emailContext);
			}
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Email Subject: " + emailMessageModel.getSubject());
				LOG.debug("Email Body: " + emailMessageModel.getBody());
			}
		}
		return emailMessageModel;
		}

		else
		{

			ServicesUtil.validateParameterNotNull(emailPageModel, "EmailPageModel cannot be null");
			Assert.isInstanceOf(EmailPageTemplateModel.class, emailPageModel.getMasterTemplate(),
					"MasterTemplate associated with EmailPageModel should be EmailPageTemplate");

			final EmailPageTemplateModel emailPageTemplateModel = (EmailPageTemplateModel) emailPageModel.getMasterTemplate();
			final RendererTemplateModel bodyRenderTemplate = emailPageTemplateModel.getHtmlTemplate();
			Assert.notNull(bodyRenderTemplate, "HtmlTemplate associated with MasterTemplate of EmailPageModel cannot be null");
			final RendererTemplateModel subjectRenderTemplate = emailPageTemplateModel.getSubject();
			Assert.notNull(subjectRenderTemplate, "Subject associated with MasterTemplate of EmailPageModel cannot be null");

			final EmailMessageModel emailMessageModel;
			//This call creates the context to be used for rendering of subject and body templates.
			final AbstractEmailContext<BusinessProcessModel> emailContext = getEmailContextFactory().create(businessProcessModel,
					emailPageModel, bodyRenderTemplate);

			if (emailContext == null)
			{
				LOG.error("Failed to create email context for businessProcess [{}]", businessProcessModel);
				throw new EmailContextErrorException(
						"Failed to create email context for businessProcess [" + businessProcessModel + "]");
			}
			else
			{
				if (!validate(emailContext))
				{
					LOG.error("Email context for businessProcess [{}] is not valid: {}", businessProcessModel,
							ReflectionToStringBuilder.toString(emailContext));
					throw new EmailContextErrorException("Email context for businessProcess [" + businessProcessModel + "] is not valid: "
							+ ReflectionToStringBuilder.toString(emailContext));
				}

				emailContext.put("staticHostPath", Config.getString("statics.host.path", ""));

				final StringWriter subject = new StringWriter();
				getRendererService().render(subjectRenderTemplate, emailContext, subject);

				final StringWriter body = new StringWriter();
				getRendererService().render(bodyRenderTemplate, emailContext, body);

				emailMessageModel = createEmailMessage(subject.toString(), body.toString(), emailContext, businessProcessModel);

				LOG.debug("Email Subject: {}", emailMessageModel.getSubject());
				LOG.debug("Email Body: {}", emailMessageModel.getBody());
			}

			return emailMessageModel;

		}

	}

	protected EmailMessageModel createEmailMessage(final String emailSubject, final String emailBody,
			final AbstractEmailContext<BusinessProcessModel> emailContext, final BusinessProcessModel businessProcessModel)
	{
		final EmailAddressModel fromAddress = getEmailService().getOrCreateEmailAddressForEmail(emailContext.getFromEmail(),
				emailContext.getFromDisplayName());

		if (businessProcessModel instanceof ConfirmEnabledDealProcessModel)
		{
			// to send emails
			final List<EmailAddressModel> toAddresses = new ArrayList<EmailAddressModel>();
			if (CollectionUtils.isNotEmpty(((ConfirmEnabledDealProcessModel) businessProcessModel).getToEmails()))
			{
				for (final String toEmail : ((ConfirmEnabledDealProcessModel) businessProcessModel).getToEmails())
				{
					final EmailAddressModel toEmails = getEmailService().getOrCreateEmailAddressForEmail(toEmail,
							getDisplayNameFromEmialAddress(toEmail));
					toAddresses.add(toEmails);
				}

			}
			//cc send emails
			final List<EmailAddressModel> ccAddresses = new ArrayList<EmailAddressModel>();
			if (CollectionUtils.isNotEmpty(((ConfirmEnabledDealProcessModel) businessProcessModel).getCcEmails()))
			{
				for (final String ccEmail : ((ConfirmEnabledDealProcessModel) businessProcessModel).getCcEmails())
				{
					final EmailAddressModel ccEmails = getEmailService().getOrCreateEmailAddressForEmail(ccEmail,
							getDisplayNameFromEmialAddress(ccEmail));
					ccAddresses.add(ccEmails);
				}
			}
			return getEmailService().createEmailMessage(toAddresses, ccAddresses, new ArrayList<EmailAddressModel>(), fromAddress,
					emailContext.getFromEmail(), emailSubject, emailBody, null);
		}
		else if (businessProcessModel instanceof BusinessEnquiryEmailProcessModel)
		{
			// to send emails
			final List<EmailAddressModel> toAddresses = new ArrayList<EmailAddressModel>();
			if (CollectionUtils.isNotEmpty(((BusinessEnquiryEmailProcessModel) businessProcessModel).getToEmails()))
			{
				for (final String toEmail : ((BusinessEnquiryEmailProcessModel) businessProcessModel).getToEmails())
				{
					final EmailAddressModel toEmails = getEmailService().getOrCreateEmailAddressForEmail(toEmail, toEmail);
					toAddresses.add(toEmails);
				}

			}
			//cc send emails
			final List<EmailAddressModel> ccAddresses = new ArrayList<EmailAddressModel>();
			if (CollectionUtils.isNotEmpty(((BusinessEnquiryEmailProcessModel) businessProcessModel).getCcEmails()))
			{
				for (final String ccEmail : ((BusinessEnquiryEmailProcessModel) businessProcessModel).getCcEmails())
				{
					final EmailAddressModel ccEmails = getEmailService().getOrCreateEmailAddressForEmail(ccEmail, ccEmail);
					ccAddresses.add(ccEmails);
				}
			}
			return getEmailService().createEmailMessage(toAddresses, ccAddresses, new ArrayList<EmailAddressModel>(), fromAddress,
					emailContext.getFromEmail(), emailSubject, emailBody, null);
		}
		else if (businessProcessModel instanceof OrderProcessModel
				&& BooleanUtils.isTrue(((OrderProcessModel) businessProcessModel).getOrder().getBdeOrder()))
		{
			// to send emails
			final List<EmailAddressModel> toAddresses = new ArrayList<EmailAddressModel>();
			if (CollectionUtils.isNotEmpty(((OrderProcessModel) businessProcessModel).getToEmails()))
			{
				for (final String toEmail : ((OrderProcessModel) businessProcessModel).getToEmails())
				{
					final EmailAddressModel toEmails = getEmailService().getOrCreateEmailAddressForEmail(toEmail,
							getDisplayNameFromEmialAddress(toEmail));
					toAddresses.add(toEmails);
				}

			}

			return getEmailService().createEmailMessage(toAddresses, null, new ArrayList<EmailAddressModel>(), fromAddress,
					emailContext.getFromEmail(), emailSubject, emailBody, null);
		}

		final List<EmailAddressModel> toEmails = new ArrayList<EmailAddressModel>();
		final EmailAddressModel toAddress = getEmailService().getOrCreateEmailAddressForEmail(emailContext.getToEmail(),
				emailContext.getToDisplayName());

		toEmails.add(toAddress);
		return getEmailService().createEmailMessage(toEmails, new ArrayList<EmailAddressModel>(),
				new ArrayList<EmailAddressModel>(), fromAddress, emailContext.getFromEmail(), emailSubject, emailBody, null);
	}

	protected String getDisplayNameFromEmialAddress(final String email)
	{
		final Object b2bcustomer = b2BCustomerService.getUserForUID(email);
		if (b2bcustomer instanceof B2BCustomerModel)
		{
			return ((B2BCustomerModel) b2bcustomer).getDisplayName();
		}
		return "";
	}
	
	protected String getDisplayNameForBDEFromEmialAddress(final String email)
	{
		try {
			final UserModel userModel = userService.getUserForUID(email);
			return userModel.getName() + ThreadLocalRandom.current().nextDouble();
		}
		catch(final Exception exp) {
			LOG.error("user not found" + email);
		}
		return ""+ThreadLocalRandom.current().nextDouble();
	}

	@Override
	protected boolean validate(final AbstractEmailContext<BusinessProcessModel> emailContext)
	{
		if (asahiSiteUtil.isCub())
		{
		boolean valid = true;
		if (StringUtils.isBlank(emailContext.getFromEmail()))
		{
			LOG.error("Missing FromEmail in AbstractEmailContext");
			valid = false;
		}
		return valid;
		}
		else
		{
			return super.validate(emailContext);
		}
	}

	/**
	 * Call createEmailMessage method only for Registration
	 */
	@Override
	protected EmailMessageModel createEmailMessage(final String emailSubject, final String emailBody,
			final AbstractEmailContext<BusinessProcessModel> emailContext)
	{
		if(!asahiSiteUtil.isCub())
		{
		final List<EmailAddressModel> toEmails = new ArrayList<EmailAddressModel>();
		final EmailAddressModel toAddress = getEmailService().getOrCreateEmailAddressForEmail(emailContext.getToEmail(),
				emailContext.getToDisplayName());
		toEmails.add(toAddress);
		final EmailAddressModel fromAddress = getEmailService().getOrCreateEmailAddressForEmail(emailContext.getFromEmail(),
				emailContext.getFromDisplayName());
		return getEmailService().createEmailMessage(toEmails, new ArrayList<EmailAddressModel>(),
				new ArrayList<EmailAddressModel>(), fromAddress, emailContext.getFromEmail(), emailSubject, emailBody, null);
		}
		return super.createEmailMessage(emailSubject, emailBody, emailContext);
	}
}
