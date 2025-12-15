package com.apb.facades.comparator.order.entry;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;

import java.util.Comparator;


public class AsahiOrderEntryDataComparator
{
	public enum SORT_BASED_ON implements Comparator<OrderEntryData>
	{
		WET_SORT
		{
			public int compare(final OrderEntryData o1, final OrderEntryData o2)
			{
				if (null == o1.getWetItem() || null == o2.getWetItem())
				{
					return 0;
				}
				return o1.getWetItem().compareTo(o2.getWetItem());
			}
		},
		BRAND_SORT
		{
			public int compare(final OrderEntryData o1, final OrderEntryData o2)
			{
				if ((null != o1.getProduct().getBrand() && null != o1.getProduct().getApbBrand().getName())
						&& (null != o2.getProduct().getBrand() && null != o2.getProduct().getApbBrand().getName()))
				{
					return o1.getProduct().getApbBrand().getName().compareTo(o2.getProduct().getApbBrand().getName());
				}
				else
				{
					return 0;
				}
			}
		},
		BONUS_STOCK_SORT
		{
			public int compare(final OrderEntryData entryData1, final OrderEntryData entryData2)
			{
				if (entryData1.getProduct().getCode().equals(entryData2.getProduct().getCode()))
				{
					return (entryData1.getIsBonusStock().booleanValue() ? 1 : -1);
				}
				else
				{
					return entryData1.getProduct().getCode().compareTo(entryData2.getProduct().getCode());
				}
			}

		},
		PRODUCT_NAME_SORT
		{
			public int compare(final OrderEntryData o1, final OrderEntryData o2)
			{
				if (null == o1.getProduct().getName() || null == (o2.getProduct().getName()))
				{
					return 0;
				}
				return o1.getProduct().getName().compareTo(o2.getProduct().getName());
			}
		},
		PRODUCT_CODE_SORT
		{
			public int compare(final OrderEntryData o1, final OrderEntryData o2)
			{
				if (null == o1.getProduct().getCode() || null == (o2.getProduct().getCode()))
				{
					return 0;
				}
				return o1.getProduct().getCode().compareTo(o2.getProduct().getCode());
			}
		};


	}

	private AsahiOrderEntryDataComparator()
	{
		// default constructor
	}

	public static Comparator<OrderEntryData> getComparator(final SORT_BASED_ON... multipleOptions)
	{
		return new Comparator<OrderEntryData>()
		{
			public int compare(final OrderEntryData o1, final OrderEntryData o2)
			{
				for (final SORT_BASED_ON option : multipleOptions)
				{
					final int result = option.compare(o1, o2);
					if (result != 0)
					{
						return result;
					}
				}
				return 0;
			}
		};

	}
}
