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
package com.sabmiller.core.storesession.impl;

import de.hybris.platform.commerceservices.storesession.impl.DefaultStoreSessionService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


public class SABMStoreSessionServiceImpl extends DefaultStoreSessionService
{
	private static final Logger LOG = LoggerFactory.getLogger(SABMStoreSessionServiceImpl.class);

	@Resource
	private ModelService modelService;

	/**
	 * Overriding the OOTB setCurrentCurrency to avoid Cart Calculation (and Order Simulate call).
	 *
	 * @param isocode
	 *           the new current currency
	 */
	@Override
	public void setCurrentCurrency(final String isocode)
	{
		Collection<CurrencyModel> currencies = getCommerceCommonI18NService().getAllCurrencies();
		if (CollectionUtils.isEmpty(currencies))
		{
			LOG.debug("No supported currencies found for the current site, look for all session currencies instead.");
			currencies = getCommonI18NService().getAllCurrencies();
		}
		Assert.notEmpty(currencies,
				"No supported currencies found for the current site. Please create currency for proper base store.");
		CurrencyModel currencyModel = null;
		for (final CurrencyModel currency : currencies)
		{
			if (StringUtils.equals(currency.getIsocode(), isocode))
			{
				currencyModel = currency;
				break;
			}
		}
		Assert.notNull(currencyModel, "Currency to set is not supported.");

		if (getCommonI18NService().getCurrentCurrency() != null)
		{
			if (!getCommonI18NService().getCurrentCurrency().getIsocode().equals(currencyModel.getIsocode()))
			{
				getCommonI18NService().setCurrentCurrency(currencyModel);
			}
		}
		else
		{
			getCommonI18NService().setCurrentCurrency(currencyModel);
		}

		if (getCartService().hasSessionCart())
		{
			final CartModel cart = getCartService().getSessionCart();

			if (cart.getCurrency() == null || !cart.getCurrency().equals(currencyModel))
			{
				cart.setCurrency(currencyModel);
				modelService.save(cart);
			}
		}
	}
}
