/**
 *
 */
package com.sabmiller.webservice.customer.converters.populator;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.log4j.Logger;

import com.sabmiller.core.enums.BlockTypeEnum;
import com.sabmiller.webservice.customer.Customer;
import com.sabmiller.webservice.customer.constants.CustomerImportConstants;


/**
 * @author joshua.a.antony
 *
 */
public class CustomerBlockStatusPopulator implements Populator<Customer, B2BUnitData>
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
		if (source.getSalesData() != null && !source.getSalesData().isEmpty())
		{
			final String blockCode = source.getSalesData().get(0).getCustomerBlockingReasonCode();
			if (CustomerImportConstants.BLOCK_ACCOUNT.getCode().equals(blockCode))
			{
				target.setBlockType(BlockTypeEnum.BLOCK_ACCOUNT);
			}
			else if (CustomerImportConstants.BLOCK_CHECKOUT.getCode().equals(blockCode))
			{
				target.setBlockType(BlockTypeEnum.BLOCK_CHECKOUT);
			}
			target.setCustomerFlag(source.getSalesData().get(0).getCustomerFlag());
		}

		LOG.debug("Block Type : " + target.getBlockType());
	}

}
