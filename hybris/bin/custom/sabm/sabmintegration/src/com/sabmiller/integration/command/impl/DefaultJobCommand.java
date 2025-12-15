package com.sabmiller.integration.command.impl;

import de.hybris.platform.cronjob.model.CronJobModel;

import java.util.Collections;
import java.util.Map;

import com.sabmiller.integration.processor.Processor;
import com.sabmiller.integration.utils.Result;


/**
 * The Class DefaultJobCommand.
 *
 * @param <T>
 *           the generic type
 */
public class DefaultJobCommand<T> extends AbstractJobCommand<T>
{

	/** The processors. */
	private Map<Class<?>, Processor<T, Void>> processors = Collections.emptyMap();


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.integration.command.impl.AbstractJobCommand#processItem(java.lang.Object)
	 */
	@Override
	protected Result processItem(final T item)
	{
		final Processor<T, Void> processor = getProcessors().get(item.getClass());
		if (processor != null)
		{
			try
			{
				processor.process(item);
				return Result.SUCCESS;
			}
			catch (final Exception ex)
			{
				LOG.error("An exception occurred: " + ex, ex);
				return new Result(false, ex.getMessage());
			}
		}
		return new Result(false, "Missing processor, skip object: " + item);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.integration.command.impl.AbstractJobCommand#save(com.sabmiller.integration.utils.Result,
	 * java.lang.Object)
	 */
	@Override
	protected void save(final Result res, final T item)
	{
		LOG.debug("No save operation in this method for this job with result: {} and item: {}", res, item);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.integration.command.impl.AbstractJobCommand#postExecute(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */
	@Override
	protected void postExecute(final CronJobModel cronJobModel)
	{
		LOG.debug("No operation in this method for job {}", cronJobModel.getCode());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.integration.command.impl.AbstractJobCommand#finalizeExecute(boolean)
	 */
	@Override
	protected void finalizeExecute(final boolean result)
	{
		LOG.debug("No operation in this method for this job with result: {}", result);
	}

	/**
	 * Gets the processors.
	 *
	 * @return the processors
	 */
	protected Map<Class<?>, Processor<T, Void>> getProcessors()
	{
		return processors;
	}

	/**
	 * Sets the processors.
	 *
	 * @param processors
	 *           the processors
	 */
	public void setProcessors(final Map<Class<?>, Processor<T, Void>> processors)
	{
		this.processors = processors;
	}
}
