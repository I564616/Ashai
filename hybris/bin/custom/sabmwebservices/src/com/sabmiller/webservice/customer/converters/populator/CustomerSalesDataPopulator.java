/**
 *
 */
package com.sabmiller.webservice.customer.converters.populator;

import com.sabmiller.facades.b2bunit.data.SalesData;
import com.sabmiller.webservice.customer.Customer;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import org.apache.log4j.Logger;

import java.util.List;


/**
 * @author joshua.a.antony
 *
 */
public class CustomerSalesDataPopulator implements Populator<Customer, B2BUnitData>
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
		final List<Customer.SalesData> salesDataList = source.getSalesData();
		if (salesDataList != null && !salesDataList.isEmpty())
		{
			final Customer.SalesData salesWsDta = salesDataList.get(0);
			final SalesData salesData = new SalesData();
			salesData.setSalesOrgId(salesWsDta.getSalesOrganisation());
			salesData.setDefaultDeliveryPlant(salesWsDta.getDefaultDeliveryPlant() != null ? salesWsDta.getDefaultDeliveryPlant()
					.getValue() : null);
			salesData.setDeletionIndicator(salesWsDta.getDeletionIndicator());
			if (salesWsDta.getDivision() != null)
			{
				salesData.setDivision(salesWsDta.getDivision().getValue());
			}
			if (salesWsDta.getDistributionChannel() != null)
			{
				salesData.setDistributionChannel(salesWsDta.getDistributionChannel().getValue());
			}
			target.setSalesData(salesData);

			LOG.debug("salesOrgId : " + salesData.getSalesOrgId() + " , defaultDeliveryPlant : "
					+ salesData.getDefaultDeliveryPlant() + " , deletionIndicator : " + salesData.getDeletionIndicator() + " , "
					+ "division : " + salesData.getDivision() + " , distributionChannel : " + salesData.getDistributionChannel());

		}


	}



}
