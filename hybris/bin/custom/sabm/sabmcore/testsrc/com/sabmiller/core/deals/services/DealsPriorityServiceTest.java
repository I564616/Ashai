/**
 *
 */
package com.sabmiller.core.deals.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.sabmiller.core.deals.vo.DealsResponse;
import com.sabmiller.core.deals.vo.DealsResponse.DealItem;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.integration.sap.deals.pricediscount.response.PricingDiscountConditionsResponse;
import com.sabmiller.integration.sap.deals.pricediscount.response.PricingDiscountConditionsResponse.PricingDiscountConditionsHeader;
import com.sabmiller.integration.sap.deals.pricediscount.response.PricingDiscountConditionsResponse.PricingDiscountConditionsItem;


/**
 * @author joshua.a.antony
 *
 */
@IntegrationTest
public class DealsPriorityServiceTest
{
	private final DealsPriorityService dealsPriorityService = new DefaultDealsPriorityService();

	private static final String DATE_PATTERN = "yyyy-MM-dd";

	@Test
	public void testFetchOverlappingRecords() throws ParseException
	{
		final List<DealItem> dealItems = new ArrayList<DealItem>();
		dealItems.add(mockDealItem("2015-01-01", "2015-02-25", "1", "20", "Mat1"));
		dealItems.add(mockDealItem("2015-01-15", "2015-03-15", "2", "30", "Mat1"));

		dealItems.add(mockDealItem("2015-01-01", "2015-02-25", "1", "40", "Mat2"));
		dealItems.add(mockDealItem("2015-01-15", "2015-03-15", "2", "50", "Mat2"));
		dealItems.add(mockDealItem("2014-11-10", "2014-12-10", "3", "60", "Mat2"));
		dealItems.add(mockDealItem("2014-10-01", "2015-04-15", "4", "70", "Mat2"));

		dealItems.add(mockDealItem("2015-01-01", "2015-02-25", "5", "80", "Mat3"));
		dealItems.add(mockDealItem("2015-01-15", "2015-03-15", "6", "90", "Mat3"));
		dealItems.add(mockDealItem("2016-01-15", "2016-03-15", "6", "100", "Mat3"));

		final Map<String, List<DealItem>> map = dealsPriorityService.fetchOverlappingRecords(dealItems);
		assertTrue(map.size() == 3);
		assertTrue(map.containsKey("Mat13CAS"));
		assertTrue(map.containsKey("Mat23CAS"));
		assertTrue(map.containsKey("Mat33CAS"));

		assertTrue(map.get("Mat13CAS").size() == 2);
		assertEquals("20", map.get("Mat13CAS").get(0).getAmount());
		assertEquals("30", map.get("Mat13CAS").get(1).getAmount());

		assertTrue(map.get("Mat23CAS").size() == 4);
		assertEquals("40", map.get("Mat23CAS").get(0).getAmount());
		assertEquals("50", map.get("Mat23CAS").get(1).getAmount());
		assertEquals("60", map.get("Mat23CAS").get(2).getAmount());
		assertEquals("70", map.get("Mat23CAS").get(3).getAmount());

		assertTrue(map.get("Mat33CAS").size() == 3);
		assertEquals("80", map.get("Mat33CAS").get(0).getAmount());
		assertEquals("90", map.get("Mat33CAS").get(1).getAmount());
		assertEquals("100", map.get("Mat33CAS").get(2).getAmount());
	}

