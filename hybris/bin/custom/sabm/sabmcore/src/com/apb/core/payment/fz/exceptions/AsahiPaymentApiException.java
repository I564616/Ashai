package com.apb.core.payment.fz.exceptions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * Represents an API exception from the gateway
 */
public class AsahiPaymentApiException extends Exception
{
	public static final long serialVersionUID = 1;
	private final List<String> messages;
	private Throwable ex;

	/**
	 * Initialises the error object
	 * 
	 * @param messages
	 *           error messages
	 */
	public AsahiPaymentApiException(final List<String> messages)
	{
		this.messages = messages;
	}

	/**
	 * Initialises the error object with an encapsulated error
	 * 
	 * @param messages
	 *           error messages
	 * @param ex
	 *           encapsulated exception
	 */
	public AsahiPaymentApiException(final List<String> messages, final Throwable ex)
	{
		this.messages = messages;
		this.ex = ex;
	}

	/**
	 * Gets the messages for the error
	 * 
	 * @return the messages
	 */
	public List<String> getMessages()
	{
		return this.messages;
	}

	/**
	 * Gets the inner exception for the error
	 * 
	 * @return encapsulated exception
	 */
	public Throwable getInnerException()
	{
		return this.ex;
	}

	/**
	 * Gets the messages as a joined string
	 * 
	 * @return
	 */
	@Override
	public String getMessage()
	{
		return join(messages, ", ");
	}

	/**
	 * Joins a list of strings in to a sentence
	 * 
	 * @param s
	 *           the collection of strings
	 * @param delimiter
	 *           the delimiter (e.g. ',')
	 * @return Joined string
	 */
	private static String join(final Collection<?> s, final String delimiter)
	{
		final StringBuilder builder = new StringBuilder();
		final Iterator<?> iter = s.iterator();
		while (iter.hasNext())
		{
			builder.append(iter.next());
			if (!iter.hasNext())
			{
				break;
			}
			builder.append(delimiter);
		}
		return builder.toString();
	}
}
