/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.salesforcerestclient;

import de.hybris.platform.servicelayer.exceptions.BusinessException;


/**
 * Exception thrown if the cart cannot be modified.
 */
public class SABMSFIntegrationException extends BusinessException
{

	/**
	 * Instantiates a new SABM integration exception.
	 *
	 * @param message
	 *           the message
	 */
	public SABMSFIntegrationException(final String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new SABM integration exception.
	 *
	 * @param message
	 *           the message
	 * @param cause
	 *           the cause
	 */
	public SABMSFIntegrationException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
