package com.sabmiller.integration.processor.exception;

import java.io.Serial;

/**
 * The Class ValidatorException.
 */
public class ValidatorException extends ProcessorException
{

	/** The Constant serialVersionUID. */
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new validator exception.
	 */
	public ValidatorException()
	{
		super();
	}

	/**
	 * Instantiates a new validator exception.
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
	protected ValidatorException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new validator exception.
	 *
	 * @param message
	 *           the message
	 * @param cause
	 *           the cause
	 */
	public ValidatorException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Instantiates a new validator exception.
	 *
	 * @param message
	 *           the message
	 */
	public ValidatorException(final String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new validator exception.
	 *
	 * @param cause
	 *           the cause
	 */
	public ValidatorException(final Throwable cause)
	{
		super(cause);
	}
}
