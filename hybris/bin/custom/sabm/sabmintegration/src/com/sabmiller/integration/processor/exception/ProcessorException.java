package com.sabmiller.integration.processor.exception;

import java.io.Serial;

/**
 * The Class ProcessorException.
 */
public abstract class ProcessorException extends Exception
{

	/** The Constant serialVersionUID. */
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new processor exception.
	 */
	public ProcessorException()
	{
		super();
	}

	/**
	 * Instantiates a new processor exception.
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
	protected ProcessorException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new processor exception.
	 *
	 * @param message
	 *           the message
	 * @param cause
	 *           the cause
	 */
	public ProcessorException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Instantiates a new processor exception.
	 *
	 * @param message
	 *           the message
	 */
	public ProcessorException(final String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new processor exception.
	 *
	 * @param cause
	 *           the cause
	 */
	public ProcessorException(final Throwable cause)
	{
		super(cause);
	}
}
