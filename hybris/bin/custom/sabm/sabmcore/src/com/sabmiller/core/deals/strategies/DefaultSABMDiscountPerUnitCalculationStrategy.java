/**
 *
 */
package com.sabmiller.core.deals.strategies;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.session.SessionService;

import java.math.BigDecimal;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmPriceRowService;
import com.sabmiller.core.product.SabmProductService;


/**
 * The Class DefaultSABMDiscountPerUnitCalculationStrategy.
 */
public class DefaultSABMDiscountPerUnitCalculationStrategy implements SABMDiscountPerUnitCalculationStrategy
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSABMDiscountPerUnitCalculationStrategy.class.getName());

	/** The price row service. */
	@Resource(name = "priceRowService")
	private SabmPriceRowService priceRowService;

	/** The product service. */
	@Resource(name = "productService")
	private SabmProductService productService;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.strategies.SABMDiscountPerUnitCalculationStrategy#calculateDiscountPerUnit()
	 */
	@Override
	public BigDecimal calculateDiscountPerUnit(final ProductModel product, final Double percentage)
	{
		if (product == null)
		{
			return BigDecimal.ZERO;
		}

		PriceRowModel priceRow = null;

		final String b2bUnitId = sessionService.getAttribute(SabmCoreConstants.SESSION_SELECT_B2BUNIT_UID_DATA);
		if (b2bUnitId != null)
		{
			final B2BUnitModel b2bUnit = b2bUnitService.getUnitForUid(b2bUnitId);
			priceRow = getPriceRow(product, b2bUnit);
		}
		else
		{
			priceRow = getPriceRow(product, null);
		}

		return calculateDiscountPerUnit(priceRow, percentage);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.strategies.SABMDiscountPerUnitCalculationStrategy#calculateSimpleDiscountPerUnit(de.
	 * hybris.platform.core.model.product.ProductModel, java.lang.Double)
	 */
	@Override
	public BigDecimal calculateSimpleDiscountPerUnit(final ProductModel product, final Double percentage)
	{
		if (product == null)
		{
			return BigDecimal.ZERO;
		}

		return calculateSimpleDiscountByPrice(getPriceRow(product, null), percentage);
	}

	/**
	 * Calculate discount per unit.
	 *
	 * @param product
	 *           the product
	 * @param percentage
	 *           the percentage
	 * @param b2bUnit
	 *           the b2b unit
	 * @return the big decimal
	 */
	public BigDecimal calculateDiscountPerUnit(final ProductModel product, final Double percentage, final B2BUnitModel b2bUnit)
	{
		LOG.debug("Calculating discount per unit with product: {}, percentage: {}, b2bunit: {}", product, percentage, b2bUnit);
		if (product == null)
		{
			return BigDecimal.ZERO;
		}

		return calculateDiscountPerUnit(getPriceRow(product, b2bUnit), percentage);
	}

	/**
	 * Gets the price row.
	 *
	 * @param product
	 *           the product
	 * @param b2bUnit
	 *           the b2b unit
	 * @return the price row
	 */
	protected PriceRowModel getPriceRow(final ProductModel product, final B2BUnitModel b2bUnit)
	{
		ProductModel priceProduct = null;
		if (product instanceof SABMAlcoholVariantProductMaterialModel)
		{
			priceProduct = ((SABMAlcoholVariantProductMaterialModel) product).getBaseProduct();

		}
		else if (product instanceof SABMAlcoholVariantProductEANModel)
		{
			priceProduct = product;
		}

		if (b2bUnit == null)
		{
			return priceRowService.getPriceRowByProduct(priceProduct);
		}
		return priceRowService.getPriceRowByProduct(priceProduct, b2bUnit);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.deals.strategies.SABMDiscountPerUnitCalculationStrategy#calculateDiscountPerUnit(java.lang.
	 * String, java.lang.Double)
	 */
	@Override
	public BigDecimal calculateDiscountPerUnit(final String product, final Double percentage)
	{
		final ProductModel productModel = productService.getProductForCodeSafe(product);

		return calculateDiscountPerUnit(productModel, percentage);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.deals.strategies.SABMDiscountPerUnitCalculationStrategy#calculateSimpleDiscountPerUnit(java.
	 * lang.String, java.lang.Double)
	 */
	@Override
	public BigDecimal calculateSimpleDiscountPerUnit(final String product, final Double percentage)
	{
		final ProductModel productModel = productService.getProductForCodeSafe(product);

		return calculateSimpleDiscountPerUnit(productModel, percentage);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.deals.strategies.SABMDiscountPerUnitCalculationStrategy#calculateDiscountPerUnit(java.lang.
	 * String, java.lang.Double, de.hybris.platform.b2b.model.B2BUnitModel)
	 */
	@Override
	public BigDecimal calculateDiscountPerUnit(final String product, final Double percentage, final B2BUnitModel b2bUnit)
	{
		final ProductModel productModel = productService.getProductForCodeSafe(product);

		return calculateDiscountPerUnit(productModel, percentage, b2bUnit);
	}

	/**
	 * Calculate discount per unit.
	 *
	 * @param priceRow
	 *           the price row
	 * @param percentage
	 *           the percentage
	 * @return the big decimal
	 */
	@Override
	public BigDecimal calculateDiscountPerUnit(final PriceRowModel priceRow, final Double percentage)
	{
		if (priceRow != null && (priceRow.getBasePrice() != null || priceRow.getPrice() != null) && percentage != null)
		{
			BigDecimal productPrice = null;

			if (priceRow.getBasePrice() != null && priceRow.getBasePrice() > 0)
			{
				productPrice = BigDecimal.valueOf(priceRow.getBasePrice());

				productPrice = roundAmount(productPrice.multiply(BigDecimal.valueOf(percentage).divide(BigDecimal.valueOf(100))));

				if (priceRow.getPrice() != null)
				{
					productPrice = roundAmount(productPrice
							.add(BigDecimal.valueOf(priceRow.getBasePrice()).subtract(BigDecimal.valueOf(priceRow.getPrice()))));
				}

				return productPrice;
			}
			productPrice = BigDecimal.valueOf(priceRow.getPrice());

			return roundAmount(productPrice.multiply(BigDecimal.valueOf(percentage).divide(BigDecimal.valueOf(100))));
		}

		return BigDecimal.ZERO;
	}

	/**
	 * Calculate simple discount by price.
	 *
	 * @param priceRow
	 *           the price row
	 * @param percentage
	 *           the percentage
	 * @return the big decimal
	 */
	protected BigDecimal calculateSimpleDiscountByPrice(final PriceRowModel priceRow, final Double percentage)
	{
		if (priceRow != null && (priceRow.getBasePrice() != null || priceRow.getPrice() != null) && percentage != null)
		{
			BigDecimal productPrice = null;

			if (priceRow.getBasePrice() != null && priceRow.getBasePrice() > 0)
			{
				productPrice = BigDecimal.valueOf(priceRow.getBasePrice());
			}
			else
			{
				productPrice = BigDecimal.valueOf(priceRow.getPrice());
			}
			return roundAmount(productPrice.multiply(BigDecimal.valueOf(percentage).divide(BigDecimal.valueOf(100))));
		}

		return BigDecimal.ZERO;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.strategies.SABMDiscountPerUnitCalculationStrategy#roundAmount(java.math.BigDecimal)
	 */
	@Override
	public BigDecimal roundAmount(final BigDecimal amount)
	{
		if (amount == null)
		{
			return BigDecimal.ZERO;
		}

		if (amount.stripTrailingZeros().scale() <= 0)
		{
			return amount.stripTrailingZeros().setScale(0);
		}
		return amount.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

}
