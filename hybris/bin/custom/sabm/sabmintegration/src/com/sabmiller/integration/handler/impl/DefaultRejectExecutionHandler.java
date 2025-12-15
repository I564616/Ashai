package com.sabmiller.integration.handler.impl;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class DefaultRejectExecutionHandler.
 */
public class DefaultRejectExecutionHandler implements RejectedExecutionHandler
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRejectExecutionHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.RejectedExecutionHandler#rejectedExecution(java.lang.Runnable,
	 * java.util.concurrent.ThreadPoolExecutor)
	 */
	@Override
	public void rejectedExecution(final Runnable paramRunnable, final ThreadPoolExecutor paramThreadPoolExecutor)
	{
		LOG.warn("[rejectHandler] {} is rejected", paramRunnable.toString());
	}

}
