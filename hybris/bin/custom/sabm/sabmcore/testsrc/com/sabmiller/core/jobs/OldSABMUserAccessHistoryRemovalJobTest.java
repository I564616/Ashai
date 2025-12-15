/**
 *
 */
package com.sabmiller.core.jobs;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.customer.service.SABMUserAccessHistoryService;
import com.sabmiller.core.model.OldSABMUserAccessHistoryRemovalCronJobModel;
import com.sabmiller.core.model.SABMUserAccessHistoryModel;

import junit.framework.Assert;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;

/**
 * @author bonnie
 *
 */
@IntegrationTest
public class OldSABMUserAccessHistoryRemovalJobTest extends ServicelayerTransactionalTest
{
	private static final Logger LOG = LoggerFactory.getLogger(OldSABMUserAccessHistoryRemovalJobTest.class);

	private static final int DEFAULT_MAX_AGE = 730;
	private static final int DEFAULT_MAX_BATCH_SIZE = 1000;
	private OldSABMUserAccessHistoryRemovalCronJobModel oldSABMUserAccessHistoryRemovalCronJob;

	@Resource
	private TimeService timeService;
	@Resource
	private ModelService modelService;
	@Resource
	private OldSABMUserAccessHistoryRemovalJob oldSABMUserAccessHistoryRemovalJob;
	@Resource
	private SABMUserAccessHistoryService userAccessHistoryService;


	@Before
	public void setUp() throws Exception
	{
		final ServicelayerJobModel job = modelService.create(ServicelayerJobModel.class);
		job.setCode("oldSABMUserAccessHistoryRemovalJob");
		job.setSpringId("oldSABMUserAccessHistoryRemovalJob");

		oldSABMUserAccessHistoryRemovalCronJob = new OldSABMUserAccessHistoryRemovalCronJobModel();
		oldSABMUserAccessHistoryRemovalCronJob.setAge(DEFAULT_MAX_AGE);
		oldSABMUserAccessHistoryRemovalCronJob.setBatchSize(DEFAULT_MAX_BATCH_SIZE);
		oldSABMUserAccessHistoryRemovalCronJob.setJob(job);
		modelService.save(oldSABMUserAccessHistoryRemovalCronJob);
	}

	/**
	 * No saved user access history exist and test that no error happens
	 */
	@Test
	public void testNoUserAccessHistoryAtAll()
	{
		final PerformResult result = oldSABMUserAccessHistoryRemovalJob.perform(oldSABMUserAccessHistoryRemovalCronJob);

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

	}

	/**
	 * Test that user access history exceeded max age date will not be removed
	 */
	@Test
	public void testNonOldUserAccessHistory()
	{
		final SABMUserAccessHistoryModel sabmUserAccessHistory = this.createTestUserAccessHistory("testUser1");

		final List<SABMUserAccessHistoryModel> oldUserAccessHistory = userAccessHistoryService.findOldUserAccessHistory(
				new DateTime(timeService.getCurrentTime()).minusDays(oldSABMUserAccessHistoryRemovalCronJob.getAge()).toDate(),
				oldSABMUserAccessHistoryRemovalCronJob.getBatchSize());

		Assert.assertEquals(0, oldUserAccessHistory.size());

		final PerformResult result = oldSABMUserAccessHistoryRemovalJob.perform(oldSABMUserAccessHistoryRemovalCronJob);

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

		//test user access should not be removed
		final List<SABMUserAccessHistoryModel> userAccessHistory = userAccessHistoryService.findOldUserAccessHistory(new Date(), 0);
		Assert.assertEquals(1, userAccessHistory.size());
	}

	/**
	 * Test if all old user access history will be removed in batch
	 */
	@Test
	public void testRemoveOldUserAccessHistoryInBatch()
	{
		oldSABMUserAccessHistoryRemovalCronJob.setAge(100);
		oldSABMUserAccessHistoryRemovalCronJob.setBatchSize(3);
		createTestUserAccessHistory("testUser1");
		createTestUserAccessHistory("testUser2");
		createTestUserAccessHistory("testUser3");
		createTestUserAccessHistory("testUser4");
		createTestUserAccessHistory("testUser5");
		createTestUserAccessHistory("testUser6");
		createTestUserAccessHistory("testUser7");
		createTestUserAccessHistory("testUser8");

		final List<SABMUserAccessHistoryModel> oldUserAccessHistory = userAccessHistoryService.findOldUserAccessHistory(
				new DateTime(timeService.getCurrentTime()).minusDays(oldSABMUserAccessHistoryRemovalCronJob.getAge()).toDate(),
				oldSABMUserAccessHistoryRemovalCronJob.getBatchSize());

		Assert.assertEquals(3, oldUserAccessHistory.size());

		final PerformResult result = oldSABMUserAccessHistoryRemovalJob.perform(oldSABMUserAccessHistoryRemovalCronJob);

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

		//test user access should not be removed
		final List<SABMUserAccessHistoryModel> userAccessHistory = userAccessHistoryService.findOldUserAccessHistory(new Date(), 0);
		Assert.assertEquals(8, userAccessHistory.size());
	}

	/**
	 * Test if all old user access history will be removed one time
	 */
	@Test
	public void testRemoveOldUserAccessHistory()
	{
		oldSABMUserAccessHistoryRemovalCronJob.setAge(100);
		oldSABMUserAccessHistoryRemovalCronJob.setBatchSize(0);
		createTestUserAccessHistory("testUser1");
		createTestUserAccessHistory("testUser2");
		createTestUserAccessHistory("testUser3");
		createTestUserAccessHistory("testUser4");
		createTestUserAccessHistory("testUser5");
		createTestUserAccessHistory("testUser6");
		createTestUserAccessHistory("testUser7");
		createTestUserAccessHistory("testUser8");

		final List<SABMUserAccessHistoryModel> oldUserAccessHistory = userAccessHistoryService.findOldUserAccessHistory(
				new DateTime(timeService.getCurrentTime()).minusDays(oldSABMUserAccessHistoryRemovalCronJob.getAge()).toDate(),
				oldSABMUserAccessHistoryRemovalCronJob.getBatchSize());

		Assert.assertEquals(8, oldUserAccessHistory.size());

		final PerformResult result = oldSABMUserAccessHistoryRemovalJob.perform(oldSABMUserAccessHistoryRemovalCronJob);

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

		//test user access should not be removed
		final List<SABMUserAccessHistoryModel> userAccessHistory = userAccessHistoryService.findOldUserAccessHistory(new Date(), 0);
		Assert.assertEquals(8, userAccessHistory.size());
	}

	/**
	 * @param string
	 * @return
	 */
	private SABMUserAccessHistoryModel createTestUserAccessHistory(final String uid)
	{
		final SABMUserAccessHistoryModel userAccessHistoryModel = new SABMUserAccessHistoryModel();
		userAccessHistoryModel.setUid(uid);
		userAccessHistoryModel.setPublicIPAddress("10.0.10.98");
		userAccessHistoryModel.setRememberMeEnabled(true);
		userAccessHistoryModel.setUserAgent("Mobile");
		userAccessHistoryModel.setCreationtime(new DateTime(timeService.getCurrentTime()).minusYears(1).toDate());
		modelService.save(userAccessHistoryModel);
		return null;
	}
}
