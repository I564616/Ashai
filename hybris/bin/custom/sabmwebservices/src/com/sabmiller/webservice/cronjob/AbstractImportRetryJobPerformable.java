/**
 *
 */
package com.sabmiller.webservice.cronjob;

import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import com.sabmiller.integration.service.MasterRecordService;
import com.sabmiller.webservice.model.ImportRetryJobModel;


/**
 * The Class AbstractImportRetryJobPerformable.
 */
public abstract class AbstractImportRetryJobPerformable extends AbstractJobPerformable<ImportRetryJobModel>
{

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public abstract String getUrl();

	/** The master record service. */
	private MasterRecordService masterRecordService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */
	@Override
	public PerformResult perform(final ImportRetryJobModel jobModel)
	{
		return masterRecordService.processRecords(jobModel.getImportList(), getUrl());
	}

	/**
	 * Sets the master record service.
	 *
	 * @param masterRecordService
	 *           the new master record service
	 */
	public void setMasterRecordService(final MasterRecordService masterRecordService)
	{
		this.masterRecordService = masterRecordService;
	}
}
