/**
 *
 */
package com.sabmiller.core.comparators;

import java.util.Comparator;
import java.util.Objects;

import com.sabmiller.core.model.AbstractDealBenefitModel;


/**
 * The Class DealBenefitScaleComparator.
 */
public class DealBenefitScaleComparator implements Comparator<AbstractDealBenefitModel>
{

	/** The Constant INSTANCE. */
	public static final DealBenefitScaleComparator INSTANCE = new DealBenefitScaleComparator();

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final AbstractDealBenefitModel o1, final AbstractDealBenefitModel o2)
	{
		return Objects.compare(o1.getScale(), o2.getScale(), Comparator.naturalOrder());
	}
}
