/**
 *
 */
package com.sabmiller.core.b2b.services;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.sabmiller.core.cart.service.impl.SABMCalculationServiceImpl;
import com.sabmiller.core.enums.DealConditionStatus;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem.DealCondition;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.Before;
/**
 *
 */
public class SABMCalculationServiceImplTest
{
	@Mock
	private ModelService modelService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testSetRejectedComplexDeal() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException
	{
		Method method = null;

		final AbstractOrderModel cart = new AbstractOrderModel();
		final List<CartDealConditionModel> deals = new ArrayList<CartDealConditionModel>();
		final CartDealConditionModel dealCondition1 = new CartDealConditionModel();
		final CartDealConditionModel dealCondition2 = new CartDealConditionModel();
		dealCondition1.setStatus(DealConditionStatus.MANUAL);
		dealCondition2.setStatus(DealConditionStatus.MANUAL);
		final DealModel dealmodel1 = new DealModel();
		final DealModel dealmodel2 = new DealModel();
		dealmodel1.setDealType(DealTypeEnum.COMPLEX);
		dealmodel2.setDealType(DealTypeEnum.COMPLEX);
		dealmodel1.setCode("deal1");
		dealmodel2.setCode("deal2");
		dealCondition1.setDeal(dealmodel1);
		dealCondition2.setDeal(dealmodel2);
		deals.add(dealCondition1);
		deals.add(dealCondition2);
		cart.setComplexDealConditions(deals);
		final SalesOrderSimulateResponse Response = new SalesOrderSimulateResponse();
		final SalesOrderResItem item = new SalesOrderResItem();
		final DealCondition deal = new DealCondition();
		deal.setDealConditionNumber("deal1");
		item.getDealCondition().add(deal);
		Response.getSalesOrderResItem().add(item);
		final SABMCalculationServiceImpl testClass = new SABMCalculationServiceImpl();

		testClass.setModelService(modelService);
		//doNothing().when(testClass.modelService).save(Mockito.any());

		method = testClass.getClass().getDeclaredMethod("setRejectedComplexDeal", new Class[]
		{ AbstractOrderModel.class, SalesOrderSimulateResponse.class });
		method.setAccessible(true);

		method.invoke(testClass, cart, Response);
		Assert.assertEquals(DealConditionStatus.REJECTED, dealCondition2.getStatus());

	}
}
