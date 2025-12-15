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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.deals.services.RepDrivenDealConditionStatusService;
import com.sabmiller.core.enums.RepDrivenDealStatus;
import com.sabmiller.core.model.OldRepDrivenDealConditionStatusRemovalCronJobModel;
import com.sabmiller.core.model.RepDrivenDealConditionStatusModel;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;

/**
 * @author bonnie
 *
 */
@IntegrationTest
public class OldRepDrivenDealConditionStatusRemovalJobTest extends ServicelayerTransactionalTest
{
	private static final Logger LOG = LoggerFactory.getLogger(OldRepDrivenDealConditionStatusRemovalJobTest.class);

	private static final int DEFAULT_MAX_AGE = 180;
	private static final int DEFAULT_MAX_BATCH_SIZE = 1000;

	private OldRepDrivenDealConditionStatusRemovalCronJobModel oldRepDrivenDealConditionStatusRemovalCronJob;

	@Resource
	private TimeService timeService;
	/*@Mock
	private ModelService modelService;*/
	@Resource
	private OldRepDrivenDealConditionStatusRemovalJob oldRepDrivenDealConditionStatusRemovalJob;
	@Resource
	private RepDrivenDealConditionStatusService repDrivenDealConditionStatusService;

	@Resource(name="modelService")
	private ModelService modelService;

	@Resource
	@Before
	public void setUp() throws Exception
	{
		/*oldRepDrivenDealConditionStatusRemovalCronJob = mock(OldRepDrivenDealConditionStatusRemovalCronJobModel.class);

		Mockito.when(oldRepDrivenDealConditionStatusRemovalCronJob.getAge()).thenReturn(DEFAULT_MAX_AGE);

		Mockito.when(oldRepDrivenDealConditionStatusRemovalCronJob.getBatchSize()).thenReturn(DEFAULT_MAX_BATCH_SIZE);

		modelService = Mockito.mock(ModelService.class);
		oldRepDrivenDealConditionStatusRemovalJob.setModelService(modelService);*/

		final ServicelayerJobModel job = modelService.create(ServicelayerJobModel.class);
		job.setCode("oldRepDrivenDealConditionStatusRemovalJob");
		job.setSpringId("oldRepDrivenDealConditionStatusRemovalJob");
		oldRepDrivenDealConditionStatusRemovalCronJob = new OldRepDrivenDealConditionStatusRemovalCronJobModel();
		oldRepDrivenDealConditionStatusRemovalCronJob.setAge(DEFAULT_MAX_AGE);
		oldRepDrivenDealConditionStatusRemovalCronJob.setBatchSize(DEFAULT_MAX_BATCH_SIZE);
		oldRepDrivenDealConditionStatusRemovalCronJob.setJob(job);
		modelService.save(oldRepDrivenDealConditionStatusRemovalCronJob);
	}

	/**
	 * No RepDrivenDealConditionStatus exist and test that no error happens
	 */
	@Test
	public void testNoUserAccessHistoryAtAll()
	{
		final PerformResult result = oldRepDrivenDealConditionStatusRemovalJob.perform(oldRepDrivenDealConditionStatusRemovalCronJob);

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

	}

	/**
	 * Test that RepDrivenDealConditionStatus max age date will not be removed
	 */
	@Test
	public void testNonOldRepDrivenDealConditionStatus()
	{
		this.createTestRepDrivenDealConditionStatus("0000001");

		final List<RepDrivenDealConditionStatusModel> oldRepDrivenDealConditionStatus = repDrivenDealConditionStatusService
				.findRepDrivenDealConditionStatus(new DateTime(timeService.getCurrentTime())
						.minusDays(oldRepDrivenDealConditionStatusRemovalCronJob.getAge()).toDate(),
						oldRepDrivenDealConditionStatusRemovalCronJob.getBatchSize());

		Assert.assertEquals(0, oldRepDrivenDealConditionStatus.size());

		final PerformResult result = oldRepDrivenDealConditionStatusRemovalJob.perform(oldRepDrivenDealConditionStatusRemovalCronJob);

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

		//test RepDrivenDealConditionStatus should not be removed
		final List<RepDrivenDealConditionStatusModel> repDrivenDealConditionStatus = repDrivenDealConditionStatusService
				.findRepDrivenDealConditionStatus(new Date(), 0);
		Assert.assertEquals(1, repDrivenDealConditionStatus.size());
	}

