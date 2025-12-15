/**
 *
 */
package com.sabmiller.commons.email.service.impl;

import de.hybris.platform.acceleratorservices.email.impl.DefaultEmailService;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.core.model.media.MediaModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail2.jakarta.HtmlEmail;
import org.apache.commons.mail2.core.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.commons.email.dao.SystemEmailDao;
import com.sabmiller.commons.email.service.SystemEmailService;
import com.sabmiller.commons.model.SystemEmailMessageModel;


/**
 * The service of system email
 *
 */
public class SystemEmailServiceImpl extends DefaultEmailService implements SystemEmailService
{
	public static final String EMAIL_BODY_END_OF_LINE = "<br>";
	public static final String EMAIL_BODY_END_OF_LINE_KEY = "email.end.of.line";
	public static final String EMAIL_LIMIT_EXCEED_MSG = "email.limit.exceed.message";

	private SystemEmailDao systemEmailDao;

	private static final Logger LOG = LoggerFactory.getLogger(SystemEmailServiceImpl.class);

	/**
	 * Creates a SystemEmailMessage model instead of the usual Email Message.
	 *
	 * @param toAddresses
	 * @param ccAddresses
	 * @param bccAddresses
	 * @param fromAddress
	 * @param replyToAddress
	 * @param subject
	 * @param body
	 * @param attachments
	 * @return SystemEmailMessageModel
	 */
	@Override
	public SystemEmailMessageModel createEmailMessage(final List<EmailAddressModel> toAddresses,
			final List<EmailAddressModel> ccAddresses, final List<EmailAddressModel> bccAddresses,
			final EmailAddressModel fromAddress, final String replyToAddress, final String subject, final String body,
			final List<EmailAttachmentModel> attachments)
	{
		// Do all validation now before creating the message
		validateParameters(toAddresses, fromAddress, subject, body);

		validateEmailAddress(replyToAddress, "replyToAddress");

		final SystemEmailMessageModel emailMessageModel = getModelService().create(SystemEmailMessageModel.class);
		emailMessageModel.setToAddresses(toAddresses);
		emailMessageModel.setCcAddresses(ccAddresses);
		emailMessageModel.setBccAddresses(bccAddresses);
		emailMessageModel.setFromAddress(fromAddress);
		emailMessageModel.setReplyToAddress(
				(replyToAddress != null && !replyToAddress.isEmpty()) ? replyToAddress : fromAddress.getEmailAddress());
		emailMessageModel.setSubject(subject);
		emailMessageModel.setAttachments(attachments);
		if (body.length() < DefaultEmailService.EMAIL_BODY_MAX_LENGTH)
		{
			emailMessageModel.setBody(body);
			getModelService().save(emailMessageModel);
		}
		else
		{
			getModelService().save(emailMessageModel);
			final MediaModel bodyMedia = createBodyMedia("bodyMedia-" + emailMessageModel.getPk(), body);
			emailMessageModel.setBodyMedia(bodyMedia);
			getModelService().save(emailMessageModel);
		}

		return emailMessageModel;
	}

	/**
	 * Validate the parameters
	 *
	 * @param toAddresses
	 * @param fromAddress
	 * @param subject
	 * @param body
	 */
	protected void validateParameters(final List<EmailAddressModel> toAddresses, final EmailAddressModel fromAddress,
			final String subject, final String body)
	{
		if (CollectionUtils.isEmpty(toAddresses))
		{
			throw new IllegalArgumentException("toAddresses must not be empty");
		}
		if (fromAddress == null)
		{
			throw new IllegalArgumentException("fromAddress must not be null");
		}
		if (StringUtils.isEmpty(subject))
		{
			throw new IllegalArgumentException("subject must not be empty");
		}
		if (StringUtils.isEmpty(body))
		{
			throw new IllegalArgumentException("body must not be empty");
		}
	}


	@Override
	public List<SystemEmailMessageModel> getUnsentSystemEmails()
	{
		final List<SystemEmailMessageModel> systemEmailMessages = getSystemEmailDao().findSystemEmailsBySentStatus(false);

		if (CollectionUtils.isNotEmpty(systemEmailMessages))
		{
			return systemEmailMessages;
		}

		return Collections.emptyList();
	}

	/**
	 * Ensures that any email sent is done synchronously.
	 *
	 * @param message
	 * @return boolean
	 */
	@Override
	public synchronized boolean syncSend(final EmailMessageModel message)
	{
		return send(message);
	}

	/**
	 * Removes the email message model from persistence if it has been sent.
	 *
	 * @param message
	 *           the Email Message to remove.
	 */
	@Override
	public void removeSentEmail(final EmailMessageModel message)
	{
		if (message.isSent())
		{
			getModelService().remove(message);
		}
	}

