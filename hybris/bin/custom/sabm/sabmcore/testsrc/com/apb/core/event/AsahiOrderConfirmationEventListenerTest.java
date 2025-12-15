/**
 *
 */
package com.apb.core.event;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.apb.core.services.ApbCustomerAccountService;


/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AsahiOrderConfirmationEventListenerTest
{
	@Spy
	@InjectMocks
	private final AsahiOrderConfirmationEventListener asahiOrderConfirmationEventListener = new AsahiOrderConfirmationEventListener();

	@Mock
	private ModelService modelService;
	@Mock
	private BusinessProcessService businessProcessService;
	@Mock
	private UserService userService;
	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private ApbCustomerAccountService customerAccountService;
	@Mock
	private OrderModel orderModel;
	@Mock
	private AsahiOrderPlacedEvent asahiOrderPlacedEvent;
	@Mock
	private OrderProcessModel orderProcessModel;
	@Mock
	private UserModel userModel1, userModel2;

	@Before
	public void setup() {
		when(asahiOrderPlacedEvent.getOrderModel()).thenReturn(orderModel);
		when(businessProcessService.createProcess(Mockito.anyString(), Mockito.anyString())).thenReturn(orderProcessModel);
		when(orderModel.getBdeOrderUserEmails()).thenReturn(Collections.singletonList("user"));
		when(userService.getUserForUID("user")).thenReturn(userModel1);
		when(orderModel.getCode()).thenReturn("orderCode");
		when(orderModel.getBdeOrderCustomerEmails()).thenReturn(Collections.singletonList("customer"));
		when(customerAccountService.getUserByUid("customer")).thenReturn(userModel2);
		doNothing().when(modelService).save(orderProcessModel);
		doNothing().when(businessProcessService).startProcess(orderProcessModel);
		when(userModel1.getName()).thenReturn("user1");
		when(userModel2.getName()).thenReturn("user2");
	}

	@Test
	public void onSiteEventBDEOrderTest()
	{
		when(orderModel.getBdeOrder()).thenReturn(true);
		asahiOrderConfirmationEventListener.onSiteEvent(asahiOrderPlacedEvent);
		Mockito.verify(businessProcessService, Mockito.times(2)).startProcess(Mockito.any(OrderProcessModel.class));
	}

	@Test
	public void onSiteEventOrderTest()
	{
		asahiOrderConfirmationEventListener.onSiteEvent(asahiOrderPlacedEvent);
		Mockito.verify(businessProcessService).startProcess(Mockito.any(OrderProcessModel.class));
	}

}
