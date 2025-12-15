/**
*
*/
package com.sabmiller.core.jobs;

import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.GenericSearchConstants.LOG;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.util.Config;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.commons.email.service.SystemEmailService;
import com.sabmiller.commons.model.SystemEmailMessageModel;
import com.sabmiller.core.model.CustomerNotificationCronJobModel;
import com.sabmiller.core.report.service.NotificationReportService;


/**
 * @author g.charan.pandit.raj
 *
 */
public class CustomerNotificationJob extends AbstractJobPerformable<CustomerNotificationCronJobModel>
{

	private static final Logger LOG = LoggerFactory.getLogger(CustomerNotificationJob.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */

	private NotificationReportService notificationReportService;

	@Resource(name = "emailService")
	private SystemEmailService emailService;

	@Resource(name = "mediaService")
	private MediaService mediaService;

	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	private CustomerNotificationCronJobModel customerNotificationEmailCronJobModel = null;

	final String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());

	final String fromEmail = Config.getString("notification.report.email.fromEmail", "from email");
	final String displayName = Config.getString("notification.report.email.displayName", "Display Name ");
	final String subject = Config.getString("notification.report.email.subject", "Notification Sheet");
	final String directory = Config.getString("notification.report.file.creation.directory", "File creation directory");
	final String fileName = Config.getString("notification.report.file.creation.directory", "File creation directory")
			+ File.separator + "NotificationReport.csv";

	@Resource(name = "sabmCronJobStatus")
	private SabmCronJobStatus sabmCronJobStatus;

	/**
	 * @return the sabmCronJobStatus
	 */
	public SabmCronJobStatus getSabmCronJobStatus()
	{
		return sabmCronJobStatus;
	}


	/**
	 * @param sabmCronJobStatus
	 *           the sabmCronJobStatus to set
	 */
	public void setSabmCronJobStatus(final SabmCronJobStatus sabmCronJobStatus)
	{
		this.sabmCronJobStatus = sabmCronJobStatus;
	}

	@Override
	public PerformResult perform(final CustomerNotificationCronJobModel cronjob)
	{
		boolean status = true;

		try
		{
			final File Notificationfile = notificationReportService.generateNotificationReport("notification");
			customerNotificationEmailCronJobModel = cronjob;
			sendcustomnotificationEmail(Notificationfile);
			status = true;
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			status = false;
			LOG.error("Problem in During notification process.",e);
			return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
		}
		finally
		{

			if (!status)
			{
				sabmCronJobStatus.sendJobStatusNotification(cronjob.getCode(), "Aborted", timeStamp);
			}
		}
	}

	/**
	 * @param notificationfile
	 *
	 *
	 */
	private void sendcustomnotificationEmail(final File notificationfile)
	{

		try
		{
			final InputStream input = new FileInputStream(notificationfile);
			final DataInputStream inst = new DataInputStream(input);
			final List<String> emailBody = new ArrayList<String>();
			emailBody.add("Hi");
			emailBody.add("</br>");
			emailBody.add("</br>");
			emailBody.add("Kindly find the attached notification report.");

			//Setting catalog in session for emailAttachment
			catalogVersionService.setSessionCatalogVersion(
					Config.getString("email.attachment.default.catalog", "sabmContentCatalog"),
					Config.getString("email.attachment.default.catalog.version", "Staged"));

			//Creating attachment for the email with generated report file.
			final EmailAttachmentModel emailAttachment = emailService.createEmailAttachment(inst, getAttachmentFileName(),
					getMimeType());

			final List<String> toemailIds = customerNotificationEmailCronJobModel.getReceipientEmails();

			final SystemEmailMessageModel systemEmailMessageModel = emailService.constructSystemEmailForDailyMonitoringSheet(
					fromEmail, toemailIds, displayName, subject, emailBody, Collections.singletonList(emailAttachment));
			emailService.send(systemEmailMessageModel);

		}
		catch (final Exception e)
		{
			LOG.error("File not found to read the input stream", e);
		}

	}

	public NotificationReportService getNotificationReportService()
	{
		return notificationReportService;
	}

	public void setNotificationReportService(final NotificationReportService notificationReportService)
	{
		this.notificationReportService = notificationReportService;
	}

	protected String getAttachmentFileName()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(Config.getString("customer.export.media.code", "notification"));
		sb.append("_");
		sb.append(new Date().getTime());
		//sb.append(BooleanUtils.isTrue(singleFile) ? ".csv" : ".zip");
		sb.append(".csv");

		return sb.toString();
	}

	/**
	 * Gets the mime type checking the singleFile flag.
	 *
	 * @return the mime type. If the singleFile flag is true, it will be text/csv, else a application/zip.
	 */
	protected String getMimeType()
	{
		//return BooleanUtils.isTrue(singleFile) ? "text/csv" : "application/zip";
		return "text/csv";
	}



}

