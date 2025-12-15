/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2023 hybris AG
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

import de.hybris.platform.core.Registry;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.cache.jcache.JCacheManagerFactoryBean;


/**
 * {@link org.springframework.beans.factory.FactoryBean} that exposes a tenant-aware EhCache 3
 * {@link javax.cache.CacheManager} instance using the JCache (JSR-107) API.
 */
public class SabmTenantAwareEhCacheManagerFactoryBean extends JCacheManagerFactoryBean
{
	/**
	 * Configures a tenant-specific URI for the CacheManager before initialization.
	 * This ensures that each tenant gets a distinct CacheManager instance.
	 */
	@Override
	public void afterPropertiesSet()
	{
		try
		{
			// JCache uses a URI to uniquely identify a CacheManager.
			// We create a unique URI for each tenant to ensure cache isolation.
			final String tenantId = Registry.getCurrentTenant().getTenantID();
			final URI tenantSpecificUri = new URI("urn:ehcache:tenant:" + tenantId);
			setCacheManagerUri(tenantSpecificUri);
		}
		catch (final URISyntaxException e)
		{
			throw new IllegalStateException("Failed to create a tenant-specific URI for the cache manager", e);
		}

		super.afterPropertiesSet();
	}
}
