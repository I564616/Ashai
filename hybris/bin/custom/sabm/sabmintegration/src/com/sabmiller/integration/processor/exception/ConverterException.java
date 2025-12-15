package com.sabmiller.integration.processor.exception;

import java.io.Serial;

/**
 * The Class ConverterException.
 */
public class ConverterException extends RuntimeException
{

	/** The Constant serialVersionUID. */
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new converter exception.
	 */
	public ConverterException()
	{
		super();
	}

	/**
	 * Instantiates a new converter exception.
	 *
	 * @param message
	 *           the message
	 * @param cause
	 *           the cause
	 */
	public ConverterException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Instantiates a new converter exception.
	 *
	 * @param message
	 *           the message
	 */
	public ConverterException(final String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new converter exception.
	 *
	 * @param cause
	 *           the cause
	 */
	public ConverterException(final Throwable cause)
	{
		super(cause);
	}

	/**
	 * Instantiates a new converter exception.
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
	protected ConverterException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
