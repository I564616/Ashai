/**
 *
 */
package com.apb.core.cronjob.users;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.apb.integration.service.config.AsahiConfigurationService;
import com.apb.integration.user.service.AsahiUserIntegrationService;
import com.google.common.collect.Lists;
import com.sabmiller.core.b2b.services.SabmB2BCustomerService;


/**
 * @author GQ485VQ
 *
 */
public class AsahiSendAllUsersToSFJob extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOGGER = LogManager.getLogger(AsahiSendAllUsersToSFJob.class);

	private static final String USERS_BATCH_SIZE = "integration.salesforce.users.batchsize.sga";

	@Resource(name = "sabmB2BCustomerService")
	private SabmB2BCustomerService sabmB2BCustomerService;

	@Resource(name = "asahiUserIntegrationService")
	private AsahiUserIntegrationService asahiUserIntegrationService;

	@Resource(name = "asahiIconfigurationService")
	private AsahiConfigurationService asahiconfigurationService;

	@Override
	public PerformResult perform(final CronJobModel job)
	{
		final List<B2BCustomerModel> customersSendToSF = sabmB2BCustomerService.getAllALBCustomersToSFList();
		LOGGER.info("ALB users count for full load:"+customersSendToSF.size());
		final String batchSize = asahiconfigurationService.getString(USERS_BATCH_SIZE, "200");
		if (CollectionUtils.isNotEmpty(customersSendToSF))
		{
			final List<List<B2BCustomerModel>> customersSendToSFBatch = Lists.partition(customersSendToSF, Integer.valueOf(batchSize));
			for (final List<B2BCustomerModel> eachCustomersSendToSFBatch : customersSendToSFBatch)
			{
				LOGGER.info("Number of customers to be sent to SF:" + eachCustomersSendToSFBatch.size());
				asahiUserIntegrationService.sendUsersToSalesforce(eachCustomersSendToSFBatch);
			}

			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		else
		{
			LOGGER.info("No Customers found to send to SF hence not calling service");
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
	}

}
