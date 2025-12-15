/**
 *
 */
package com.sabmiller.core.comparators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sabmiller.facades.deal.data.DealConditionData;
import com.sabmiller.facades.deal.data.DealConditionGroupData;
import com.sabmiller.facades.deal.data.DealData;


/**
 * @author xue.zeng
 *
 */
@UnitTest
public class DealBrandMinQtyComparatorTest
{
	private static DealBrandMinQtyComparator dealBrandMinQtyComparator = new DealBrandMinQtyComparator();

	@Before
	public void setUp()
	{
	}

	@Test
	public void testCompare()
	{
		final ProductData product1 = new ProductData();
		product1.setBrand("A");

		final ProductData product2 = new ProductData();
		product2.setBrand("B");

		final DealConditionData condition1 = new DealConditionData();
		condition1.setProduct(product1);
		condition1.setMinQty(Integer.valueOf(1));
		final DealConditionData condition2 = new DealConditionData();
		condition2.setProduct(product2);
		condition2.setMinQty(Integer.valueOf(2));

		final List<DealConditionData> dealConditions1 = new ArrayList<DealConditionData>();
		dealConditions1.add(condition1);
		final List<DealConditionData> dealConditions2 = new ArrayList<DealConditionData>();
		dealConditions2.add(condition2);

		final DealConditionGroupData group1 = new DealConditionGroupData();
		group1.setDealConditions(dealConditions1);
		final DealConditionGroupData group2 = new DealConditionGroupData();
		group2.setDealConditions(dealConditions2);

		final DealData deal1 = new DealData();
		deal1.setDealConditionGroupData(group1);
		final DealData deal2 = new DealData();
		deal2.setDealConditionGroupData(group2);

		int comVal = dealBrandMinQtyComparator.compareInstances(deal2, deal1);
		Assert.assertEquals(1, comVal);

		condition1.setMinQty(Integer.valueOf(1));
		condition2.setMinQty(Integer.valueOf(1));
		comVal = dealBrandMinQtyComparator.compareInstances(deal2, deal1);
		Assert.assertEquals(1, comVal);

		condition1.setMinQty(Integer.valueOf(2));
		condition2.setMinQty(Integer.valueOf(1));
		comVal = dealBrandMinQtyComparator.compareInstances(deal2, deal1);
		Assert.assertEquals(1, comVal);

		product1.setBrand("B");
		product2.setBrand("A");
		condition1.setMinQty(Integer.valueOf(1));
		condition2.setMinQty(Integer.valueOf(2));
		comVal = dealBrandMinQtyComparator.compareInstances(deal2, deal1);
		Assert.assertEquals(-1, comVal);

		condition1.setMinQty(Integer.valueOf(2));
		condition2.setMinQty(Integer.valueOf(2));
		comVal = dealBrandMinQtyComparator.compareInstances(deal2, deal1);
		Assert.assertEquals(-1, comVal);

		condition1.setMinQty(Integer.valueOf(2));
		condition2.setMinQty(Integer.valueOf(1));
		comVal = dealBrandMinQtyComparator.compareInstances(deal2, deal1);
		Assert.assertEquals(-1, comVal);

		product1.setBrand("B");
		product2.setBrand("B");
		condition1.setMinQty(Integer.valueOf(1));
		condition2.setMinQty(Integer.valueOf(2));
		comVal = dealBrandMinQtyComparator.compareInstances(deal2, deal1);
		Assert.assertEquals(1, comVal);

		condition1.setMinQty(Integer.valueOf(1));
		condition2.setMinQty(Integer.valueOf(1));
		comVal = dealBrandMinQtyComparator.compareInstances(deal2, deal1);
		Assert.assertEquals(0, comVal);

		condition1.setMinQty(Integer.valueOf(2));
		condition2.setMinQty(Integer.valueOf(1));
		comVal = dealBrandMinQtyComparator.compareInstances(deal2, deal1);
		Assert.assertEquals(-1, comVal);

		comVal = dealBrandMinQtyComparator.compareInstances(deal1, deal1);
		Assert.assertEquals(0, comVal);

		comVal = dealBrandMinQtyComparator.compareInstances(null, deal1);
		Assert.assertEquals(-1, comVal);

		comVal = dealBrandMinQtyComparator.compareInstances(null, deal1);
		Assert.assertEquals(-1, comVal);

		comVal = dealBrandMinQtyComparator.compareInstances(deal1, null);
		Assert.assertEquals(1, comVal);

		comVal = dealBrandMinQtyComparator.compareInstances(null, null);
		Assert.assertEquals(0, comVal);
	}
}
