/**
 *
 */
package com.sabmiller.facades.cart.impl;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.sabmiller.facades.deal.data.DealBaseProductJson;
import com.sabmiller.facades.deal.data.DealFreeProductJson;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.deal.data.DealRangeJson;


/**
 * this helper is to check the deals applied time base the cart entry
 *
 * @author xiaowu.a.zhang
 * @date 06/28/2016
 *
 */
public class DealAppliedTimesHelper
{

	/**
	 * check how many times the deal have been applied and modify the deal json base the applied times only the
	 * proportional free product deal could invoke this method the deal must have been applied
	 *
	 * @param dealJson
	 *           the deal need to be check
	 * @param entries
	 *           the cart entries
	 */
	public void checkDealAppliedTimes(final DealJson dealJson, final List<OrderEntryData> entries)
	{
		int minMultiplier = Integer.MAX_VALUE;
		for (final DealRangeJson dealRangeJson : CollectionUtils.emptyIfNull(dealJson.getRanges()))
		{
			int thisMultiplier = 0;
			if (dealRangeJson.getMinQty() == 0)
			{
				thisMultiplier = checkMandatoryProductCondition(dealRangeJson, entries);
			}
			else
			{
				thisMultiplier = checkOptionalProductCondition(dealRangeJson, entries);
			}
			minMultiplier = minMultiplier > thisMultiplier ? thisMultiplier : minMultiplier;
		}
		if (minMultiplier != Integer.MAX_VALUE)
		{
			for (final DealFreeProductJson freeProductJson : CollectionUtils.emptyIfNull(dealJson.getSelectableProducts()))
			{
				if (freeProductJson.getProportionalFreeGood())
				{
					final Integer oldQty = freeProductJson.getQty().get(0);
					freeProductJson.getQty().put(0, oldQty * minMultiplier);
				}
			}
		}
	}

	/**
	 * check how many time the mandatory product condition have been applied
	 *
	 * @param dealRangeJson
	 *           the deal json need to be check
	 * @param entries
	 *           the cart entries
	 * @return the min applied times
	 */
	protected int checkMandatoryProductCondition(final DealRangeJson dealRangeJson, final List<OrderEntryData> entries)
	{
		final Map<String, Integer> conditionProductQtyMap = new HashMap<>();
		bulidProductQtyMap(dealRangeJson, conditionProductQtyMap);
		int minTimes = Integer.MAX_VALUE;
		for (final OrderEntryData entry : CollectionUtils.emptyIfNull(entries))
		{
			if (BooleanUtils.isNotTrue(entry.isIsFreeGood()) && conditionProductQtyMap.containsKey(entry.getProduct().getCode()))
			{
				final int newTimes = (int) Math
						.floor(entry.getBaseQuantity() / conditionProductQtyMap.get(entry.getProduct().getCode()));
				minTimes = minTimes > newTimes ? newTimes : minTimes;
			}
		}
		return minTimes;
	}

	/**
	 * build the product to qty map
	 *
	 * @param dealRangeJson
	 *           the deal json need to be check
	 * @param conditionProductQtyMap
	 *           product to qty map
	 */
	protected void bulidProductQtyMap(final DealRangeJson dealRangeJson, final Map<String, Integer> conditionProductQtyMap)
	{
		for (final DealBaseProductJson dealBaseProductJson : CollectionUtils.emptyIfNull(dealRangeJson.getBaseProducts()))
		{
			conditionProductQtyMap.put(dealBaseProductJson.getProductCode(), dealBaseProductJson.getQty());
		}
	}

	/**
	 * check how many time the optional product condition have been applied
	 *
	 * @param dealRangeJson
	 *           the deal json need to be check
	 * @param entries
	 *           the cart entries
	 * @return the min applied times
	 */
	protected int checkOptionalProductCondition(final DealRangeJson dealRangeJson, final List<OrderEntryData> entries)
	{
		final List<String> productCodes = getProductCodesFromDealRange(dealRangeJson);
		long totalQty = 0;
		for (final OrderEntryData entry : CollectionUtils.emptyIfNull(entries))
		{
			if (BooleanUtils.isNotTrue(entry.isIsFreeGood()) && productCodes.contains(entry.getProduct().getCode()))
			{
				totalQty += entry.getBaseQuantity();
			}
		}
		return (int) Math.floor(totalQty / dealRangeJson.getMinQty());
	}

	/**
	 * get the product code list in the range
	 *
	 * @param dealRangeJson
	 *           the deal json need to be check
	 * @return the product code list of the range
	 */
	protected List<String> getProductCodesFromDealRange(final DealRangeJson dealRangeJson)
	{
		final List<String> productCodes = new ArrayList<>();
		for (final DealBaseProductJson dealBaseProductJson : CollectionUtils.emptyIfNull(dealRangeJson.getBaseProducts()))
		{
			productCodes.add(dealBaseProductJson.getProductCode());
		}
		return productCodes;
	}

}
