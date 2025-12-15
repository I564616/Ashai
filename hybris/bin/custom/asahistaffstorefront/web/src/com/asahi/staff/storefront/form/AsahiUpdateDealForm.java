/**
 *
 */
package com.asahi.staff.storefront.form;

import java.util.List;


/**
 * @author Ranjith.Karuvachery
 *
 */
public class AsahiUpdateDealForm
{
	private List<String> dealsToRemove;
	private List<String> dealsToActivate;
	private List<String> customerEmails;
	private String dealsDetails;
	private String customerAccount;

	/**
	 * @return the dealsToRemove
	 */
	public List<String> getDealsToRemove()
	{
		return dealsToRemove;
	}

	/**
	 * @param dealsToRemove
	 *           the dealsToRemove to set
	 */
	public void setDealsToRemove(final List<String> dealsToRemove)
	{
		this.dealsToRemove = dealsToRemove;
	}

	/**
	 * @return the dealsToActivate
	 */
	public List<String> getDealsToActivate()
	{
		return dealsToActivate;
	}

	/**
	 * @param dealsToActivate
	 *           the dealsToActivate to set
	 */
	public void setDealsToActivate(final List<String> dealsToActivate)
	{
		this.dealsToActivate = dealsToActivate;
	}

	/**
	 * @return the customerEmails
	 */
	public List<String> getCustomerEmails()
	{
		return customerEmails;
	}

	/**
	 * @param customerEmails
	 *           the customerEmails to set
	 */
	public void setCustomerEmails(final List<String> customerEmails)
	{
		this.customerEmails = customerEmails;
	}

	/**
	 * @return the dealsDetails
	 */
	public String getDealsDetails()
	{
		return dealsDetails;
	}

	/**
	 * @param dealsDetails
	 *           the dealsDetails to set
	 */
	public void setDealsDetails(final String dealsDetails)
	{
		this.dealsDetails = dealsDetails;
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


}
