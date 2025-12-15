/**
 *
 */
package com.sabmiller.storefront.form;

/**
 * Form for Create User page.
 *
 * @author yuxiao.wang
 *
 */
public class SABMCreateUserForm
{
	private String firstName;
	private String surName;
	private String email;
	private String businessUnit;
	private String userRole;
	private String canPlaceOrder;
	private String canViewPayInvoice;
	private String orderLimit;
	private String phoneNumber;



	/**
	 *
	 * @return phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 *
	 * @param phoneNumber
	 */
	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * @param firstName
	 *           the firstName to set
	 */
	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * @return the surName
	 */
	public String getSurName()
	{
		return surName;
	}

	/**
	 * @param surName
	 *           the surName to set
	 */
	public void setSurName(final String surName)
	{
		this.surName = surName;
	}

	/**
	 * @return the email
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 * @param email
	 *           the email to set
	 */
	public void setEmail(final String email)
	{
		this.email = email;
	}

	/**
	 * @return the userRole
	 */
	public String getUserRole()
	{
		return userRole;
	}

	/**
	 * @param userRole
	 *           the userRole to set
	 */
	public void setUserRole(final String userRole)
	{
		this.userRole = userRole;
	}


	/**
	 * @return the canPlaceOrder
	 */
	public String getCanPlaceOrder()
	{
		return canPlaceOrder;
	}

	/**
	 * @param canPlaceOrder
	 *           the canPlaceOrder to set
	 */
	public void setCanPlaceOrder(final String canPlaceOrder)
	{
		this.canPlaceOrder = canPlaceOrder;
	}

	/**
	 * @return the canViewPayInvoice
	 */
	public String getCanViewPayInvoice()
	{
		return canViewPayInvoice;
	}

	/**
	 * @param canViewPayInvoice
	 *           the canViewPayInvoice to set
	 */
	public void setCanViewPayInvoice(final String canViewPayInvoice)
	{
		this.canViewPayInvoice = canViewPayInvoice;
	}

	/**
	 * @return the orderLimit
	 */
	public String getOrderLimit()
	{
		return orderLimit;
	}

	/**
	 * @param orderLimit
	 *           the orderLimit to set
	 */
	public void setOrderLimit(final String orderLimit)
	{
		this.orderLimit = orderLimit;
	}

	/**
	 * @return the businessUnit
	 */
	public String getBusinessUnit()
	{
		return businessUnit;
	}

	/**
	 * @param businessUnit
	 *           the businessUnit to set
	 */
	public void setBusinessUnit(final String businessUnit)
	{
		this.businessUnit = businessUnit;
	}


}
