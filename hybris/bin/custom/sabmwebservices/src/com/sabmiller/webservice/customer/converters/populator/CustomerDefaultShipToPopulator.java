/**
 *
 */
package com.sabmiller.webservice.customer.converters.populator;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;

import com.sabmiller.webservice.customer.Customer;
import com.sabmiller.webservice.customer.Customer.CustomerRelationship;
import com.sabmiller.webservice.customer.constants.CustomerImportConstants;


/**
 * @author joshua.a.antony
 *
 */
public class CustomerDefaultShipToPopulator implements Populator<Customer, B2BUnitData>
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
		for (final CustomerRelationship cr : ListUtils.emptyIfNull(source.getCustomerRelationship()))
		{
			if (CustomerImportConstants.SHIP_TO.getCode().equals(cr.getPartnerCode())
					&& BooleanUtils.isTrue(cr.isDefaultPartner()))
			{
				target.setDefaultShipTo(cr.getID().getValue());
				break;
			}
		}
		LOG.debug("The default ship to is : " + target.getDefaultShipTo());
	}


}
