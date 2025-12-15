/**
 *
 */
package com.sabmiller.core.comparators;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.util.AbstractComparator;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.sabmiller.facades.deal.data.DealConditionData;
import com.sabmiller.facades.deal.data.DealConditionGroupData;
import com.sabmiller.facades.deal.data.DealData;


/**
 * Comparator SABMAlcoholProductModel.brand and ProductDealCondition.minQty
 *
 * @author xue.zeng
 *
 */
public class DealBrandMinQtyComparator extends AbstractComparator<DealData>
{

	public static final DealBrandMinQtyComparator INSTANCE = new DealBrandMinQtyComparator();

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commerceservices.util.AbstractComparator#compareInstances(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	protected int compareInstances(final DealData deal1, final DealData deal2)
	{
		if (null != deal1 && null != deal2)
		{
			return dealCompare(deal1, deal2);
		}
		else if (deal1 == deal2)
		{
			return 0;
		}
		else if (null == deal1)
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}

	/**
	 * Comparing product deals
	 *
	 * @param deal1
	 * @param deal2
	 * @return int
	 */
	private int dealCompare(final DealData deal1, final DealData deal2)
	{
		final DealConditionGroupData group1 = deal1.getDealConditionGroupData();
		final DealConditionGroupData group2 = deal2.getDealConditionGroupData();

		if (null != group1 && null != group2)
		{
			return preDealCondition(group1, group2);
		}
		else if (group1 == group2)
		{
			return 0;
		}
		else if (null == group1)
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}

	/**
	 * Obtain condition in the deal
	 *
	 * @param group1
	 * @param group2
	 * @return int
	 */
	private int preDealCondition(final DealConditionGroupData group1, final DealConditionGroupData group2)
	{
		final List<DealConditionData> conditions1 = group1.getDealConditions();
		final List<DealConditionData> conditions2 = group2.getDealConditions();
		if (CollectionUtils.isNotEmpty(conditions1) && CollectionUtils.isNotEmpty(conditions2))
		{
			final DealConditionData condition1 = conditions1.get(0);
			final DealConditionData condition2 = conditions2.get(0);
			return dealConditionCompare(condition1, condition2);
		}
		else if (conditions1 == conditions2)
		{
			return 0;
		}
		else if (CollectionUtils.isEmpty(conditions1))
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}

	/**
	 * Deal condition in the product brand and minQty comparison
	 *
	 * @param condition1
	 * @param condition2
	 */
	private int dealConditionCompare(final DealConditionData condition1, final DealConditionData condition2)
	{
		if (null != condition1 && null != condition2)
		{
			final int brandCt = productBrandCompare(condition1, condition2);

			final int minQtyCt = Objects.compare(condition1.getMinQty(), condition2.getMinQty(), Comparator.naturalOrder());

			//a) All other brands in alphabetical order
			//b) Within the brands, the discounts will be ranked based on minimum order qty (1) - to max qty (unlimited)
			if (brandCt == 0)
			{
				return minQtyCt;
			}

			return brandCt;
		}
		else if (condition1 == condition2)
		{
			return 0;
		}
		else if (null == condition1)

		{
			return -1;
		}
		else
		{
			return 1;
		}
	}

	/**
	 * product brand in alphabetical order
	 *
	 * @param condition1
	 * @param condition2
	 * @return int
	 */
	private int productBrandCompare(final DealConditionData condition1, final DealConditionData condition2)
	{
		final ProductData productData1 = condition1.getProduct();
		final ProductData productData2 = condition2.getProduct();
		if (null != productData1 && null != productData2)
		{
			return Objects.compare(StringUtils.trimToEmpty(productData1.getBrand()).toLowerCase(),
					StringUtils.trimToEmpty(productData2.getBrand()).toLowerCase(), Comparator.naturalOrder());
		}
		else if (productData1 == productData2)
		{
			return 0;
		}
		else if (null == productData1)
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}

}
