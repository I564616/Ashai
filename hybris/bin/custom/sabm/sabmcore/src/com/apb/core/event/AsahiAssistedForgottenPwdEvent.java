/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.apb.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

import java.io.Serial;


/**
 * The class works as a event for "Assisted forgotten password" 
 * functionality.
 */
public class AsahiAssistedForgottenPwdEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	private String token;

	/**
	 * Default constructor
	 */
	public AsahiAssistedForgottenPwdEvent()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 * 
	 * @param token
	 */
	public AsahiAssistedForgottenPwdEvent(final String token)
	{
		super();
		this.token = token;
	}

	/**
	 * @return the token
	 */
	public String getToken()
	{
		return token;
	}

	/**
	 * @param token
	 *           the token to set
	 */
	public void setToken(final String token)
	{
		this.token = token;
	}

}
