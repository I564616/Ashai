/**
 *
 */
package com.sabmiller.core.deals.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DealScaleModel;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.facades.complexdeals.data.ComplexDealData;
import com.sabmiller.facades.complexdeals.data.DealScaleData;


/**
 * @author joshua.a.antony
 *
 */
public class DealScaleReversePopulator implements Populator<ComplexDealData, DealModel>
{

	private static final Logger LOG = LoggerFactory.getLogger(DealScaleReversePopulator.class);

	@Resource(name = "modelService")
	private ModelService modelService;


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final ComplexDealData source, final DealModel target) throws ConversionException
	{
		final List<DealScaleModel> dealScales = new ArrayList<DealScaleModel>();
		for (final DealScaleData eachDealScale : source.getScales())
		{
			final DealScaleModel model = modelService.create(DealScaleModel.class);
			model.setScale(eachDealScale.getScale());
			model.setFrom(eachDealScale.getQuantityFrom() != null ? SabmStringUtils.toInt(eachDealScale.getQuantityFrom()) : null);
			model.setTo(eachDealScale.getQuantityTo() != null ? SabmStringUtils.toInt(eachDealScale.getQuantityTo()) : null);

			modelService.save(model);

			dealScales.add(model);
		}

		target.getConditionGroup().setDealScales(dealScales);
		target.getConditionGroup().setMultipleScales(isMultipleScale(dealScales));

		if (!dealScales.isEmpty())
		{
			target.getConditionGroup().setScales(fromQtyScales(dealScales));
		}

		LOG.info("multipleScales : {} , Total deal scales  {} ", dealScales.size());
	}

	private List<Integer> fromQtyScales(final List<DealScaleModel> dealScales)
	{
		final List<Integer> fromQtyScales = new ArrayList<Integer>();
		for (final DealScaleModel eachScale : dealScales)
		{
			fromQtyScales.add(eachScale.getFrom());
		}
		return fromQtyScales;
	}

	private boolean isMultipleScale(final List<DealScaleModel> dealScale)
	{
		if (!dealScale.isEmpty())
		{
			if (dealScale.size() == 1 && dealScale.get(0).getFrom().equals(Integer.valueOf(1)))
			{
				return false;
			}
			return true;
		}
		return false;
	}

}
