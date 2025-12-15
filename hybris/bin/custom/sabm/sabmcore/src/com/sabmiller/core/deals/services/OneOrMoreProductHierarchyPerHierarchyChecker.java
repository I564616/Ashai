/**
 *
 */
package com.sabmiller.core.deals.services;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;


/**
 * Deal 003 - One or more product hierarchy with quantity per hierarchy.
 */
public class OneOrMoreProductHierarchyPerHierarchyChecker extends AbstractLostDealChecker<DealModel>
{
	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.AbstractLostDealChecker#isThisDealType(com.sabmiller.core.model.DealModel)
	 */
	/*
	 * To decide this deal is Deal 003
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
		if (BooleanUtils.isTrue(conditionGroup.getMultipleScales()) && (conditionGroup.getDealScales().size() > 1
				|| conditionGroup.getDealScales().size() == 1 && conditionGroup.getDealScales().get(0).getFrom() > 0))
		{
			return false;
		}
		else if (!isAllProductCondition(conditionGroup.getDealConditions()))
		{
			return !hasExcludeCondition(conditionGroup.getDealConditions())
					&& !hasProductCondition(conditionGroup.getDealConditions());
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
		long minQty = Long.MAX_VALUE;
		final List<ProductModel> excludedProduct = getProductService().findExcludedProduct(conditions);
		for (final AbstractDealConditionModel condition : conditions)
		{

			List<ProductModel> productList = new ArrayList<>();
			if (condition instanceof ComplexDealConditionModel)
			{
				final ComplexDealConditionModel complexCondition = (ComplexDealConditionModel) condition;
				productList.addAll(getProductService().getProductByHierarchy(complexCondition.getLine(), complexCondition.getBrand(),
						complexCondition.getVariety(), complexCondition.getEmpties(), complexCondition.getEmptyType(),
						complexCondition.getPresentation()));
				productList = ListUtils.subtract(productList, excludedProduct);
				minQty = complexCondition.getQuantity();
			}

			if (productList.contains(entry.getProduct()))
			{
				for (final AbstractOrderEntryModel entryModel : entries)
				{
					if (entryModel.getIsFreeGood() != null && entryModel.getIsFreeGood())
					{
						continue;
					}

					if (productList.contains(entryModel.getProduct()))
					{
						oldTotal += entryModel.getQuantity();
						if (entryModel == entry)
						{
							newTotal += newQuantity;
						}
						else
						{
							newTotal += entryModel.getQuantity();
						}
					}
				}
				break;
			}
		}

		return (newTotal < minQty) && (oldTotal >= minQty);
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
		long oldTime = 0;//Max applied time for current brand
		long newTime = 0;//Max applied time for current brand after reduction
		long actualTimes = Long.MAX_VALUE;// actual times for this deal in cart
		final List<ProductModel> excludedProduct = getProductService().findExcludedProduct(conditions);
		for (final AbstractDealConditionModel condition : conditions)
		{
			List<ProductModel> productList = new ArrayList<>();
			if (condition instanceof ComplexDealConditionModel)
			{
				final ComplexDealConditionModel complexCondition = (ComplexDealConditionModel) condition;
				productList.addAll(getProductService().getProductByHierarchy(complexCondition.getLine(), complexCondition.getBrand(),
						complexCondition.getVariety(), complexCondition.getEmpties(), complexCondition.getEmptyType(),
						complexCondition.getPresentation()));
				productList = ListUtils.subtract(productList, excludedProduct);

				//get the applied times for current product
				if (productList.contains(entry.getProduct()))
				{
					long oldTotal = 0;
					long newTotal = 0;
					for (final AbstractOrderEntryModel entryModel : entries)
					{
						if (BooleanUtils.isTrue(entryModel.getIsFreeGood()))
						{
							continue;
						}

						if (productList.contains(entryModel.getProduct()))
						{
							oldTotal += entryModel.getQuantity();
							if (entryModel.equals(entry))
							{
								newTotal += newQuantity;
							}
							else
							{
								newTotal += entryModel.getQuantity();
							}
						}
					}
					oldTime = oldTotal / complexCondition.getQuantity();
					newTime = newTotal / complexCondition.getQuantity();
					if (actualTimes > oldTime)
					{
						actualTimes = oldTime;
					}
				}
				else
				{
					long total = 0;
					for (final AbstractOrderEntryModel entryModel : entries)
					{
						if (BooleanUtils.isTrue(entryModel.getIsFreeGood()))
						{
							continue;
						}

						if (productList.contains(entryModel.getProduct()))
						{
							total += entryModel.getQuantity();
						}
					}
					if (actualTimes > (total / complexCondition.getQuantity()))
					{
						actualTimes = total / complexCondition.getQuantity();
					}
				}
			}

		}
		//INC1122356	B2B AUS - Deal loss modal pop up in cart despite deal still applying
		return actualTimes > newTime && actualTimes < oldTime;
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