	/**
	 * Constructs a System Email with the given parameters.
	 *
	 * @param fromAddress
	 *           the address the email is from.
	 * @param toAddress
	 *           the address to send the email to.
	 * @param displayName
	 *           the addressee's display name.
	 * @param subject
	 *           the email's subject.
	 * @param messages
	 *           the email's message.
	 * @return the created System Email model.
	 */
	@Override
	public SystemEmailMessageModel constructSystemEmail(final String fromAddress, final String toAddress, final String displayName,
			final String subject, final List<String> messages, final List<EmailAttachmentModel> attachments)
	{
		final EmailAddressModel toEmailAddress = getOrCreateEmailAddressForEmail(toAddress, displayName);
		final List<EmailAddressModel> toEmailAddresses = new ArrayList<>();
		toEmailAddresses.add(toEmailAddress);

		final EmailAddressModel fromEmailAddress = getOrCreateEmailAddressForEmail(fromAddress, displayName);

		final StringBuilder messageSb = new StringBuilder();
		for (final String message : messages)
		{
			messageSb.append(message);
			messageSb.append(
					getConfigurationService().getConfiguration().getString(EMAIL_BODY_END_OF_LINE_KEY, EMAIL_BODY_END_OF_LINE));
		}

		return createEmailMessage(toEmailAddresses, null, null, fromEmailAddress, null, subject, messageSb.toString(), attachments);
	}



	@Override
	public boolean send(final EmailMessageModel message)
	{
		if (message == null)
		{
			throw new IllegalArgumentException("message must not be null");
		}

		final boolean sendEnabled = getConfigurationService().getConfiguration().getBoolean(EMAILSERVICE_SEND_ENABLED_CONFIG_KEY,
				true);
		if (sendEnabled)
		{

			try
			{
				final HtmlEmail email = getPerConfiguredEmail();
				email.setCharset("UTF-8");

				final List<EmailAddressModel> toAddresses = message.getToAddresses();
				if (CollectionUtils.isNotEmpty(toAddresses))
				{
					email.setTo(getAddresses(toAddresses));
				}
				else
				{
					throw new IllegalArgumentException("message has no To addresses");
				}

				final List<EmailAddressModel> ccAddresses = message.getCcAddresses();
				if (ccAddresses != null && !ccAddresses.isEmpty())
				{
					email.setCc(getAddresses(ccAddresses));
				}

				final List<EmailAddressModel> bccAddresses = message.getBccAddresses();
				if (bccAddresses != null && !bccAddresses.isEmpty())
				{
					email.setBcc(getAddresses(bccAddresses));
				}

				final EmailAddressModel fromAddress = message.getFromAddress();
				email.setFrom(fromAddress.getEmailAddress(), nullifyEmpty(fromAddress.getDisplayName()));

				// Add the reply to if specified
				final String replyToAddress = message.getReplyToAddress();
				if (replyToAddress != null && !replyToAddress.isEmpty())
				{
					email.setReplyTo(Collections.singletonList(createInternetAddress(replyToAddress, null)));
				}

				email.setSubject(message.getSubject());
				email.setHtmlMsg(getBody(message));

				// To support plain text parts use email.setTextMsg()

				final List<EmailAttachmentModel> attachments = message.getAttachments();
				if (attachments != null && !attachments.isEmpty())
				{
					for (final EmailAttachmentModel attachment : attachments)
					{
						try
						{
							final DataSource dataSource = new ByteArrayDataSource(getMediaService().getDataFromMedia(attachment),
									attachment.getMime());
							email.attach(dataSource, attachment.getRealFileName(), attachment.getAltText());
						}
						catch (final EmailException ex)
						{
							LOG.error("Failed to load attachment data into data source [" + attachment + "]", ex);
							return false;
						}
					}
				}

				// Important to log all emails sent out
				LOG.info("Sending Email [" + message.getPk() + "] To [" + convertToStrings(toAddresses) + "] From ["
						+ fromAddress.getEmailAddress() + "] Subject [" + email.getSubject() + "]");

				// Send the email and capture the message ID
				final String messageID = email.send();

				message.setSent(true);
				message.setSentMessageID(messageID);
				message.setSentDate(new Date());

				// update the last attempted send date
				if (message instanceof SystemEmailMessageModel)
				{
					((SystemEmailMessageModel) message).setLastAttemptedSend(new Date());
				}

				getModelService().save(message);

				return true;
			}
			catch (final EmailException e)
			{
				LOG.error("Could not send e-mail pk [" + message.getPk() + "] subject [" + message.getSubject() + "] cause: "
						+ e.getMessage());
				LOG.error("SMTP Email sending error cause::", e);

				//Exception Handling to prevent multiple emails
				if(e.getCause() != null && e.getCause().getMessage() != null && e.getCause().getMessage().contains(
						getConfigurationService().getConfiguration().getString(EMAIL_LIMIT_EXCEED_MSG, "Concurrent connections limit exceeded")))
				{
					message.setSent(true);
					message.setSentDate(new Date());

					// update the last attempted send date
					if (message instanceof SystemEmailMessageModel)
					{
						((SystemEmailMessageModel) message).setLastAttemptedSend(new Date());
					}

					getModelService().save(message);

					return true;
				}				
			}
		}
		else
		{
			LOG.warn("Could not send e-mail pk [" + message.getPk() + "] subject [" + message.getSubject() + "]");
			LOG.info("Email sending has been disabled. Check the config property 'emailservice.send.enabled'");
			return true;
		}

		return false;
	}



