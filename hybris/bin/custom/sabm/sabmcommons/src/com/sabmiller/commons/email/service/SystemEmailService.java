/**
 *
 */
package com.sabmiller.commons.email.service;

import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;

import java.util.List;

import com.sabmiller.commons.model.SystemEmailMessageModel;



/**
 *
 */
public interface SystemEmailService extends EmailService
{
	List<SystemEmailMessageModel> getUnsentSystemEmails();

	boolean syncSend(final EmailMessageModel message);

	void removeSentEmail(EmailMessageModel message);

	SystemEmailMessageModel constructSystemEmail(String fromAddress, String toAddress, String displayName, String subject,
			List<String> messages, List<EmailAttachmentModel> attachments);

	SystemEmailMessageModel constructSystemEmailForDailyMonitoringSheet(String fromAddress, List<String> toAddresses,
			String displayName, String subject, List<String> messages, List<EmailAttachmentModel> attachments);

	SystemEmailMessageModel constructSystemEmailForJobStatusNotification(String fromAddress, List<String> toAddresses,
			String displayName, String subject, List<String> messages, List<EmailAttachmentModel> attachments);

	SystemEmailMessageModel constructSystemEmailForMultipleRecepients(String fromAddress, String toAddresses, String displayName,
			String subject, List<String> messages, List<EmailAttachmentModel> attachments);
}
