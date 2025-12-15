package com.sabmiller.facades.order.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BCheckoutFacadeTest;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.cart.service.SabmCommerceCartService;
import com.sabmiller.core.enums.DealConditionStatus;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.OrderMessageModel;
import com.sabmiller.core.order.SabmCommerceCheckoutService;
import com.sabmiller.core.salesordercreate.service.SABMSalesOrderCreateService;


@UnitTest
public class DefaultSABMCheckoutFacadeTest extends DefaultB2BCheckoutFacadeTest
{

	@Mock
	private SABMSalesOrderCreateService sabmSalesOrderCreateService;
	@Mock
	private SabmCommerceCheckoutService sabmCommerceCheckoutService;
	@Mock
	private SABMCartService cartService;
	@Mock
	private CartFacade cartFacade;
	@Mock
	private SabmCommerceCartService commerceCartService;
	@Mock
	private DeliveryService deliveryService;
	@Mock
	private AbstractPopulatingConverter<OrderModel, OrderData> orderConverter;
	@Mock
	private CheckoutCustomerStrategy checkoutCustomerStrategy;
	@Mock
	private CommerceCheckoutService commerceCheckoutService;

	@InjectMocks
	private DefaultSABMCheckoutFacade defaultSABMCheckoutFacade;


	@Override
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		super.setUp();
		defaultSABMCheckoutFacade.setCartService(cartService);
		defaultSABMCheckoutFacade.setDeliveryService(deliveryService);
		defaultSABMCheckoutFacade.setCheckoutCustomerStrategy(checkoutCustomerStrategy);
		defaultSABMCheckoutFacade.setCommerceCheckoutService(commerceCheckoutService);
		//defaultSABMCheckoutFacade.setModelService(modelService);
		//cartData = new CartData();
		//cartModel = new CartModel();
	}


	@Test
	public void testRunOrderSimulate() throws Exception
	{
		final CartModel cartModel = mock(CartModel.class);
		when(cartService.getSessionCart()).thenReturn(cartModel);
		defaultSABMCheckoutFacade.runOrderSimulate(false);

		verify(commerceCartService).calculateCart(any(CommerceCartParameter.class));
	}

	@Test
	public void testForceRunOrderSimulate() throws Exception
	{
		final CartModel cartModel = mock(CartModel.class);
		when(cartService.getSessionCart()).thenReturn(cartModel);
		defaultSABMCheckoutFacade.runOrderSimulate(true);

		verify(commerceCartService).recalculateCart(any(CommerceCartParameter.class));
	}

	@Test
	public void testGetSapCartChanges() throws Exception
	{
		final CartModel cartModel = mock(CartModel.class);
		when(cartService.hasSessionCart()).thenReturn(true);
		when(cartService.getSessionCart()).thenReturn(cartModel);
		final OrderMessageModel message = new OrderMessageModel();
		message.setCode("1");
		when(commerceCartService.getSAPOrderSimulateChanges(any(CartModel.class))).thenReturn(Lists.newArrayList(message));

		final List changes = defaultSABMCheckoutFacade.getSapCartChanges();
		assertTrue("List shouldn't be empty", CollectionUtils.isNotEmpty(changes));
	}

	@Test
	public void testGetEmptySapCartChanges() throws Exception
	{
		when(cartService.hasSessionCart()).thenReturn(false);
		final List changes = defaultSABMCheckoutFacade.getSapCartChanges();
		assertTrue("List should be empty", CollectionUtils.isEmpty(changes));
		verify(commerceCartService, never()).getSAPOrderSimulateChanges(any(CartModel.class));
	}

	@Test
	public void testPrepareCartForCheckout() throws Exception
	{
		//
	}

	@Test
	public void testValidateCartForCredictcardPayment() throws Exception
	{
		//
	}

	@Test
	public void testIsCheckoutCountdownValid() throws Exception
	{
		final CartModel cartModel = mock(CartModel.class);
		when(cartService.hasSessionCart()).thenReturn(true);
		when(cartFacade.hasSessionCart()).thenReturn(true);
		when(cartService.getSessionCart()).thenReturn(cartModel);
		defaultSABMCheckoutFacade.setCartFacade(cartFacade);

		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 1);
		when(cartModel.getCheckoutCountdown()).thenReturn(cal.getTime());

		assertTrue("countdown date is not a future date", defaultSABMCheckoutFacade.isCheckoutCountdownValid());
	}

	@Test
	public void testValidateDealconditions() throws Exception
	{
		final AbstractOrderModel cartModel = new AbstractOrderModel();
		final List<CartDealConditionModel> deals = new ArrayList<CartDealConditionModel>();
		final CartDealConditionModel dealCondition1 = new CartDealConditionModel();
		final CartDealConditionModel dealCondition2 = new CartDealConditionModel();
		dealCondition1.setStatus(DealConditionStatus.MANUAL);
		dealCondition2.setStatus(DealConditionStatus.MANUAL);
		final DealModel dealmodel1 = new DealModel();
		final DealModel dealmodel2 = null;
		dealmodel1.setDealType(DealTypeEnum.COMPLEX);
		dealmodel1.setCode("deal1");
		dealCondition1.setDeal(dealmodel1);
		dealCondition2.setDeal(dealmodel2);
		deals.add(dealCondition1);
		deals.add(dealCondition2);
		cartModel.setComplexDealConditions(deals);
		defaultSABMCheckoutFacade.validateDealconditions(cartModel);
		cartModel.getComplexDealConditions().get(0).getDeal().getCode();
		Assert.assertTrue(cartModel.getComplexDealConditions().get(0).getDeal().getCode().equals("deal1"));
	}

	@Test
	public void testValidateNullDealsInDealconditions() throws Exception
	{
		final AbstractOrderModel cartModel = new AbstractOrderModel();
		final List<CartDealConditionModel> deals = new ArrayList<CartDealConditionModel>();
		final CartDealConditionModel dealCondition1 = new CartDealConditionModel();
		final CartDealConditionModel dealCondition2 = new CartDealConditionModel();
		dealCondition1.setStatus(DealConditionStatus.MANUAL);
		dealCondition2.setStatus(DealConditionStatus.MANUAL);
		final DealModel dealmodel1 = null;
		final DealModel dealmodel2 = null;
		dealCondition1.setDeal(dealmodel1);
		dealCondition2.setDeal(dealmodel2);
		deals.add(dealCondition1);
		deals.add(dealCondition2);
		cartModel.setComplexDealConditions(deals);
		Assert.assertTrue(cartModel.getComplexDealConditions().size() == 2);
		defaultSABMCheckoutFacade.validateDealconditions(cartModel);
		assertNotNull(cartModel.getComplexDealConditions());
	}
	@Test
	public void testSetDeliveryAddress() throws Exception
	{
		final CartModel cartModel = mock(CartModel.class);
		final PK pk1 = PK.parse("100020");
		when(cartModel.getPk()).thenReturn(pk1);
		when(cartFacade.hasSessionCart()).thenReturn(true);
		when(cartService.getSessionCart()).thenReturn(cartModel);
		defaultSABMCheckoutFacade.setCartFacade(cartFacade);
		final String addressId = "10002";
		final List<AddressModel> addressModels = new ArrayList<AddressModel>();

		final AddressModel address1 = mock(AddressModel.class);
		final AddressModel address2 = mock(AddressModel.class);
		final PK pk2 = PK.parse("10002");
		final PK pk3 = PK.parse("100025");
		given(address1.getPk()).willReturn(pk2);
		given(address1.getStreetname()).willReturn("streetname1");
		given(address2.getPk()).willReturn(pk3);
		given(address2.getStreetname()).willReturn("streetname2");
		addressModels.add(address1);
		addressModels.add(address2);
		given(deliveryService.getSupportedDeliveryAddressesForOrder(cartModel, false)).willReturn(addressModels);
		given(checkoutCustomerStrategy.getCurrentUserForCheckout()).willReturn(mock(CustomerModel.class));

		Assert.assertEquals(false, defaultSABMCheckoutFacade.setDeliveryAddress(addressId, false));
	}

}