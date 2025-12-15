/**
 *
 */
package com.sabmiller.core.customer.service.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.customer.service.SABMUserFlagService;


/**
 * The Class SABMUserFlagServiceImpl.
 */
public class SABMUserFlagServiceImpl implements SABMUserFlagService
{

	/** The b2b unit service. */
	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	/** The b2b commerce unit service. */
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	/** The cash only flag. */
	@Value(value = "${cart.customer.cashonly.flag:ONLCA}")
	private String cashOnlyFlag;

	/**
	 * Gets the b2b unit service.
	 *
	 * @return the b2bUnitService
	 */
	public SabmB2BUnitService getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * Sets the b2b unit service.
	 *
	 * @param b2bUnitService
	 *           the b2bUnitService to set
	 */
	public void setB2bUnitService(final SabmB2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.customer.service.SABMUserFlagService#isCashOnlyCustomer(de.hybris.platform.core.model.user.
	 * CustomerModel)
	 */
	public Boolean isCashOnlyCustomer()
	{
		final B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();

		if (b2bUnit != null && b2bUnit.getPayerId() != null)
		{
			final B2BUnitModel parentB2BUnitModel = b2bUnitService.findTopLevelB2BUnit(b2bUnit.getPayerId());

			if (parentB2BUnitModel != null && StringUtils.equalsIgnoreCase(cashOnlyFlag, parentB2BUnitModel.getCustomerFlag()))
			{
				return Boolean.TRUE;
			}

		}
		return Boolean.FALSE;
	}
}
