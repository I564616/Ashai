package com.sabmiller.integration.utils;

import java.io.Serial;
import java.io.Serializable;


/**
 * The Class Result.
 */
public class Result implements Serializable
{

	/** The Constant serialVersionUID. */
	@Serial
	private static final long serialVersionUID = 1L;

	/** The Constant SUCCESS. */
	public static final Result SUCCESS = new Result(true);

	/** The message. */
	private String message;

	/** The status. */
	private boolean status;

	/**
	 * Instantiates a new result.
	 *
	 * @param status
	 *           the status
	 */
	public Result(final boolean status)
	{
		this.status = status;
	}

	/**
	 * Instantiates a new result.
	 *
	 * @param status
	 *           the status
	 * @param message
	 *           the message
	 */
	public Result(final boolean status, final String message)
	{
		this.status = status;
		this.message = message;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param message
	 *           the new message
	 */
	public void setMessage(final String message)
	{
		this.message = message;
	}

	/**
	 * Checks if is status.
	 *
	 * @return true, if is status
	 */
	public boolean isStatus()
	{
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status
	 *           the new status
	 */
	public void setStatus(final boolean status)
	{
		this.status = status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Result [" + this.status + ", " + this.message + "]";
	}

}