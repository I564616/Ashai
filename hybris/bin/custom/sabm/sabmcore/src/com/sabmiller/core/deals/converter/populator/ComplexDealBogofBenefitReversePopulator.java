/**
 *
 */
package com.sabmiller.core.deals.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.product.SabmUnitService;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.facades.complexdeals.data.ComplexDealData;
import com.sabmiller.facades.complexdeals.data.ComplexDealResultFreeGoodData;


/**
 * @author joshua.a.antony
 *
 */

public class ComplexDealBogofBenefitReversePopulator implements Populator<ComplexDealData, DealModel>
{

	private static final Logger LOG = Logger.getLogger(ComplexDealBogofBenefitReversePopulator.class);

	@Resource(name = "catalogVersionDeterminationStrategy")
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "unitService")
	private SabmUnitService unitService;

	@Override
	public void populate(final ComplexDealData source, final DealModel target)
	{
		LOG.debug("Populatingthe deal benefits for  " + target + ". Source is " + ReflectionToStringBuilder.toString(source));

		final List<FreeGoodsDealBenefitModel> freeGoodBenefits = new ArrayList<FreeGoodsDealBenefitModel>();
		for (final ComplexDealResultFreeGoodData eachBenefit : source.getResultFreeGood())
		{
			final FreeGoodsDealBenefitModel freeGoodBenefitModel = findOrCreateDealBenefit(eachBenefit.getMaterial(), target);
			freeGoodBenefitModel.setProductCode(eachBenefit.getMaterial());
			freeGoodBenefitModel.setQuantity(Double.valueOf(eachBenefit.getQuantity()).intValue());
			freeGoodBenefitModel.setUnit(unitService.getUnitForCode(eachBenefit.getUom()));
			freeGoodBenefitModel.setProportionalFreeGood(eachBenefit.isFreeGoodsFlag());
			freeGoodBenefitModel.setProportionalAmount(eachBenefit.isAmounts());
			freeGoodBenefitModel.setSequenceNumber(eachBenefit.getSequenceNumber() != null ? SabmStringUtils.toInt(eachBenefit
					.getSequenceNumber()) : null);
			freeGoodBenefitModel.setScale(eachBenefit.getScale());

			modelService.save(freeGoodBenefitModel);

			freeGoodBenefits.add(freeGoodBenefitModel);
		}

		final List<AbstractDealBenefitModel> existingBenefits = target.getConditionGroup().getDealBenefits();
		final List<AbstractDealBenefitModel> benefits = new ArrayList<AbstractDealBenefitModel>();
		for (final AbstractDealBenefitModel benefitModel : ListUtils.emptyIfNull(existingBenefits))
		{
			benefits.add(benefitModel);
		}
		benefits.addAll(freeGoodBenefits);

		target.getConditionGroup().setDealBenefits(benefits);
	}


	protected FreeGoodsDealBenefitModel findOrCreateDealBenefit(final String material, final DealModel dealModel)
	{
		final FreeGoodsDealBenefitModel freeGoodBenefitModel = findDealBenefit(material, dealModel);
		return freeGoodBenefitModel != null ? freeGoodBenefitModel : modelService
				.<FreeGoodsDealBenefitModel> create(FreeGoodsDealBenefitModel.class);
	}



	protected FreeGoodsDealBenefitModel findDealBenefit(final String material, final DealModel dealModel)
	{
		if (dealModel.getConditionGroup() != null)
		{
			for (final AbstractDealBenefitModel dealBenefit : ListUtils.emptyIfNull(dealModel.getConditionGroup().getDealBenefits()))
			{
				if (dealBenefit instanceof FreeGoodsDealBenefitModel
						&& material.equals(((FreeGoodsDealBenefitModel) dealBenefit).getProductCode()))
				{
					return (FreeGoodsDealBenefitModel) dealBenefit;
				}
			}
		}
		return null;
	}

}
