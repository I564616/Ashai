package com.sabmiller.commons.jobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.List;

import org.apache.log4j.Logger;

import com.sabmiller.commons.email.service.impl.SystemEmailServiceImpl;
import com.sabmiller.commons.model.SystemEmailMessageModel;


/**
 * This job pulls up a list of SystemEmailMessage Models which has yet to be successfully sent. It leverages on the
 * extended Hybris DefaultEmailService to send off the emails.
 *
 * @see de.hybris.platform.acceleratorservices.email.impl.DefaultEmailService
 *
 * @author wei.yang.ng@accenture.com
 */
public class SystemEmailJob extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOG = Logger.getLogger(SystemEmailJob.class);

	private SystemEmailServiceImpl systemEmailService;

	/**
	 * 1. Fetch a list of system emails to send. - Email Message Model - sent boolean flag is false -
	 *
	 * @param cronJob
	 * @return result
	 */
	@Override
	public PerformResult perform(final CronJobModel cronJob)
	{

		LOG.debug("Retrieving a list of Unsent System Emails.");
		final List<SystemEmailMessageModel> unsentEmails = getSystemEmailService().getUnsentSystemEmails();

		LOG.debug(String.format("Found [%d] unsent system emails", Integer.valueOf(unsentEmails.size())));

		int sentMessages = 0;
		for (final SystemEmailMessageModel systemEmailMessage : unsentEmails)
		{

			try
			{
				final boolean successful = getSystemEmailService().syncSend(systemEmailMessage);
				if (successful)
				{
					//getSystemEmailService().removeSentEmail(systemEmailMessage);
					++sentMessages;
				}
			}
			catch (final Exception ex)
			{
				LOG.error("An error occurred while attempting to send a System Email", ex);
			}
		}

		LOG.debug(String.format("Successfully sent [%d] system emails.", Integer.valueOf(sentMessages)));

		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	public SystemEmailServiceImpl getSystemEmailService()
	{
		return systemEmailService;
	}

	public void setSystemEmailService(final SystemEmailServiceImpl systemEmailService)
	{
		this.systemEmailService = systemEmailService;
	}
}
