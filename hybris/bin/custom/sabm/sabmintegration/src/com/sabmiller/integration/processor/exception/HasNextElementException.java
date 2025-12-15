package com.sabmiller.integration.processor.exception;

import java.io.Serial;

/**
 * The Class HasNextElementException.
 */
public class HasNextElementException extends RuntimeException
{

	/** The Constant serialVersionUID. */
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new checks for next element exception.
	 */
	public HasNextElementException()
	{
		super();
	}

	/**
	 * Instantiates a new checks for next element exception.
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
	protected HasNextElementException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new checks for next element exception.
	 *
	 * @param message
	 *           the message
	 * @param cause
	 *           the cause
	 */
	public HasNextElementException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Instantiates a new checks for next element exception.
	 *
	 * @param message
	 *           the message
	 */
	public HasNextElementException(final String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new checks for next element exception.
	 *
	 * @param cause
	 *           the cause
	 */
	public HasNextElementException(final Throwable cause)
	{
		super(cause);
	}

}
