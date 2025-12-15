package com.sabmiller.integration.processor;


import de.hybris.platform.core.Registry;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.integration.processor.exception.ExecutionException;


/**
 * The Class AbstractThreadProcessor.
 *
 * @param <T>
 *           the generic type
 */
public abstract class AbstractThreadProcessor<T> extends AbstractProcessor<T, Void>
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractThreadProcessor.class);

	/** The thread pool executor. */
	private ThreadPoolExecutor threadPoolExecutor;


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.integration.processor.AbstractProcessor#execute(java.lang.Object)
	 */
	@Override
	protected Void execute(final T item) throws ExecutionException
	{
		if (getThreadPoolExecutor() != null && getThreadPoolExecutor().getQueue() != null
				&& getThreadPoolExecutor().getQueue().remainingCapacity() > 0)
		{
			LOG.debug("Executing processor '{}' in new thread", this.getClass());
			final Runnable runnable = new Runnable()
			{
				@Override
				public void run()
				{
					preExecuteProcessor(item);
				}
			};

			getThreadPoolExecutor().execute(runnable);
		}
		else
		{
			LOG.debug("Executing processor '{}' in same thread because the queue is full or the pool executor is null",
					this.getClass());
			executeProcessor(item);
		}

		return null;
	}

	/**
	 * Pre execute processor method used to activate the master tenant in a new thread.
	 *
	 * @param item
	 *           the item
	 */
	protected void preExecuteProcessor(final T item)
	{
		Registry.activateMasterTenant();

		executeProcessor(item);
	}

	/**
	 * Execute processor.
	 *
	 * @param item
	 *           the item
	 */
	protected abstract void executeProcessor(final T item);

	/**
	 * Gets the thread pool executor.
	 *
	 * @return the threadPoolExecutor
	 */
	public ThreadPoolExecutor getThreadPoolExecutor()
	{
		return threadPoolExecutor;
	}

	/**
	 * Sets the thread pool executor.
	 *
	 * @param threadPoolExecutor
	 *           the threadPoolExecutor to set
	 */
	public void setThreadPoolExecutor(final ThreadPoolExecutor threadPoolExecutor)
	{
		this.threadPoolExecutor = threadPoolExecutor;
	}


}
