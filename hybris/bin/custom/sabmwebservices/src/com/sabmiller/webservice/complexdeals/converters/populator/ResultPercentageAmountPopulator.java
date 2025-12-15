/**
 *
 */
package com.sabmiller.webservice.complexdeals.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.facades.complexdeals.data.ComplexDealData;
import com.sabmiller.facades.complexdeals.data.ComplexDealResultPercentAmountData;
import com.sabmiller.webservice.complexdeals.DealCondition;
import com.sabmiller.webservice.complexdeals.DealCondition.PercentageAndAmount;


/**
 * @author joshua.a.antony
 *
 */
public class ResultPercentageAmountPopulator implements Populator<DealCondition, ComplexDealData>
{

	private static final Logger LOG = LoggerFactory.getLogger(ResultPercentageAmountPopulator.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final DealCondition source, final ComplexDealData target) throws ConversionException
	{
		final List<ComplexDealResultPercentAmountData> discountResults = new ArrayList<ComplexDealResultPercentAmountData>();
		for (final PercentageAndAmount a : CollectionUtils.emptyIfNull(source.getPercentageAndAmount()))
		{
			final ComplexDealResultPercentAmountData result = new ComplexDealResultPercentAmountData();
			result.setAmounts(BooleanUtils.toBoolean(a.isAmounts()));
			result.setConditionRate(a.getConditionRate());
			result.setFreeGoodsFlag(BooleanUtils.toBoolean(a.isFreeGoodsFlag()));
			result.setHeaderResult(a.getHeaderResult());
			result.setScale(a.getScale());
			result.setSequenceNumber(a.getSequenceNumber());
			result.setUnit(a.getUnit());

			discountResults.add(result);

			//	LOG.info("In populate(). Added Discount : [{}] to the list ", ReflectionToStringBuilder.toString(discountResults));
		}
		target.setResultPercentAmount(discountResults);
	}

}
