package com.sabmiller.facades.order;

import com.sabmiller.core.cart.errors.exceptions.SalesOrderCreateException;
import com.sabmiller.core.cart.errors.exceptions.SalesOrderSimulateCartUpdateException;
import com.sabmiller.core.cart.service.impl.DefaultSABMCartService;
import com.sabmiller.core.salesordercreate.service.impl.DefalutSABMSalesOrderCreateService;
import com.sabmiller.facades.b2bunit.data.SalesData;
import com.sabmiller.facades.b2bunit.data.ShippingCarrier;
import com.sabmiller.facades.b2bunit.data.UnloadingPoint;
import com.sabmiller.facades.order.impl.DefaultSABMCheckoutFacade;
import com.sabmiller.facades.product.data.UomData;
import com.sabmiller.integration.restclient.commons.SABMIntegrationException;
import com.sabmiller.integration.sap.ordercreate.SalesOrderCreateRequestHandler;
import com.sabmiller.integration.sap.ordercreate.request.SalesOrderCreateRequest;
import com.sabmiller.integration.sap.ordercreate.response.SalesOrderCreateResponse;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.basecommerce.util.AbstractCommerceServicelayerTransactionalTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.BDDMockito.given;

/**
 *
 */


/**
 *
 */
@IntegrationTest
public class SalesOrderCreateBeforePlaceOrderTest extends AbstractCommerceServicelayerTransactionalTest

{
	@Mock
	private DefalutSABMSalesOrderCreateService sabmSalesOrderCreateService;

	@Resource
	private DefaultSABMCartService cartService;

	@Resource
	private DefaultSABMCheckoutFacade checkoutFacade;

	@Mock
	private CheckoutCustomerStrategy checkoutCustomerStrategy;

	@Mock
	private SalesOrderCreateRequestHandler salesOrderCreateRestHandler;

	@Resource
	private Converter<CartData, SalesOrderCreateRequest> salesOrderCreateRequestConverter;

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

	@Mock
	private UserService userService;
	@Mock
	private UserModel user;
	@Mock
	private ModelService mockModelService;
	@Mock
	private AbstractPopulatingConverter<UserModel, CustomerData> customerConverter;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private StoreSessionFacade storeSessionFacade;

	@Mock
	private UserFacade userFacade;
	@Mock
	private SessionService sessionService;
	@Mock
	private OrderFacade orderFacade;
	@Mock
	CustomerModel customerModel;

	@Mock
	private CartFacade cartFacade;

	@Mock
	private CommerceCheckoutParameter parameter;

	private UnitModel product1Uom;

	CatalogVersionModel catalogVersion;

	private ProductModel productP1;
	private ProductModel productP2;
	UnitModel unit;
	private CustomerModel userModel;



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

		product1Uom = createUnit("CAS");



		Mockito.when(unitService.getUnitForCode("CAS")).thenReturn(product1Uom);

		catalogVersion = createCatalogVersion("myCatalog", "Standard");

		productP2 = new ProductModel();
		productP2.setCode("P2");
		productP2.setName("Carlton cold", Locale.ENGLISH);
		productP2.setCatalogVersion(catalogVersion);
		productP1 = new ProductModel();
		productP1.setCode("P1");
		productP1.setName("Carlton dry", Locale.ENGLISH);
		productP1.setCatalogVersion(catalogVersion);
		modelService.save(productP1);
		modelService.save(productP2);

		Mockito.when(productService.getProductForCode("P1")).thenReturn(productP1);
		Mockito.when(productService.getProductForCode("P2")).thenReturn(productP2);

		sabmSalesOrderCreateService.setSalesOrderCreateRestHandler(salesOrderCreateRestHandler);
		given(Boolean.valueOf(cartFacade.hasSessionCart())).willReturn(Boolean.TRUE);
		given(cartFacade.getSessionCart()).willReturn(cartData);

		sabmSalesOrderCreateService.setSalesOrderCreateRestHandler(salesOrderCreateRestHandler);


		given(checkoutCustomerStrategy.getCurrentUserForCheckout()).willReturn(userModel);
		given(Boolean.valueOf(checkoutCustomerStrategy.isAnonymousCheckout())).willReturn(Boolean.TRUE);
		checkoutFacade.setCheckoutCustomerStrategy(checkoutCustomerStrategy);
		checkoutFacade.setCartFacade(cartFacade);
		checkoutFacade.setSabmSalesOrderCreateService(sabmSalesOrderCreateService);

	}


	@Test
	public void testSalesOrderCreateBeforePlaceOrder() throws SalesOrderSimulateCartUpdateException
	{

		final SalesOrderCreateResponse response = new SalesOrderCreateResponse();
		response.setSalesOrderNumber("SAP0111");

		try
		{
			Mockito.when(salesOrderCreateRestHandler.sendPostRequest(Mockito.any(SalesOrderCreateRequest.class)))
					.thenReturn(response);
		}
		catch (final SABMIntegrationException e)
		{
			e.printStackTrace();
		}

		try
		{
			checkoutFacade.placeOrder();

			Mockito.verify(sabmSalesOrderCreateService, Mockito.times(1)).createOrderInSAP(Mockito.any(AbstractOrderModel.class));
		}
		catch (final InvalidCartException | SalesOrderCreateException e)
		{
			e.printStackTrace();
		}

	}
	
	@Test
	public void testSalesOrderCreateWithEmptySapOrderNumberBeforePlaceOrder() throws SalesOrderSimulateCartUpdateException
	{

		final SalesOrderCreateResponse response = new SalesOrderCreateResponse();
		response.setSalesOrderNumber("");

		try
		{
			Mockito.when(salesOrderCreateRestHandler.sendPostRequest(Mockito.any(SalesOrderCreateRequest.class)))
					.thenReturn(response);
		}
		catch (final SABMIntegrationException e)
		{
			e.printStackTrace();
		}

		try
		{
			checkoutFacade.placeOrder();

			Mockito.verify(sabmSalesOrderCreateService, Mockito.times(1)).createOrderInSAP(Mockito.any(AbstractOrderModel.class));
		}
		catch (final InvalidCartException | SalesOrderCreateException e)
		{
			e.printStackTrace();
		}

	}

	@Test
	public void testSalesOrderCreateWithNullSapOrderNumberBeforePlaceOrder() throws SalesOrderSimulateCartUpdateException
	{

		final SalesOrderCreateResponse response = new SalesOrderCreateResponse();
		response.setSalesOrderNumber(null);

		try
		{
			Mockito.when(salesOrderCreateRestHandler.sendPostRequest(Mockito.any(SalesOrderCreateRequest.class)))
					.thenReturn(response);
		}
		catch (final SABMIntegrationException e)
		{
			e.printStackTrace();
		}

		try
		{
			checkoutFacade.placeOrder();

			Mockito.verify(sabmSalesOrderCreateService, Mockito.times(1)).createOrderInSAP(Mockito.any(AbstractOrderModel.class));
		}
		catch (final InvalidCartException | SalesOrderCreateException e)
		{
			e.printStackTrace();
		}

	}


}
