/**
 *
 */
package com.sabmiller.core.cart.service.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;

import java.util.Date;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import jakarta.annotation.Resource;

/**
 * Dummy calculation service to be used on cart operation (add, update, remove) and bypass cart calculation.
 * The real calculation is done on another service invoking SAP
 */
public class SabmDummyCalculationServiceImpl implements CalculationService
{

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Override
    public boolean requiresCalculation(final AbstractOrderModel order)
    {
        //forced to false to avoid calculation every time the cart changes
		return configurationService.getConfiguration().getBoolean("cub.dummy.order.simulate",false);

	}

	@Override
	public void calculate(final AbstractOrderModel order) throws CalculationException
	{
        //do nothing
    }

	@Override
	public void calculate(final AbstractOrderModel order, final Date date) throws CalculationException
	{
        //do nothing
	}


	@Override
	public void recalculate(final AbstractOrderModel order) throws CalculationException
	{
        //do nothing
	}

	@Override
	public void recalculate(final AbstractOrderModel order, final Date date) throws CalculationException
	{
        //do nothing
	}

	@Override
	public void calculateTotals(final AbstractOrderModel order, final boolean recalculate) throws CalculationException
	{
        //do nothing
    }

	@Override
	public void calculateTotals(final AbstractOrderEntryModel entry, final boolean recalculate)
	{
        //do nothing
	}

	@Override
	public void recalculate(final AbstractOrderEntryModel entry) throws CalculationException
	{
        //do nothing
	}

}
