/**
 *
 */
package com.sabmiller.core.deals.services;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
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
 * Deal 006 - Two or more product hierarchy with quantity across hierarchies with exceptions.
 */
public class TwoOrMoreHierarchyAcrossWithExceptionsChecker extends AbstractLostDealChecker<DealModel>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.AbstractLostDealChecker#isThisDealType(com.sabmiller.core.model.DealModel)
	 */
	/*
	 * To decide this deal is Deal 006
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
				return hasExcludeCondition(conditionGroup.getDealConditions());
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
		final List<SABMAlcoholVariantProductMaterialModel> productList = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		fillBrandProductMap(productList, conditionGroup.getDealConditions());
		final AbstractDealBenefitModel benefit = conditionGroup.getDealBenefits().get(0);
		if ((benefit.getProportionalAmount() != null && benefit.getProportionalAmount())
				|| (benefit.getProportionalFreeGood() != null && benefit.getProportionalFreeGood()))
		{
			// deal can be applied multiple times
			return isLostForAppliedMultipleTimes(entry, newQuantity, cart, conditionGroup, productList);
		}
		//deal can be applied only once
		return isLostForAppliedOnce(entry, newQuantity, cart, conditionGroup, productList);
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
	 * @param productList
	 *           the product list
	 * @return true, if is lost for applied once
	 */
	private boolean isLostForAppliedOnce(final AbstractOrderEntryModel entry, final int newQuantity, final CartModel cart,
			final DealConditionGroupModel conditionGroup, final List<SABMAlcoholVariantProductMaterialModel> productList)
	{
		if (isLostCheckForComplexQuantity(entry, newQuantity, cart, conditionGroup))
		{
			return true;
		}
		final List<AbstractOrderEntryModel> entries = cart.getEntries();
		long oldTotal = 0;
		long newTotal = 0;
		for (final AbstractOrderEntryModel abstractOrderEntryModel : entries)
		{
			if (abstractOrderEntryModel.getIsFreeGood() != null && abstractOrderEntryModel.getIsFreeGood())
			{
				continue;
			}

			if (isBelongToBrand(abstractOrderEntryModel.getProduct(), productList))
			{
				oldTotal += abstractOrderEntryModel.getQuantity();
				if (entry == abstractOrderEntryModel)
				{
					newTotal += newQuantity;
				}
				else
				{
					newTotal += abstractOrderEntryModel.getQuantity();
				}
			}
		}
		if (newTotal < conditionGroup.getScales().get(0) && oldTotal >= conditionGroup.getScales().get(0))
		{
			return true;
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
	 * @param productList
	 *           the product list
	 * @return true, if is lost for applied multiple times
	 */
	private boolean isLostForAppliedMultipleTimes(final AbstractOrderEntryModel entry, final int newQuantity, final CartModel cart,
			final DealConditionGroupModel conditionGroup, final List<SABMAlcoholVariantProductMaterialModel> productList)
	{

		if (isLostCheckForComplexQuantity(entry, newQuantity, cart, conditionGroup))
		{
			return true;
		}

		final List<AbstractOrderEntryModel> entries = cart.getEntries();
		long oldTotal = 0;
		long newTotal = 0;
		for (final AbstractOrderEntryModel abstractOrderEntryModel : entries)
		{
			if (abstractOrderEntryModel.getIsFreeGood() != null && abstractOrderEntryModel.getIsFreeGood())
			{
				continue;
			}

			if (isBelongToBrand(abstractOrderEntryModel.getProduct(), productList))
			{
				oldTotal += abstractOrderEntryModel.getQuantity();
				if (entry == abstractOrderEntryModel)
				{
					newTotal += newQuantity;
				}
				else
				{
					newTotal += abstractOrderEntryModel.getQuantity();
				}
			}
		}
		return (newTotal / conditionGroup.getScales().get(0)) < (oldTotal / conditionGroup.getScales().get(0));
	}

	/**
	 * Fill brand product map.
	 *
	 * @param productList
	 *           the product list
	 * @param dealConditions
	 *           the deal conditions
	 */
	private void fillBrandProductMap(final List<SABMAlcoholVariantProductMaterialModel> productList,
			final List<AbstractDealConditionModel> dealConditions)
	{
		for (final AbstractDealConditionModel abstractDealConditionModel : dealConditions)
		{
			if (abstractDealConditionModel instanceof ComplexDealConditionModel)
			{
				final ComplexDealConditionModel complexCondition = (ComplexDealConditionModel) abstractDealConditionModel;
				productList.addAll(getProductService().getProductByHierarchy(complexCondition.getLine(), complexCondition.getBrand(),
						complexCondition.getVariety(), complexCondition.getEmpties(), complexCondition.getEmptyType(),
						complexCondition.getPresentation()));
			}
		}
		final List<ProductModel> excludeProductList = getProductService().findExcludedProduct(dealConditions);
		for (int i = productList.size() - 1; i >= 0; i--)
		{
			final SABMAlcoholVariantProductMaterialModel product = productList.get(i);
			for (final ProductModel excludeProduct : excludeProductList)
			{

				if (product.getCode().equals(excludeProduct.getCode()))
				{
					productList.remove(product);
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
		final List<SABMAlcoholVariantProductMaterialModel> productList = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		fillBrandProductMap(productList, conditionGroup.getDealConditions());
		return isLostForAppliedOnce(entry, newQuantity, cart, conditionGroup, productList);
	}
}