	/**
	 * Test if all old RepDrivenDealConditionStatus will be removed in batch
	 */
	@Test
	public void testRemoveOldRepDrivenDealConditionStatusInBatch()
	{
		/*Mockito.when(oldRepDrivenDealConditionStatusRemovalCronJob.getAge()).thenReturn(80);
		Mockito.when(oldRepDrivenDealConditionStatusRemovalCronJob.getBatchSize()).thenReturn(3);*/

		oldRepDrivenDealConditionStatusRemovalCronJob.setAge(80);
		oldRepDrivenDealConditionStatusRemovalCronJob.setBatchSize(3);


		createTestRepDrivenDealConditionStatus("0000001");
		createTestRepDrivenDealConditionStatus("0000002");
		createTestRepDrivenDealConditionStatus("0000003");
		createTestRepDrivenDealConditionStatus("0000004");
		createTestRepDrivenDealConditionStatus("0000005");
		createTestRepDrivenDealConditionStatus("0000006");
		createTestRepDrivenDealConditionStatus("0000007");
		createTestRepDrivenDealConditionStatus("0000008");

		final List<RepDrivenDealConditionStatusModel> oldRepDrivenDealConditionStatus = repDrivenDealConditionStatusService
				.findRepDrivenDealConditionStatus(new DateTime(timeService.getCurrentTime())
						.minusDays(oldRepDrivenDealConditionStatusRemovalCronJob.getAge()).toDate(),
						oldRepDrivenDealConditionStatusRemovalCronJob.getBatchSize());

		Assert.assertEquals(3, oldRepDrivenDealConditionStatus.size());

		//when(modelService.removeAll(oldRepDrivenDealConditionStatus)).then(modelService1.removeAll(oldRepDrivenDealConditionStatus));

		final PerformResult result = oldRepDrivenDealConditionStatusRemovalJob.perform(oldRepDrivenDealConditionStatusRemovalCronJob);

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

		//test user access should not be removed
		final List<RepDrivenDealConditionStatusModel> repDrivenDealConditionStatus = repDrivenDealConditionStatusService
				.findRepDrivenDealConditionStatus(new Date(), 0);
		Assert.assertEquals(8, repDrivenDealConditionStatus.size());
	}

	/**
	 * Test if all old user access history will be removed one time
	 */
	@Test
	public void testRemoveOldUserAccessHistory()
	{
		oldRepDrivenDealConditionStatusRemovalCronJob.setAge(80);
		oldRepDrivenDealConditionStatusRemovalCronJob.setBatchSize(0);
		createTestRepDrivenDealConditionStatus("0000001");
		createTestRepDrivenDealConditionStatus("0000002");
		createTestRepDrivenDealConditionStatus("0000003");
		createTestRepDrivenDealConditionStatus("0000004");
		createTestRepDrivenDealConditionStatus("0000005");
		createTestRepDrivenDealConditionStatus("0000006");
		createTestRepDrivenDealConditionStatus("0000007");
		createTestRepDrivenDealConditionStatus("0000008");

		final List<RepDrivenDealConditionStatusModel> oldRepDrivenDealConditionStatus = repDrivenDealConditionStatusService
				.findRepDrivenDealConditionStatus(new DateTime(timeService.getCurrentTime())
						.minusDays(oldRepDrivenDealConditionStatusRemovalCronJob.getAge()).toDate(),
						oldRepDrivenDealConditionStatusRemovalCronJob.getBatchSize());

		Assert.assertEquals(8, oldRepDrivenDealConditionStatus.size());

		final PerformResult result = oldRepDrivenDealConditionStatusRemovalJob.perform(oldRepDrivenDealConditionStatusRemovalCronJob);

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

		//test RepDrivenDealConditionStatu should not be removed
		final List<RepDrivenDealConditionStatusModel> repDrivenDealConditionStatus = repDrivenDealConditionStatusService
				.findRepDrivenDealConditionStatus(new Date(), 0);
		Assert.assertEquals(8, repDrivenDealConditionStatus.size());
	}

	/**
	 * @param string
	 * @return
	 */
	private RepDrivenDealConditionStatusModel createTestRepDrivenDealConditionStatus(final String dealConditionNumber)
	{
		final RepDrivenDealConditionStatusModel repDrivenDealConditionStatusModel = new RepDrivenDealConditionStatusModel();
		repDrivenDealConditionStatusModel.setAssignedTo("Unit1");
		//repDrivenDealConditionStatusModel.setChangedBy(Mockito.mock(UserModel.class));
		repDrivenDealConditionStatusModel.setDealConditionNumber(dealConditionNumber);
		repDrivenDealConditionStatusModel.setDate(new Date());
		repDrivenDealConditionStatusModel.setStatus(RepDrivenDealStatus.LOCKED);
		repDrivenDealConditionStatusModel.setCreationtime(new DateTime(timeService.getCurrentTime()).minusDays(100).toDate());
		modelService.save(repDrivenDealConditionStatusModel);
		return null;
	}
}
