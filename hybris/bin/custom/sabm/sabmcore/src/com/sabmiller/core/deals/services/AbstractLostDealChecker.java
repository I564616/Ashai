/**
 *
 */
package com.sabmiller.core.deals.services;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.EntryOfferInfoModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmProductService;


/**
 * The Class AbstractLostDealChecker.
 *
 * @param <T>
 *           the generic type
 */
public abstract class AbstractLostDealChecker<T>
{

	/** The product service. */
	@Resource(name = "productService")
	private SabmProductService productService;

	/** The deals service. */
	@Resource(name = "dealsService")
	private DealsService dealsService;

	/**
	 * Checks if is this deal type.
	 *
	 * @param deal
	 *           the deal
	 * @return true, if is this deal type
	 */
	public boolean isThisDealType(final Object deal)
	{
		if (deal instanceof DealModel)
		{
			return isThisDealType((DealModel) deal);
		}
		else if (deal instanceof EntryOfferInfoModel)
		{
			return isThisDealType((EntryOfferInfoModel) deal);
		}
		else
		{
			return false;
		}

	}

	/**
	 * Checks if is this deal type.
	 *
	 * @param deal
	 *           the deal
	 * @return true, if is this deal type
	 */
	public boolean isThisDealType(final DealModel deal)
	{
		return false;
	}

	/**
	 * Checks if is this deal type.
	 *
	 * @param offerInfo
	 *           the offer info
	 * @return true, if is this deal type
	 */
	public boolean isThisDealType(final EntryOfferInfoModel offerInfo)
	{
		return false;
	}

	/**
	 * To check this cart whether it will lost current deal, after reduce product or delete item.
	 *
	 * @param deal
	 *           the deal
	 * @param entry
	 *           the entry
	 * @param newQuantity
	 *           the new quantity
	 * @param cart
	 *           the cart
	 * @return true, if is lost deal
	 */
	public abstract boolean isLostDeal(T deal, AbstractOrderEntryModel entry, int newQuantity, CartModel cart);

	/**
	 * Checks if is delete deal.
	 *
	 * @param deal
	 *           the deal
	 * @param entry
	 *           the entry
	 * @param newQuantity
	 *           the new quantity
	 * @param cart
	 *           the cart
	 * @return true, if is delete deal
	 */
	public abstract boolean isDeleteDeal(T deal, AbstractOrderEntryModel entry, int newQuantity, CartModel cart);

