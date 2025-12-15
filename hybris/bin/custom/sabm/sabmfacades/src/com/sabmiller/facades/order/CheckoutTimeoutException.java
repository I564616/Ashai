/**
 *
 */
package com.sabmiller.facades.order;

/**
 * @author joshua.a.antony
 *
 */
public class CheckoutTimeoutException extends CartStateException
{

	public CheckoutTimeoutException(final String msg)
	{
		super(msg);
	}

	public CheckoutTimeoutException(final String msg, final Throwable e)
	{
		super(msg, e);
	}

}
