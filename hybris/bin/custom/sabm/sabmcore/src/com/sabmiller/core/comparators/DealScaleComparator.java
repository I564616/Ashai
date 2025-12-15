/**
 *
 */
package com.sabmiller.core.comparators;

import java.util.Comparator;

import com.sabmiller.core.model.DealScaleModel;


/**
 * The Class DealScaleComparator.
 */
public class DealScaleComparator implements Comparator<DealScaleModel>
{

	/** The Constant INSTANCE. */
	public static final DealScaleComparator INSTANCE = new DealScaleComparator();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final DealScaleModel o1, final DealScaleModel o2)
	{
		return o1.getFrom().compareTo(o2.getFrom());
	}
}
