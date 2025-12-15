package com.sabmiller.core.order.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;
import com.sabmiller.core.model.DeliveryDefaultAddressModel;


@UnitTest
public class SabmCommerceCheckoutServiceImplTest
{

	@Mock
	private ModelService modelService;

	@Mock
	private B2BCommerceUnitService b2bCommerceUnitService;

	@InjectMocks
	private final SabmCommerceCheckoutServiceImpl sabmCommerceCheckoutService = new SabmCommerceCheckoutServiceImpl();


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testStartCheckoutCountdown() throws Exception
	{

		final CartModel cartModel = mock(CartModel.class);

		sabmCommerceCheckoutService.startCheckoutCountdown(cartModel);

		verify(modelService, times(1)).save(cartModel);
		final ArgumentCaptor<Date> argument = ArgumentCaptor.forClass(Date.class);
		verify(cartModel, times(1)).setCheckoutCountdown(argument.capture());
		assertNotNull(argument.getValue());
		assertTrue(argument.getValue().after(Calendar.getInstance().getTime()));

	}

	@Test
	public void testUpdateDefaultAddress()
	{
		// Test the current login user does not have B2BUnit
		final AddressModel address = new AddressModel();
		final CartModel cartModel = mock(CartModel.class);
		final B2BCustomerModel b2bCustomer = new B2BCustomerModel();
		given(cartModel.getUser()).willReturn(b2bCustomer);
		given(b2bCommerceUnitService.getParentUnit()).willReturn(null);
		sabmCommerceCheckoutService.updateDefaultAddress(address, b2bCustomer);
		Assert.assertEquals(null, b2bCustomer.getDefaultAddresses());

		// Test the current login user does not set the default B2BUnit address
		final B2BUnitModel b2bUnit = new B2BUnitModel();
		final DeliveryDefaultAddressModel newAddress = new DeliveryDefaultAddressModel();
		given(b2bCommerceUnitService.getParentUnit()).willReturn(b2bUnit);
		given(modelService.create(DeliveryDefaultAddressModel.class)).willReturn(newAddress);
		sabmCommerceCheckoutService.updateDefaultAddress(address, b2bCustomer);
		Assert.assertEquals(1, b2bCustomer.getDefaultAddresses().size());

		// Test the current login user has set the default B2BUnit address
		final Set<DeliveryDefaultAddressModel> defaultAddresses = Sets.newConcurrentHashSet();
		final DeliveryDefaultAddressModel defaultAddress = new DeliveryDefaultAddressModel();
		defaultAddress.setB2bUnit(b2bUnit);
		defaultAddresses.add(defaultAddress);
		b2bCustomer.setDefaultAddresses(defaultAddresses);
		sabmCommerceCheckoutService.updateDefaultAddress(address, b2bCustomer);
		Assert.assertEquals(1, b2bCustomer.getDefaultAddresses().size());
	}
}