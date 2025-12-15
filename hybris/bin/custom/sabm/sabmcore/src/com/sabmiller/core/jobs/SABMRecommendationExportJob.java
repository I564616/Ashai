package com.sabmiller.core.jobs;

import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.impex.jalo.exp.Export;
import de.hybris.platform.impex.jalo.exp.ExportConfiguration;
import de.hybris.platform.impex.jalo.exp.HeaderLibrary;
import de.hybris.platform.impex.jalo.exp.ImpExExportMedia;
import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.sabmiller.commons.email.service.SystemEmailService;
import com.sabmiller.commons.model.SystemEmailMessageModel;
import com.sabmiller.core.enums.RecommendationStatus;
import com.sabmiller.core.enums.RecommendationType;
import com.sabmiller.core.model.SABMEmailExportCronJobModel;
import com.sabmiller.core.model.SABMRecommendationModel;
import com.sabmiller.core.recommendation.dao.SabmRecommendationDao;


/**
 * The Class ExportCustomerJob.
 */
public class SABMRecommendationExportJob extends AbstractJobPerformable<SABMEmailExportCronJobModel>
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMRecommendationExportJob.class);

	final String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());

	/** The sabm imp ex media dao. */
	@Resource(name = "sabmImpExMediaDao")
	private GenericDao<ImpExMediaModel> sabmImpExMediaDao;

	/** The email service. */
	@Resource(name = "emailService")
	private SystemEmailService emailService;

	/** The catalog version service. */
	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	/** The model service. */
	@Resource
	private ModelService modelService;

	/** The single file. */
	@Value(value = "${customer.export.single.file:false}")
	private Boolean singleFile;

	/** The single file. */
	@Value(value = "${customer.export.field.separator:,}")
	private String fieldseparator;

	@Resource(name = "recommendationDao")
	private SabmRecommendationDao recommendationDao;

	@Resource
	private BaseStoreService baseStoreService;

	final static String RECOMMENDATIONNUMBER = "No of Recommendations:";
	final static String DEALRECOMMENDATIONNUMBER = "No of Deal Recommendations:";
	final static String PRODUCTRECOMMENDATIONNUMBER = "No of Product Recommendations:";
	final static String ACCEPTEDRECOMMENDATIONNUMBER = "No of Accepted Recommendations:";
	final static String REJECTEDRECOMMENDATIONNUMBER = "No of Rejected Recommendations:";
	final static String EXPIREDRECOMMENDATIONNUMBER = "No of Expired Recommendations:";
	final static String TOTALRECOMMENDATIONS = "Total Recommendation Statistics";
	final static String CURRENTMONTHRECOMMENDATIONS = "Current Month Recommendation Statistics";
	final static String PREVIOUSMONTHRECOMMENDATIONS = "Previous Month Recommendation Statistics";

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


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */
	@Override
	public PerformResult perform(final SABMEmailExportCronJobModel cronJob)
	{
		CronJobResult result = CronJobResult.ERROR;

		final Map<String, Object> params = new HashMap<>();
		params.put("code", Config.getString("customer.export.media.code", "Sabmrecommendation_report"));

		//Getting the already existing HeaderLibrary with the script containing the attributes to export.
		final List<ImpExMediaModel> impexMediaList = sabmImpExMediaDao.find(params);

		LOG.debug("Retrieved ImpExMedia: {}", impexMediaList);
		boolean status = true;
		if (clearAbortRequestedIfNeeded(cronJob))
		{
			status = false;
			LOG.debug("The job is aborted.");
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}

		if (CollectionUtils.isNotEmpty(impexMediaList))
		{
			final HeaderLibrary impexMedia = modelService.getSource(impexMediaList.get(0));
			try
			{
				final ExportConfiguration config = new ExportConfiguration(impexMedia, ImpExManager.getExportOnlyMode());
				config.setFieldSeparator(fieldseparator);
				config.setSingleFile(BooleanUtils.isTrue(singleFile));
				final Export export = ImpExManager.getInstance().exportDataLight(config);

				final ImpExExportMedia exportedData = export.getExportedData();

				LOG.debug("Exported data [{}] by job [{}]", exportedData, cronJob);

				//Setting catalog in session for emailAttachment
				catalogVersionService.setSessionCatalogVersion(
						Config.getString("email.attachment.default.catalog", "sabmContentCatalog"),
						Config.getString("email.attachment.default.catalog.version", "Staged"));

				//Creating attachment for the email with generated report file.
				final EmailAttachmentModel emailAttachment = emailService.createEmailAttachment(
						new DataInputStream(new FileInputStream(exportedData.getFile())), getAttachmentFileName(), getMimeType());

				final SystemEmailMessageModel systemEmail = emailService.constructSystemEmail(cronJob.getEmailFrom(),
						cronJob.getEmailTo(), cronJob.getEmailTo(), cronJob.getSubject(),
						Collections.singletonList(cronJob.getBody() + getRecommendationStatistics()),
						Collections.singletonList(emailAttachment));

				//If the email is correctly generated than it will be sent by the systemEmailCronJob.
				if (systemEmail != null)
				{
					result = CronJobResult.SUCCESS;
				}
			}
			catch (final ImpExException | FileNotFoundException | JaloSystemException | ModelNotFoundException e)
			{
				status = false;
				LOG.error("Exception performing recommendation export " + e, e);
			}
			finally
			{

				if (!status)
				{
					sabmCronJobStatus.sendJobStatusNotification(cronJob.getCode(), "Aborted", timeStamp);
				}
			}
		}
		else
		{
			LOG.error("Missing ImpExMediaModel [{}]! It's required for recommendation export report.",
					Config.getString("customer.export.media.code", "Sabmrecommendation_report"));
		}

		LOG.info("SABMRecommendationExportJob performed with status");

		return new PerformResult(result, CronJobStatus.FINISHED);
	}

	/**
	 * Gets the attachment file name. It contains a timestamp to avoid duplicate names.
	 *
	 * @return the attachment file name. If the singleFile flag is true, it will be a CSV file, else a ZIP one.
	 */
	protected String getAttachmentFileName()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(Config.getString("customer.export.media.code", "Sabmrecommendation_report"));
		sb.append("_");
		sb.append(new Date().getTime());
		sb.append(BooleanUtils.isTrue(singleFile) ? ".csv" : ".zip");

		return sb.toString();
	}

	/**
	 * Gets the mime type checking the singleFile flag.
	 *
	 * @return the mime type. If the singleFile flag is true, it will be text/csv, else a application/zip.
	 */
	protected String getMimeType()
	{
		return BooleanUtils.isTrue(singleFile) ? "text/csv" : "application/zip";
	}

	//Extract Recommendation Statistics
	private StringBuilder getRecommendationStatistics()
	{
		final StringBuilder recommendationStaticticsString = new StringBuilder();
		final List<SABMRecommendationModel> recommendationList = recommendationDao.getAllRecommendations();
		final List<SABMRecommendationModel> currentMonthRecommendationList = new ArrayList<SABMRecommendationModel>();
		final List<SABMRecommendationModel> previousMonthRecommendationList = new ArrayList<SABMRecommendationModel>();
		final Calendar aCalendar = Calendar.getInstance();
		//Previous month start and end date
		aCalendar.set(Calendar.DATE, 1);
		aCalendar.add(Calendar.DAY_OF_MONTH, -1);
		final Date lastDateOfPreviousMonth = aCalendar.getTime();
		aCalendar.set(Calendar.DATE, 1);
		final Date firstDateOfPreviousMonth = aCalendar.getTime();
		//Current month start date
		aCalendar.set(Calendar.DAY_OF_MONTH, 1);
		final Date firstDateOfCurrentMonth = aCalendar.getTime();

		final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid("sabmStore");
		TimeZone toTimeZone = null;
		//Getting BaseStore timezone
		if (baseStore != null && baseStore.getTimeZone() != null)
		{
			toTimeZone = TimeZone.getTimeZone(baseStore.getTimeZone().getCode());
		}
		// Server timezone
		final TimeZone fromTimeZone = Calendar.getInstance().getTimeZone();


		for (final SABMRecommendationModel recommendation : recommendationList)
		{
			final Date offsetModifiedTime = new Date(
					recommendation.getModifiedtime().getTime() - fromTimeZone.getOffset(recommendation.getModifiedtime().getTime())
							+ toTimeZone.getOffset(recommendation.getModifiedtime().getTime()));
			final Date offsetCreatedTime = new Date(
					recommendation.getCreationtime().getTime() - fromTimeZone.getOffset(recommendation.getCreationtime().getTime())
							+ toTimeZone.getOffset(recommendation.getCreationtime().getTime()));
			//Get previous month recommendation list
			if ((offsetModifiedTime.compareTo(firstDateOfPreviousMonth) >= 0
					&& offsetModifiedTime.compareTo(lastDateOfPreviousMonth) <= 0)
					|| (offsetCreatedTime.compareTo(firstDateOfPreviousMonth) >= 0
							&& offsetCreatedTime.compareTo(lastDateOfPreviousMonth) <= 0))
			{
				previousMonthRecommendationList.add(recommendation);
			}
			//Get current month recommendation list
			else if (offsetModifiedTime.compareTo(firstDateOfCurrentMonth) >= 0
					|| offsetCreatedTime.compareTo(firstDateOfCurrentMonth) >= 0)
			{
				currentMonthRecommendationList.add(recommendation);
			}
		}
		return recommendationStaticticsString.append("<br><b>").append(TOTALRECOMMENDATIONS).append("</b><br>")
				.append(extractAndParseRecommendations(recommendationList)).append("<br><b>").append(PREVIOUSMONTHRECOMMENDATIONS)
				.append("</b><br>").append(extractAndParseRecommendations(previousMonthRecommendationList).toString())
				.append("<br><b>").append(CURRENTMONTHRECOMMENDATIONS).append("</b><br>")
				.append(extractAndParseRecommendations(currentMonthRecommendationList).toString());
	}

	private StringBuilder extractAndParseRecommendations(final List<SABMRecommendationModel> recommendationList)
	{
		final StringBuilder recommendationStaticticsString = new StringBuilder();
		if (CollectionUtils.isNotEmpty(recommendationList))
		{
			final int totalRecommendationCount = recommendationList.size();
			int totalDealRecommendationCount = 0;
			int totalProductRecommendationCount = 0;
			int totalAcceptedRecommendationCount = 0;
			int totalRejectedRecommendationCount = 0;
			int totalExpiredRecommendationCount = 0;

			for (final SABMRecommendationModel recommendation : recommendationList)
			{
				if (recommendation.getRecommendationType().equals(RecommendationType.PRODUCT))
				{
					totalProductRecommendationCount++;
				}
				if (recommendation.getRecommendationType().equals(RecommendationType.DEAL))
				{
					totalDealRecommendationCount++;
				}
				if (recommendation.getStatus().equals(RecommendationStatus.ACCEPTED))
				{
					totalAcceptedRecommendationCount++;
				}
				if (recommendation.getStatus().equals(RecommendationStatus.REJECTED))
				{
					totalRejectedRecommendationCount++;
				}
				if (recommendation.getStatus().equals(RecommendationStatus.EXPIRED))
				{
					totalExpiredRecommendationCount++;
				}
			}
			recommendationStaticticsString.append("<br>").append(RECOMMENDATIONNUMBER).append(totalRecommendationCount)
					.append("<br>").append(PRODUCTRECOMMENDATIONNUMBER).append(totalProductRecommendationCount).append("<br>")
					.append(DEALRECOMMENDATIONNUMBER).append(totalDealRecommendationCount).append("<br>")
					.append(ACCEPTEDRECOMMENDATIONNUMBER).append(totalAcceptedRecommendationCount).append("<br>")
					.append(REJECTEDRECOMMENDATIONNUMBER).append(totalRejectedRecommendationCount).append("<br>")
					.append(EXPIREDRECOMMENDATIONNUMBER).append(totalExpiredRecommendationCount).append("<br>");
		}
		else
		{
			recommendationStaticticsString.append("NA");
		}
		return recommendationStaticticsString;
	}

}