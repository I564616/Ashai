/**
 *
 */
package com.sabmiller.core.jobs;

import de.hybris.platform.commerceservices.dataimport.impl.CoreDataImportService;
import de.hybris.platform.commerceservices.setup.SetupSyncJobService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.SABMCatalogVersionSyncJobModel;


/**
 * This class SABMCatalogVersionSyncJob <br>
 * Synchronous [sabmProductCatalog, sabmContentCatalog]
 *
 * @author xue.zeng
 *
 */
public class SABMCatalogVersionSyncJob extends AbstractJobPerformable<SABMCatalogVersionSyncJobModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(SABMCatalogVersionSyncJob.class);

	@Resource
	private SetupSyncJobService setupSyncJobService;
	@Resource
	private CoreDataImportService coreDataImportService;

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




	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */
	@Override
	public PerformResult perform(final SABMCatalogVersionSyncJobModel cronJob)
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
			final boolean result = synchronizeCatalog();
			return new PerformResult(result ? CronJobResult.SUCCESS : CronJobResult.ERROR, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			status = false;
			LOG.error("Exception perform synchronizing catalogs", e);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
		}
		finally
		{

			if (!status)
			{
				sabmCronJobStatus.sendJobStatusNotification(cronJob.getCode(), "Aborted", timeStamp);
			}
		}
	}

	protected boolean synchronizeCatalog()
	{
		final boolean syncProduct = synchronizeCatalog("sabmProductCatalog");
		final boolean syncContent = synchronizeCatalog("sabmContentCatalog");
		return BooleanUtils.isTrue(syncProduct) && BooleanUtils.isTrue(syncContent);
	}

	protected boolean synchronizeCatalog(final String catalogName)
	{
		LOG.info("Begin synchronizing Catalog [{}]", catalogName);
		final PerformResult syncCronJobResult = setupSyncJobService.executeCatalogSyncJob(catalogName);
		if (coreDataImportService.isSyncRerunNeeded(syncCronJobResult))
		{
			LOG.warn("Error occurs during Catalog [{}] synchrozation.", catalogName);
			return false;
		}

		return true;
	}
}
