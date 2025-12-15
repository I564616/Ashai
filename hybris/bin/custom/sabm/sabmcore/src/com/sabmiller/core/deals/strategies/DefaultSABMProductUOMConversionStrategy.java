/**
 *
 */
package com.sabmiller.core.deals.strategies;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.math.BigDecimal;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.ProductUOMMappingModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;


/**
 * The Class DefaultSABMProductUOMConversionStrategy.
 */
public class DefaultSABMProductUOMConversionStrategy implements SABMProductUOMConversionStrategy
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSABMProductUOMConversionStrategy.class.getName());

	/** The unit service. */
	@Resource(name = "defaultSabmUnitService")
	private UnitService unitService;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.deals.strategies.SABMProductUOMConversionStrategy#convertQuantity(de.hybris.platform.core.model
	 * .product.ProductModel, int, java.lang.String)
	 */
	@Override
	public int convertQuantity(final ProductModel product, final int quantity, final String uom)
	{
		if (quantity == 0)
		{
			return 0;
		}
		if (product == null || !(product instanceof SABMAlcoholVariantProductEANModel))
		{
			LOG.error("Unable to convert quantity for null or not EAN product");
			return 0;
		}

		SABMAlcoholVariantProductEANModel eanProduct = null;

		if (product instanceof SABMAlcoholVariantProductMaterialModel)
		{
			eanProduct = (SABMAlcoholVariantProductEANModel) ((SABMAlcoholVariantProductMaterialModel) product).getBaseProduct();
		}
		else
		{
			eanProduct = (SABMAlcoholVariantProductEANModel) product;
		}

		UnitModel unit = null;
		try
		{
			unit = unitService.getUnitForCode(uom);
		}
		catch (IllegalArgumentException | UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			LOG.warn("Error getting UnitModel with code: " + uom, e);
		}

		if (unit == null || unit.equals(eanProduct.getUnit()))
		{
			return quantity;
		}

		final List<ProductUOMMappingModel> uomMappings = eanProduct.getUomMappings();

		if (CollectionUtils.isNotEmpty(uomMappings))
		{
			for (final ProductUOMMappingModel productUOM : uomMappings)
			{
				//Find the corresponding base information where  the one selected from the dropdown be equal to  the CommerceCartParameter.unit
				if (unit.equals(productUOM.getFromUnit()) && productUOM.getToUnit() != null
						&& productUOM.getToUnit().equals(eanProduct.getUnit()) && productUOM.getQtyConversion() != null)
				{
					return BigDecimal.valueOf(productUOM.getQtyConversion().doubleValue())
							.multiply(BigDecimal.valueOf(Integer.valueOf(quantity).longValue())).intValue();
				}
			}

			LOG.warn("Unable to find conversion mapping for product: {} and units: {}", eanProduct, unit);
		}
		else
		{
			LOG.debug("ProductData.UomMappingList is empty for the product: {}, return old quantity: {}", eanProduct, quantity);
		}
		return 0;
	}
}
