/**
 *
 */
package com.sabmiller.core.jobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.scripting.engine.ScriptExecutable;
import de.hybris.platform.scripting.engine.ScriptExecutionResult;
import de.hybris.platform.scripting.engine.ScriptingLanguagesService;
import de.hybris.platform.scripting.engine.content.ScriptContent;
import de.hybris.platform.scripting.engine.content.impl.ResourceScriptContent;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.util.Config;

import java.io.File;
import java.util.Date;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;


/**
 * @author Biswaranjan Sahu
 *
 */
/**
 * @FetchCustomerCUPJob : Fetch the prices form SAP for last week active user and update in Hybris.
 *
 */
public class FetchCustomerCUPJob extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(FetchCustomerCUPJob.class);

	private ScriptingLanguagesService scriptingLanguagesService;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

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
		final Date timeStampdate = new Date();
		final String timeStamp = timeStampdate.toGMTString();

		LOG.info("Inside perform method of Job Name : FetchCustomerCUPJob");
		boolean status = true;
		//b2bUnitService.importLastWeekCustomersCUP();

		try
		{
			final String directory = Config.getString("preloginGroovyScript.file.creation.directory", "File creation directory");

			final String groovyFile = directory + File.separator
					+ Config.getString("preloginGroovyScript.fileName", "Groovy File Name");

			final org.springframework.core.io.Resource resource = new FileSystemResource(groovyFile);

			final ScriptContent scriptContent = new ResourceScriptContent(resource);
			final ScriptExecutable executable = scriptingLanguagesService.getExecutableByContent(scriptContent);
			final ScriptExecutionResult result = executable.execute();
			LOG.info((String) result.getScriptResult());
		}
		catch (final Exception e)
		{
			status = false;
			LOG.error("Error occur during perform the FetchCustomerCUPJob");
		}
		if (clearAbortRequestedIfNeeded(cronJob))
		{
			LOG.info("The job is aborted.");
			status = false;
			sabmCronJobStatus.sendJobStatusNotification(cronJob.getCode(), "Job is Aborted", timeStamp);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}

		if (!status)
		{
			sabmCronJobStatus.sendJobStatusNotification(cronJob.getCode(), "Job is Aborted", timeStamp);
			return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
		}
		else
		{
			sabmCronJobStatus.sendJobStatusNotification(cronJob.getCode(), "performed Successfully", timeStamp);
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
	}

	@Override
	public boolean isAbortable()
	{
		return true;
	}

	/**
	 * @return the b2bUnitService
	 */
	public SabmB2BUnitService getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * @param b2bUnitService
	 *           the b2bUnitService to set
	 */
	public void setB2bUnitService(final SabmB2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	public void setScriptingLanguagesService(final ScriptingLanguagesService scriptingLanguagesService)
	{
		this.scriptingLanguagesService = scriptingLanguagesService;
	}
}
