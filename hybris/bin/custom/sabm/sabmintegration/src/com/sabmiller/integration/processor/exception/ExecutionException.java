package com.sabmiller.integration.processor.exception;

import java.io.Serial;

/**
 * The Class ExecutionException.
 */
public class ExecutionException extends ProcessorException
{

	/** The Constant serialVersionUID. */
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new execution exception.
	 */
	public ExecutionException()
	{
		super();
	}

	/**
	 * Instantiates a new execution exception.
	 *
	 * @param message
	 *           the message
	 * @param cause
	 *           the cause
	 * @param enableSuppression
	 *           the enable suppression
	 * @param writableStackTrace
	 *           the writable stack trace
	 */
	protected ExecutionException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new execution exception.
	 *
	 * @param message
	 *           the message
	 * @param cause
	 *           the cause
	 */
	public ExecutionException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Instantiates a new execution exception.
	 *
	 * @param message
	 *           the message
	 */
	public ExecutionException(final String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new execution exception.
	 *
	 * @param cause
	 *           the cause
	 */
	public ExecutionException(final Throwable cause)
	{
		super(cause);
	}
}
