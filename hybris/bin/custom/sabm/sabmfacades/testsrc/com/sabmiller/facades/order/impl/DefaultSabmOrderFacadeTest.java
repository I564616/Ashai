/**
 *
 */
package com.sabmiller.facades.order.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.enums.SalesApplication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.sabmiller.core.b2b.services.DefaultSabmB2BUnitServiceTest;import com.sabmiller.core.product.SabmUnitService;
import com.sabmiller.facades.order.SABMOrderFacade;
import com.sabmiller.facades.product.data.UomData;


/**
 * @author joshua.a.antony
 *
 */
@IntegrationTest
public class DefaultSabmOrderFacadeTest extends DefaultSabmB2BUnitServiceTest
{

	private static final String CODE = "o123";

	@Resource(name = "b2bOrderFacade")
	private SABMOrderFacade orderFacade;

	@Resource(name = "unitService")
	private SabmUnitService unitService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		unitService.createUnit("CS");
		unitService.createUnit("CAS");
		unitService.createUnit("CAR");
	}

	/**
	 * Test condition for order that does not exist in Hybris. In this case, a new order needs to be created.
	 */
	@Test
	public void testPersistNonHybrisOrder()
	{
		orderFacade.persistOrder(createOrderData());
	}

	private OrderData createOrderData()
	{
		final OrderData orderData = new OrderData();
		orderData.setCode(CODE);
		orderData.setSoldTo("0000794396");//The B2BUnitModel is created in parent class
		//orderData.setDeliveryAddress(deliveryAddress);
		//orderData.setDeliveryShippingCarrier(deliveryShippingCarrier);
		orderData.setEntries(createOrderEntries());
		orderData.setNetAmount(createPriceData(500));
		orderData.setTotalPrice(createPriceData(1200.60));
		orderData.setRequestedDeliveryDate(new Date());
		orderData.setSalesApplication(SalesApplication.CALLCENTER.toString());
		orderData.setSapSalesOrderNumber("sap" + CODE);
		//orderData.setSite(site);
		orderData.setSubTotal(createPriceData(990));
		return orderData;
	}

	private List<OrderEntryData> createOrderEntries()
	{
		final List<OrderEntryData> entries = new ArrayList<OrderEntryData>();
		entries.add(createOrderEntry(cupProduct1, 2l, "10", 100.20d, "CAS", "Case"));
		entries.add(createOrderEntry(cupProduct2, 1l, "20", 50.20d, "CAS", "Case"));
		entries.add(createOrderEntry(cupProduct3, 6l, "30", 300.20d, "CAS", "Case"));
		entries.add(createOrderEntry(cupProduct4, 5l, "40", 90.20d, "CAS", "Case"));
		entries.add(createOrderEntry(cupProduct5, 1l, "50", 400.20d, "CAS", "Case"));

		return entries;
	}

	private OrderEntryData createOrderEntry(final String productCode, final long quantity, final String sapLineNumber,
			final double totalPrice, final String unitCode, final String unitName)
	{

		final OrderEntryData orderEntryData = new OrderEntryData();

		orderEntryData.setProduct(createProduct(productCode));
		orderEntryData.setQuantity(quantity);
		orderEntryData.setSapLineNumber(sapLineNumber);
		orderEntryData.setTotalPrice(createPriceData(totalPrice));
		orderEntryData.setUnit(createUnit(unitCode, unitName));

		return orderEntryData;

	}

	private UomData createUnit(final String code, final String name)
	{
		final UomData uomData = new UomData();
		uomData.setCode(code);
		uomData.setName(name);
		return uomData;
	}

	private ProductData createProduct(final String material)
	{
		final ProductData productData = new ProductData();
		productData.setCode(material);
		return productData;
	}

	private PriceData createPriceData(final double d)
	{
		final PriceData priceData = new PriceData();
		priceData.setValue(BigDecimal.valueOf(d));
		return priceData;
	}
}
