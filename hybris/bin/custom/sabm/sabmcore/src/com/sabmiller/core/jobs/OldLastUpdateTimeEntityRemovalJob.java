/**
 *
 */
package com.sabmiller.core.jobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.time.TimeService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import jakarta.annotation.Resource;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.model.LastUpdateTimeEntityModel;
import com.sabmiller.core.model.OldLastUpdateTimeEntityRemovalCronJobModel;


/**
 * A Cron Job to clean up old LastUpdateTimeEntityModel.
 *
 */
public class OldLastUpdateTimeEntityRemovalJob extends AbstractJobPerformable<OldLastUpdateTimeEntityRemovalCronJobModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(OldLastUpdateTimeEntityRemovalJob.class);

	/** The time service. */
	@Resource(name = "timeService")
	private TimeService timeService;

	/** The b2b unit service. */
	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	private static final int DEFAULT_AGE = 180;

	private static final int DEFAULT_BATCH_SIZE = 1000;

	private static final int DEFAULT_MAX_BATCH_SIZE = 100000;

	final String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());

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
	public PerformResult perform(final OldLastUpdateTimeEntityRemovalCronJobModel job)
	{
		boolean status = true;
		if (clearAbortRequestedIfNeeded(job))
		{
			status = false;
			LOG.debug("The job is aborted.");
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
		try
		{
			//Reset age size if empty or invalid
			int age = DEFAULT_AGE;

			if (job.getAge() != null && job.getAge() > 0)
			{
				age = job.getAge().intValue();
			}
			else
			{
				LOG.warn("Age '" + job.getAge() + "' is invalid, set to default value '" + DEFAULT_AGE + "'");
			}

			//Reset batch size if empty or invalid
			int batchSize = DEFAULT_BATCH_SIZE;

			if (job.getBatchSize() != null && job.getBatchSize() > 0 && job.getBatchSize() < DEFAULT_MAX_BATCH_SIZE)
			{
				batchSize = job.getBatchSize().intValue();
			}
			else
			{
				LOG.warn("Batch size '" + job.getBatchSize() + "' is invalid, set to default value '" + DEFAULT_BATCH_SIZE + "'");
			}

			List<LastUpdateTimeEntityModel> lastUpdateTimeEntities;

			//Remove old lastUpdateTimeEntities data in batch considering the risk of returning a very large number of search results
			do
			{
				lastUpdateTimeEntities = b2bUnitService
						.findOldLastUpdateTimeEntities(new DateTime(timeService.getCurrentTime()).minusDays(age).toDate(), batchSize);
				modelService.removeAll(lastUpdateTimeEntities);

			}
			while (lastUpdateTimeEntities != null && !lastUpdateTimeEntities.isEmpty());

			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			status = false;
			LOG.error("Exception occurred during Old last update time entity cleanup", e);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
		finally
		{

			if (!status)
			{
				sabmCronJobStatus.sendJobStatusNotification(job.getCode(), "Aborted", timeStamp);
			}
		}
	}

}