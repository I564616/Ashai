/**
 * 
 */
package com.sabmiller.storefront.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * @author dale.bryan.a.mercado
 *
 */
public class PersonalAssistanceForm
{
	private String request_type;
	private String account_no;
	private String customer_no;
	private String customer_name;
	private String user_email;

	/**
	 * @return the request_type
	 */
	@NotNull
	@Size(min = 1)
	public String getRequest_type()
	{
		return request_type;
	}

	/**
	 * @param request_type
	 *           the request_type to set
	 */
	public void setRequest_type(String request_type)
	{
		this.request_type = request_type;
	}

	/**
	 * @return the account_no
	 */
	public String getAccount_no()
	{
		return account_no;
	}

	/**
	 * @param account_no
	 *           the account_no to set
	 */
	public void setAccount_no(String account_no)
	{
		this.account_no = account_no;
	}

	/**
	 * @return the customer_no
	 */
	public String getCustomer_no()
	{
		return customer_no;
	}

	/**
	 * @param customer_no
	 *           the customer_no to set
	 */
	public void setCustomer_no(String customer_no)
	{
		this.customer_no = customer_no;
	}

	/**
	 * @return the customer_name
	 */
	public String getCustomer_name()
	{
		return customer_name;
	}

	/**
	 * @param customer_name
	 *           the customer_name to set
	 */
	public void setCustomer_name(String customer_name)
	{
		this.customer_name = customer_name;
	}

	/**
	 * @return the user_email
	 */
	public String getUser_email()
	{
		return user_email;
	}

	/**
	 * @param user_email
	 *           the user_email to set
	 */
	public void setUser_email(String user_email)
	{
		this.user_email = user_email;
	}


}
