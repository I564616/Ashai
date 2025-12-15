package com.sabmiller.core.comparators;

import java.util.Comparator;

import com.sabmiller.facades.b2bunit.data.ShippingCarrier;


/**
 * Created by evariz.d.paragoso on 7/25/17.
 */
public class ShippingCarrierComparator implements Comparator<ShippingCarrier>
{

	@Override
	public int compare(final ShippingCarrier shipcar1, final ShippingCarrier shipcar2)
	{
		return shipcar1.getDescription().compareToIgnoreCase(shipcar2.getDescription());
	}
}
