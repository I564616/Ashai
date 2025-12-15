/**
 *
 */
package com.sabmiller.core.comparators;

import de.hybris.platform.commerceservices.util.AbstractComparator;

import com.sabmiller.core.model.ProductUOMMappingModel;

import java.util.Comparator;
import java.util.Objects;


/**
 * Product UOM Sort by name
 *
 * @author xue.zeng
 *
 */
public class ProductUOMComparator extends AbstractComparator<ProductUOMMappingModel>
{
	public static final ProductUOMComparator INSTANCE = new ProductUOMComparator();

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commerceservices.util.AbstractComparator#compareInstances(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	protected int compareInstances(final ProductUOMMappingModel uom1, final ProductUOMMappingModel uom2)
	{
		if (uom1 != null && uom2 != null)
		{
			return Objects.compare(uom1.getQtyConversion(), uom2.getQtyConversion(), Comparator.naturalOrder());
		}
		else if (uom1 == uom2)
		{
			return 0;
		}
		else if (uom1 == null)
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}
}
