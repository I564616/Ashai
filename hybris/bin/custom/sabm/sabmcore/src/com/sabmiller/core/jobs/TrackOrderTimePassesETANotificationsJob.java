package com.sabmiller.core.jobs;

import de.hybris.platform.notificationservices.enums.NotificationType;
import com.sabmiller.core.notification.service.NotificationService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;


/**
 * The Class ExportCustomerJob.
 */
public class TrackOrderTimePassesETANotificationsJob extends AbstractJobPerformable<CronJobModel>
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(TrackOrderTimePassesETANotificationsJob.class);

	/** The sabm imp ex media dao. */
	@Resource(name = "notificationService")
	private NotificationService notificationService;


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */
	@Override
	public PerformResult perform(final CronJobModel cronJob)
	{
		CronJobResult result = CronJobResult.SUCCESS;
		try {
//			notificationService.sendNotifications(NotificationType.UPDATE_FOR_ETA);

			notificationService.sendTrackOrderTimePassesETAEmailOrSms(NotificationType.UPDATE_FOR_ETA);

		} catch (Exception e) {
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
		return new PerformResult(result, CronJobStatus.FINISHED);
	}


}