package com.sabmiller.core.jobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.io.IOException;
import java.security.NoSuchProviderException;

import jakarta.annotation.Resource;

import org.bouncycastle.openpgp.PGPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.report.service.WelcomeEmailSaleForceDataExportService;
import com.sabmiller.salesforcerestclient.SABMSFIntegrationException;


public class SalesforceSendCustomerWelcomeEmailJob extends AbstractJobPerformable<CronJobModel>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(SalesforceSendCustomerWelcomeEmailJob.class);


	@Resource(name = "welcomeEmailSaleForceDataExportService")
	WelcomeEmailSaleForceDataExportService welcomeEmailSaleForceDataExportService;

	@Override
	public PerformResult perform(final CronJobModel cronJob)
	{
		CronJobResult result = CronJobResult.SUCCESS;
		try
		{
			welcomeEmailSaleForceDataExportService.sendWelcomeEmail();
		}
		catch( final SABMSFIntegrationException e){
			result = CronJobResult.ERROR;
         LOG.error(e.getMessage(), e);
		}
		catch (final Exception e)
		{
			result = CronJobResult.ERROR;
         LOG.error(e.getMessage(), e);
		}
		
		LOG.info("SalesforceSendCustomerWelcomeEmailJob performed");		
		return new PerformResult(result, CronJobStatus.FINISHED);
	}


}