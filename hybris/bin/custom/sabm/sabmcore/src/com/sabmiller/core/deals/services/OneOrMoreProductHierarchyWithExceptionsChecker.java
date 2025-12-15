/**
 *
 */
package com.sabmiller.core.deals.services;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;


/**
 * Deal 005 - One or more product hierarchy with quantity per hierarchy with exceptions.
 */
public class OneOrMoreProductHierarchyWithExceptionsChecker extends AbstractLostDealChecker<DealModel>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.AbstractLostDealChecker#isThisDealType(com.sabmiller.core.model.DealModel)
	 */
	/*
	 * To decide this deal is Deal 005
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
			return hasExcludeCondition(conditionGroup.getDealConditions());
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
		final HashMap<String, List<SABMAlcoholVariantProductMaterialModel>> brandProductMap = new HashMap<String, List<SABMAlcoholVariantProductMaterialModel>>();
		fillBrandProductMap(brandProductMap, conditionGroup.getDealConditions());
		if ((benefit.getProportionalAmount() != null && benefit.getProportionalAmount())
				|| (benefit.getProportionalFreeGood() != null && benefit.getProportionalFreeGood()))
		{
			// deal can be applied multiple times
			return isLostForAppliedMultipleTimes(entry, newQuantity, cart, brandProductMap, conditionGroup.getDealConditions());
		}
		//deal can be applied only once
		return isLostForAppliedOnce(entry, newQuantity, cart, brandProductMap, conditionGroup.getDealConditions());
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
	 * @param brandProductMap
	 *           the brand product map
	 * @param list
	 *           the list
	 * @return true, if is lost for applied once
	 */
	private boolean isLostForAppliedOnce(final AbstractOrderEntryModel entry, final int newQuantity, final CartModel cart,
			final HashMap<String, List<SABMAlcoholVariantProductMaterialModel>> brandProductMap,
			final List<AbstractDealConditionModel> list)
	{
		final List<AbstractOrderEntryModel> entries = cart.getEntries();
		long oldTotal = 0;
		long newTotal = 0;
		for (final AbstractDealConditionModel condition : list)
		{
			if (condition instanceof ComplexDealConditionModel)
			{
				final ComplexDealConditionModel complexCondition = (ComplexDealConditionModel) condition;
				final List<SABMAlcoholVariantProductMaterialModel> productList = brandProductMap
						.get(complexCondition.getPk().getLongValueAsString());
				if (isBelongToBrand(entry.getProduct(), productList))
				{
					for (final AbstractOrderEntryModel entryModel : entries)
					{
						if (entryModel.getIsFreeGood() != null && entryModel.getIsFreeGood())
						{
							continue;
						}

						if (isBelongToBrand(entryModel.getProduct(), productList))
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
					if (oldTotal >= complexCondition.getQuantity() && newTotal < complexCondition.getQuantity())
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
	 * @param brandProductMap
	 *           the brand product map
	 * @param list
	 *           the list
	 * @return true, if is lost for applied multiple times
	 */
	private boolean isLostForAppliedMultipleTimes(final AbstractOrderEntryModel entry, final int newQuantity, final CartModel cart,
			final HashMap<String, List<SABMAlcoholVariantProductMaterialModel>> brandProductMap,
			final List<AbstractDealConditionModel> list)
	{
		final List<AbstractOrderEntryModel> entries = cart.getEntries();
		long oldTimes = 0;
		long newTimes = 0;
		long actuelTime = Long.MAX_VALUE;
		for (final AbstractDealConditionModel condition : list)
		{
			if (condition instanceof ComplexDealConditionModel)
			{
				final ComplexDealConditionModel complexCondition = (ComplexDealConditionModel) condition;
				final List<SABMAlcoholVariantProductMaterialModel> productList = brandProductMap.get(complexCondition.getBrand());
				if (isBelongToBrand(entry.getProduct(), productList))
				{
					//calculate current product
					long oldTotal = 0;
					long newTotal = 0;
					for (final AbstractOrderEntryModel entryModel : entries)
					{
						if (entryModel.getIsFreeGood() != null && entryModel.getIsFreeGood())
						{
							continue;
						}

						if (isBelongToBrand(entryModel.getProduct(), productList))
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
					oldTimes = oldTotal / complexCondition.getQuantity();
					newTimes = newTotal / complexCondition.getQuantity();
					if (actuelTime > oldTimes)
					{
						actuelTime = oldTimes;
					}
				}
				else
				{
					//calculate other products in deal condition
					long tempTotal = 0;
					for (final AbstractOrderEntryModel entryModel : entries)
					{
						if (entryModel.getIsFreeGood() != null && entryModel.getIsFreeGood())
						{
							continue;
						}

						if (isBelongToBrand(entryModel.getProduct(), productList))
						{
							tempTotal += entryModel.getQuantity();
						}
					}
					if (tempTotal / complexCondition.getQuantity() < actuelTime)
					{
						actuelTime = tempTotal / complexCondition.getQuantity();
					}
				}

			}
		}
		return newTimes < actuelTime;
	}

	/**
	 * Fill brand product map.
	 *
	 * @param brandProductMap
	 *           the brand product map
	 * @param dealConditions
	 *           the deal conditions
	 */
	private void fillBrandProductMap(final HashMap<String, List<SABMAlcoholVariantProductMaterialModel>> brandProductMap,
			final List<AbstractDealConditionModel> dealConditions)
	{
		for (final AbstractDealConditionModel abstractDealConditionModel : dealConditions)
		{
			if (abstractDealConditionModel instanceof ComplexDealConditionModel)
			{
				final ComplexDealConditionModel complexCondition = (ComplexDealConditionModel) abstractDealConditionModel;
				brandProductMap.put(complexCondition.getPk().getLongValueAsString(),
						new ArrayList<>(getProductService().getProductByHierarchy(complexCondition.getLine(),
								complexCondition.getBrand(), complexCondition.getVariety(), complexCondition.getEmpties(),
								complexCondition.getEmptyType(), complexCondition.getPresentation())));
			}
		}

		final List<ProductModel> excludeProductList = getProductService().findExcludedProduct(dealConditions);
		for (final List<SABMAlcoholVariantProductMaterialModel> productList : brandProductMap.values())
		{
			for (int i = productList.size() - 1; i >= 0; i--)
			{
				final SABMAlcoholVariantProductMaterialModel sabmAlcoholVariantProductMaterialModel = productList.get(i);
				for (final ProductModel product : excludeProductList)
				{
					if (sabmAlcoholVariantProductMaterialModel.getCode().equals(product.getCode()))
					{
						productList.remove(sabmAlcoholVariantProductMaterialModel);
					}
				}
			}
		}

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
		final HashMap<String, List<SABMAlcoholVariantProductMaterialModel>> brandProductMap = new HashMap<String, List<SABMAlcoholVariantProductMaterialModel>>();
		fillBrandProductMap(brandProductMap, conditionGroup.getDealConditions());
		return isLostForAppliedOnce(entry, newQuantity, cart, brandProductMap, conditionGroup.getDealConditions());
	}
}
