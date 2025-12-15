package com.apb.storefront.forms;

import jakarta.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;


/**
 * APB specific login form with j_rememberMe attribute.
 */
public class ApbLoginForm
{
	@NotNull(message = "{general.required}")
	private String j_username; // NOSONAR
	@NotNull(message = "{general.required}")
	private String j_password; // NOSONAR

	private boolean j_rememberMe;



	/**
	 * @return j_rememberMe
	 */
	public boolean isJ_rememberMe()
	{
		return j_rememberMe;
	}

	/**
	 * @param j_rememberMe
	 */
	public void setJ_rememberMe(final boolean j_rememberMe)
	{
		this.j_rememberMe = j_rememberMe;
	}

	/**
	 * @return the j_username
	 */
	public String getJ_username() // NOSONAR  NOPMD
	{
		return j_username;
	}

	/**
	 * @param j_username
	 *           the j_username to set
	 */
	public void setJ_username(final String j_username) // NOSONAR NOPMD
	{
		if(StringUtils.isNotEmpty(j_username)){
			this.j_username = j_username.toLowerCase();
		}
	}

	/**
	 * @return the j_password
	 */
	public String getJ_password() // NOSONAR NOPMD
	{
		return j_password;
	}

	/**
	 * @param j_password
	 *           the j_password to set
	 */
	public void setJ_password(final String j_password) // NOSONAR NOPMD
	{
		this.j_password = j_password;
	}
}
