/**
 *
 */
package com.sabmiller.webservice.customer.converters.populator;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.sabmiller.webservice.customer.Customer;
import com.sabmiller.webservice.customer.Customer.CustomerRelationship;
import com.sabmiller.webservice.customer.constants.CustomerImportConstants;
import com.sabmiller.webservice.importer.DataImportValidationException;


/**
 * @author joshua.a.antony
 *
 */
public class CustomerSoldToPopulator implements Populator<Customer, B2BUnitData>
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
		if (source.getCustomerRelationship() != null)
		{
			for (final CustomerRelationship cr : source.getCustomerRelationship())
			{
				if (CustomerImportConstants.SOLD_TO.getCode().equals(cr.getPartnerCode()))
				{
					LOG.debug("Found sold to " + cr.getID());
					target.setSoldTo(cr.getID().getValue());
					break;
				}
			}
		}
		if (!CustomerImportConstants.ALTERNATIVE_ADDRESS_PARTNER_ACCOUNT.getCode().equalsIgnoreCase(source.getAccountGroup())
				&& StringUtils.isBlank(target.getSoldTo()))
		{
			throw new DataImportValidationException("Sold to unvailable in the request. It is mandatory in Hybris !!!");
		}
	}

}
