/**
 *
 */
package com.sabmiller.core.deals.services;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.ProductDealConditionModel;


/**
 * Deal 001 - One or more SKUs with quantity per item.
 */
public class OneOrMoreSKUsPerItemChecker extends AbstractLostDealChecker<DealModel>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.AbstractLostDealChecker#isThisDealType(com.sabmiller.core.model.DealModel)
	 */
	/*
	 * To decide this deal is Deal 001
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
		if (BooleanUtils.isTrue(conditionGroup.getMultipleScales()))
		{
			return false;
		}
		else if (isAllProductCondition(conditionGroup.getDealConditions()))
		{
			if (conditionGroup.getScales().size() > 1)
			{
				return false;
			}
			else if (deal.getMaxConditionBaseValue() != null && deal.getMaxConditionBaseValue() > 0)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else
		{
			return false;
		}
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

		final DealConditionGroupModel conditionGroup = deal.getConditionGroup();
		final AbstractDealBenefitModel benefit = conditionGroup.getDealBenefits().get(0);
		if (BooleanUtils.isTrue(benefit.getProportionalAmount()) || BooleanUtils.isTrue(benefit.getProportionalFreeGood()))
		{
			// deal can be applied multiple times
			return isLostForAppliedMultipleTimes(entry, newQuantity, cart, conditionGroup);
		}
		//deal can be applied only once
		return isLostForAppliedOnce(entry, newQuantity, cart, conditionGroup);

	}

	/**
	 * Checks if is lost for applied once.
	 *
	 * @param entry
	 *           the entry
	 * @param newQuantity
	 *           the new quantity
	 * @param cart
	 *           the cart
	 * @param conditionGroup
	 *           the condition group
	 * @return true, if is lost for applied once
	 */
	private boolean isLostForAppliedOnce(final AbstractOrderEntryModel entry, final int newQuantity, final CartModel cart,
			final DealConditionGroupModel conditionGroup)
	{
		final List<AbstractDealConditionModel> conditions = conditionGroup.getDealConditions();
		for (final AbstractDealConditionModel condition : conditions)
		{
			if (condition instanceof ProductDealConditionModel)
			{
				final ProductDealConditionModel productCondition = (ProductDealConditionModel) condition;
				if (productCondition.getProductCode().equals(entry.getProduct().getCode()))
				{
					if (productCondition.getMinQty() > newQuantity && productCondition.getMinQty() <= entry.getQuantity())
					{
						return true;
					}
				}
			}
		}
		return false;
	}


	/**
	 * Checks if is lost for applied multiple times.
	 *
	 * @param entry
	 *           the entry
	 * @param newQuantity
	 *           the new quantity
	 * @param cart
	 *           the cart
	 * @param conditionGroup
	 *           the condition group
	 * @return true, if is lost for applied multiple times
	 */
	private boolean isLostForAppliedMultipleTimes(final AbstractOrderEntryModel entry, final int newQuantity, final CartModel cart,
			final DealConditionGroupModel conditionGroup)
	{
		final List<AbstractDealConditionModel> conditions = conditionGroup.getDealConditions();
		final List<AbstractOrderEntryModel> entries = cart.getEntries();
		long minTimes = Long.MAX_VALUE;
		long oldProductTimes = 0;
		long newProductTimes = 0;
		for (final AbstractDealConditionModel condition : conditions)
		{
			if (condition instanceof ProductDealConditionModel)
			{
				final ProductDealConditionModel productCondition = (ProductDealConditionModel) condition;
				for (final AbstractOrderEntryModel entryModel : entries)
				{
					if (entryModel.getIsFreeGood() != null && entryModel.getIsFreeGood())
					{
						continue;
					}

					if (productCondition.getProductCode().equals(entryModel.getProduct().getCode()) && entryModel != entry)
					{
						final long tempTime = entryModel.getQuantity() / productCondition.getMinQty();
						if (minTimes > tempTime)
						{
							minTimes = tempTime;
						}
					}
					if (productCondition.getProductCode().equals(entryModel.getProduct().getCode()) && entryModel == entry)
					{
						oldProductTimes = entryModel.getQuantity() / productCondition.getMinQty();
						newProductTimes = newQuantity / productCondition.getMinQty();
					}
				}
			}
		}

		return newProductTimes < oldProductTimes && newProductTimes < minTimes;
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
		final DealConditionGroupModel conditionGroup = deal.getConditionGroup();
		return isLostForAppliedOnce(entry, newQuantity, cart, conditionGroup);
	}

}
