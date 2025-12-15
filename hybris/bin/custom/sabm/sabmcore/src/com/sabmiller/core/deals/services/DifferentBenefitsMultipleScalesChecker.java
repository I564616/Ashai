/**
 *
 */
package com.sabmiller.core.deals.services;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.comparators.DealScaleComparator;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DealScaleModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;


/**
 * Deal 008 - Different benefits for deal with multiple scales and different quantity per scale
 */
public class DifferentBenefitsMultipleScalesChecker extends AbstractLostDealChecker<DealModel>
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DifferentBenefitsMultipleScalesChecker.class);

	/*
	 * To decide this deal is Deal 008
	 *
	 * @see com.sabmiller.core.deals.services.AbstractLostDealChecker#isThisDealType(com.sabmiller.core.model.DealModel)
	 */
	@Override
	public boolean isThisDealType(final DealModel deal)
	{
		if (DealTypeEnum.LIMITED.equals(deal.getDealType()))
		{
			return false;
		}
		final DealConditionGroupModel conditionGroup = deal.getConditionGroup();
		if (BooleanUtils.isNotTrue(conditionGroup.getMultipleScales()))
		{
			return false;
		}
		return !CollectionUtils.isEmpty(conditionGroup.getDealScales()) && (conditionGroup.getDealScales().size() > 1);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.AbstractLostDealChecker#isLostDeal(com.sabmiller.core.model.DealModel,
	 * de.hybris.platform.core.model.order.AbstractOrderEntryModel, int, de.hybris.platform.core.model.order.CartModel)
	 */
	@Override
	public boolean isLostDeal(final DealModel deal, final AbstractOrderEntryModel entry, final int newQuantity,
			final CartModel cart)
	{
		int oldTotal = 0;
		int newTotal = 0;

		final List<SABMAlcoholVariantProductMaterialModel> productsByDeal = getProductService().getProductsByDeal(deal);
		for (final AbstractOrderEntryModel cartEntry : cart.getEntries())
		{
			if (BooleanUtils.isNotTrue(cartEntry.getIsFreeGood()) && productsByDeal.contains(cartEntry.getProduct()))
			{
				oldTotal += cartEntry.getQuantity();
				newTotal += cartEntry.equals(entry) ? newQuantity : cartEntry.getQuantity();
			}
		}

		final List<DealScaleModel> dealScales = new ArrayList<>(deal.getConditionGroup().getDealScales());
		Collections.sort(dealScales, DealScaleComparator.INSTANCE);

		int oldRatio = 0;
		int newRatio = 0;
		final boolean minimumConditions = haveMinimumConditions(deal);
		if (minimumConditions && notMatchMinCondition(entry, newQuantity, cart, deal.getConditionGroup()))
		{
			return true;
		}

		for (final DealScaleModel scale : dealScales)
		{
			if (newTotal < scale.getFrom() && oldTotal >= scale.getFrom() && !minimumConditions)
			{
				return true;
			}

			if (oldTotal >= scale.getFrom())
			{
				oldRatio++;
			}

			if (newTotal >= scale.getFrom())
			{
				newRatio++;
			}
		}

		for (final AbstractDealConditionModel condition : deal.getConditionGroup().getDealConditions())
		{
			if (BooleanUtils.isTrue(condition.getExclude())
					|| (BooleanUtils.isNotTrue(condition.getMandatory()) && deal.getConditionGroup().getDealConditions().size() > 1))
			{
				continue;
			}

			if (condition instanceof ProductDealConditionModel)
			{
				final ProductModel product = getProductService()
						.getProductForCodeSafe(((ProductDealConditionModel) condition).getProductCode());
				if (product != null)
				{
					for (final AbstractOrderEntryModel cartEntry : cart.getEntries())
					{
						if (cartEntry.getProduct().equals(product))
						{
							if (cartEntry.getQuantity() / ((ProductDealConditionModel) condition).getMinQty() < oldRatio)
							{
								oldRatio = (int) (cartEntry.getQuantity() / ((ProductDealConditionModel) condition).getMinQty());
							}

							if (cartEntry.equals(entry) && newQuantity / ((ProductDealConditionModel) condition).getMinQty() < newRatio)
							{
								newRatio = newQuantity / ((ProductDealConditionModel) condition).getMinQty();
							}
						}
					}
				}
			}
			else if (condition instanceof ComplexDealConditionModel)
			{
				final List<SABMAlcoholVariantProductMaterialModel> hierarchyFilterExcluded = getProductService()
						.getProductByHierarchyFilterExcluded((ComplexDealConditionModel) condition);

				int conditionQty = 0;
				int newConditionQty = 0;
				for (final AbstractOrderEntryModel cartEntry : cart.getEntries())
				{
					if (hierarchyFilterExcluded.contains(cartEntry.getProduct()))
					{
						conditionQty += cartEntry.getQuantity();
						newConditionQty += cartEntry.equals(entry) ? newQuantity : cartEntry.getQuantity();
					}
				}

				if (conditionQty / ((ComplexDealConditionModel) condition).getQuantity() < oldRatio)
				{
					oldRatio = conditionQty / ((ComplexDealConditionModel) condition).getQuantity();
				}

				if (newConditionQty / ((ComplexDealConditionModel) condition).getQuantity() < newRatio)
				{
					newRatio = newConditionQty / ((ComplexDealConditionModel) condition).getQuantity();
				}
			}
		}

		return newRatio < oldRatio;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.AbstractLostDealChecker#isDeleteDeal(java.lang.Object,
	 * de.hybris.platform.core.model.order.AbstractOrderEntryModel, int, de.hybris.platform.core.model.order.CartModel)
	 */
	@Override
	public boolean isDeleteDeal(final DealModel deal, final AbstractOrderEntryModel entry, final int newQuantity,
			final CartModel cart)
	{
		int newTotal = 0;

		final List<SABMAlcoholVariantProductMaterialModel> productsByDeal = getProductService().getProductsByDeal(deal);
		for (final AbstractOrderEntryModel cartEntry : cart.getEntries())
		{
			if (BooleanUtils.isNotTrue(cartEntry.getIsFreeGood()) && productsByDeal.contains(cartEntry.getProduct()))
			{
				newTotal += cartEntry.equals(entry) ? newQuantity : cartEntry.getQuantity();
			}
		}

		final List<DealScaleModel> dealScales = new ArrayList<>(deal.getConditionGroup().getDealScales());
		Collections.sort(dealScales, DealScaleComparator.INSTANCE);

		int newRatio = 0;
		final boolean minimumConditions = haveMinimumConditions(deal);
		if (minimumConditions && notMatchMinCondition(entry, newQuantity, cart, deal.getConditionGroup()))
		{
			return true;
		}

		if (!minimumConditions && CollectionUtils.isNotEmpty(dealScales))
		{
			if (newTotal < dealScales.get(0).getFrom())
			{
				return true;
			}
		}

		for (final DealScaleModel scale : dealScales)
		{
			if (newTotal >= scale.getFrom())
			{
				newRatio++;
			}
			else
			{
				break;
			}
		}

		for (final AbstractDealConditionModel condition : deal.getConditionGroup().getDealConditions())
		{
			if (BooleanUtils.isTrue(condition.getExclude())
					|| (BooleanUtils.isNotTrue(condition.getMandatory()) && deal.getConditionGroup().getDealConditions().size() > 1))
			{
				continue;
			}

			if (condition instanceof ProductDealConditionModel)
			{
				final ProductModel product = getProductService()
						.getProductForCodeSafe(((ProductDealConditionModel) condition).getProductCode());
				if (product != null)
				{
					for (final AbstractOrderEntryModel cartEntry : cart.getEntries())
					{
						if (cartEntry.getProduct().equals(product))
						{
							if (cartEntry.equals(entry) && newQuantity / ((ProductDealConditionModel) condition).getMinQty() < newRatio)
							{
								newRatio = newQuantity / ((ProductDealConditionModel) condition).getMinQty();
							}
						}
					}
				}
			}
			else if (condition instanceof ComplexDealConditionModel)
			{
				final List<SABMAlcoholVariantProductMaterialModel> hierarchyFilterExcluded = getProductService()
						.getProductByHierarchyFilterExcluded((ComplexDealConditionModel) condition);

				int newConditionQty = 0;
				for (final AbstractOrderEntryModel cartEntry : cart.getEntries())
				{
					if (hierarchyFilterExcluded.contains(cartEntry.getProduct()))
					{
						newConditionQty += cartEntry.equals(entry) ? newQuantity : cartEntry.getQuantity();
					}
				}

				if (newConditionQty / ((ComplexDealConditionModel) condition).getQuantity() < newRatio)
				{
					newRatio = newConditionQty / ((ComplexDealConditionModel) condition).getQuantity();
				}
			}
		}

		return newRatio == 0;
	}

	protected boolean haveMinimumConditions(final DealModel deal)
	{
		if (CollectionUtils.isNotEmpty(deal.getConditionGroup().getDealConditions()))
		{
			for (final AbstractDealConditionModel condition : deal.getConditionGroup().getDealConditions())
			{
				if (BooleanUtils.isNotTrue(condition.getExclude()) && BooleanUtils.isTrue(condition.getMandatory()))
				{
					if (condition instanceof ProductDealConditionModel && ((ProductDealConditionModel) condition).getMinQty() > 0
							&& BooleanUtils.isTrue(condition.getMandatory()))
					{
						return true;
					}
				}
				else if (condition instanceof ComplexDealConditionModel && ((ComplexDealConditionModel) condition).getQuantity() > 0
						&& BooleanUtils.isTrue(condition.getMandatory()))
				{
					return true;
				}
			}
		}
		return false;
	}
}
