
package com.sabmiller.core.order.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.order.dao.SabmOrderDao;


public class SABMOrderDaoTest extends ServicelayerTransactionalTest
{
	@Resource
	private SabmOrderDao orderDao;
	@Resource
	private SabmB2BUnitService defaultSabmB2BUnitService;
	private B2BUnitModel unitmodel1, unitmodel2;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/test/sabmOrderDaoTest1.impex", "UTF-8");
	}

	@After
	public void removeModels() throws Exception
	{
		importCsv("/test/sabmOrderDaoTest2.impex", "UTF-8");
	}

	@Test
	public void getPagedOrdersByB2BUnit()
	{

		unitmodel1 = defaultSabmB2BUnitService.getUnitForUid("Test Sample B2B Sydney");
		unitmodel2 = defaultSabmB2BUnitService.getUnitForUid("Test Sample B2B Brisbane");

		final List<OrderModel> pagedOrdersByB2BUnit1 = orderDao.getPreviousPagedOrdersByB2BUnit(unitmodel1, 0, new Date());
		final List<OrderModel> pagedOrdersByB2BUnit2 = orderDao.getPreviousPagedOrdersByB2BUnit(unitmodel2, 0, new Date());

		Assert.assertEquals(4, pagedOrdersByB2BUnit1.size());
		Assert.assertEquals(0, pagedOrdersByB2BUnit2.size());
	}


}
