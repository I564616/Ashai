/**
 *
 */
package com.sabmiller.core.cart;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.basecommerce.util.AbstractCommerceServicelayerTransactionalTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;
import com.sabmiller.core.b2b.dao.SabmB2BCustomerDao;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.cart.errors.exceptions.SalesOrderCreateException;
import com.sabmiller.core.cart.errors.exceptions.SalesOrderSimulateCartUpdateException;
import com.sabmiller.core.salesordercreate.service.impl.DefalutSABMSalesOrderCreateService;
import com.sabmiller.facades.b2bunit.data.SalesData;
import com.sabmiller.facades.b2bunit.data.ShippingCarrier;
import com.sabmiller.facades.b2bunit.data.UnloadingPoint;
import com.sabmiller.facades.product.data.UomData;
import com.sabmiller.facades.ysdm.data.YSDMRequest;
import com.sabmiller.integration.restclient.commons.SABMIntegrationException;
import com.sabmiller.integration.sap.ordercreate.SalesOrderCreateRequestHandler;
import com.sabmiller.integration.sap.ordercreate.request.SalesOrderCreateRequest;
import com.sabmiller.integration.sap.ordercreate.response.SalesOrderCreateResponse;


/**
 *
 */
@IntegrationTest
public class SalesOrderCreateServiceTest extends AbstractCommerceServicelayerTransactionalTest

{
	@Mock
	private CartService cartService;

	@Mock
	private SabmB2BUnitService b2bUnitService;

	@Resource(name = "sabmB2BCustomerDao")
	private SabmB2BCustomerDao sabmB2BCustomerDao;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource
	private UserService userService;
	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private DefalutSABMSalesOrderCreateService sabmSalesOrderCreateService;

	@Mock
	private SalesOrderCreateRequestHandler salesOrderCreateRestHandler;

	@Mock
	private ProductService productService;

	@Resource
	private CalculationService calculationService;

	@Mock
	private UnitService unitService;

	private CartData cartData;

	private CartModel cartModel;

	@Resource
	private ModelService modelService;

	CurrencyModel curency;


	CatalogVersionModel catalogVersion;


	@Resource
	private Converter<CartData, SalesOrderCreateRequest> salesOrderCreateRequestConverter;

	UnitModel unit;

	@Before
	public void setUp()
	{

		MockitoAnnotations.initMocks(this);

		cartData = new CartData();

		final B2BUnitData b2BUnitData = new B2BUnitData();
		final SalesData salesData = new SalesData();
		final List<OrderEntryData> cartEntries = new ArrayList<OrderEntryData>();

		b2BUnitData.setSalesOrgId("SalesOrg1");
		b2BUnitData.setRequestedDeliveryDate("01-04-2016");
		b2BUnitData.setShipTo("shipTo1");
		b2BUnitData.setSoldTo("soldTo1");
		b2BUnitData.setDefaultCarrier(new ShippingCarrier());
		b2BUnitData.setDefaultUnloadingPoint(new UnloadingPoint());

		salesData.setDefaultDeliveryPlant("DeliveryPlant1");
		salesData.setDistributionChannel("DistributionChannel1");
		salesData.setDivision("division1");

		cartData.setPurchaseOrderNumber("PO111");
		b2BUnitData.setSalesData(salesData);
		cartData.setB2bUnit(b2BUnitData);
		final ShippingCarrier carrier = new ShippingCarrier();
		carrier.setCode("carrier");
		cartData.setDeliveryShippingCarrier(carrier);

		final OrderEntryData cartEntry1 = new OrderEntryData();
		final OrderEntryData cartEntry2 = new OrderEntryData();

		final ProductData product1 = new ProductData();
		product1.setCode("product1");

		final ProductData product2 = new ProductData();
		product2.setCode("product1");

		final UomData uom1 = new UomData();
		uom1.setCode("uom1");

		final UomData uom2 = new UomData();
		uom2.setCode("uom2");


		cartEntry1.setProduct(product1);
		cartEntry1.setQuantity(Long.valueOf("20"));
		cartEntry1.setMpaCode("MPACode1");
		cartEntry1.setUnit(uom1);

		cartEntry2.setProduct(product2);
		cartEntry2.setQuantity(Long.valueOf("20"));
		cartEntry2.setMpaCode("MPACode2");
		cartEntry2.setUnit(uom2);
		cartEntries.add(cartEntry1);
		cartEntries.add(cartEntry2);

		cartData.setEntries(cartEntries);

		cartModel = getCart();
		sabmSalesOrderCreateService.setSalesOrderCreateRestHandler(salesOrderCreateRestHandler);
	}


