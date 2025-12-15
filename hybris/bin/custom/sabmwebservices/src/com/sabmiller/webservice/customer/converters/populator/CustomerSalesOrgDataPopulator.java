/**
 *
 */
package com.sabmiller.webservice.customer.converters.populator;

import com.sabmiller.facades.b2bunit.data.SalesOrgData;
import com.sabmiller.webservice.customer.Customer;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import org.apache.log4j.Logger;

import java.util.List;


/**
 * @author joshua.a.antony
 *
 */
public class CustomerSalesOrgDataPopulator implements Populator<Customer, B2BUnitData>
{

	private final Logger LOG = Logger.getLogger(this.getClass());

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.impl.AbstractConverter#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final Customer source, final B2BUnitData target)
	{
		final SalesOrgData salesOrgData = new SalesOrgData();
		final List<com.sabmiller.webservice.customer.Customer.SalesOrgData> salesOrgList = source.getSalesOrgData();
		if (salesOrgList != null && !salesOrgList.isEmpty())
		{
			final com.sabmiller.webservice.customer.Customer.SalesOrgData salesOrgWsData = salesOrgList.get(0);
			salesOrgData.setCustomerGroup(salesOrgWsData.getCustomerGroup());
			salesOrgData.setCustomerStatisticGroup(salesOrgWsData.getCustomerStatisticsGroup());
			salesOrgData.setSalesDistrictCode(salesOrgWsData.getSalesDistrictCode());
			salesOrgData.setSalesGroup(salesOrgWsData.getSalesGroup());
			salesOrgData.setSalesOfficeCode(salesOrgWsData.getSalesOfficeCode());
			salesOrgData.setGroupDescription(salesOrgWsData.getGroupDescription());
			salesOrgData.setPriceGroup(salesOrgWsData.getPriceGroup());
			salesOrgData.setShippingCondition(salesOrgWsData.getShippingConditions());
			target.setSalesOrgData(salesOrgData);

			LOG.debug("customerGroup : " + salesOrgData.getCustomerGroup() + " , customerStatisticGroup : "
					+ salesOrgData.getCustomerStatisticGroup() + "" + " , salesDistrictCode : " + salesOrgData.getSalesDistrictCode()
					+ " , salesGroup : " + salesOrgData.getSalesGroup() + " , " + "salesOfficeCode : "
					+ salesOrgData.getSalesOfficeCode() + " , groupDescription : " + salesOrgData.getGroupDescription());
		}
	}

}
