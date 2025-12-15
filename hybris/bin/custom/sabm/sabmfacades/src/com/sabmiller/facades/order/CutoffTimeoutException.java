/**
 *
 */
package com.sabmiller.facades.order;

/**
 * @author joshua.a.antony
 *
 */
public class CutoffTimeoutException extends RuntimeException
{
	public CutoffTimeoutException(final String msg)
	{
		super(msg);
	}

	public CutoffTimeoutException(final String msg, final Throwable e)
	{
		super(msg, e);
	}

}
