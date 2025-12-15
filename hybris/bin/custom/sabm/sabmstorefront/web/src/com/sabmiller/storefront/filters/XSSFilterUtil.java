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

import de.hybris.platform.core.Registry;
import de.hybris.platform.spring.HybrisContextLoaderListener;
import de.hybris.platform.util.Utilities;
import de.hybris.platform.util.config.ConfigIntf;
import jakarta.servlet.FilterConfig;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;


/**
 * Filters given string to prevent cross-site scripting
 */
public class XSSFilterUtil
{
	
	private final Pattern NULL_CHAR = Pattern.compile("\000");
	protected static Logger LOG = Logger.getLogger(XSSFilterUtil.class.getName());
	
	private static ConfigIntf cfg;
	private static String webroot;
	private static String extensionName;
	
	public static void setXSSFilterUtil(final FilterConfig filterConfig)
	{
		cfg = Registry.getMasterTenant().getConfig();
		webroot = filterConfig.getServletContext().getContextPath();
		final String tenantIDForWebapp = HybrisContextLoaderListener.getTenantIDForWebapp(filterConfig.getServletContext());
		extensionName = Utilities.getExtensionForWebroot(webroot, tenantIDForWebapp == null ? "master" : tenantIDForWebapp);
	}
	
	/**
	 *
	 * @param value
	 *           to be sanitized
	 * @return sanitized content
	 */
	public static String filter(final String value)
	{
		if (value == null)
		{
			return null;
		}
		String sanitized = value;
		
		//Filter XSS vulunerability
		
		XSSFilterUtil xSSFilterUtil = new XSSFilterUtil();
		Map<String, String> rules = xSSFilterUtil.getPatternDefinitions();
		List<Pattern> patterns = xSSFilterUtil.compilePatterns(rules);		
		for(Pattern eachPattern : patterns){			
			sanitized = eachPattern.matcher(sanitized).replaceAll("");
		}
				
		sanitized = sanitized.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		sanitized = sanitized.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
		//		sanitized = sanitized.replaceAll("'", "&#39;");
		sanitized = sanitized.replaceAll("eval\\((.*)\\)", "");
		sanitized = sanitized.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		return sanitized;
	}
	
	protected Map<String, String> getPatternDefinitions()
	{
		Map<String, String> rules = new LinkedHashMap<String, String>(cfg.getParametersMatching(extensionName + "\\.(" + "xss\\.filter\\.rule\\..*(?i)" + ")", true));
		//rules.putAll(cfg.getParametersMatching(extensionName + "\\.(" + "xss\\.filter\\.rule\\..*(?i)" + ")", true));
	    
		return rules;
	}
	
	protected List<Pattern> compilePatterns(Map<String, String> rules)
	{
   	List<Pattern> ret = new ArrayList<Pattern>(rules.size() + 1);   	     
   	ret.add(NULL_CHAR);   	     
   	for (Map.Entry<String, String> entry : rules.entrySet())
   	{
   		   String expr = StringEscapeUtils.unescapeJava((String)entry.getValue());
   		   if (StringUtils.isNotBlank(expr))
   		   {
      			 try
      			 {
      			   ret.add(Pattern.compile(expr, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
      			   if (LOG.isDebugEnabled())
      			   {
      				 LOG.debug("loaded xss filter rule " + (String)entry.getKey() + "=\"" + StringEscapeUtils.escapeJava((String)entry.getValue()) + 
      				   "\"");
      			   }
      			 }
      			 catch (IllegalArgumentException e)
      			 {
      			   LOG.error("error parsing xss filter rule " + entry, e);
      			 }
   		   }
   	 }
	     return ret;
	 }
	
}
