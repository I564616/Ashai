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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.product.SabmUnitService;
import com.sabmiller.facades.complexdeals.data.ComplexDealData;
import com.sabmiller.facades.complexdeals.data.ComplexDealPreconditionData;


/**
 * @author joshua.a.antony
 *
 */
public class PreconditionReversePopulator implements Populator<ComplexDealData, DealModel>
{

	private static final Logger LOG = LoggerFactory.getLogger(PreconditionReversePopulator.class);

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "unitService")
	private SabmUnitService unitService;


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final ComplexDealData source, final DealModel target) throws ConversionException
	{
		final List<AbstractDealConditionModel> dealConditions = new ArrayList<AbstractDealConditionModel>();
		for (final ComplexDealPreconditionData eachPrecondition : source.getPreconditions())
		{
			final AbstractDealConditionModel model = populateDealCondition(source.getCode(), eachPrecondition);
			modelService.save(model);

			dealConditions.add(model);
		}

		target.getConditionGroup().setDealConditions(new ArrayList<AbstractDealConditionModel>(dealConditions));

		//LOG.info("In populate() Complex Deal Condition is {} ", ReflectionToStringBuilder.toString(dealConditions));
	}

	protected AbstractDealConditionModel populateDealCondition(final String dealCode,
			final ComplexDealPreconditionData preCondition)
	{
		if (StringUtils.isNotBlank(preCondition.getMaterial()))
		{
			return populateProductDealCondition(dealCode, preCondition);
		}
		return populateHierarchialDealCondition(dealCode, preCondition);
	}


	protected ProductDealConditionModel populateProductDealCondition(final String dealCode,
			final ComplexDealPreconditionData preCondition)
	{
		final ProductDealConditionModel productDealConditionModel = modelService.create(ProductDealConditionModel.class);
		productDealConditionModel.setProductCode(preCondition.getMaterial());
		productDealConditionModel.setMinQty(Double.valueOf(preCondition.getQuantity()).intValue());
		if (!StringUtils.isBlank(preCondition.getUom()))
		{
			productDealConditionModel.setUnit(unitService.getUnitForCode(preCondition.getUom()));
		}
		productDealConditionModel.setDealCode(dealCode);
		productDealConditionModel.setExclude(preCondition.isExclusion());
		productDealConditionModel.setMandatory(preCondition.isMandatory());
		productDealConditionModel.setSequenceNumber(
				preCondition.getSequenceNumber() != null ? Integer.parseInt(preCondition.getSequenceNumber()) : null);

		return productDealConditionModel;
	}

	protected ComplexDealConditionModel populateHierarchialDealCondition(final String dealCode,
			final ComplexDealPreconditionData preCondition)
	{
		final ComplexDealConditionModel model = modelService.create(ComplexDealConditionModel.class);
		model.setDealCode(dealCode);
		model.setBrand(preCondition.getBrand());
		model.setEmpties(preCondition.getEmpties());
		model.setEmptyType(preCondition.getEmptyType());
		model.setExclude(preCondition.isExclusion());
		model.setLine(preCondition.getLine());
		model.setMandatory(preCondition.isMandatory());
		model.setPresentation(preCondition.getPresentation());
		model.setProductCode(preCondition.getMaterial());
		model.setQuantity(Double.valueOf(preCondition.getQuantity()).intValue());
		model.setSequenceNumber(
				preCondition.getSequenceNumber() != null ? Integer.parseInt(preCondition.getSequenceNumber()) : null);
		if (!StringUtils.isBlank(preCondition.getUom()))
		{
			model.setUnit(unitService.getUnitForCode(preCondition.getUom()));
		}
		model.setVariety(preCondition.getVariety());

		return model;
	}

}
