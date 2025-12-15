package com.apb.core.exception;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;


/**
 * The Class AsahiBusinessException.
 *
 * @see specify Asahi custom business exception.
 * @author Kuldeep.Singh1
 */
public class AsahiBusinessException extends RuntimeException
{

	@Serial
	private static final long serialVersionUID = 1L;
	private List<String> errorMessages = new ArrayList<String>();

	/**
	 * Instantiates a new business exception.
	 */
	public AsahiBusinessException()
	{
		super();
	}

	/**
	 * Instantiates a new business exception.
	 *
	 * @param message
	 *           the message
	 */
	public AsahiBusinessException(final String message)
	{
		super(message);
	}

	/**
	 * @param errorMessages
	 */
	public AsahiBusinessException(final List<String> errorMessages)
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
