package com.sabmiller.integration.command.impl;

import de.hybris.platform.cronjob.model.CronJobModel;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.MapUtils;

import com.sabmiller.integration.processor.AbstractThreadProcessor;
import com.sabmiller.integration.processor.Processor;


/**
 * The Class ThreadPoolJobCommand use extend the DefaultJobCommand implementing multithread functionality.
 *
 * @param <T>
 *           the generic type
 */
public class ThreadPoolJobCommand<T> extends DefaultJobCommand<T>
{
	/** The core pool size. */
	private int corePoolSize;

	/** The maximum pool size. */
	private int maximumPoolSize;

	/** The keep alive time. */
	private long keepAliveTime;

	/** The max wait time. */
	private long maxWaitTime;

	/** The unit. */
	private TimeUnit unit;

	/** The max queue. */
	private int maxQueue;

	/** The thread factory. */
	private ThreadFactory threadFactory;

	/** The handler. */
	private RejectedExecutionHandler handler;

	/** The thread pool executor. */
	private ThreadPoolExecutor threadPoolExecutor;


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.integration.command.impl.AbstractJobCommand#beforeExecute(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */
	@Override
	protected void beforeExecute(final CronJobModel cronJobModel)
	{
		super.beforeExecute(cronJobModel);
		threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
				new ArrayBlockingQueue<Runnable>(maxQueue), threadFactory, handler);

		if (MapUtils.isNotEmpty(getProcessors()))
		{
			for (final Processor<T, Void> processor : getProcessors().values())
			{
				if (processor instanceof AbstractThreadProcessor<?>)
				{
					((AbstractThreadProcessor<?>) processor).setThreadPoolExecutor(threadPoolExecutor);
				}
			}
		}

		LOG.debug("Job: {} ThreadpoolExecutor created.", cronJobModel.getCode());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.integration.command.impl.DefaultJobCommand#postExecute(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */
	@Override
	protected void postExecute(final CronJobModel cronJobModel)
	{
		super.postExecute(cronJobModel);

		LOG.debug("Job: {} ThreadpoolExecutor shutting down...", cronJobModel.getCode());

		try
		{
			threadPoolExecutor.shutdown();
			threadPoolExecutor.awaitTermination(maxWaitTime, unit);

			LOG.debug("Job: {} ThreadpoolExecutor shutdown() and awaitTermination.", cronJobModel.getCode());
		}
		catch (final InterruptedException e)
		{
			LOG.debug("All tasks completed.");
		}
	}

	/**
	 * Sets the core pool size.
	 *
	 * @param corePoolSize
	 *           the new core pool size
	 */
	public void setCorePoolSize(final int corePoolSize)
	{
		this.corePoolSize = corePoolSize;
	}

	/**
	 * Sets the maximum pool size.
	 *
	 * @param maximumPoolSize
	 *           the new maximum pool size
	 */
	public void setMaximumPoolSize(final int maximumPoolSize)
	{
		this.maximumPoolSize = maximumPoolSize;
	}

	/**
	 * Sets the keep alive time.
	 *
	 * @param keepAliveTime
	 *           the new keep alive time
	 */
	public void setKeepAliveTime(final long keepAliveTime)
	{
		this.keepAliveTime = keepAliveTime;
	}

	/**
	 * Sets the max wait time.
	 *
	 * @param maxWaitTime
	 *           the new max wait time
	 */
	public void setMaxWaitTime(final long maxWaitTime)
	{
		this.maxWaitTime = maxWaitTime;
	}

	/**
	 * Sets the unit.
	 *
	 * @param unit
	 *           the new unit
	 */
	public void setUnit(final TimeUnit unit)
	{
		this.unit = unit;
	}

	/**
	 * Sets the max queue.
	 *
	 * @param maxQueue
	 *           the new max queue
	 */
	public void setMaxQueue(final int maxQueue)
	{
		this.maxQueue = maxQueue;
	}

	/**
	 * Sets the thread factory.
	 *
	 * @param threadFactory
	 *           the new thread factory
	 */
	public void setThreadFactory(final ThreadFactory threadFactory)
	{
		this.threadFactory = threadFactory;
	}

	/**
	 * Sets the handler.
	 *
	 * @param handler
	 *           the new handler
	 */
	public void setHandler(final RejectedExecutionHandler handler)
	{
		this.handler = handler;
	}

}
