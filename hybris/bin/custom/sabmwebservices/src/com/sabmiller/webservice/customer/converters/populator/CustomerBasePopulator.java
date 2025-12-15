/**
 *
 */
package com.sabmiller.webservice.customer.converters.populator;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.converters.Populator;

import org.apache.log4j.Logger;

import com.sabmiller.webservice.customer.Customer;


/**
 * @author joshua.a.antony
 *
 */
public class CustomerBasePopulator implements Populator<Customer, B2BUnitData>
{
	private final Logger LOG = Logger.getLogger(this.getClass());

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.impl.AbstractConverter#populate(java.lang.Object, java.lang.Object)
	 */

	@Override
	public void populate(final Customer customer, final B2BUnitData target)
	{
		target.setUid(customer.getID().getValue());
		target.setAccountGroup(customer.getAccountGroup());
		target.setBlockReason(customer.getCustomerBlockingReasonName() != null ? customer.getCustomerBlockingReasonName()
				.getValue() : null);
		if (customer.getCommon() != null && customer.getCommon().getName() != null)
		{
			target.setName(customer.getCommon().getName().getFirstLineName());
		}
		if (customer.getSalesData() != null && !customer.getSalesData().isEmpty())
		{
			target.setDefaultDeliveryPlant(customer.getSalesData().get(0).getDefaultDeliveryPlant() != null ? customer
					.getSalesData().get(0).getDefaultDeliveryPlant().getValue() : null);
		}

		LOG.debug("uid : " + target.getUid() + " , accountGroup : " + target.getAccountGroup() + " , blockReason : "
				+ target.getBlockReason() + " , name : " + target.getName() + " , blockType : " + target.getBlockType()
				+ " , defaultDeliveryPlant : " + target.getDefaultDeliveryPlant());
	}
}
