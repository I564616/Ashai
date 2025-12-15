package com.sabmiller.integration.jobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.jalo.AbortCronJobException;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.beanutils2.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.integration.command.JobCommand;



/**
 * The Class AbstractJob.
 */
public class AbstractJob extends AbstractJobPerformable<CronJobModel>
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractJob.class);

	/** The Constant LAST_SUCCESS_TIME_PROPERTY. */
	private static final String LAST_SUCCESS_TIME_PROPERTY = "lastSuccessTime";

	/** The commands. */
	private List<JobCommand<CronJobModel>> commands;

	/** The model service. */
	@Resource(name = "modelService")
	private ModelService modelService;

	/** The user service. */
	@Resource(name = "userService")
	private UserService userService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */
	@Override
	public PerformResult perform(final CronJobModel cronJob)
	{

		LOG.info("Start Job {} with user {}", cronJob.getCode(), userService.getCurrentUser().getUid());
		CronJobStatus status = CronJobStatus.UNKNOWN;
		CronJobResult result = CronJobResult.UNKNOWN;

		final Date startTime = new Date();
		try
		{
			boolean commandResult = true;
			for (final JobCommand<CronJobModel> command : commands)
			{
				commandResult &= command.execute(cronJob);
				LOG.debug("Job '{} - {}' completed with status: {}", cronJob.getCode(), command, commandResult);
			}
			result = commandResult ? CronJobResult.SUCCESS : CronJobResult.FAILURE;
			status = CronJobStatus.FINISHED;
		}
		catch (final AbortCronJobException ex)
		{
			LOG.warn("Job '" + cronJob.getCode() + "' is aborted.", ex);
			result = CronJobResult.ERROR;
			status = CronJobStatus.ABORTED;
		}
		catch (final Exception ex)
		{
			LOG.error("Job '" + cronJob.getCode() + "' thows a generic exception " + ex.getMessage(), ex);
			result = CronJobResult.ERROR;
			status = CronJobStatus.FINISHED;
		}
		finally
		{
			clearAbortRequestedIfNeeded(cronJob);
			if ((result == CronJobResult.SUCCESS || result == CronJobResult.FAILURE)
					&& PropertyUtils.isWriteable(cronJob, LAST_SUCCESS_TIME_PROPERTY))
			{
				try
				{
					PropertyUtils.setProperty(cronJob, LAST_SUCCESS_TIME_PROPERTY, startTime);
				}
				catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
				{
					LOG.warn("Unable to set last success time property for cronjob: " + cronJob, e);
				}
				LOG.info("lastSuccessTime updated for job: {}", cronJob.getCode());
				modelService.save(cronJob);
			}
		}

		LOG.info("End Job {}", cronJob.getCode());
		return new PerformResult(result, status);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#isAbortable()
	 */
	@Override
	public boolean isAbortable()
	{
		return true;
	}

	/**
	 * Gets the commands.
	 *
	 * @return the commands
	 */
	public List<JobCommand<CronJobModel>> getCommands()
	{
		return commands;
	}

	/**
	 * Sets the commands.
	 *
	 * @param commands
	 *           the new commands
	 */
	public void setCommands(final List<JobCommand<CronJobModel>> commands)
	{
		this.commands = commands;
	}

}
