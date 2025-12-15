/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.fulfilmentprocess.test;

import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.event.EventService;

import org.hamcrest.BaseMatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.hamcrest.MockitoHamcrest;
import de.hybris.platform.orderprocessing.events.FraudErrorEvent;
import com.sabmiller.fulfilmentprocess.actions.order.SendOrderCompletedNotificationAction;
import de.hybris.platform.orderprocessing.events.OrderCompletedEvent;
import org.hamcrest.Description;


/**
*
*/
public class SendOrderCompletedNotificationTest
{

	@InjectMocks
	private final SendOrderCompletedNotificationAction sendOrderCompletedNotification = new SendOrderCompletedNotificationAction();

	@Mock
	private EventService eventService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test method for
	 * {@link com.sabmiller.fulfilmentprocess.actions.order.SendOrderCompletedNotificationAction#executeAction(OrderProcessModel)}
	 * .
	 */

	@Test
	public void testExecuteActionOrderProcessModel()
	{
		final OrderProcessModel process = new OrderProcessModel();
		sendOrderCompletedNotification.executeAction(process);

		Mockito.verify(eventService).publishEvent(MockitoHamcrest.argThat(new BaseMatcher<OrderCompletedEvent>()
		{

			@Override
			public boolean matches(final Object item)
			{
				if (item instanceof OrderCompletedEvent)
				{
					final OrderCompletedEvent event = (OrderCompletedEvent) item;
					if (event.getProcess().equals(process))
					{
						return true;
					}
				}
				return false;
			}

			@Override
			public void describeTo(final Description description)
			{ //nothing to do

			}
		}));
	}





}
