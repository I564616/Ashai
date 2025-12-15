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
package com.sabmiller.cache.impl;


import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.KeyGenerator;

import com.sabmiller.core.constants.SabmCoreConstants;



/**
 * Cache key generator
 *
 * @spring.bean commerceCacheKeyGenerator
 *
 */
public class SabmCacheKeyGenerator implements KeyGenerator
{
	private CommonI18NService commonI18NService;
	private BaseSiteService baseSiteService;
	private UserService userService;


	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	/** The session service. */
	@Resource(name = "sessionService")
	private SessionService sessionService;

	private static final Logger LOG = LoggerFactory.getLogger(SabmCacheKeyGenerator.class.getName());

	@Override
	public Object generate(final Object target, final Method method, final Object... params)
	{
		return generate(false, false, params);
	}

	/**
	 * Generates key based on given parameters and current session attributes ( base site, language, user, currency)
	 *
	 * @param addUserToKey
	 *           Define if current user uid should be added to key
	 * @param addCurrencyToKey
	 *           Define if current currency isocode should be added to key
	 * @param params
	 *           Values which should be added to key
	 * @return generated key
	 */
	public Object generate(final boolean addUserToKey, final boolean addCurrencyToKey, final Object... params)
	{
		final List<Object> key = new ArrayList<>();
		addLanguage(key);
		addCurrentSite(key);
		addCurrency(addCurrencyToKey, key);
		addUser(addUserToKey, key);
		addParams(key, params);
		return key;
	}


	/**
	 * Generates key based on given parameters and current session attributes ( base site, language, user, currency)
	 *
	 * @param addUserAndB2BUnitToKey
	 *           Define if current user uid should be added to key
	 * @param addCurrencyToKey
	 *           Define if current currency isocode should be added to key
	 * @param params
	 *           Values which should be added to key
	 * @return generated key
	 */
	public Object generate(final boolean addUserToKey, final boolean addB2BUnitToKey, final boolean addDeliveryDateToKey,
			final boolean addCurrencyToKey, final Object... params)
	{
		final List<Object> key = new ArrayList<>();
		addLanguage(key);
		addCurrentSite(key);
		addCurrency(addCurrencyToKey, key);
		addUser(addUserToKey, key);
		addB2BUnit(addB2BUnitToKey, key);
		addDeliveryDate(addDeliveryDateToKey, key);
		addParams(key, params);
		return key;
	}

	/**
	 * @param shouldBeAdded
	 * @param key
	 */
	private void addDeliveryDate(final boolean shouldBeAdded, final List<Object> key)
	{

		if (shouldBeAdded)
		{
			Date currentDeliveryDate = null;
			if (sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE) != null)
			{
				currentDeliveryDate = (Date) sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);
			}
			key.add(currentDeliveryDate == null ? null : currentDeliveryDate.getTime());

			try
			{
				final UserModel user = userService.getCurrentUser();
				final B2BUnitModel b2bunit = user instanceof B2BCustomerModel ? b2bCommerceUnitService.getParentUnit() : null;
				key.add(b2bunit != null && b2bunit.getRefreshEntitiesLastUpdatedTime() != null
						? b2bunit.getRefreshEntitiesLastUpdatedTime().getTime() : null);
			}
			catch (final Exception e)
			{
				LOG.warn("Exception whle generating cache key", e);
			}


		}


	}

	/**
	 * Generates key based on given parameters and current session attributes ( base site, language, user, currency).<br/>
	 * It uses Registry.getApplicationContext().getBean to have access to instance of commerceCacheKeyGenerator.<br/>
	 * This static method was added because @Cacheable annotation doesn't support using bean in SPeL expression for key :
	 * https://jira.spring.io/browse/SPR-9578
	 *
	 * @param addUserToKey
	 *           Define if current user uid should be added to key
	 * @param addCurrencyToKey
	 *           Define if current currency isocode should be added to key
	 * @param params
	 *           Values which should be added to key
	 * @return generated key
	 */
	public static Object generateKey(final boolean addUserToKey, final boolean addB2BUnitToKey, final boolean addDeliveryDateToKey,
			final Object... params)
	{
		final SabmCacheKeyGenerator keyGeneratorBean = (SabmCacheKeyGenerator) Registry.getApplicationContext().getBean(
				"sabmCacheKeyGenerator");
		return keyGeneratorBean.generate(addUserToKey, addB2BUnitToKey, addDeliveryDateToKey, false, params);
	}

	protected void addLanguage(final List<Object> key)
	{
		final LanguageModel language = commonI18NService.getCurrentLanguage();
		key.add(language == null ? null : language.getIsocode());
	}

	protected void addCurrentSite(final List<Object> key)
	{
		final BaseSiteModel currentSite = baseSiteService.getCurrentBaseSite();
		key.add(currentSite == null ? null : currentSite.getUid());
	}

	protected void addCurrency(final boolean shouldBeAdded, final List<Object> key)
	{
		if (shouldBeAdded)
		{
			final CurrencyModel currency = commonI18NService.getCurrentCurrency();
			key.add(currency == null ? null : currency.getIsocode());
		}
	}

	protected void addUser(final boolean shouldBeAdded, final List<Object> key)
	{
		if (shouldBeAdded)
		{
			final UserModel user = userService.getCurrentUser();
			key.add(user == null ? null : user.getUid());
		}
	}

	protected void addB2BUnit(final boolean shouldBeAdded, final List<Object> key)
	{
		if (shouldBeAdded)
		{
			try{
				final UserModel user = userService.getCurrentUser();

				final B2BUnitModel b2bunit = user instanceof B2BCustomerModel ? b2bCommerceUnitService.getParentUnit() : null;
			key.add(b2bunit == null ? null : b2bunit.getUid());

			}
			catch (final Exception e)
			{
				LOG.warn("Exception whle generating cache key", e);
			}
		}
	}
	protected void addParams(final List<Object> key, final Object... params)
	{
		for (final Object o : params)
		{
			key.add(o);
		}
	}


	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}