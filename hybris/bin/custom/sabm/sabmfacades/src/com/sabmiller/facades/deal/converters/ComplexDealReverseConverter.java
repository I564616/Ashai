/**
 *
 */
package com.sabmiller.facades.deal.converters;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.facades.complexdeals.data.ComplexDealData;


/**
 * @author joshua.a.antony
 *
 */
public class ComplexDealReverseConverter implements Converter<ComplexDealData, DealModel>
{

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "dealsService")
	private DealsService dealsService;

	@Resource(name = "assignmentReversePopulator")
	Populator<ComplexDealData, DealModel> assignmentReversePopulator;

	@Resource(name = "preconditionReversePopulator")
	Populator<ComplexDealData, DealModel> preconditionReversePopulator;

	@Resource(name = "resultPercentAmountReversePopulator")
	Populator<ComplexDealData, DealModel> resultPercentAmountReversePopulator;

	@Resource(name = "resultFreeGoodReversePopulator")
	Populator<ComplexDealData, DealModel> resultFreeGoodReversePopulator;

	@Resource(name = "dealScaleReversePopulator")
	Populator<ComplexDealData, DealModel> dealScaleReversePopulator;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public DealModel convert(final ComplexDealData complexDealData) throws ConversionException
	{
		final DealModel dealModel = findOrCreateDeal(complexDealData.getCode());
		return convert(complexDealData, dealModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public DealModel convert(final ComplexDealData source, final DealModel target) throws ConversionException
	{
		populateCore(source, target);
		assignmentReversePopulator.populate(source, target);
		preconditionReversePopulator.populate(source, target);
		resultPercentAmountReversePopulator.populate(source, target);
		resultFreeGoodReversePopulator.populate(source, target);
		dealScaleReversePopulator.populate(source, target);

		target.setTriggerHash(String.valueOf(dealsService.generateTriggerHash(target)));

		modelService.save(target);

		return target;
	}


	protected DealModel findOrCreateDeal(final String dealCode)
	{
		final DealModel dealModel = dealsService.getDeal(String.valueOf(dealCode));
		return (dealModel != null) ? dealModel : modelService.<DealModel> create(DealModel.class);
	}

	private void populateCore(final ComplexDealData source, final DealModel target)
	{
		target.setCode(source.getCode());
		target.setDealType(DealTypeEnum.COMPLEX);
		target.setGroup(source.getGroup());
		target.setStatus(source.getStatus());
		target.setValidFrom(source.getValidFromDate());
		target.setValidTo(source.getValidToDate());
		target.setInStore(source.isInStore());
		target.setCustomerPOType(source.getCustomerPOType());

		final DealConditionGroupModel dealConditionGroup = modelService.create(DealConditionGroupModel.class);
		target.setConditionGroup(dealConditionGroup);
	}

}
