/**
 *
 */
package com.apb.core.order.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.store.services.BaseStoreService;

import jakarta.annotation.Resource;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.cart.service.impl.SabmDummyCalculationServiceImpl;


/**
 * @author GQ485VQ
 *
 */
public class SabmDummyApbCommerceCartCalculationStrategy extends DefaultCommerceCartCalculationStrategy
{
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	@Resource(name = "calculationService")
	private CalculationService calculationService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource(name = "sabmDummyCalculationService")
	private SabmDummyCalculationServiceImpl sabmDummyCalculationService;

	@Override
	public boolean calculateCart(final CommerceCartParameter parameter)
	{
		final CartModel cartModel = parameter.getCart();

		validateParameterNotNull(cartModel, "Cart model cannot be null");

		if (!asahiSiteUtil.isCub())
		{

			final CalculationService calcService = getCalculationService();
			boolean recalculated = false;
			if (calcService.requiresCalculation(cartModel))
			{
				try
				{
					parameter.setRecalculate(false);
					beforeCalculate(parameter);
					calcService.calculate(cartModel);
				}
				catch (final CalculationException calculationException)
				{
					throw new IllegalStateException(
							"Cart model " + cartModel.getCode() + " was not calculated due to: " + calculationException.getMessage(),
							calculationException);
				}
				finally
				{
					afterCalculate(parameter);
				}
				recalculated = true;
			}
			if (isCalculateExternalTaxes())
			{
				getExternalTaxesService().calculateExternalTaxes(cartModel);
			}
			return recalculated;
		}
		else
		{

			return super.calculateCart(parameter);
		}
	}

	@Override
	public boolean recalculateCart(final CommerceCartParameter parameter)
	{
		final CartModel cartModel = parameter.getCart();

		if (!asahiSiteUtil.isCub())
		{
			try
			{
				parameter.setRecalculate(true);
				beforeCalculate(parameter);
				getCalculationService().recalculate(cartModel);
			}
			catch (final CalculationException calculationException)
			{
				throw new IllegalStateException(String.format("Cart model %s was not calculated due to: %s ", cartModel.getCode(),
						calculationException.getMessage()), calculationException);
			}
			finally
			{
				afterCalculate(parameter);

			}
			return true;
		}
		else
		{

			return super.recalculateCart(parameter);
		}
	}

	@Override
	protected CalculationService getCalculationService()
		{
		if (baseStoreService.getCurrentBaseStore() != null && asahiSiteUtil.isCub())
		{
			return sabmDummyCalculationService;
		}
		return calculationService;
		}
}
