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
package com.apb.forms;

import jakarta.validation.constraints.NotNull;


/**
 * Pojo for 'customer rest password' form.
 */
public class CustomerResetPasswordForm
{
	private String uid;
	private String newPassword;
	private String checkNewPassword;

	public String getUid()
	{
		return uid;
	}

	public void setUid(final String uid)
	{
		this.uid = uid;
	}

	public String getNewPassword()
	{
		return newPassword;
	}

	public void setNewPassword(final String newPassword)
	{
		this.newPassword = newPassword;
	}

	@NotNull(message = "{profile.checkNewPassword.invalid}")
	public String getCheckNewPassword()
	{
		return checkNewPassword;
	}

	public void setCheckNewPassword(final String checkNewPassword)
	{
		this.checkNewPassword = checkNewPassword;
	}
}
