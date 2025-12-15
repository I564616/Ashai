/**
 *
 */
package com.sabmiller.webservice.complexdeals.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.util.SABMFormatterUtils;
import com.sabmiller.facades.complexdeals.data.ComplexDealData;
import com.sabmiller.facades.complexdeals.data.ComplexDealPreconditionData;
import com.sabmiller.webservice.complexdeals.DealCondition;
import com.sabmiller.webservice.complexdeals.DealCondition.PreConditions.PreCondition;
import com.sabmiller.webservice.importer.DataImportValidationException;
import com.sabmiller.webservice.product.util.SapHybrisUnitOfMeasureMapper;


/**
 * @author joshua.a.antony
 *
 */
public class PreConditionPopulator implements Populator<DealCondition, ComplexDealData>
{

	private static final Logger LOG = LoggerFactory.getLogger(AssigneePopulator.class);

	@Resource(name = "sabFormatterUtil")
	private SABMFormatterUtils formatterUtils;

	@Resource(name = "sapHybrisUnitOfMeasureMapper")
	private SapHybrisUnitOfMeasureMapper mapper;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final DealCondition source, final ComplexDealData target) throws ConversionException
	{
		validate(source);

		final List<ComplexDealPreconditionData> preConditions = new ArrayList<ComplexDealPreconditionData>();

		for (final PreCondition eachPreCondition : CollectionUtils.emptyIfNull(source.getPreConditions().getPreCondition()))
		{
			final ComplexDealPreconditionData preconditionData = new ComplexDealPreconditionData();
			preconditionData.setBrand(eachPreCondition.getBrand());
			preconditionData.setEmpties(eachPreCondition.getEmpties());
			preconditionData.setEmptyType(eachPreCondition.getEmptyType());
			preconditionData.setExclusion(BooleanUtils.toBoolean(eachPreCondition.isExclusion()));
			preconditionData.setLine(eachPreCondition.getLine());
			preconditionData.setMandatory(BooleanUtils.toBoolean(eachPreCondition.isMandatory()));
			preconditionData.setMaterial(eachPreCondition.getMaterial());
			preconditionData.setPresentation(eachPreCondition.getPresent());
			preconditionData.setQuantity(eachPreCondition.getTargetQty());
			preconditionData.setSequenceNumber(eachPreCondition.getSequenceNumber());
			preconditionData.setUom(mapper.getHybrisUomCode(eachPreCondition.getUnitOfMeasure()));
			preconditionData.setVariety(eachPreCondition.getVariety());

			preConditions.add(preconditionData);

			//LOG.info("In populate(). Added precondition : [{}] to the list ", ReflectionToStringBuilder.toString(preconditionData));
		}

		target.setPreconditions(preConditions);
	}

	private void validate(final DealCondition source)
	{
		if (source.getPreConditions() == null)
		{
			throw new DataImportValidationException(
					"Deal : " + source.getConditionNumber() + ".Pre condition is empty. This is mandatory for Deal conditions!!!");
		}
	}

}
