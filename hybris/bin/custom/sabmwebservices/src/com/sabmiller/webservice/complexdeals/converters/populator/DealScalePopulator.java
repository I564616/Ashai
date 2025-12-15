/**
 *
 */
package com.sabmiller.webservice.complexdeals.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.sabmiller.facades.complexdeals.data.ComplexDealData;
import com.sabmiller.facades.complexdeals.data.DealScaleData;
import com.sabmiller.webservice.complexdeals.DealCondition;
import com.sabmiller.webservice.complexdeals.DealCondition.PreConditions.PreCoditionScale;


/**
 * @author joshua.a.antony
 */
public class DealScalePopulator implements Populator<DealCondition, ComplexDealData>
{

	@Override
	public void populate(final DealCondition source, final ComplexDealData target) throws ConversionException
	{
		final List<DealScaleData> dealScales = new ArrayList<DealScaleData>();
		for (final PreCoditionScale eachPreCondition : CollectionUtils.emptyIfNull(source.getPreConditions().getPreCoditionScale()))
		{
			final DealScaleData scaleData = new DealScaleData();
			scaleData.setScale(eachPreCondition.getScale());
			scaleData.setQuantityFrom(eachPreCondition.getQuantityFrom());
			scaleData.setQuantityTo(eachPreCondition.getQuantityTo());

			if (!contains(dealScales, scaleData))
			{
				dealScales.add(scaleData);
			}
		}
		target.setScales(dealScales);
	}

	private boolean contains(final List<DealScaleData> dealScales, final DealScaleData dsd)
	{
		for (final DealScaleData eachScale : dealScales)
		{
			if (eachScale.getScale().equals(dsd.getScale()))
			{
				return true;
			}
		}
		return false;
	}
}
