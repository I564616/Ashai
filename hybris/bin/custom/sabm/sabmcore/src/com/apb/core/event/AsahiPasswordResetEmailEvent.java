/**
 * 
 */
package com.apb.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

import java.io.Serial;

/**
 * @author Saumya.Mittal1
 *
 */
public class AsahiPasswordResetEmailEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{

	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	
	private String token;
	
	private boolean orderAccess;
	
	private boolean payAccess;
	
	private String payerEmail;
	
	private String customerAccountName;

	/**
	 * @return the token
	 */
	public String getToken()
	{
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token)
	{
		this.token = token;
	}

	/**
	 * @return the payerEmail
	 */
	public String getPayerEmail()
	{
		return payerEmail;
	}

	/**
	 * @param payerEmail the payerEmail to set
	 */
	public void setPayerEmail(String payerEmail)
	{
		this.payerEmail = payerEmail;
	}

	/**
	 * @return the customerAccountName
	 */
	public String getCustomerAccountName()
	{
		return customerAccountName;
	}

	/**
	 * @param customerAccountName the customerAccountName to set
	 */
	public void setCustomerAccountName(String customerAccountName)
	{
		this.customerAccountName = customerAccountName;
	}

	/**
	 * @return the orderAccess
	 */
	public boolean getOrderAccess()
	{
		return orderAccess;
	}

	/**
	 * @param orderAccess the orderAccess to set
	 */
	public void setOrderAccess(boolean orderAccess)
	{
		this.orderAccess = orderAccess;
	}

	/**
	 * @return the payAccess
	 */
	public boolean getPayAccess()
	{
		return payAccess;
	}

	/**
	 * @param payAccess the payAccess to set
	 */
	public void setPayAccess(boolean payAccess)
	{
		this.payAccess = payAccess;
	}
	

}
