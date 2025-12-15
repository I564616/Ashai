/**
 *
 */
package com.sabmiller.commons.email.commons;

/**
 *
 */
public class EmailContextErrorException extends RuntimeException
{

	/**
	 * Email Context Error
	 *
	 * @param message
	 *           the message
	 */
	public EmailContextErrorException(final String message)
	{
		super(message);
	}


	/**
	 * Email Context Error
	 *
	 * @param message
	 *           the message
	 * @param cause
	 *           the case
	 */
	public EmailContextErrorException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

}
