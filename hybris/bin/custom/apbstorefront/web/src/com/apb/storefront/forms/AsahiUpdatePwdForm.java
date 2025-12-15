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
package com.apb.storefront.forms;

/**
 * Form object for updating the password.
 */
public class AsahiUpdatePwdForm
{
	private String pwd;
	private String checkPwd;
	private String token;


	/**
	 * @return the pwd
	 */

	public String getPwd()
	{
		return pwd;
	}

	/**
	 * @param pwd
	 *           the pwd to set
	 */
	public void setPwd(final String pwd)
	{
		this.pwd = pwd;
	}

	/**
	 * @return the checkPwd
	 */

	public String getCheckPwd()
	{
		return checkPwd;
	}

	/**
	 * @param checkPwd
	 *           the checkPwd to set
	 */
	public void setCheckPwd(final String checkPwd)
	{
		this.checkPwd = checkPwd;
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
