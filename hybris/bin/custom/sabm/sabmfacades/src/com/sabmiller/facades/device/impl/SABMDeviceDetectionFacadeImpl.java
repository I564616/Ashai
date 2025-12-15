/**
 *
 */
package com.sabmiller.facades.device.impl;

import de.hybris.platform.acceleratorfacades.device.impl.DefaultDeviceDetectionFacade;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.util.AsahiSiteUtil;



/**
 * The Class SABMDeviceDetectionFacadeImpl override the initializeRequest to set the Desktop UX if the request path
 * match the configured pattern.
 */
public class SABMDeviceDetectionFacadeImpl extends DefaultDeviceDetectionFacade
{
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMDeviceDetectionFacadeImpl.class.getName());

	@Override
	public void initializeRequest(final HttpServletRequest request)
	{
		
		if(asahiSiteUtil.isCub())
		{
   		final String patternForceDesktop = getSiteConfigService().getString("request.path.force.desktop", ".*");
   
   		LOG.debug("analyzing request: {} with pattern: {}", request.getRequestURI(), patternForceDesktop);
   
   		final Pattern pattern = Pattern.compile(patternForceDesktop);
   
   		final Matcher matcher = pattern.matcher(request.getRequestURI());
   
   		if (matcher.matches())
   		{
   			LOG.debug("setting DESKTOP user experience for request: {}", request.getRequestURI());
   
   			//forcing the DESKTOP UX
   			getUiExperienceService().setDetectedUiExperienceLevel(UiExperienceLevel.DESKTOP);
   		}
   		else
   		{
   			LOG.debug("using OOB detection facade for request: {}", request.getRequestURI());
   
   			//The request path doesn't match the configured regex, so using the OOB functionality
   			super.initializeRequest(request);
   		}
		}
		else
		{
			super.initializeRequest(request);
		}
	}
}