	@Test
	public void testMergeOverlappingDeals() throws ParseException
	{
		final DealsResponse response = mockDealResponse();
		dealsPriorityService.mergeOverlappingDeals(response);

		Collections.sort(response.getItems(), new Comparator<DealItem>()
		{
			@Override
			public int compare(final DealItem o1, final DealItem o2)
			{
				return SabmDateUtils.toDate(o1.getValidFrom()).compareTo(SabmDateUtils.toDate(o2.getValidFrom()));
			}
		});

		final List<DealItem> items = response.getItems();
		assertEquals("2015-01-01", SabmDateUtils.toString(items.get(0).getValidFrom(), DATE_PATTERN));
		assertEquals("2015-02-14", SabmDateUtils.toString(items.get(0).getValidTo(), DATE_PATTERN));
		assertEquals("16", items.get(0).getAmount());
		assertEquals("16", items.get(1).getAmount());

		assertEquals("2015-02-15", SabmDateUtils.toString(items.get(2).getValidFrom(), DATE_PATTERN));
		assertEquals("2015-03-10", SabmDateUtils.toString(items.get(2).getValidTo(), DATE_PATTERN));
		assertEquals("2", items.get(2).getAmount());
		assertEquals("2", items.get(3).getAmount());

		assertEquals("2015-03-11", SabmDateUtils.toString(items.get(4).getValidFrom(), DATE_PATTERN));
		assertEquals("2015-03-14", SabmDateUtils.toString(items.get(4).getValidTo(), DATE_PATTERN));
		assertEquals("16", items.get(4).getAmount());
		assertEquals("16", items.get(5).getAmount());

		assertEquals("2015-03-15", SabmDateUtils.toString(items.get(6).getValidFrom(), DATE_PATTERN));
		assertEquals("2015-03-25", SabmDateUtils.toString(items.get(6).getValidTo(), DATE_PATTERN));
		assertEquals("6", items.get(6).getAmount());
		assertEquals("6", items.get(7).getAmount());

		assertEquals("2015-03-26", SabmDateUtils.toString(items.get(8).getValidFrom(), DATE_PATTERN));
		assertEquals("2015-03-31", SabmDateUtils.toString(items.get(8).getValidTo(), DATE_PATTERN));
		assertEquals("16", items.get(8).getAmount());
		assertEquals("16", items.get(9).getAmount());

		assertEquals("2015-04-01", SabmDateUtils.toString(items.get(10).getValidFrom(), DATE_PATTERN));
		assertEquals("2015-06-30", SabmDateUtils.toString(items.get(10).getValidTo(), DATE_PATTERN));
		assertEquals("8", items.get(10).getAmount());
		assertEquals("8", items.get(11).getAmount());

		assertEquals("2015-07-01", SabmDateUtils.toString(items.get(12).getValidFrom(), DATE_PATTERN));
		assertEquals("2015-07-10", SabmDateUtils.toString(items.get(12).getValidTo(), DATE_PATTERN));
		assertEquals("12", items.get(12).getAmount());
		assertEquals("12", items.get(13).getAmount());

		assertEquals("2015-07-11", SabmDateUtils.toString(items.get(14).getValidFrom(), DATE_PATTERN));
		assertEquals("2015-08-10", SabmDateUtils.toString(items.get(14).getValidTo(), DATE_PATTERN));
		assertEquals("14", items.get(14).getAmount());
		assertEquals("14", items.get(15).getAmount());

		assertEquals("2015-08-11", SabmDateUtils.toString(items.get(16).getValidFrom(), DATE_PATTERN));
		assertEquals("2015-09-17", SabmDateUtils.toString(items.get(16).getValidTo(), DATE_PATTERN));
		assertEquals("16", items.get(16).getAmount());
		assertEquals("16", items.get(17).getAmount());

		assertEquals("2015-09-18", SabmDateUtils.toString(items.get(18).getValidFrom(), DATE_PATTERN));
		assertEquals("2015-09-30", SabmDateUtils.toString(items.get(18).getValidTo(), DATE_PATTERN));
		assertEquals("24", items.get(18).getAmount());
		assertEquals("24", items.get(19).getAmount());

		assertEquals("2015-10-01", SabmDateUtils.toString(items.get(20).getValidFrom(), DATE_PATTERN));
		assertEquals("2015-10-01", SabmDateUtils.toString(items.get(20).getValidTo(), DATE_PATTERN));
		assertEquals("22", items.get(20).getAmount());
		assertEquals("22", items.get(21).getAmount());

		assertEquals("2015-10-02", SabmDateUtils.toString(items.get(22).getValidFrom(), DATE_PATTERN));
		assertEquals("2015-10-07", SabmDateUtils.toString(items.get(22).getValidTo(), DATE_PATTERN));
		assertEquals("18", items.get(22).getAmount());
		assertEquals("18", items.get(23).getAmount());

		assertEquals("2015-10-08", SabmDateUtils.toString(items.get(24).getValidFrom(), DATE_PATTERN));
		assertEquals("2015-10-08", SabmDateUtils.toString(items.get(24).getValidTo(), DATE_PATTERN));
		assertEquals("22", items.get(24).getAmount());
		assertEquals("22", items.get(25).getAmount());

		assertEquals("2015-10-09", SabmDateUtils.toString(items.get(26).getValidFrom(), DATE_PATTERN));
		assertEquals("2015-10-17", SabmDateUtils.toString(items.get(26).getValidTo(), DATE_PATTERN));
		assertEquals("20", items.get(26).getAmount());
		assertEquals("20", items.get(27).getAmount());

		assertEquals("2015-10-18", SabmDateUtils.toString(items.get(28).getValidFrom(), DATE_PATTERN));
		assertEquals("2015-10-18", SabmDateUtils.toString(items.get(28).getValidTo(), DATE_PATTERN));
		assertEquals("22", items.get(28).getAmount());
		assertEquals("22", items.get(29).getAmount());

		assertEquals("2015-10-19", SabmDateUtils.toString(items.get(30).getValidFrom(), DATE_PATTERN));
		assertEquals("9999-12-31", SabmDateUtils.toString(items.get(30).getValidTo(), DATE_PATTERN));
		assertEquals("24", items.get(30).getAmount());
		assertEquals("24", items.get(31).getAmount());

		assertEquals(16, totalMaterials(items, "MAT1"));
		assertEquals(16, totalMaterials(items, "MAT2"));
	}

