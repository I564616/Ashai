package com.sabmiller.integration.command.impl;

import de.hybris.platform.cronjob.jalo.AbortCronJobException;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.Transaction;

import java.util.Iterator;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.integration.command.JobCommand;
import com.sabmiller.integration.provider.DataSource;
import com.sabmiller.integration.strategy.CatalogVersionStrategy;
import com.sabmiller.integration.utils.MultiIterable;
import com.sabmiller.integration.utils.Result;


/**
 * The Class AbstractJobCommand is an abstract implementation of the interface JobCommand. It provides
 *
 * @param <T>
 *           the generic type
 */
public abstract class AbstractJobCommand<T> implements JobCommand<CronJobModel>
{

	/** The log. */
	protected final Logger LOG = LoggerFactory.getLogger(getClass());

	/** The Constant CRONJOB. */
	public static final String CRONJOB = "cronjob";

	/** The model service. */
	@Resource(name = "modelService")
	private ModelService modelService;

	/** The transaction size. */
	private int transactionSize = 1;

	/** The catalog version strategy. */
	private CatalogVersionStrategy catalogVersionStrategy;

	/** The data source provider. */
	private List<DataSource<T>> dataSourceProvider;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.integration.command.JobCommand#execute(java.lang.Object)
	 */
	@Override
	public boolean execute(final CronJobModel cronJobModel) throws Exception
	{
		boolean result = false;
		try
		{
			beforeExecute(cronJobModel);
			result = performExecute(cronJobModel);
			postExecute(cronJobModel);
		}
		finally
		{
			finalizeExecute(result);
		}
		return result;
	}

	/**
	 * Perform execute.
	 *
	 * @param cronJobModel
	 *           the cron job model
	 * @return true, if successful
	 * @throws AbortCronJobException
	 *            the abort cron job exception
	 */
	protected boolean performExecute(final CronJobModel cronJobModel) throws AbortCronJobException
	{
		LOG.debug("Job start with Transactionsize = {}", transactionSize);

		final Iterable<T> items = fetchData();

		//this value decide if the job finish with success or warning
		boolean successJob = true;

		int index = 0;

		final Iterator<T> itor = items.iterator();

		T item;

		Transaction transaction = null;

		if (transactionSize > 0 && itor.hasNext())
		{
			transaction = Transaction.current();
			transaction.begin();
		}
		else
		{
			LOG.debug("No Transaction will be created");
		}

		while (itor.hasNext())
		{
			item = itor.next();
			if (checkAbortRequested(cronJobModel))
			{
				throw new AbortCronJobException();
			}
			boolean success = true;
			try
			{
				LOG.debug("Next item is: {}", item);

				final Result res = processItem(item);

				LOG.debug("Result related to {} processing is {}", item, res);

				if (!res.isStatus())
				{
					reportError(res, item);
					successJob = false;
					success = false;
				}
				else if (transactionSize > 0)
				{
					LOG.debug("Save item {} with result {}", item, res);
					save(res, item);
				}
			}
			catch (final Exception ex)
			{
				LOG.error("Error in processing... ", ex);
				success = false;
			}
			finally
			{
				LOG.debug("Item: {}, is processing result success? {}", item, success);

				if (transaction != null && transactionSize > 0)
				{
					if (success)
					{
						if (++index % transactionSize == 0 || !itor.hasNext())
						{
							transaction.commit();
							LOG.debug("Transaction committed for job: {}. Open a new one", cronJobModel.getCode());
							if (itor.hasNext())
							{
								transaction.begin();
							}
						}
					}
					else
					{
						transaction.rollback();
						LOG.debug("Transaction rollbacked for job: {}. Open a new one", cronJobModel.getCode());
						if (itor.hasNext())
						{
							transaction.begin();
						}
					}
					LOG.debug("Begin new transaction for job: {}.", cronJobModel.getCode());
				}
			}
		}

		return successJob;
	}

	/**
	 * Before execute.
	 *
	 * @param cronJobModel
	 *           the cron job model
	 */
	protected void beforeExecute(final CronJobModel cronJobModel)
	{
		if (catalogVersionStrategy != null)
		{
			catalogVersionStrategy.setSessionCatalogVersion();
		}
	}

	/**
	 * Check abort requested.
	 *
	 * @param myCronJob
	 *           the my cron job
	 * @return true, if successful
	 */
	protected final boolean checkAbortRequested(final CronJobModel myCronJob)
	{
		modelService.refresh(myCronJob);
		return BooleanUtils.isTrue(myCronJob.getRequestAbort());
	}

	/**
	 * Post execute.
	 *
	 * @param cronJobModel
	 *           the cron job model
	 */
	protected abstract void postExecute(CronJobModel cronJobModel);

	/**
	 * Finalize execute.
	 *
	 * @param result
	 *           the result
	 */
	protected abstract void finalizeExecute(boolean result);

	/**
	 * Report error.
	 *
	 * @param res
	 *           the res
	 * @param item
	 *           the item
	 */
	protected void reportError(final Result res, final T item)
	{
		LOG.error("error with item: {}", item);
		LOG.error(res.getMessage());
	}

	/**
	 * Process item.
	 *
	 * @param item
	 *           the item
	 * @return the result
	 */
	protected abstract Result processItem(T item);

	/**
	 * Save.
	 *
	 * @param res
	 *           the res
	 * @param item
	 *           the item
	 */
	protected abstract void save(Result res, T item);

	/**
	 * Fetch data.
	 *
	 * @return the iterable
	 */
	protected Iterable<T> fetchData()
	{
		return new MultiIterable<T>(getDataSourceProvider());
	}

	/**
	 * Gets the data source provider.
	 *
	 * @return the data source provider
	 */
	protected List<DataSource<T>> getDataSourceProvider()
	{
		return dataSourceProvider;
	}

	/**
	 * Sets the data source provider.
	 *
	 * @param dataSourceProvider
	 *           the new data source provider
	 */
	public void setDataSourceProvider(final List<DataSource<T>> dataSourceProvider)
	{
		this.dataSourceProvider = dataSourceProvider;
	}

	/**
	 * Gets the transaction size.
	 *
	 * @return the transaction size
	 */
	protected int getTransactionSize()
	{
		return transactionSize;
	}

	/**
	 * Sets the transaction size.
	 *
	 * @param transactionSize
	 *           the new transaction size
	 */
	public void setTransactionSize(final int transactionSize)
	{
		this.transactionSize = transactionSize;
	}

	/**
	 * Sets the catalog version strategy.
	 *
	 * @param catalogVersionStrategy
	 *           the catalogVersionStrategy to set
	 */
	public void setCatalogVersionStrategy(final CatalogVersionStrategy catalogVersionStrategy)
	{
		this.catalogVersionStrategy = catalogVersionStrategy;
	}


}
