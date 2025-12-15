/**
 *
 */
package com.apb.core.cronjob.users;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.apb.integration.service.config.AsahiConfigurationService;
import com.apb.integration.user.service.AsahiUserIntegrationService;
import com.sabmiller.core.b2b.services.SabmB2BCustomerService;


/**
 * @author GQ485VQ
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AsahiSendUsersToSFJobTest
{
	private static final String CUST_UID = "testUid";
	private static final String BATCH_SIZE = "200";

	@InjectMocks
	AsahiSendUsersToSFJob asahiSendUsersToSFJob = new AsahiSendUsersToSFJob();

	@Mock
	CronJobModel cronJob;
	@Mock
	AsahiUserIntegrationService asahiUserIntegrationService;
	@Mock
	B2BCustomerModel customer;
	@Mock
	SabmB2BCustomerService sabmB2BCustomerService;
	@Mock
	AsahiConfigurationService asahiconfigurationService;


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		Mockito.lenient().when(customer.getUid()).thenReturn(CUST_UID);
		Mockito.when(sabmB2BCustomerService.getALBCustomersToSFList()).thenReturn(Arrays.asList(customer));
		Mockito.when(asahiconfigurationService.getString(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(BATCH_SIZE);
	}

	@Test
	public void testSendUsersToSFWithCustomersList()
	{
		final PerformResult result = asahiSendUsersToSFJob.perform(cronJob);
		Mockito.verify(asahiUserIntegrationService).sendUsersToSalesforce(Arrays.asList(customer));
	}
}
