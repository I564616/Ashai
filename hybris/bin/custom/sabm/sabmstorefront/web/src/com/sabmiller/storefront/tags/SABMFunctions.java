/**
 *
 */
package com.sabmiller.storefront.tags;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.sabmiller.facades.customer.CustomerJson;


/**
 *
 */
public class SABMFunctions
{


	public static ImageData getNormalImageForCategoryAndFormat(final CategoryData category, final String format)
	{
		if (category != null && format != null)
		{
			final Collection<ImageData> images = category.getImages();
			if (images != null && !images.isEmpty())
			{
				for (final ImageData image : images)
				{
					if (format.equals(image.getFormat()))
					{
						return image;
					}
				}
			}
		}
		return null;
	}

	//Add by xiaowu for other packages display
	public static ImageData getNormalImageForVariantAndFormat(final VariantOptionData variant, final String format)
	{
		if (variant != null && format != null)
		{
			final Collection<ImageData> images = variant.getImages();
			if (images != null && !images.isEmpty())
			{
				for (final ImageData image : images)
				{
					if (format.equals(image.getFormat()))
					{
						return image;
					}
				}
			}
		}
		return null;
	}

	public static boolean displaySubTotal(final AbstractOrderData order)
	{
		boolean isSubTotalToShow = false;

		if (order != null && (order.getWet() != null && order.getWet().getValue().compareTo(BigDecimal.ZERO) > 0
				|| order.getDeposit() != null && order.getDeposit().getValue().compareTo(BigDecimal.ZERO) > 0
				|| order.getTotalLoyaltyFeePrice() != null && order.getTotalLoyaltyFeePrice().getValue().compareTo(BigDecimal.ZERO) > 0
				|| order.getDeliveryCost() != null && order.getDeliveryCost().getValue().compareTo(BigDecimal.ZERO) > 0
				|| Objects.nonNull(order.getAutoPayAdvantageDiscount())
				|| Objects.nonNull(order.getAutoPayAdvantagePlusDiscount())) )
		{
			isSubTotalToShow = true;
		}

		return isSubTotalToShow;
	}

	public static String generateJson(final Object obj)
	{
		final Gson gson = new Gson();
		return gson.toJson(obj);
	}
	
	public static String generateCustomerJson(final CustomerJson customerJson) {
		final Gson gson = new Gson();
		return gson.toJson(customerJson);
	}
}
