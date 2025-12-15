/**
 *
 */
package com.sabmiller.core.order.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.session.SessionService;
import com.sabmiller.core.product.SabmProductService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.order.dao.SabmOrderDao;
import com.sabmiller.core.util.SABMFormatterUtils;
import com.sabmiller.facades.smartOrders.json.SmartOrdersJson;
import com.sabmiller.facades.smartOrders.json.SmartOrdersProductsHistoryJson;
import com.sabmiller.facades.smartOrders.json.SmartOrdersProductsJson;


/**
 * @author madhu.c.dasari
 */
@UnitTest
public class SabmB2BOrderServiceTest
{
	@InjectMocks
	DefaultSabmB2BOrderService b2bOrderService = new DefaultSabmB2BOrderService();
	@Mock
	private SabmOrderDao orderDao;
	@Mock
	SessionService sessionService;
	@Mock
	SABMFormatterUtils sabFormatterUtil;
	@Mock
	SabmProductService productService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetPagedOrdersByB2BUnit()
	{

		final B2BUnitModel b2bunit = Mockito.mock(B2BUnitModel.class);

		final List<OrderModel> pagedOrdersByB2BUnit = new ArrayList<>();
		final OrderModel ordermodel = Mockito.mock(OrderModel.class);
		final List<AbstractOrderEntryModel> value = new ArrayList<>();
		final AbstractOrderEntryModel entryModel = Mockito.mock(AbstractOrderEntryModel.class);

		given(entryModel.getQuantity()).willReturn((long) 5);

		final UnitModel value1 = Mockito.mock(UnitModel.class);
		given(entryModel.getUnit()).willReturn(value1);
		final SABMAlcoholVariantProductEANModel eanproduct = Mockito.mock(SABMAlcoholVariantProductEANModel.class);

		final SABMAlcoholVariantProductMaterialModel material = Mockito.mock(SABMAlcoholVariantProductMaterialModel.class);
		given(material.getCode()).willReturn("productcode123456");
		when(entryModel.getProduct()).thenReturn(material);
		when(material.getBaseProduct()).thenReturn(eanproduct);
		when(eanproduct.getPurchasable()).thenReturn(true);
		when(value1.getCode()).thenReturn("unitcode123");
		given(entryModel.getOrder()).willReturn(ordermodel);
		value.add(entryModel);
		given(ordermodel.getEntries()).willReturn(value);
		given(ordermodel.getDate()).willReturn(new Date());
		given(ordermodel.getCreationtime()).willReturn(new Date());
		given(ordermodel.getStatus()).willReturn(OrderStatus.CREATED);
		pagedOrdersByB2BUnit.add(ordermodel);
		when(orderDao.getNextPagedOrdersByB2BUnit(b2bunit, 0, null)).thenReturn(pagedOrdersByB2BUnit);
		when(orderDao.getPreviousPagedOrdersByB2BUnit(b2bunit, 0, null)).thenReturn(pagedOrdersByB2BUnit);

		final SmartOrdersJson pagedOrdersByB2BUnit2 = b2bOrderService.getPagedOrdersByB2BUnit(b2bunit, 0, null, null);
		final List<SmartOrdersProductsJson> products = pagedOrdersByB2BUnit2.getProducts();
		final List<SmartOrdersProductsJson> products1 = new ArrayList<>();
		if (null != products)
		{
			products1.addAll(products);
		}

		final List<SmartOrdersProductsHistoryJson> productsHistoryJson = new ArrayList<>();

		for (final SmartOrdersProductsJson product1 : products1)
		{
			productsHistoryJson.addAll(product1.getHistory());
		}

		Assert.assertNotNull("SmartOrdersJson is null", pagedOrdersByB2BUnit2);
		Assert.assertNotNull("SmartOrdersProductsJson is null", products);
		Assert.assertEquals(0, products1.size());
		Assert.assertEquals(0, productsHistoryJson.size());
	}

	@Test
	public void testExpiredProducts()
	{

		final B2BUnitModel b2bunit = Mockito.mock(B2BUnitModel.class);

		final List<OrderModel> pagedOrdersByB2BUnit = new ArrayList<>();
		final OrderModel ordermodel = Mockito.mock(OrderModel.class);
		final List<AbstractOrderEntryModel> value = new ArrayList<>();
		final AbstractOrderEntryModel entryModel = Mockito.mock(AbstractOrderEntryModel.class);

		given(entryModel.getQuantity()).willReturn((long) 5);

		final UnitModel value1 = Mockito.mock(UnitModel.class);
		given(entryModel.getUnit()).willReturn(value1);
		final SABMAlcoholVariantProductEANModel eanproduct = Mockito.mock(SABMAlcoholVariantProductEANModel.class);

		final SABMAlcoholVariantProductMaterialModel material = Mockito.mock(SABMAlcoholVariantProductMaterialModel.class);
		given(material.getCode()).willReturn("productcode123456");
		when(entryModel.getProduct()).thenReturn(material);
		when(material.getBaseProduct()).thenReturn(eanproduct);
		when(eanproduct.getPurchasable()).thenReturn(false);
		when(value1.getCode()).thenReturn("unitcode123");
		given(entryModel.getOrder()).willReturn(ordermodel);
		value.add(entryModel);
		given(ordermodel.getEntries()).willReturn(value);
		given(ordermodel.getDate()).willReturn(new Date());
		given(ordermodel.getCreationtime()).willReturn(new Date());
		given(ordermodel.getStatus()).willReturn(OrderStatus.CREATED);
		pagedOrdersByB2BUnit.add(ordermodel);
		when(orderDao.getNextPagedOrdersByB2BUnit(b2bunit, 0, null)).thenReturn(pagedOrdersByB2BUnit);
		when(orderDao.getPreviousPagedOrdersByB2BUnit(b2bunit, 0, null)).thenReturn(pagedOrdersByB2BUnit);
		when(sabFormatterUtil.parseDate("2015-03-14", "yyyy-MM-dd")).thenReturn(new Date());

		final SmartOrdersJson pagedOrdersByB2BUnit2 = b2bOrderService.getPagedOrdersByB2BUnit(b2bunit, 0, "2015-03-14", "d");
		final List<SmartOrdersProductsJson> products = pagedOrdersByB2BUnit2.getProducts();
		final List<SmartOrdersProductsJson> products1 = new ArrayList<>();
		if (null != products)
		{
			products1.addAll(products);
		}

		final List<SmartOrdersProductsHistoryJson> productsHistoryJson = new ArrayList<>();

		for (final SmartOrdersProductsJson product1 : products1)
		{
			productsHistoryJson.addAll(product1.getHistory());
		}

		Assert.assertNotNull("SmartOrdersJson is null", pagedOrdersByB2BUnit2);
		Assert.assertNotNull("SmartOrdersProductsJson is null", products);
		Assert.assertEquals(0, products1.size());
		Assert.assertEquals(0, productsHistoryJson.size());
	}

}