	@Test
	public void testSalesOrderNumber() throws SalesOrderSimulateCartUpdateException
	{
		//Given cartmodel and response for one cart item
		getCartForNullOrder();
		final SalesOrderCreateResponse response = new SalesOrderCreateResponse();
		response.setSalesOrderNumber("SAP0111");

		try
		{
			Mockito.when(salesOrderCreateRestHandler.sendPostRequest(Mockito.any(SalesOrderCreateRequest.class))).thenReturn(
					response);
		}
		catch (final SABMIntegrationException e)
		{
			e.printStackTrace();
		}

		// When I call createOrderInSAP
		try
		{
			sabmSalesOrderCreateService.createOrderInSAP(cartModel);
		}
		catch (final SalesOrderCreateException e)
		{

			e.printStackTrace();
		}

		//Then verify is sales order number is set in cart model
		Assert.assertEquals("Sales number not  set", "SAP0111", cartModel.getSapSalesOrderNumber());
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void testSalesOrderNumberIfNotReturnedFromSap() throws SalesOrderCreateException
	{
		//Given cartmodel and response for one cart item
		final SalesOrderCreateResponse response = new SalesOrderCreateResponse();


		try
		{
			Mockito.when(salesOrderCreateRestHandler.sendPostRequest(Mockito.any(SalesOrderCreateRequest.class))).thenReturn(
					response);
		}
		catch (final SABMIntegrationException e)
		{
			e.printStackTrace();
		}

		// When I call createOrderInSAP expect execption
		exception.expect(SalesOrderCreateException.class);

		sabmSalesOrderCreateService.createOrderInSAP(cartModel);

		//Then verify is sales order number is set in cart model
		Assert.assertEquals("Sales number null", null, cartModel.getSapSalesOrderNumber());
	}

	@Test
	public void testSalesOrderNumberIfNotReturnedWhenSapDelay() throws SalesOrderCreateException
	{
		//Given cartmodel and response for one cart item
		final SalesOrderCreateResponse response = new SalesOrderCreateResponse();


		try
		{
			Mockito.when(salesOrderCreateRestHandler.sendPostRequest(Mockito.any(SalesOrderCreateRequest.class)))
					.thenReturn(response);
			Thread.sleep(230000);
		}
		catch (final SABMIntegrationException e)
		{
			e.printStackTrace();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

		// When I call createOrderInSAP expect execption
		exception.expect(SalesOrderCreateException.class);
		exception.expect(Exception.class);

		sabmSalesOrderCreateService.createOrderInSAP(cartModel);

		//Then verify is sales order number is set in cart model
		Assert.assertEquals("Sales number null", null, cartModel.getSapSalesOrderNumber());
	}

	@Test
	public void testResponseNullFromSAP() throws SalesOrderCreateException
	{
		getCartForNullOrder();
		//Given cartmodel and response for one cart item
		SalesOrderCreateResponse response = new SalesOrderCreateResponse();


		try
		{
			Mockito.when(salesOrderCreateRestHandler.sendPostRequest(Mockito.any(SalesOrderCreateRequest.class)))
					.thenReturn(response);
			Thread.sleep(230000);
			response = null;
		}
		catch (final SABMIntegrationException e)
		{
			e.printStackTrace();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

		// When I call createOrderInSAP expect execption
		exception.expect(SalesOrderCreateException.class);
		exception.expect(Exception.class);

		sabmSalesOrderCreateService.createOrderInSAP(cartModel);

		//Then verify is sales order number is set in cart model
		Assert.assertEquals("Sales number null", null, cartModel.getSapSalesOrderNumber());
	}

	//The test passes if no exceptions are triggered
	@Test
	public void testYSDMCall() throws SABMIntegrationException
	{
		Mockito.when(salesOrderCreateRestHandler.sendPostRequest(Mockito.any(SalesOrderCreateRequest.class))).thenReturn(
				new SalesOrderCreateResponse());

		sabmSalesOrderCreateService.createYSDMOrderInSAP(mockYSDMRequest());
	}

	private YSDMRequest mockYSDMRequest()
	{
		final YSDMRequest request = new YSDMRequest();
		request.setCarrier("0006000044");
		request.setCcPaymentFlag("V");
		request.setCurrency("AUD");
		request.setDistributionChannel("00");
		request.setDivision("00");
		request.setGrossTotal(160.90);
		request.setPoNumber("841557@test.com");
		request.setPoOrderType("B2B");
		request.setRequestedDeliveryDate(new Date());
		request.setSalesOrg("7001");
		request.setShipTo("0000858039");
		request.setSoldTo("0000858039");
		request.setUnloadingPoint("PACK-MTWTHF-1");

		return request;
	}

	private CartModel getCart()
	{

		final CartModel cartModel = cartService.getSessionCart();


		return cartModel;
	}

	/**
	 * Utility method to prevent the test case from using session carts
	 */
	private void getCartForNullOrder()
	{
		final B2BCustomerModel newCustomer = new B2BCustomerModel();
		final B2BUnitModel b2bUnit = new B2BUnitModel();
		newCustomer.setDefaultB2BUnit(b2bUnit);
		newCustomer.setUid("junittest@test.com");
		curency = modelService.create(CurrencyModel.class);
		curency.setIsocode("AUD");
		curency.setDigits(Integer.valueOf(2));
		modelService.save(curency);
		final Set<PrincipalGroupModel> groups = Sets.newConcurrentHashSet();
		final UserGroupModel userGroup1 = new UserGroupModel();
		userGroup1.setUid("customergroup");
		groups.add(userGroup1);
		newCustomer.setSessionCurrency(commonI18NService.getCurrency("AUD"));
		newCustomer.setSessionLanguage(commonI18NService.getLanguage("en"));
		newCustomer.setGroups(groups);
		newCustomer.setActive(true);

		final CartModel cart = new CartModel();
		cart.setCode("mockCartCode");
		cart.setUser(newCustomer);
		cart.setCurrency(curency);
		cart.setDate(new Date());
		cart.setNet(Boolean.TRUE);
		cart.setUnit(newCustomer.getDefaultB2BUnit());
		cart.setSapSalesOrderNumber("SAP0111");
		cartModel = cart;
		//sessionService.setAttribute("mockCart", cart);

	}


}
