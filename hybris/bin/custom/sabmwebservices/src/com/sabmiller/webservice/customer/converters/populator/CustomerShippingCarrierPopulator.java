/**
 *
 */
package com.sabmiller.webservice.customer.converters.populator;


import com.sabmiller.facades.b2bunit.data.ShippingCarrier;
import com.sabmiller.webservice.customer.Customer;
import com.sabmiller.webservice.customer.Customer.CustomerRelationship;
import com.sabmiller.webservice.customer.constants.CustomerImportConstants;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * @author joshua.a.antony
 *
 */
public class CustomerShippingCarrierPopulator implements Populator<Customer, B2BUnitData>
{
	private final Logger LOG = Logger.getLogger(this.getClass());

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final Customer source, final B2BUnitData target) throws ConversionException
	{
		final List<ShippingCarrier> shippingCarriers = new ArrayList<ShippingCarrier>();
		final List<CustomerRelationship> relationships = source.getCustomerRelationship();
		if (relationships != null)
		{
			for (final CustomerRelationship cr : filterShippingCarrierRelationships(relationships))
			{
				final ShippingCarrier shippingCarrier = new ShippingCarrier();
				shippingCarrier.setCode(cr.getID().getValue());
				shippingCarrier.setCustomerOwned(CustomerImportConstants.CARRIER_CUSTOMER_OWNED.getCode().equals(
						cr.getAccountGroupKey()));
				shippingCarrier.setShippingCondition(cr.getShippingCondition());
				if (cr.getName() != null)
				{
					shippingCarrier.setDescription(cr.getName().getFirstLineName());
				}

				shippingCarriers.add(shippingCarrier);

				if (cr.isDefaultPartner() != null && cr.isDefaultPartner())
				{
					target.setDefaultCarrier(shippingCarrier);
				}
			}
		}
		LOG.debug("Total shipping carriers : " + shippingCarriers.size());
		target.setShippingCarriers(shippingCarriers);
	}

	protected List<CustomerRelationship> filterShippingCarrierRelationships(final List<CustomerRelationship> crs)
	{
		final List<CustomerRelationship> crList = new ArrayList<Customer.CustomerRelationship>();
		for (final CustomerRelationship eachCustomerRelationship : crs)
		{
			if (CustomerImportConstants.CARRIER.getCode().equals(eachCustomerRelationship.getPartnerCode()))
			{
				crList.add(eachCustomerRelationship);
			}
		}
		return crList;
	}
}
