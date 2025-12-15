/**
 *
 */
package com.sabmiller.core.jobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.enums.RecommendationStatus;
import com.sabmiller.core.model.SABMRecommendationModel;
import com.sabmiller.core.model.SabmRecommendationCleanUpCronJobModel;
import com.sabmiller.core.recommendation.dao.SabmRecommendationDao;


/**
 * @author Siddarth
 *
 */
public class SabmRecommendationCleanUpPerformable extends AbstractJobPerformable<SabmRecommendationCleanUpCronJobModel>
{

	@Resource(name = "recommendationDao")
	private SabmRecommendationDao recommendationDao;

	@Resource
	private ModelService modelService;

	@Resource(name = "timeService")
	private TimeService timeService;

	@Resource(name = "sabmCronJobStatus")
	private SabmCronJobStatus sabmCronJobStatus;

	final String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());


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



	private static final Logger LOG = LoggerFactory.getLogger(SabmRecommendationCleanUpPerformable.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */
	//CRONJOB to Expire both Product and Deal Recommendations
	@Override
	public PerformResult perform(final SabmRecommendationCleanUpCronJobModel cronJob)
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
			expireRecommendations(cronJob.getNoOfDaysForExpiry());
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			status = false;
			LOG.error("Exception occurred during recommendations cleanup", e);
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

	private void expireRecommendations(final int noOfDaysForExpiry)
	{
		final List<SABMRecommendationModel> recommendationList = recommendationDao.getEligibleRecommendationsForExpiry(
				new DateTime(timeService.getCurrentTime()).minusDays(noOfDaysForExpiry).toDate());
		if (CollectionUtils.isNotEmpty(recommendationList))
		{
			LOG.debug("No of recommendations to be expired " + recommendationList.size());
			for (int i = 0; i < recommendationList.size(); i++)
			{
				recommendationList.get(i).setStatus(RecommendationStatus.EXPIRED);
			}
			modelService.saveAll(recommendationList);
		}

	}

}
