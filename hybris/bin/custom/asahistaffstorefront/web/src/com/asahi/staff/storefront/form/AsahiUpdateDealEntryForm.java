/**
 *
 */
package com.asahi.staff.storefront.form;

/**
 * @author Ranjith.Karuvachery
 *
 */
public class AsahiUpdateDealEntryForm
{
	/* deal Id */
	private String dealCode;

	/* customerAccount - b2bunit code */
	private String customerAccount;

	/* Option to save */
	private boolean activate;

	/**
	 * @return the dealCode
	 */
	public String getDealCode()
	{
		return dealCode;
	}

	/**
	 * @param dealCode
	 *           the dealCode to set
	 */
	public void setDealCode(final String dealCode)
	{
		this.dealCode = dealCode;
	}

	/**
	 * @return the customerAccount
	 */
	public String getCustomerAccount()
	{
		return customerAccount;
	}

	/**
	 * @param customerAccount
	 *           the customerAccount to set
	 */
	public void setCustomerAccount(final String customerAccount)
	{
		this.customerAccount = customerAccount;
	}

	/**
	 * @return the activate
	 */
	public boolean isActivate()
	{
		return activate;
	}

	/**
	 * @param activate
	 *           the activate to set
	 */
	public void setActivate(final boolean activate)
	{
		this.activate = activate;
	}



}