	private int totalMaterials(final List<DealItem> dealItems, final String material)
	{
		int counter = 0;
		for (final DealItem item : dealItems)
		{
			if (material.equals(item.getMaterial()))
			{
				++counter;
			}
		}
		return counter;
	}

	private DealsResponse mockDealResponse() throws ParseException
	{
		final PricingDiscountConditionsResponse discountConditionsResponse = new PricingDiscountConditionsResponse();

		final PricingDiscountConditionsHeader header = new PricingDiscountConditionsHeader();
		header.setCustomer("123");
		header.setSalesOrganisation("SO123");

		discountConditionsResponse.setPricingDiscountConditionsHeader(header);
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-02-15", "2015-03-10", "1", "2", "MAT1"));
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-02-15", "2015-03-10", "2", "4", "MAT1"));//Duplicate with lower priority
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-03-15", "2015-03-25", "3", "6", "MAT1"));
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-04-01", "2015-06-30", "04", "8", "MAT1"));
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-05-15", "2015-06-20", "5", "10", "MAT1"));
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-06-05", "2015-07-10", "6", "12", "MAT1"));
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-07-01", "2015-08-10", "7", "14", "MAT1"));
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-01-01", "2015-09-17", "08", "16", "MAT1"));//Split into multiple deals
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-02-15", "2015-07-10", "12", "16", "MAT1"));//No effect
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-07-11", "2015-09-17", "11", "16", "MAT1"));//No effect
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-10-02", "2015-10-07", "17", "18", "MAT1"));
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-10-09", "2015-10-17", "18", "20", "MAT1"));//Just 1 day gap with previous one
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-10-01", "2015-10-18", "19", "22", "MAT1"));

		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-10-01", "2015-10-18", "19", "22", "MAT1"));
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-10-01", "2015-10-18", "19", "22", "MAT1"));

		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-01-01", "9999-12-31", "25", "24", "MAT1"));



		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-02-15", "2015-03-10", "1", "2", "MAT2"));
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-02-15", "2015-03-10", "2", "4", "MAT2"));//Duplicate with lower priority
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-03-15", "2015-03-25", "3", "6", "MAT2"));
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-04-01", "2015-06-30", "04", "8", "MAT2"));
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-05-15", "2015-06-20", "5", "10", "MAT2"));
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-06-05", "2015-07-10", "6", "12", "MAT2"));
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-07-01", "2015-08-10", "7", "14", "MAT2"));
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-01-01", "2015-09-17", "08", "16", "MAT2"));//Split into multiple deals
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-02-15", "2015-07-10", "12", "16", "MAT2"));//No effect
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-07-11", "2015-09-17", "11", "16", "MAT2"));//No effect
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-10-02", "2015-10-07", "17", "18", "MAT2"));
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-10-09", "2015-10-17", "18", "20", "MAT2"));//Just 1 day gap with previous one
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-10-01", "2015-10-18", "19", "22", "MAT2"));

		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-10-01", "2015-10-18", "19", "22", "MAT2"));
		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-10-01", "2015-10-18", "19", "22", "MAT2"));

		discountConditionsResponse.getPricingDiscountConditionsItem().add(mockItem("2015-01-01", "9999-12-31", "25", "24", "MAT2"));


		return new DealsResponse(discountConditionsResponse);
	}

	private PricingDiscountConditionsItem mockItem(final String from, final String to, final String priority, final String amount,
			final String material) throws ParseException
	{
		final Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse(from);
		final Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse(to);
		final PricingDiscountConditionsItem item = new PricingDiscountConditionsItem();
		item.setMaterial(material);
		item.setMinimumQuantity("3");
		item.setSaleUnit("CAS");
		item.setUnit("CAS");
		item.setUnitOfMeasure("CAS");
		item.setUnitOfMeasure2("CAS");
		item.setAmount(amount);
		item.setPriority(priority);
		item.setValidFrom(SabmDateUtils.getGregorianCalendar(d1));
		item.setValidTo(SabmDateUtils.getGregorianCalendar(d2));
		return item;
	}

	private DealItem mockDealItem(final String from, final String to, final String priority, final String amount,
			final String material) throws ParseException
	{
		final Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse(from);
		final Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse(to);
		final DealItem item = new DealItem();
		item.setMaterial(material);
		item.setMinimumQuantity(3);
		item.setSaleUnit("CAS");
		item.setUnit("CAS");
		item.setUnitOfMeasure("CAS");
		item.setUnitOfMeasure2("CAS");
		item.setAmount(amount);
		item.setPriority(priority);
		item.setValidFrom(SabmDateUtils.getGregorianCalendar(d1));
		item.setValidTo(SabmDateUtils.getGregorianCalendar(d2));
		return item;
	}
}
