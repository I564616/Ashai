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
package com.sabmiller.storefront.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.*;
import java.io.IOException;

/**
 * Filters given string to prevent cross-site scripting
 */

public class SabmXSSFilter implements Filter {

    protected static final Logger LOG = LoggerFactory.getLogger(SabmXSSFilter.class);

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override public void init(FilterConfig paramFilterConfig) throws ServletException {
        XSSFilterUtil.setXSSFilterUtil(paramFilterConfig);

    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override public void doFilter(ServletRequest paramServletRequest, ServletResponse paramServletResponse, FilterChain paramFilterChain)
            throws IOException, ServletException {
        try {
            paramFilterChain.doFilter(paramServletRequest, paramServletResponse);
        } catch(Exception ex) {
            LOG.error("SABMXSSFilter doFilter() error", ex);
            throw ex;
        }

    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    @Override public void destroy() {
        // DO NOTHING
    }

}
