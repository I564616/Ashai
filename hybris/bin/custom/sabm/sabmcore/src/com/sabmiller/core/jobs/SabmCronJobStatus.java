/**
 *
 */
package com.sabmiller.core.jobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.util.Config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import com.sabmiller.commons.email.service.SystemEmailService;
import com.sabmiller.commons.model.SystemEmailMessageModel;


/**
 * @author anil.kumar.kuruba
 *
 */
public class SabmCronJobStatus
{
	@Resource(name = "emailService")
	private SystemEmailService emailService;

	@Resource(name = "mediaService")
	private MediaService mediaService;


	final String fromEmail = Config.getString("cronjob.jobstatus.email.fromEmail", "from email");
	final String toEmail = Config.getString("cronjob.jobstatus.email.toEmail", "to email");
	final String displayName = Config.getString("cronjob.jobstatus.email.displayName", "Display Name ");

	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");

	public void sendJobStatusNotification(final String jobName, final String jobStatus, final String jobStartedtime)
	{
		final Date timeStampdate = new Date();
		final String jobFinishedtime = timeStampdate.toGMTString();

		try
		{
			final List<String> emailBody = new ArrayList<String>();
			emailBody.add("<b>Job : </b>" + jobName);
			emailBody.add("</br>");
			emailBody.add("</br>");
			emailBody.add("<b>Started :</b>" + jobStartedtime);
			emailBody.add("<b>Finished :</b>" + jobFinishedtime);
			emailBody.add("</br>");
			emailBody.add("</br>");
			if (jobStatus.equals("performed Successfully"))
			{
				emailBody.add("<html style='color: #ff2e66;'>The " + jobName + " status has been " + jobStatus + ".</html>");
			}
			else
			{
				emailBody.add("<html style='color: #ff2e66;'>The " + jobName + " status has been " + jobStatus
						+ ". Could you please verify.</html>");
			}


			//	emailBody.add("The " + jobName + " status has been " + jobStatus + ". Could you please verify");
			final String subject = jobName + " has been " + jobStatus;



			final List<String> toemailIds = Arrays.asList(toEmail.split(","));

			final SystemEmailMessageModel systemEmailMessageModel = emailService
					.constructSystemEmailForJobStatusNotification(fromEmail, toemailIds, displayName, subject, emailBody, null);
			emailService.send(systemEmailMessageModel);

		}
		catch (final Exception e)
		{
			//		LOG.("We got exception", e);
		}
	}

	public void sendJobStatusNotification (final CronJobModel cronJob, final PerformResult performResult) {
		final List<String> emailBody = new ArrayList<String>();
		emailBody.add("<b>Job : </b>" + cronJob.getCode());
		emailBody.add("</br>");
		emailBody.add("</br>");
		emailBody.add("<b>Started : </b>" + sdf.format(cronJob.getStartTime()));
		emailBody.add("<b>Finished : </b>" + sdf.format(new Date()));
		emailBody.add("<b>Status : </b>" + performResult.getResult());
		emailBody.add("</br>");
		if (performResult.getResult().equals(CronJobResult.ERROR)) {
			emailBody.add("<html style='color: #ff2e66;'> Error encountered while processing the file. Please check the job's latest log from Backoffice.</html>");
		}

		final List<String> toemailIds = Arrays.asList(toEmail.split(","));

		final String subject = "[" + cronJob.getCode() + "] " + performResult.getResult().getCode();

		final SystemEmailMessageModel systemEmailMessageModel = emailService
				.constructSystemEmailForJobStatusNotification(fromEmail, toemailIds, displayName, subject, emailBody, null);
		emailService.send(systemEmailMessageModel);
	}

	public void sendNoFileNotification (final CronJobModel cronJob) {
		final List<String> emailBody = new ArrayList<String>();
		emailBody.add("<b>Job : </b>" + cronJob.getCode());
		emailBody.add("</br>");
		emailBody.add("</br>");
		emailBody.add("<b>Started : </b>" + sdf.format(cronJob.getStartTime()));
		emailBody.add("<b>Finished : </b>" + sdf.format(new Date()));
		emailBody.add("</br>");
		emailBody.add("<html style='color: #ff2e66;'> Input File for job not found.</html>");


		final List<String> toemailIds = Arrays.asList(toEmail.split(","));

		final String subject = "[" + cronJob.getCode() + "] No File Found";

		final SystemEmailMessageModel systemEmailMessageModel = emailService
				.constructSystemEmailForJobStatusNotification(fromEmail, toemailIds, displayName, subject, emailBody, null);
		emailService.send(systemEmailMessageModel);
	}

	/**
	 * @return the emailService
	 */
	public SystemEmailService getEmailService()
	{
		return emailService;
	}

	/**
	 * @param emailService
	 *           the emailService to set
	 */
	public void setEmailService(final SystemEmailService emailService)
	{
		this.emailService = emailService;
	}

	/**
	 * @return the mediaService
	 */
	public MediaService getMediaService()
	{
		return mediaService;
	}

	/**
	 * @param mediaService
	 *           the mediaService to set
	 */
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}


}
