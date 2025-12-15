package com.sabmiller.facades.util;

import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;

import java.util.Comparator;


@SuppressWarnings("SE_COMPARATOR_SHOULD_BE_SERIALIZABLE")
public class FacetComparator implements Comparator<FacetValueData<SearchStateData>>
{
	@Override
	public int compare(final FacetValueData<SearchStateData> o1, final FacetValueData<SearchStateData> o2)
	{
		if (o1.getCount() < o2.getCount())
		{
			return 1;
		}
		else if (o1.getCount() == o2.getCount())
		{
			return o1.getName().compareTo(o2.getName());
		}
		else
		{
			return -1;
		}
	}
}
