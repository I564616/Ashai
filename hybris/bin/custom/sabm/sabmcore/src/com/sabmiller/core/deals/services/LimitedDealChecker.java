/**
 *
 */
package com.sabmiller.core.deals.services;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.List;

import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.ProductDealConditionModel;


/**
 * Deal 009 - Once off / limited price conditions
 */
public class LimitedDealChecker extends AbstractLostDealChecker<DealModel>
{
	/*
	 * To decide this deal is Deal 009
	 *
	 * @see com.sabmiller.core.deals.services.AbstractLostDealChecker#isThisDealType(com.sabmiller.core.model.DealModel)
	 */
	@Override
	public boolean isThisDealType(final DealModel deal)
	{
		return DealTypeEnum.LIMITED.equals(deal.getDealType());
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
		final List<AbstractDealConditionModel> conditions = conditionGroup.getDealConditions();

		for (final AbstractDealConditionModel condition : conditions)
		{
			final List<ProductModel> productList = getConditionProduct(condition);
			if (productList.contains(entry.getProduct()))
			{
				if (condition instanceof ComplexDealConditionModel)
				{
					final ComplexDealConditionModel complexCondition = (ComplexDealConditionModel) condition;
					if (newQuantity < complexCondition.getQuantity() && entry.getQuantity() >= complexCondition.getQuantity())
					{
						return true;
					}
				}
				else
				{
					final ProductDealConditionModel productCondition = (ProductDealConditionModel) condition;
					if (newQuantity < productCondition.getMinQty() && entry.getQuantity() >= productCondition.getMinQty())
					{
						return true;
					}
				}
			}
		}


		return false;
	}

	/**
	 * @param condition
	 */
	private List<ProductModel> getConditionProduct(final AbstractDealConditionModel condition)
	{
		final List<ProductModel> productList = new ArrayList<>();
		if (condition instanceof ComplexDealConditionModel)
		{
			final ComplexDealConditionModel complexCondition = (ComplexDealConditionModel) condition;
			productList.addAll(getProductService().getProductByHierarchy(complexCondition.getLine(), complexCondition.getBrand(),
					complexCondition.getVariety(), complexCondition.getEmpties(), complexCondition.getEmptyType(),
					complexCondition.getPresentation()));
		}
		else
		{
			final ProductDealConditionModel productCondition = (ProductDealConditionModel) condition;
			productList.add(getProductService().getProductForCode(productCondition.getProductCode()));
		}
		return productList;
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
		// YTODO Auto-generated method stub
		return false;
	}

}
