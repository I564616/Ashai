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
package com.sabmiller.storefront.filters.btg;

import de.hybris.platform.cms2.misc.CMSFilter;
import com.sabmiller.storefront.filters.btg.support.BTGSegmentStrategy;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * Filter that evaluates the BTG context for the current request. This is a spring configured filter that is executed by
 * the PlatformFilterChain.
 * The segments are evaluated after the nested chain is executed.
 */
public class BTGSegmentFilter extends OncePerRequestFilter implements CMSFilter
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(BTGSegmentFilter.class);

	private BTGSegmentStrategy btgSegmentStrategy;

	@Override
	protected void doFilterInternal(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse,
			final FilterChain filterChain) throws ServletException, IOException
	{
		filterChain.doFilter(httpRequest, httpResponse);
		getBtgSegmentStrategy().evaluateSegment(httpRequest);
	}

	protected BTGSegmentStrategy getBtgSegmentStrategy()
	{
		return btgSegmentStrategy;
	}

	public void setBtgSegmentStrategy(final BTGSegmentStrategy btgSegmentStrategy)
	{
		this.btgSegmentStrategy = btgSegmentStrategy;
	}
}
