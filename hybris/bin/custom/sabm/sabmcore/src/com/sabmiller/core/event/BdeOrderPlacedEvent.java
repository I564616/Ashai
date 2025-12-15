/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.orderprocessing.events.OrderProcessingEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;

import java.io.Serial;

/**
 * @author ramsatish.jagajyothi
 *
 */
public class BdeOrderPlacedEvent extends OrderProcessingEvent
{
	@Serial
	private static final long serialVersionUID = 1L;


	public BdeOrderPlacedEvent(final OrderProcessModel process)
	{
		super(process);
	}


}
