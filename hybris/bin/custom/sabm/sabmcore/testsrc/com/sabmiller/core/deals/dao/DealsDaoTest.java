/**
 *
 *@author joshua.a.antony
 */
package com.sabmiller.core.deals.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.sabmiller.core.deals.SabmProductSampleDataTest;
import com.sabmiller.core.model.DealModel;


@IntegrationTest
public class DealsDaoTest extends SabmProductSampleDataTest
{
	@Resource
	private DealsDao dealsDao;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
	}


	@Test
	public void testGetDeals()
	{

		final List<DealModel> deals = dealsDao.getDeals(companyModel, fromCal.getTime(), toCal.getTime());
		assertTrue(deals.size() == 5);
		assertEquals(dealCode1, deals.get(0).getCode());
		assertEquals(dealCode2, deals.get(1).getCode());
		assertEquals(dealCode4, deals.get(3).getCode());
	}

	@Test
	public void testGetDealsForProduct()
	{
		final List<DealModel> d1 = dealsDao.getDealsForProduct(companyModel, Arrays.asList(dcp3), fromCal.getTime(),
				toCal.getTime());
		assertTrue(d1.size() == 1);
		assertEquals(dealCode3, d1.get(0).getCode());


		final List<DealModel> deals = dealsDao.getDealsForProduct(companyModel, Arrays.asList(dcp1), fromCal.getTime(),
				toCal.getTime());
		assertTrue(deals.size() == 2);
		assertEquals(dealCode1, deals.get(0).getCode());
		assertEquals(dealCode5, deals.get(1).getCode());


		final List<DealModel> d = dealsDao.getDealsForProduct(companyModel, Arrays.asList(dealBenefitProduct), fromCal.getTime(),
				toCal.getTime());
		assertTrue(d.isEmpty());
	}

	public void testGetSpecificDeals()
	{
		final List<DealModel> deals = dealsDao.getNonComplexDeals(companyModel, true);
		assertTrue(deals.size() == 3);
		assertEquals(dealCode1, deals.get(0).getCode());
		assertEquals(dealCode3, deals.get(1).getCode());
	}

}
