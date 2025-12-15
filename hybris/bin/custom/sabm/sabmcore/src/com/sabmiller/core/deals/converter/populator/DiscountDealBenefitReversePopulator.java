/**
 *
 */
package com.sabmiller.core.deals.converter.populator;

import com.sabmiller.core.deals.vo.DealsResponse.DealItem;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DiscountDealBenefitModel;
import com.sabmiller.core.product.SabmUnitService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * @author joshua.a.antony
 *
 */

public class DiscountDealBenefitReversePopulator implements Populator<DealItem, DealModel>
{

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "unitService")
	private SabmUnitService unitService;

	@Override
	public void populate(final DealItem response, final DealModel target) throws ConversionException
	{
		response.setAmount(convertAmount(response.getAmount()));

		final DiscountDealBenefitModel discountBenefitModel = findOrCreateDealBenefit(response, target);
		discountBenefitModel.setAmount(Double.valueOf(response.getAmount().trim()));
		discountBenefitModel.setCurrency(!isPercentDiscount(response));
		discountBenefitModel.setSaleUnit(Double.valueOf(response.getSaleUnit().trim()).intValue());

		if (!StringUtils.isBlank(response.getUnitOfMeasure2()))
		{
			discountBenefitModel.setUnit(unitService.getUnitForCode(response.getUnitOfMeasure2()));
		}

		final List<AbstractDealBenefitModel> existingBenefits = target.getConditionGroup().getDealBenefits();
		final List<AbstractDealBenefitModel> benefits = new ArrayList<>();
		for (final AbstractDealBenefitModel benefitModel : ListUtils.emptyIfNull(existingBenefits))
		{
			benefits.add(benefitModel);
		}
		benefits.add(discountBenefitModel);

		target.getConditionGroup().setDealBenefits(benefits);
	}

	protected DiscountDealBenefitModel findOrCreateDealBenefit(final DealItem discountResponse, final DealModel dealModel)
	{
		final DiscountDealBenefitModel freeGoodBenefitModel = findDealBenefit(discountResponse, dealModel);
		return freeGoodBenefitModel != null ? freeGoodBenefitModel
				: modelService.<DiscountDealBenefitModel> create(DiscountDealBenefitModel.class);
	}



	protected DiscountDealBenefitModel findDealBenefit(final DealItem discountResponse, final DealModel dealModel)
	{
		if (dealModel.getConditionGroup() != null)
		{
			for (final AbstractDealBenefitModel dealBenefit : ListUtils.emptyIfNull(dealModel.getConditionGroup().getDealBenefits()))
			{
				if (dealBenefit instanceof DiscountDealBenefitModel
						&& isEqual(discountResponse, (DiscountDealBenefitModel) dealBenefit))
				{
					return (DiscountDealBenefitModel) dealBenefit;
				}
			}
		}
		return null;
	}

	private boolean isEqual(final DealItem discountResponse, final DiscountDealBenefitModel model)
	{
		return compare(discountResponse.getAmount(), model.getAmount()) && compare(discountResponse.getSaleUnit(), model.getSaleUnit())
				&& discountResponse.getUnitOfMeasure2().equals(model.getUnit().getCode())
				&& discountTypeEquals(discountResponse, model);

	}

    protected boolean compare(final String str, final Integer number){
        return StringUtils.isNotBlank(str) && number != null && number.equals(Integer.parseInt(str));
    }

	protected boolean compare(final String str, final Double dbl){
        return StringUtils.isNotBlank(str) && dbl != null && dbl.equals(Double.parseDouble(str));
    }

	private boolean discountTypeEquals(final DealItem discountResponse, final DiscountDealBenefitModel model)
	{
		return BooleanUtils.isTrue(model.getCurrency()) == !isPercentDiscount(discountResponse);
	}

	private boolean isPercentDiscount(final DealItem discountResponse)
	{
		return "%".equals(discountResponse.getUnit());
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
