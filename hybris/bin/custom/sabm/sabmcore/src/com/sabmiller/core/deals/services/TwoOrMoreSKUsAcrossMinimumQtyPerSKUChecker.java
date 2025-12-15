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
 * Deal 007 - Two or more SKUs with quantity across the SKUs and minimum quantity per SKU.
 */
public class TwoOrMoreSKUsAcrossMinimumQtyPerSKUChecker extends AbstractLostDealChecker<DealModel>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.AbstractLostDealChecker#isThisDealType(com.sabmiller.core.model.DealModel)
	 */
	/*
	 * To decide this deal is Deal 007
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
			if (isAllProductCondition(conditionGroup.getDealConditions()))
			{
				final ProductDealConditionModel productCondition = (ProductDealConditionModel) conditionGroup.getDealConditions()
						.get(0);
				return productCondition.getMinQty() != null && productCondition.getMinQty() > 0;
			}
			return false;
		}
		return false;
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
		if ((benefit.getProportionalAmount() != null && benefit.getProportionalAmount())
				|| (benefit.getProportionalFreeGood() != null && benefit.getProportionalFreeGood()))
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
		final List<AbstractOrderEntryModel> entries = cart.getEntries();
		long oldTotal = 0;
		long newTotal = 0;
		if (notMatchMinCondition(entry, newQuantity, cart, conditionGroup))
		{
			return true;
		}
		for (final AbstractOrderEntryModel abstractOrderEntryModel : entries)
		{
			if (abstractOrderEntryModel.getIsFreeGood() != null && abstractOrderEntryModel.getIsFreeGood())
			{
				continue;
			}

			for (final AbstractDealConditionModel condition : conditions)
			{
				final ProductDealConditionModel productCondition = (ProductDealConditionModel) condition;
				if (productCondition.getProductCode().equals(abstractOrderEntryModel.getProduct().getCode()))
				{
					oldTotal += abstractOrderEntryModel.getQuantity();

					if (entry == abstractOrderEntryModel)
					{
						if (productCondition.getMinQty() > newQuantity)
						{
							return true;
						}
						newTotal += newQuantity;
					}
					else
					{
						newTotal += abstractOrderEntryModel.getQuantity();
					}
				}

			}
		}
		return (newTotal < conditionGroup.getScales().get(0)) && (oldTotal >= conditionGroup.getScales().get(0));
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
		long oldTotal = 0;
		long newTotal = 0;
		if (notMatchMinCondition(entry, newQuantity, cart, conditionGroup))
		{
			return true;
		}
		for (final AbstractOrderEntryModel abstractOrderEntryModel : entries)
		{
			if (abstractOrderEntryModel.getIsFreeGood() != null && abstractOrderEntryModel.getIsFreeGood())
			{
				continue;
			}

			for (final AbstractDealConditionModel condition : conditions)
			{
				final ProductDealConditionModel productCondition = (ProductDealConditionModel) condition;
				if (productCondition.getProductCode().equals(abstractOrderEntryModel.getProduct().getCode()))
				{
					oldTotal += abstractOrderEntryModel.getQuantity();

					if (entry == abstractOrderEntryModel)
					{
						if (productCondition.getMinQty() > newQuantity)
						{
							return true;
						}
						newTotal += newQuantity;
					}
					else
					{
						newTotal += abstractOrderEntryModel.getQuantity();
					}
				}

			}
		}
		return (newTotal / conditionGroup.getScales().get(0)) < (oldTotal / conditionGroup.getScales().get(0));
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
