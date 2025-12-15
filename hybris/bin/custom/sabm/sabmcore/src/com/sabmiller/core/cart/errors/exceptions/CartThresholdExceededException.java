/**
 *
 */
package com.sabmiller.core.cart.errors.exceptions;

import de.hybris.platform.servicelayer.exceptions.BusinessException;


/**
 * @author joshua.a.antony
 *
 */
public class CartThresholdExceededException extends BusinessException
{
	public CartThresholdExceededException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public CartThresholdExceededException(final String message)
	{
		super(message);
	}

	public CartThresholdExceededException(final Throwable cause)
	{
		super(cause);
	}
}
