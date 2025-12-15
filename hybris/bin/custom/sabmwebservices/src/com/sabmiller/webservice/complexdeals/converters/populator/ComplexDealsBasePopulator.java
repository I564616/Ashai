/**
 *
 */
package com.sabmiller.webservice.complexdeals.converters.populator;

import de.hybris.platform.converters.Populator;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.facades.complexdeals.data.ComplexDealData;
import com.sabmiller.webservice.complexdeals.DealCondition;



/**
 * @author joshua.a.antony
 *
 */
public class ComplexDealsBasePopulator implements Populator<DealCondition, ComplexDealData>
{

	private static final Logger LOG = LoggerFactory.getLogger(ComplexDealsBasePopulator.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.converters.impl.AbstractConverter#populate(java.lang.Object, java.lang.Object)
	 */

	@Override
	public void populate(final DealCondition source, final ComplexDealData target)
	{
		target.setCode(source.getConditionNumber());
		target.setValidFromDate(SabmDateUtils.toDate(source.getValidFrom()));
		target.setValidToDate(SabmDateUtils.toDate(source.getValidTo()));
		target.setDealType(source.getDealType());
		target.setStatus(source.getStatus());
		target.setMandatory(BooleanUtils.toBoolean(source.isMandatory()));
		target.setInStore(BooleanUtils.toBoolean(source.isInStore()));
		target.setCustomerPOType(source.getCustomerPOType());
		target.setGroup(source.getDealType());

		if (LOG.isDebugEnabled())
		{
			LOG.debug(
					"In populate(). After populating, code : {}, validFrom : {}, validTo : {}, dealType : {}, status : {}, mandatory : {}, inStore : {}, customerPOType : {} ",
					target.getCode(), SabmDateUtils.toFormattedString(target.getValidFromDate()),
					SabmDateUtils.toFormattedString(target.getValidToDate()), target.getDealType(), target.getStatus(),
					target.isMandatory(), target.isInStore(), target.getCustomerPOType());
		}
	}

}
