/**
 *
 */
package com.sabmiller.commons.email.service.impl;

import de.hybris.platform.acceleratorservices.email.impl.DefaultEmailGenerationService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageTemplateModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.Config;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.sabm.core.model.BusinessEnquiryEmailProcessModel;
import com.sabm.core.model.ConfirmEnabledDealProcessModel;
import com.sabmiller.commons.email.commons.EmailContextErrorException;


/**
 * Override the default function of create Email Message
 *
 *
 */
public class SabmEmailGenerationService extends DefaultEmailGenerationService
{

	private static final Logger LOG = LoggerFactory.getLogger(SabmEmailGenerationService.class);

	@Resource(name = "b2bCustomerService")
	private B2BCustomerService b2bCustomerService;

	@Override
	public EmailMessageModel generate(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel)
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
		final Object b2bcustomer = b2bCustomerService.getUserForUID(email);
		if (b2bcustomer instanceof B2BCustomerModel)
		{
			return ((B2BCustomerModel) b2bcustomer).getDisplayName();
		}
		return "";
	}

	@Override
	protected boolean validate(final AbstractEmailContext<BusinessProcessModel> emailContext)
	{
		boolean valid = true;

		if (StringUtils.isBlank(emailContext.getFromEmail()))
		{
			LOG.error("Missing FromEmail in AbstractEmailContext");
			valid = false;
		}
		return valid;
	}
}