	/**
	 * to check whether all of the condtions are the product condition in deal condition group.
	 *
	 * @param dealConditions
	 *           the deal conditions
	 * @return true, if is all product condition
	 */
	public boolean isAllProductCondition(final List<AbstractDealConditionModel> dealConditions)
	{
		for (final AbstractDealConditionModel abstractDealConditionModel : dealConditions)
		{
			if (!(abstractDealConditionModel instanceof ProductDealConditionModel))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * to check whether there are at least one product condition in deal condition group.
	 *
	 * @param dealConditions
	 *           the deal conditions
	 * @return true, if successful
	 */
	public boolean hasProductCondition(final List<AbstractDealConditionModel> dealConditions)
	{
		for (final AbstractDealConditionModel abstractDealConditionModel : dealConditions)
		{
			if (abstractDealConditionModel instanceof ProductDealConditionModel)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * to check whether the product is belong to the current brandProductList.
	 *
	 * @param product
	 *           the product
	 * @param brandProductList
	 *           the brand product list
	 * @return true, if is belong to brand
	 */
	public boolean isBelongToBrand(final ProductModel product, final List<? extends ProductModel> brandProductList)
	{
		if (product == null)
		{
			return false;
		}

		for (final ProductModel material : ListUtils.emptyIfNull(brandProductList))
		{
			if (material != null && material.getCode().equals(product.getCode()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * to check whether there are at least one exclude condition in deal condition group.
	 *
	 * @param dealConditions
	 *           the deal conditions
	 * @return true, if successful
	 */
	public boolean hasExcludeCondition(final List<AbstractDealConditionModel> dealConditions)
	{
		for (final AbstractDealConditionModel abstractDealConditionModel : dealConditions)
		{
			if (BooleanUtils.isTrue(abstractDealConditionModel.getExclude()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the product service.
	 *
	 * @return the productService
	 */
	public SabmProductService getProductService()
	{
		return productService;
	}

	/**
	 * Gets the deals service.
	 *
	 * @return the dealsService
	 */
	public DealsService getDealsService()
	{
		return dealsService;
	}

	/**
	 * Check the quantity in each ComplexDealCondition
	 *
	 * @param entry
	 * @param newQuantity
	 * @param cart
	 * @param conditionGroup
	 */
	protected boolean isLostCheckForComplexQuantity(final AbstractOrderEntryModel entry, final int newQuantity,
			final CartModel cart, final DealConditionGroupModel conditionGroup)
	{
		/*
		 * Check the quantity in each ComplexDealCondition
		 */
		final List<AbstractDealConditionModel> dealConditions = conditionGroup.getDealConditions();
		for (final AbstractDealConditionModel dealCondition : dealConditions)
		{
			if (BooleanUtils.isNotTrue(dealCondition.getMandatory()))
			{
				continue;
			}

			if (!(dealCondition instanceof ComplexDealConditionModel))
			{
				continue;
			}

			// Check whether the current deal condition is excluded
			if (BooleanUtils.isTrue(dealCondition.getExclude()))
			{
				continue;
			}

			// Check whether the quantity is null or 0
			final ComplexDealConditionModel complexCondition = (ComplexDealConditionModel) dealCondition;
			if (complexCondition.getQuantity() == null || complexCondition.getQuantity() == 0)
			{
				continue;
			}

			final List<SABMAlcoholVariantProductMaterialModel> productByHierarchy = getProductService()
					.getProductByHierarchyFilterExcluded(complexCondition);
			// Check whether the changed product is belong to the current complex deal condition.
			if (!productByHierarchy.contains(entry.getProduct()))
			{
				continue;
			}

			int oldTotal = 0;
			int newTotal = 0;
			final List<AbstractOrderEntryModel> entries = cart.getEntries();
			for (final AbstractOrderEntryModel entry1 : entries)
			{
				if (entry1.getIsFreeGood() != null && entry1.getIsFreeGood())
				{
					continue;
				}

				if (!productByHierarchy.contains(entry1.getProduct()))
				{
					continue;
				}

				oldTotal += entry1.getQuantity();

				if (entry1.equals(entry))
				{
					newTotal += newQuantity;
				}
				else
				{
					newTotal += entry1.getQuantity();
				}
			}

			if (oldTotal >= complexCondition.getQuantity() && newTotal < complexCondition.getQuantity())
			{
				return true;
			}

		}
		return false;
	}

	protected boolean isLostCheckForPrdConditionQuantity(final AbstractOrderEntryModel entry, final int newQuantity,
			final CartModel cart, final DealConditionGroupModel conditionGroup)
	{
		/*
		 * Check the quantity in each Product Condition
		 */
		final List<AbstractDealConditionModel> dealConditions = conditionGroup.getDealConditions();
		for (final AbstractDealConditionModel dealCondition : dealConditions)
		{
			if (BooleanUtils.isNotTrue(dealCondition.getMandatory()))
			{
				continue;
			}

			if (!(dealCondition instanceof ProductDealConditionModel))
			{
				continue;
			}

			// Check whether the current deal condition is excluded
			if (BooleanUtils.isTrue(dealCondition.getExclude()))
			{
				continue;
			}

			// Check whether the quantity is null or 0
			final ProductDealConditionModel productCondition = (ProductDealConditionModel) dealCondition;
			if (productCondition.getQuantity() == null || productCondition.getQuantity() == 0)
			{
				continue;
			}

			final ProductModel product = getProductService().getProductForCode(productCondition.getDealCode());
			// Check whether the changed product is belong to the current complex deal condition.
			if (!product.equals(entry.getProduct()))
			{
				continue;
			}

			if (entry.getQuantity() >= productCondition.getMinQty() && newQuantity < productCondition.getMinQty())
			{
				return true;
			}

		}
		return false;
	}

	/**
	 * @param deal
	 * @return
	 */
	protected boolean notMatchMinCondition(final AbstractOrderEntryModel entry, final int newQuantity, final CartModel cart,
			final DealConditionGroupModel conditionGroup)
	{
		// YTODO Auto-generated method stub
		return isLostCheckForPrdConditionQuantity(entry, newQuantity, cart, conditionGroup)
				|| isLostCheckForComplexQuantity(entry, newQuantity, cart, conditionGroup);
	}

}
