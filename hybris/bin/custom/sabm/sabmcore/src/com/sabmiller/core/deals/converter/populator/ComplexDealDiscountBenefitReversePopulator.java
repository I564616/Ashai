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

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DiscountDealBenefitModel;
import com.sabmiller.core.product.SabmUnitService;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.facades.complexdeals.data.ComplexDealData;
import com.sabmiller.facades.complexdeals.data.ComplexDealResultPercentAmountData;


/**
 * @author joshua.a.antony
 *
 */

public class ComplexDealDiscountBenefitReversePopulator implements Populator<ComplexDealData, DealModel>
{

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "unitService")
	private SabmUnitService unitService;

	@Override
	public void populate(final ComplexDealData response, final DealModel target) throws ConversionException
	{
		final List<DiscountDealBenefitModel> discountBenefits = new ArrayList<DiscountDealBenefitModel>();
		for (final ComplexDealResultPercentAmountData eachBenefit : response.getResultPercentAmount())
		{
			final DiscountDealBenefitModel discountBenefitModel = modelService
					.<DiscountDealBenefitModel> create(DiscountDealBenefitModel.class);
			discountBenefitModel.setAmount(Double.valueOf(convertAmount(eachBenefit.getConditionRate())));
			discountBenefitModel.setCurrency(!isPercentDiscount(eachBenefit));
			discountBenefitModel.setScale(eachBenefit.getScale());
			discountBenefitModel.setProportionalFreeGood(eachBenefit.isFreeGoodsFlag());
			discountBenefitModel.setProportionalAmount(eachBenefit.isAmounts());
			discountBenefitModel.setSequenceNumber(eachBenefit.getSequenceNumber() != null ? SabmStringUtils.toInt(eachBenefit
					.getSequenceNumber()) : null);
			//discountBenefitModel.setSaleUnit(Double.valueOf(eachBenefit.gets);
			modelService.save(discountBenefitModel);
			discountBenefits.add(discountBenefitModel);
		}

		final List<AbstractDealBenefitModel> existingBenefits = target.getConditionGroup().getDealBenefits();
		final List<AbstractDealBenefitModel> benefits = new ArrayList<>();
		for (final AbstractDealBenefitModel benefitModel : ListUtils.emptyIfNull(existingBenefits))
		{
			benefits.add(benefitModel);
		}
		benefits.addAll(discountBenefits);

		target.getConditionGroup().setDealBenefits(benefits);
	}

	private boolean isPercentDiscount(final ComplexDealResultPercentAmountData discountResponse)
	{
		return StringUtils.isBlank(discountResponse.getUnit());
	}


	private String convertAmount(final String amount)
	{
		if (StringUtils.endsWith(amount, "-"))
		{
			return "-" + amount.substring(0, amount.length() - 1);
		}
		return amount;
	}
}
