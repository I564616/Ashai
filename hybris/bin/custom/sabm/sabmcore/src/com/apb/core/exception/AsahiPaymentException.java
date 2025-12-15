package com.apb.core.exception;

import java.io.Serial;
import java.util.List;


/**
 * Asahi Payment exception
 */
public class AsahiPaymentException extends Exception
{

	/**
	 *
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	private final List<String> errorMessages;

	/**
	 * @param message
	 * @param errorMessages
	 */
	public AsahiPaymentException(final String message, final List<String> errorMessages)
	{
		super(message);
		this.errorMessages = errorMessages;
	}

	/**
	 * @param errorMessages
	 */
	public AsahiPaymentException(final List<String> errorMessages)
	{
		this.errorMessages = errorMessages;
	}

	/**
	 * @return
	 */
	public List<String> getErrorMessages()
	{
		return errorMessages;
	}





}
