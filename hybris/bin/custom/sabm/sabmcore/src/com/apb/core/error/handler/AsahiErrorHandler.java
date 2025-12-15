package com.apb.core.error.handler;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchException;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.CleanupHelper;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.ErrorHandler;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessagingException;


public class AsahiErrorHandler extends ErrorHandler
{

	private static final Logger LOG = LoggerFactory.getLogger(AsahiErrorHandler.class);

	private CleanupHelper cleanupHelper;

	/**
	 * Point of entry for errors during processing routed to the error channel.
	 *
	 * @param exception
	 */
	@Override
	public void onError(final MessagingException exception)
	{
		LOG.error("unexpected exception caught", exception);
		if (exception.getCause() instanceof BatchException)
		{
			cleanupHelper.cleanup(((BatchException) exception.getCause()).getHeader(), true);
		}
	}


	/**
	 * Point of entry for errors during processing routed to the error channel.
	 *
	 * @param exception
	 */
	@Override
	public void onError(final IllegalStateException exception)
	{
		LOG.error("unexpected exception caught", exception);
		if (exception.getCause() instanceof BatchException)
		{
			cleanupHelper.cleanup(((BatchException) exception.getCause()).getHeader(), true);
		}
	}
}
