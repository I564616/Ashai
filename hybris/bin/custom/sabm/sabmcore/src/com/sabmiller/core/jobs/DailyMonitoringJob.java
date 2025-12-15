/**
 *
 */
package com.sabmiller.core.jobs;

import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.Config;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.commons.email.service.SystemEmailService;
import com.sabmiller.commons.model.SystemEmailMessageModel;
import com.sabmiller.core.monitoringsheet.impl.CreateExcelSheetImpl;


/**
 * @author praveenkumar.k.reddy
 *
 */
public class DailyMonitoringJob extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(DailyMonitoringJob.class);

	@Resource(name = "createExcelSheet")
	private CreateExcelSheetImpl createExcelSheet;

	@Resource(name = "emailService")
	private SystemEmailService emailService;

	@Resource(name = "mediaService")
	private MediaService mediaService;

	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;
	final String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());

	final String fromEmail = Config.getString("monitoring.report.email.fromEmail", "from email");
	final String toEmail = Config.getString("monitoring.report.email.toEmail", "to email");
	final String displayName = Config.getString("monitoring.report.email.displayName", "Display Name ");
	final String subject = Config.getString("monitoring.report.email.subject", "Daily Monitoing Sheet");
	final String fileName = Config.getString("monitoring.report.file.creation.directory", "File creation directory")
			+ File.separator + "DailyMonitoringSheet.csv";

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
	public PerformResult perform(final CronJobModel cronJob)
	{
		final List<CronJobModel> joblist = new ArrayList<CronJobModel>();
		boolean status = true;
		try
		{
			for (final CronJobModel jobModel : getAlljobsdetails())
			{
				LOG.info(" ################################ Job Name : " + jobModel.getCode() + "Job Status : " + jobModel.getStatus()
						+ "Job Result : " + jobModel.getResult() + "Start Time : " + jobModel.getStartTime() + "End Time : "
						+ jobModel.getEndTime());

				joblist.add(jobModel);
			}
			status = createExcelSheet.createExcelSheet(joblist, getAllInterfacedetails());
			sendDailyMonitoringSheetEmail();
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			status = false;
			LOG.info("Problem in During daily monitoring.");
			return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
		}
		finally
		{

			if (!status)
			{
				sabmCronJobStatus.sendJobStatusNotification(cronJob.getCode(), "Aborted", timeStamp);
			}
		}
	}

	protected List<CronJobModel> getAlljobsdetails()
	{
		final String query = "SELECT {job.pk} FROM {cronjob as job}, {CronJobStatus as status}, {CronJobResult as result}, {trigger as tr} "
				+ "WHERE {job.status} = {status.pk} " + "AND {job.result} = {result.pk} " + "AND {tr.cronjob} = {job.pk} "
				+ "AND {job.code} IN ('imageImportCronJob', 'sendCustomerWelcomeEmailCronJob', 'full-sabmStoreIndex-cronJob', 'update-sabmStoreIndex-cronJob', 'ProductExclusionImportCleanupJob', 'systemEmailCronJob', 'SabmRecommendationCleanUpCronJob',  'OldSABMDealsRemovalCronJob', 'SalesForceReportExportJob_WelcomeEmai', 'SalesForceReportExportCronJob_Products', 'salesForceExportReportCronjob_Users_Customers_Notifications', 'salesForceExportReportCronjob_Transactions' )";
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
		final SearchResult<CronJobModel> processes = flexibleSearchService.search(searchQuery);
		return processes.getResult();
	}

	protected List<List<?>> getAllInterfacedetails()
	{

		final String query = "SELECT {ety.code},{dims.code},count({dims.code}) FROM {masterimport as mi},{EntityTypeEnum as ety}, {DataImportStatusEnum as dims}"
				+ "WHERE {mi.entity} = {ety.pk} AND {mi.status} = {dims.pk} AND {dims.code} NOT IN ('NEW') AND ({mi.creationtime} >= ?ysterday) GROUP BY {ety.code},{dims.code} ORDER BY {ety.code}";
		final DateTime toDay = new DateTime();
		final DateTime ysterday = toDay.minusDays(1);
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("ysterday", ysterday.toDate());

		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query, params);
		searchQuery.setResultClassList(Arrays.asList(String.class, String.class, Long.class));
		final SearchResult<List<?>> processes = flexibleSearchService.search(searchQuery);

		return processes.getResult();
	}

	private void sendDailyMonitoringSheetEmail()
	{
		try
		{
			final InputStream input = new FileInputStream(fileName);
			final DataInputStream inst = new DataInputStream(input);

			final List<String> emailBody = new ArrayList<String>();
			emailBody.add("Hi");
			emailBody.add("</br>");
			emailBody.add("</br>");
			emailBody.add("Kindly find the attached daily monitoring report.");

			//Setting catalog in session for emailAttachment
			catalogVersionService.setSessionCatalogVersion(
					Config.getString("email.attachment.default.catalog", "sabmContentCatalog"),
					Config.getString("email.attachment.default.catalog.version", "Staged"));

			//Creating attachment for the email with generated report file.
			final EmailAttachmentModel emailAttachment = emailService.createEmailAttachment(inst, getAttachmentFileName(),
					getMimeType());

			final List<String> toemailIds = Arrays.asList(toEmail.split(","));

			final SystemEmailMessageModel systemEmailMessageModel = emailService.constructSystemEmailForDailyMonitoringSheet(
					fromEmail, toemailIds, displayName, subject, emailBody, Collections.singletonList(emailAttachment));
			emailService.send(systemEmailMessageModel);

		}
		catch (final FileNotFoundException e)
		{
			LOG.error("File not found to read the input stream", e);
		}
	}


	protected String getAttachmentFileName()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(Config.getString("customer.export.media.code", "dailymonitoring_report"));
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


	/**
	 * @return the createExcelSheet
	 */
	public CreateExcelSheetImpl getCreateExcelSheet()
	{
		return createExcelSheet;
	}

	/**
	 * @param createExcelSheet
	 *           the createExcelSheet to set
	 */
	public void setCreateExcelSheet(final CreateExcelSheetImpl createExcelSheet)
	{
		this.createExcelSheet = createExcelSheet;
	}

	/**
	 * @return the catalogVersionService
	 */
	public CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	/**
	 * @param catalogVersionService
	 *           the catalogVersionService to set
	 */
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
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
