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
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.sabmiller.core.comparators.DealScaleComparator;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DealScaleModel;


/**
 * Deal 004 - Two or more product hierarchy with quantity across hierarchies.
 */
public class TwoOrMoreProductHierarchyAcrossChecker extends AbstractLostDealChecker<DealModel>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.AbstractLostDealChecker#isThisDealType(com.sabmiller.core.model.DealModel)
	 */
	/*
	 * To decide this deal is Deal 004
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
			if (!isAllProductCondition(conditionGroup.getDealConditions()))
			{
				return !hasExcludeCondition(conditionGroup.getDealConditions())
						&& !hasProductCondition(conditionGroup.getDealConditions());
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

		if (isLostCheckForComplexQuantity(entry, newQuantity, cart, conditionGroup))
		{
			return true;
		}

		final List<AbstractDealConditionModel> conditions = conditionGroup.getDealConditions();
		final List<ProductModel> brandProductList = getBrandProductList(conditions);

		final List<AbstractOrderEntryModel> entries = cart.getEntries();
		long oldTotal = 0;
		long newTotal = 0;
		for (final AbstractOrderEntryModel entryModel : entries)
		{
			if (entryModel.getIsFreeGood() != null && entryModel.getIsFreeGood())
			{
				continue;
			}

			if (!isBelongToBrand(entryModel.getProduct(), brandProductList))
			{
				continue;
			}
			oldTotal += entryModel.getQuantity();
			if (entry.equals(entryModel))
			{
				newTotal += newQuantity;
			}
			else
			{
				newTotal += entryModel.getQuantity();
			}
		}
		return newTotal < conditionGroup.getScales().get(0) && oldTotal >= conditionGroup.getScales().get(0);
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
		if (isLostCheckForComplexQuantity(entry, newQuantity, cart, conditionGroup))
		{
			return true;
		}
		final List<AbstractDealConditionModel> conditions = conditionGroup.getDealConditions();
		final List<ProductModel> brandProductList = getBrandProductList(conditions);
		final List<AbstractOrderEntryModel> entries = cart.getEntries();
		long oldTotal = 0;
		long newTotal = 0;
		for (final AbstractOrderEntryModel entryModel : entries)
		{
			if (entryModel.getIsFreeGood() != null && entryModel.getIsFreeGood())
			{
				continue;
			}

			if (CollectionUtils.isNotEmpty(brandProductList) && isBelongToBrand(entryModel.getProduct(), brandProductList))
			{
				oldTotal += entryModel.getQuantity();
				if (entry == entryModel)
				{
					newTotal += newQuantity;
				}
				else
				{
					newTotal += entryModel.getQuantity();
				}
			}
		}

		final List<DealScaleModel> dealScales = new ArrayList<>(conditionGroup.getDealScales());

		Collections.sort(dealScales, DealScaleComparator.INSTANCE);

		if (dealScales.get(0).getFrom() > 0 && (newTotal / dealScales.get(0).getFrom()) < (oldTotal / dealScales.get(0).getFrom()))
		{
			return true;
		}
		return false;
	}

	/**
	 * Gets the brand product list.
	 *
	 * @param conditions
	 *           the conditions
	 * @return the brand product list
	 */
	protected List<ProductModel> getBrandProductList(final List<AbstractDealConditionModel> conditions)
	{
		List<ProductModel> brandProductList = new ArrayList<>();
		for (final AbstractDealConditionModel condition : conditions)
		{
			if (condition instanceof ComplexDealConditionModel && BooleanUtils.isNotTrue(condition.getExclude()))
			{
				final ComplexDealConditionModel complexCondition = (ComplexDealConditionModel) condition;
				brandProductList.addAll(getProductService().getProductByHierarchy(complexCondition.getLine(),
						complexCondition.getBrand(), complexCondition.getVariety(), complexCondition.getEmpties(),
						complexCondition.getEmptyType(), complexCondition.getPresentation()));
			}
		}

		final List<ProductModel> excludedProduct = getProductService().findExcludedProduct(conditions);

		brandProductList = ListUtils.subtract(brandProductList, excludedProduct);
		return brandProductList;
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
