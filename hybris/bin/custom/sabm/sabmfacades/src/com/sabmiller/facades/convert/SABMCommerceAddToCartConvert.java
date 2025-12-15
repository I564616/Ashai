/**
 *
 */
package com.sabmiller.facades.convert;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import com.sabmiller.core.model.ProductUOMMappingModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;


/**
 * SABMCommerceAddToCartConvert
 *
 * @author yaopeng
 *
 */
public class SABMCommerceAddToCartConvert implements CommerceAddToCartMethodHook
{
	private static final Logger LOG = Logger.getLogger(SABMCommerceAddToCartConvert.class);

	/*
	 * Add the right quantity of products to cart
	 *
	 * @param source the @CommerceCartParameter
	 *
	 * @see
	 * de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook#beforeAddToCart(de.hybris.platform.
	 * commerceservices.service.data.CommerceCartParameter)
	 */
	@Override
	public void beforeAddToCart(final CommerceCartParameter parameters) throws CommerceCartModificationException
	{
		if (parameters != null)
		{
			if (parameters.getProduct() instanceof SABMAlcoholVariantProductEANModel && parameters.getUnit() != null
					&& !parameters.getUnit().equals(parameters.getProduct().getUnit()))
			{
				final List<ProductUOMMappingModel> uomMappings = ((SABMAlcoholVariantProductEANModel) parameters.getProduct())
						.getUomMappings();

				if (CollectionUtils.isNotEmpty(uomMappings))
				{
					boolean isConversion = false;
					double calculatedBase = 0d;
					for (final ProductUOMMappingModel productUOM : uomMappings)
					{
						//Find the corresponding base information where  the one selected from the dropdown be equal to  the CommerceCartParameter.unit
						if (parameters.getUnit().equals(productUOM.getFromUnit()) && productUOM.getToUnit() != null
								&& productUOM.getToUnit().equals(parameters.getProduct().getUnit())
								&& productUOM.getQtyConversion() != null)
						{
							calculatedBase = productUOM.getQtyConversion().doubleValue();
							isConversion = true;
							break;
						}
					}
					if (isConversion)
					{
						// Calculated new value,update the quantity of the CommerceCartParameter.quantity with the new  value
						parameters.setQuantity(
								BigDecimal.valueOf(calculatedBase).multiply(BigDecimal.valueOf(parameters.getQuantity())).longValue());
					}
					else
					{
						LOG.warn("Unable to find conversion mapping for product:" + parameters.getProduct().getCode() + " and units: "
								+ parameters.getUnit().getCode() + " - " + parameters.getProduct().getUnit().getCode());
					}
				}
				else
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug("ProductData.UomMappingList is empty for the product: " + parameters.getProduct().getCode()
								+ ", return old quantity");
					}

				}
			}

			LOG.debug("SABMCommerceAddToCartConvert.beforeAddToCart product code=" + parameters.getProduct().getCode());

			//after the calculation, put the Product.unit in the CommerceCartParameter.unit
			parameters.setUnit(parameters.getProduct().getUnit());
		}
	}

	/*
	 *
	 * @see de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook#afterAddToCart(de.hybris.platform.
	 * commerceservices.service.data.CommerceCartParameter,
	 * de.hybris.platform.commerceservices.order.CommerceCartModification)
	 */
	@Override
	public void afterAddToCart(final CommerceCartParameter parameters, final CommerceCartModification result)
			throws CommerceCartModificationException
	{
		// Empty method
	}

}
