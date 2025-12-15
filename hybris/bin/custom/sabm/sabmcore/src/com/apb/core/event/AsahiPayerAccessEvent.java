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

import com.sabmiller.core.model.AsahiSAMAccessModel;

import java.io.Serial;


/**
 * The class works as a event for "Payer Access" functionality.
 */
public class AsahiPayerAccessEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	/**
	 *
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	private String emailType;
	private AsahiSAMAccessModel access;

	/**
	 * Default constructor
	 */
	public AsahiPayerAccessEvent()
	{
		super();
	}

	/**
	 * @return type
	 */
	public String getEmailType()
	{
		return emailType;
	}

	/**
	 * @param emailType
	 */
	public void setEmailType(final String emailType)
	{
		this.emailType = emailType;
	}

	/**
	 * @return AsahiSAMAccessModel
	 */
	public AsahiSAMAccessModel getAccess()
	{
		return access;
	}

	/**
	 * @param access
	 */
	public void setAccess(final AsahiSAMAccessModel access)
	{
		this.access = access;
	}

}
