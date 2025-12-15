/**
 *
 */
package com.sabmiller.core.deals.strategies;


import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DiscountDealBenefitModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmPriceRowService;
import com.sabmiller.core.product.SabmProductService;


/**
 * The Class DefaultSABMDealValidationStrategy.
 */
public class DefaultSABMDealValidationStrategy implements SABMDealValidationStrategy
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSABMDealValidationStrategy.class);

	/** The product service. */
	@Resource(name = "productService")
	private SabmProductService productService;

	/** The price row service. */
	@Resource(name = "priceRowService")
	private SabmPriceRowService priceRowService;

	/** The discount per unit calculation strategy. */
	@Resource(name = "discountPerUnitCalculationStrategy")
	private SABMDiscountPerUnitCalculationStrategy discountPerUnitCalculationStrategy;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.deals.strategies.SABMDealValidationStrategy#validateDeal(com.sabmiller.core.model.DealModel)
	 */
	@Override
	public boolean validateDeal(final DealModel deal)
	{
		return validateNoExpired(deal, new Date()) && validateConditions(deal) && validateBenefits(deal, null)
				&& validateLimited(deal, null);
	}

	@Override
	public boolean validateDeal(final DealModel deal, final B2BUnitModel b2bUnit)
	{
		return validateNoExpired(deal, new Date()) && validateConditions(deal) && validateBenefits(deal, b2bUnit)
				&& validateLimited(deal, b2bUnit);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.deals.strategies.SABMDealValidationStrategy#validateNowAvailableDeal(com.sabmiller.core.model.
	 * DealModel)
	 */
	@Override
	public boolean validateNowAvailableDeal(final DealModel deal)
	{
		return validateNoExpired(deal, new Date()) && validateStarted(deal, new Date());
	}

	/**
	 * Validate no Expired.
	 *
	 * @param deal
	 * @param referenceDate
	 * @return true, if no expired
	 */
	protected boolean validateNoExpired(final DealModel deal, final Date referenceDate)
	{
		final int result = DateUtils.truncatedCompareTo(deal.getValidTo(), referenceDate, Calendar.DAY_OF_MONTH);
		return result < 0 ? false : true;
	}

	/**
	 * Validate Started.
	 *
	 * @param deal
	 * @param referenceDate
	 * @return true if started
	 */
	protected boolean validateStarted(final DealModel deal, final Date referenceDate)
	{
		final int result = DateUtils.truncatedCompareTo(referenceDate, deal.getValidFrom(), Calendar.DAY_OF_MONTH);
		return result < 0 ? false : true;
	}

	/**
	 * Validate conditions.
	 *
	 * @param deal
	 *           the deal
	 * @return true, if successful
	 */
	protected boolean validateConditions(final DealModel deal)
	{
		if (deal == null || deal.getConditionGroup() == null
				|| CollectionUtils.isEmpty(deal.getConditionGroup().getDealConditions()))
		{
			LOG.debug("The deal [{}] doesn't have any conditions. It's invalid", deal);
			return false;
		}

		//Checking if the Complex deals is valid, based on the conditions' type
		if (DealTypeEnum.COMPLEX.equals(deal.getDealType()))
		{
			boolean isComplexCondition = false;
			boolean isSimpleProductCondition = false;
			final List<AbstractDealConditionModel> dealConditions = deal.getConditionGroup().getDealConditions();
			final List<ProductModel> excludedProduct = productService.findExcludedProduct(dealConditions);

			for (final AbstractDealConditionModel condition : dealConditions)
			{
				if (condition instanceof ComplexDealConditionModel)
				{
					isComplexCondition = true;

					if (isSimpleProductCondition)
					{
						LOG.debug("The complex deal [{}] has a mix of Product and Complex condition. It's invalid", deal);
						return false;
					}


					if (!validateProducts((ComplexDealConditionModel) condition, excludedProduct, deal))
					{
						return false;
					}
				}
				else if (condition instanceof ProductDealConditionModel)
				{
					if (!validateProducts((ProductDealConditionModel) condition, deal))
					{
						return false;
					}

					if (BooleanUtils.isNotTrue(condition.getExclude()))
					{
						isSimpleProductCondition = true;

						if (isComplexCondition)
						{
							LOG.debug("The deal [{}] has a mix of Product and Complex condition. It's invalid", deal);
							return false;
						}
					}
				}
			}
		}
		//If is not a Complex deal, there MUST NOT be any complex condition
		else
		{
			for (final AbstractDealConditionModel condition : deal.getConditionGroup().getDealConditions())
			{
				if (condition instanceof ComplexDealConditionModel)
				{
					LOG.debug("The non complex deal [{}] has a Complex condition. It's invalid", deal);
					return false;
				}
				if (condition instanceof ProductDealConditionModel)
				{
					if (!validateProducts((ProductDealConditionModel) condition, deal))
					{
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * Validate benefits.
	 *
	 * @param deal
	 *           the deal
	 * @return true, if successful
	 */
	protected boolean validateBenefits(final DealModel deal, final B2BUnitModel b2bUnit)
	{
		if (deal == null || deal.getConditionGroup() == null || CollectionUtils.isEmpty(deal.getConditionGroup().getDealBenefits()))
		{
			LOG.debug("The deal [{}] doesn't have any benefits. It's invalid", deal);
			return false;
		}
		final List<String> scales = new ArrayList<>();
		boolean hasPercentageDiscount = false;
		for (final AbstractDealBenefitModel benefit : deal.getConditionGroup().getDealBenefits())
		{
			if (benefit instanceof FreeGoodsDealBenefitModel)
			{
				if (DealTypeEnum.LIMITED.equals(deal.getDealType()))
				{
					LOG.debug("Limited deal can only have discount benefits. It's invalid", deal);
					return false;
				}

				if (scales.contains("FG" + benefit.getScale()))
				{
					LOG.debug("The deal [{}] has more than one free good benefit with the same scale. It's invalid", deal);
					return false;
				}

				if (!validateProducts((FreeGoodsDealBenefitModel) benefit, deal))
				{
					return false;
				}
				scales.add("FG" + benefit.getScale());
			}
			else if (benefit instanceof DiscountDealBenefitModel)
			{
				hasPercentageDiscount = BooleanUtils.isNotTrue(((DiscountDealBenefitModel) benefit).getCurrency());
				if (scales.contains("D" + benefit.getScale()))
				{
					LOG.debug("The deal [{}] has more than one discount benefit with the same scale. It's invalid", deal);
					return false;
				}
				scales.add("D" + benefit.getScale());
			}
		}

		if (hasPercentageDiscount && !checkProductsPriceUOM(deal, b2bUnit))
		{
			return false;
		}

		return true;
	}

	/**
	 * Validate limited.
	 *
	 * @param deal
	 *           the deal
	 * @return true, if successful
	 */
	protected boolean validateLimited(final DealModel deal, final B2BUnitModel b2bUnit)
	{
		if (deal == null || deal.getConditionGroup() == null)
		{
			return false;
		}

		if (DealTypeEnum.LIMITED.equals(deal.getDealType()))
		{
			double availableQty = 0;
			double qty = 0;
			boolean isAmount = true;

			if (deal.getMaxConditionBaseValue() != null && deal.getMaxConditionBaseValue() > 0)
			{
				availableQty = deal.getMaxConditionBaseValue()
						- (deal.getUsedConditionBaseValue() == null ? 0 : deal.getUsedConditionBaseValue());
				isAmount = false;
			}
			else if (deal.getMaxConditionValue() != null && Math.abs(deal.getMaxConditionValue()) > 0)
			{
				availableQty = Math.abs(deal.getMaxConditionValue())
						- (deal.getUsedConditionValue() == null ? 0 : Math.abs(deal.getUsedConditionValue()));
			}

			String productCode = StringUtils.EMPTY;

			for (final AbstractDealConditionModel abstractCondition : deal.getConditionGroup().getDealConditions())
			{
				if (BooleanUtils.isNotTrue(abstractCondition.getExclude()))
				{
					if (abstractCondition instanceof ProductDealConditionModel)
					{
						final ProductDealConditionModel condition = (ProductDealConditionModel) abstractCondition;
						qty = condition.getMinQty() != null ? condition.getMinQty()
								: condition.getQuantity() != null ? condition.getQuantity() : 0;

						productCode = condition.getProductCode();

						// do not need to validate the product here.
						// because the condition has been validate in the validateConditions.
					}
					else
					{
						LOG.debug("A limited deal [{}] cannot be populated with hierarchy, only material code", deal);
						return false;
					}
				}
			}

			if (isAmount)
			{
				if (CollectionUtils.isEmpty(deal.getConditionGroup().getDealBenefits()))
				{
					return false;
				}

				final AbstractDealBenefitModel benefit = deal.getConditionGroup().getDealBenefits().get(0);
				if (!(benefit instanceof DiscountDealBenefitModel))
				{
					return false;
				}

				BigDecimal discountPerUnit = BigDecimal.ONE;
				final DiscountDealBenefitModel discount = (DiscountDealBenefitModel) benefit;
				if (BooleanUtils.isTrue(discount.getCurrency()))
				{
					if (discount.getAmount() != null)
					{
						discountPerUnit = discountPerUnitCalculationStrategy
								.roundAmount(BigDecimal.valueOf(Math.abs(discount.getAmount())));
					}
				}
				else
				{
					if (b2bUnit == null)
					{
						discountPerUnit = discountPerUnitCalculationStrategy.calculateDiscountPerUnit(productCode,
								discount.getAmount());
					}
					else
					{
						discountPerUnit = discountPerUnitCalculationStrategy.calculateDiscountPerUnit(productCode, discount.getAmount(),
								b2bUnit);
					}
				}

				return availableQty >= discountPerUnit.multiply(BigDecimal.valueOf(qty)).doubleValue();
			}

			return availableQty >= qty;
		}
		return true;
	}

	/**
	 * Check products price uom.
	 *
	 * @param deal
	 *           the deal
	 * @return true, if successful
	 */
	protected boolean checkProductsPriceUOM(final DealModel deal, final B2BUnitModel b2bUnit)
	{
		if (deal == null || deal.getConditionGroup() == null
				|| CollectionUtils.isEmpty(deal.getConditionGroup().getDealConditions()))
		{
			return false;
		}

		final List<? extends ProductModel> products = productService.getProductsByDeal(deal);

		Double amount = null;
		UnitModel uom = null;

		for (ProductModel product : products)
		{
			SABMAlcoholVariantProductEANModel eanProduct = null;
			while (product instanceof VariantProductModel)
			{
				if (product.getClass().equals(SABMAlcoholVariantProductEANModel.class))
				{
					eanProduct = (SABMAlcoholVariantProductEANModel) product;
					break;
				}

				product = ((VariantProductModel) product).getBaseProduct();
			}

			if (eanProduct != null)
			{
				if (uom != null && !uom.equals(eanProduct.getUnit()))
				{
					LOG.debug("The deal [{}] contains products with different UOM", deal);
					return false;
				}
				uom = eanProduct.getUnit();

				PriceRowModel priceRow = null;
				if (b2bUnit == null)
				{
					// use the current user;
					priceRow = priceRowService.getPriceRowByProduct(eanProduct);
				}
				else
				{
					priceRow = priceRowService.getPriceRowByProduct(eanProduct, b2bUnit);
				}

				if (priceRow != null)
				{
					if (amount != null && !amount.equals(priceRow.getPrice()))
					{
						LOG.debug("The deal [{}] contains products with different price", deal);
						return false;
					}
					amount = priceRow.getPrice();
				}
			}
		}

		return true;
	}



	/**
	 * Check products for complex deal condition
	 *
	 */
	protected boolean validateProducts(final ComplexDealConditionModel complexCondition, final List<ProductModel> excludedProduct,
			final DealModel deal)
	{
		// check if used non-exist brand.
		//Get the products in the ranges by the brand
		final List<? extends ProductModel> materials = productService.getProductByHierarchy(complexCondition.getLine(),
				complexCondition.getBrand(), complexCondition.getVariety(), complexCondition.getEmpties(),
				complexCondition.getEmptyType(), complexCondition.getPresentation());

		if (CollectionUtils.isEmpty(materials))
		{
			LOG.warn("data issue! no products found for ComplexDealConditionModel[{}] of deal[{}]", complexCondition, deal);
			return false;
		}
		Collection<ProductModel> filteredMaterial = null;
		if (!CollectionUtils.isEmpty(excludedProduct))
		{
			filteredMaterial = CollectionUtils.subtract(materials, excludedProduct);
			if (CollectionUtils.isEmpty(filteredMaterial))
			{
				LOG.warn("data issue! no products found for ComplexDealConditionModel[{}] of deal[{}] after excluding",
						complexCondition, deal);
				return false;
			}
		}
		else
		{
			filteredMaterial = new ArrayList<>();
			filteredMaterial.addAll(materials);
		}

		final Map<ProductModel, ProductModel> mapMaterial = new HashMap<>();
		for (final ProductModel product : filteredMaterial)
		{
			if (product instanceof SABMAlcoholVariantProductMaterialModel)
			{
				final SABMAlcoholVariantProductEANModel eanProduct = (SABMAlcoholVariantProductEANModel) ((SABMAlcoholVariantProductMaterialModel) product)
						.getBaseProduct();
				if (!mapMaterial.containsKey(eanProduct) && eanProduct.getPurchasable())
				{
					mapMaterial.put(eanProduct, product);
				}
			}
		}
		if (CollectionUtils.isEmpty(mapMaterial.values()))
		{
			LOG.warn("data issue! no products found for ComplexDealConditionModel[{}] of deal[{}] after purchasable check.",
					complexCondition, deal);
			return false;
		}


		return true;
	}

	/**
	 * Check products for product deal condition
	 *
	 */
	protected boolean validateProducts(final FreeGoodsDealBenefitModel fgBenefit, final DealModel deal)
	{
		// check if used non-exist brand.
		//Get the products in the ranges by the brand
		final String prodCode = fgBenefit.getProductCode();

		if (productService.getProductForCodeSafe(prodCode) == null)
		{
			LOG.warn("data issue! no product found for FreeGoodsDealBenefit[{}] of deal[{}]", fgBenefit, deal);
			return false;
		}
		return true;
	}

	/**
	 * Check products for product deal condition
	 *
	 */
	protected boolean validateProducts(final ProductDealConditionModel productCondition, final DealModel deal)
	{
		final String prodCode = productCondition.getProductCode();

		/*
		 * if (productService.getProductForCodeSafe(prodCode) == null) { LOG.warn(
		 * "data issue! no product found for ProductDealConditionModel[{}] of deal[{}]", productCondition, deal); return
		 * false; } return true;
		 */
		// Changed as per the incident INC0309398 fixed.
		if (productService.getProductForCodeSafe(prodCode) != null)
		{
			final ProductModel productModel = productService.getProductForCodeSafe(prodCode);
			if (productModel instanceof SABMAlcoholVariantProductMaterialModel)
			{
				final SABMAlcoholVariantProductEANModel eanProduct = (SABMAlcoholVariantProductEANModel) ((SABMAlcoholVariantProductMaterialModel) productModel)
						.getBaseProduct();
				if (eanProduct != null && eanProduct.getPurchasable())
				{
					return true;
				}
			}
		}
		LOG.warn("data issue! no product found for ProductDealConditionModel[{}] of deal[{}]", productCondition, deal);
		return false;
	}
}
