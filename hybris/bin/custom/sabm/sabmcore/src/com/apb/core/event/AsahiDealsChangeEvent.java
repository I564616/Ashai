package com.apb.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.core.model.order.OrderModel;

import java.io.Serial;
import java.util.List;


/**
 * The Event Class represents Asahi Order Placed Event
 */
public class AsahiDealsChangeEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	/**
	 *
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	private List<String> customerEmailIds;
	private List<String> activatedDeals;
	private List<String> removedDeals;
	private String additionalDealDetails;
	

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}


	/**
	 * @return the customerEmailIds
	 */
	public List<String> getCustomerEmailIds()
	{
		return customerEmailIds;
	}


	/**
	 * @param customerEmailIds the customerEmailIds to set
	 */
	public void setCustomerEmailIds(List<String> customerEmailIds)
	{
		this.customerEmailIds = customerEmailIds;
	}


	/**
	 * @return the activatedDeals
	 */
	public List<String> getActivatedDeals()
	{
		return activatedDeals;
	}


	/**
	 * @param activatedDeals the activatedDeals to set
	 */
	public void setActivatedDeals(List<String> activatedDeals)
	{
		this.activatedDeals = activatedDeals;
	}


	/**
	 * @return the removedDeals
	 */
	public List<String> getRemovedDeals()
	{
		return removedDeals;
	}


	/**
	 * @param removedDeals the removedDeals to set
	 */
	public void setRemovedDeals(List<String> removedDeals)
	{
		this.removedDeals = removedDeals;
	}


	/**
	 * @return the additionalDealDetails
	 */
	public String getAdditionalDealDetails()
	{
		return additionalDealDetails;
	}


	/**
	 * @param additionalDealDetails the additionalDealDetails to set
	 */
	public void setAdditionalDealDetails(String additionalDealDetails)
	{
		this.additionalDealDetails = additionalDealDetails;
	}

}
