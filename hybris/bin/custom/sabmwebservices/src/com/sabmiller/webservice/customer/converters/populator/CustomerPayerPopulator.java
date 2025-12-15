/**
 *
 */
package com.sabmiller.webservice.customer.converters.populator;


import com.sabmiller.webservice.customer.Customer;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.sabmiller.webservice.customer.Customer.CustomerRelationship;
import com.sabmiller.webservice.customer.constants.CustomerImportConstants;
import com.sabmiller.webservice.importer.DataImportValidationException;


/**
 * @author joshua.a.antony
 *
 */
public class CustomerPayerPopulator implements Populator<Customer, B2BUnitData>
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
		final List<CustomerRelationship> relationships = source.getCustomerRelationship();
		if (relationships != null)
		{
			for (final CustomerRelationship cr : relationships)
			{
				if (CustomerImportConstants.PAYER.getCode().equals(cr.getPartnerCode()))
				{
					target.setPayerId(cr.getID().getValue());
					break;
				}
			}
		}
		LOG.debug("Payer Id is " + target.getPayerId());

		if (!CustomerImportConstants.ALTERNATIVE_ADDRESS_PARTNER_ACCOUNT.getCode().equalsIgnoreCase(source.getAccountGroup())
				&& StringUtils.isBlank(target.getPayerId()))
		{
			throw new DataImportValidationException("Payer Id unvailable in the request. It is mandatory in Hybris !!!");
		}
	}
}