	/**
	 * @return the systemEmailDao
	 */
	public SystemEmailDao getSystemEmailDao()
	{
		return systemEmailDao;
	}

	/**
	 * @param systemEmailDao
	 *           the systemEmailDao to set
	 */
	public void setSystemEmailDao(final SystemEmailDao systemEmailDao)
	{
		this.systemEmailDao = systemEmailDao;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.commons.email.service.SystemEmailService#constructSystemEmailForDailyMonitoringSheet(java.lang.
	 * String, java.util.List, java.lang.String, java.lang.String, java.util.List, java.util.List)
	 */
	@Override
	public SystemEmailMessageModel constructSystemEmailForDailyMonitoringSheet(final String fromAddress,
			final List<String> toAddresses, final String displayName, final String subject, final List<String> messages,
			final List<EmailAttachmentModel> attachments)
	{
		final List<EmailAddressModel> toEmailAddresses = new ArrayList<>();
		for (final String toAddress : toAddresses)
		{
			final EmailAddressModel toEmailAddress = getOrCreateEmailAddressForEmail(toAddress, toAddress);
			toEmailAddresses.add(toEmailAddress);
		}

		final EmailAddressModel fromEmailAddress = getOrCreateEmailAddressForEmail(fromAddress, displayName);

		final StringBuilder messageSb = new StringBuilder();
		for (final String message : messages)
		{
			messageSb.append(message);
			messageSb.append(
					getConfigurationService().getConfiguration().getString(EMAIL_BODY_END_OF_LINE_KEY, EMAIL_BODY_END_OF_LINE));
		}

		return createEmailMessage(toEmailAddresses, null, null, fromEmailAddress, null, subject, messageSb.toString(), attachments);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sabmiller.commons.email.service.SystemEmailService#constructSystemEmailForJobStatusNotification(java.lang.
	 * String, java.util.List, java.lang.String, java.lang.String, java.util.List, java.util.List)
	 */
	@Override
	public SystemEmailMessageModel constructSystemEmailForJobStatusNotification(final String fromAddress,
			final List<String> toAddresses, final String displayName, final String subject, final List<String> messages,
			final List<EmailAttachmentModel> attachments)
	{
		final List<EmailAddressModel> toEmailAddresses = new ArrayList<>();
		for (final String toAddress : toAddresses)
		{
			final EmailAddressModel toEmailAddress = getOrCreateEmailAddressForEmail(toAddress, toAddress);
			toEmailAddresses.add(toEmailAddress);
		}

		final EmailAddressModel fromEmailAddress = getOrCreateEmailAddressForEmail(fromAddress, displayName);

		final StringBuilder messageSb = new StringBuilder();
		for (final String message : messages)
		{
			messageSb.append(message);
			messageSb.append(
					getConfigurationService().getConfiguration().getString(EMAIL_BODY_END_OF_LINE_KEY, EMAIL_BODY_END_OF_LINE));
		}

		return createEmailMessage(toEmailAddresses, null, null, fromEmailAddress, null, subject, messageSb.toString(), attachments);
	}

	@Override
	public SystemEmailMessageModel constructSystemEmailForMultipleRecepients(final String fromAddress, final String toAddresses,
			final String displayName, final String subject, final List<String> messages,
			final List<EmailAttachmentModel> attachments)
	{
		final List<String> toemailIds = Arrays.asList(toAddresses.split("[,;]"));
		final List<EmailAddressModel> toEmailAddresses = new ArrayList<>();
		for (final String toAddress : toemailIds)
		{
			final EmailAddressModel toEmailAddress = getOrCreateEmailAddressForEmail(toAddress, toAddress);
			toEmailAddresses.add(toEmailAddress);
		}

		final EmailAddressModel fromEmailAddress = getOrCreateEmailAddressForEmail(fromAddress, displayName);

		final StringBuilder messageSb = new StringBuilder();
		for (final String message : messages)
		{
			messageSb.append(message);
			messageSb.append(
					getConfigurationService().getConfiguration().getString(EMAIL_BODY_END_OF_LINE_KEY, EMAIL_BODY_END_OF_LINE));
		}

		return createEmailMessage(toEmailAddresses, null, null, fromEmailAddress, null, subject, messageSb.toString(), attachments);
	}




}

