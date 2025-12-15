/**
 *
 */
package com.sabmiller.core.jobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.dao.StagingSABMRecommendationDao;
import com.sabmiller.core.model.StagingSABMRecommendationModel;


/**
 * @author Siddarth
 *
 */
public class StagingSABMRecommendationCleanUpPerformable extends AbstractJobPerformable<CronJobModel>
{

	@Resource
	private ModelService modelService;
	@Resource
	private StagingSABMRecommendationDao stagingSABMRecommendationDao;

	final String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());


	private static final Logger LOG = LoggerFactory.getLogger(StagingSABMRecommendationCleanUpPerformable.class);



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
		boolean status = true;
		if (clearAbortRequestedIfNeeded(cronJob))
		{
			status = false;
			LOG.debug("The job is aborted.");
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
		try
		{
			//Delete the current Stock Lines
			final List<StagingSABMRecommendationModel> existingRecords = stagingSABMRecommendationDao.getAllRecords();
			if (CollectionUtils.isNotEmpty(existingRecords))
			{
				LOG.info("Deleting existing Staging SABM Recommendations");
				LOG.debug("Deleting Number of Staging SABM Recommendations" + existingRecords.size());
				modelService.removeAll(existingRecords);
			}
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			status = false;
			LOG.error("Exception Deleting existing Staging SABM Recommendations", e);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
		finally
		{

			if (!status)
			{
				sabmCronJobStatus.sendJobStatusNotification(cronJob.getCode(), "Aborted", timeStamp);
			}
		}
	}
}
