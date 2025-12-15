/**
 *
 */
package com.sabmiller.webservice.customer.converters.populator;

import com.sabmiller.webservice.customer.Customer;
import com.sabmiller.webservice.customer.Customer.SalesOrgData;
import com.sabmiller.webservice.customer.constants.CustomerImportConstants;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.log4j.Logger;

import java.util.List;



/**
 * @author joshua.a.antony
 *
 */
public class CustomerPaymentTermsPopulator implements Populator<Customer, B2BUnitData>
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
		final List<SalesOrgData> salesOrgDataList = source.getSalesOrgData();
		if (salesOrgDataList != null && !salesOrgDataList.isEmpty())
		{
			final SalesOrgData salesOrgData = salesOrgDataList.get(0);
			target.setPaymentRequired(CustomerImportConstants.PAYMENT_REQUIRED.getCode().equals(salesOrgData.getTermsOfPaymentKey()));
		}
		LOG.debug("Is Payment Required ? " + target.isPaymentRequired());
	}

}
