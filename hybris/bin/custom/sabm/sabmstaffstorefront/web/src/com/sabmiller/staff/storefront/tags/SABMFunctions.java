/**
 *
 */
package com.sabmiller.staff.storefront.tags;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.google.gson.Gson;


/**
 *
 */
public class SABMFunctions
{

	private SABMFunctions()
	{
	}

	public static ImageData getNormalImageForCategoryAndFormat(final CategoryData category, final String format)
	{
		if (category != null && format != null)
		{
			final Collection<ImageData> images = category.getImages();
			return getImage(format, images);
		}
		return null;
	}

	//Add by xiaowu for other packages display
	public static ImageData getNormalImageForVariantAndFormat(final VariantOptionData variant, final String format)
	{
		if (variant != null && format != null)
		{
			final Collection<ImageData> images = variant.getImages();
			return getImage(format, images);
		}
		return null;
	}

	private static ImageData getImage(final String format, final Collection<ImageData> images)
	{
		if (CollectionUtils.isNotEmpty(images))
		{
			for (final ImageData image : images)
			{
				if (format.equals(image.getFormat()))
				{
					return image;
				}
			}
		}
		return null;
	}

	public static boolean displaySubTotal(final AbstractOrderData order)
	{
		boolean isSubTotalToShow = false;

		if (order != null && (isWetCondition(order) || isDeposit(order) || isTotalLoyaltyFeePrice(order) || isDeliveryCost(order)))
		{
			isSubTotalToShow = true;
		}

		return isSubTotalToShow;
	}

	private static boolean isWetCondition(final AbstractOrderData order)
	{
		return order.getWet() != null && order.getWet().getValue().compareTo(BigDecimal.ZERO) > 0;
	}

	private static boolean isDeposit(final AbstractOrderData order)
	{
		return order.getDeposit() != null && order.getDeposit().getValue().compareTo(BigDecimal.ZERO) > 0;
	}

	private static boolean isTotalLoyaltyFeePrice(final AbstractOrderData order)
	{
		return order.getTotalLoyaltyFeePrice() != null && order.getTotalLoyaltyFeePrice().getValue().compareTo(BigDecimal.ZERO) > 0;
	}

	private static boolean isDeliveryCost(final AbstractOrderData order)
	{
		return order.getDeliveryCost() != null && order.getDeliveryCost().getValue().compareTo(BigDecimal.ZERO) > 0;
	}

	public static String generateDealJson(final List<Object> deals)
	{
		final Gson gson = new Gson();
		return gson.toJson(deals);
	}
}
