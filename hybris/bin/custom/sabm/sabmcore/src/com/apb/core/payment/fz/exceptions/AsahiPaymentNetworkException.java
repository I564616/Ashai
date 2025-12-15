package com.apb.core.payment.fz.exceptions;

/**
 * Represents a network/connectivity error
 */
public class AsahiPaymentNetworkException extends Exception
{
	/**
	* 
	*/
	public static final long serialVersionUID = 1;
	private String message;
	private Throwable ex;
	private boolean timeout = false;

	/**
	 * Initialises a new error
	 * 
	 * @param message
	 *           the error message
	 * @param timeout
	 *           indicates if this error was a result of a timeout
	 */
	public AsahiPaymentNetworkException(final String message, final boolean timeout)
	{
		this.message = message;
		this.timeout = timeout;
	}

	/**
	 * Initialises a new error with an encapsulated exception
	 * 
	 * @param message
	 *           the error message
	 * @param timeout
	 *           indicates if this error was a result of a timeout
	 * @param ex
	 *           the encapsulated exception
	 */
	public AsahiPaymentNetworkException(final String message, final boolean timeout, final Throwable ex)
	{
		super(message, ex);
		this.timeout = timeout;
	}

	/**
	 * Gets the message for the exception
	 * 
	 * @return the message
	 */
	@Override
	public String getMessage()
	{
		return this.message;
	}

	/**
	 * Gets the timeout indicator for the exception
	 * 
	 * @return indicate if the error was a result of a timeout
	 */
	public boolean getTimeout()
	{
		return this.timeout;
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
}
