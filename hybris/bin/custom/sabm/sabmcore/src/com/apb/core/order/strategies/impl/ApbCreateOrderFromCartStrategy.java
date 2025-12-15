package com.apb.core.order.strategies.impl;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.strategies.impl.DefaultCreateOrderFromCartStrategy;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;


public class ApbCreateOrderFromCartStrategy extends DefaultCreateOrderFromCartStrategy
{
	@Resource
	private AsahiConfigurationService asahiConfigurationService;

	@Resource
	private CMSSiteService cmsSiteService;

	@Autowired
	private AsahiOrderCodeGenerator asahiOrderCodeGenerator;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	private static final String ASAHI_ORDER_CODE_PREFIX = "asahi.order.code.prefix.";

	@Override
	protected String generateOrderCode(final CartModel cart)
	{
		if(!asahiSiteUtil.isCub())
		{
   		Object generatedValue = asahiOrderCodeGenerator.getAsahiKeyGenerator().generate();
   		
   		if (generatedValue instanceof String)
   		{
   			return addPrefixToCode((String) generatedValue);
   		}
   		else
   		{
   			return addPrefixToCode(String.valueOf(generatedValue));
   		}
		}
		else
		{
			return super.generateOrderCode(cart);
		}
	}

	protected String addPrefixToCode(final String generatedOrderCode)
	{
		final StringBuilder orderCode = new StringBuilder();
		return orderCode
				.append(asahiConfigurationService.getConfiguration()
						.getString(ASAHI_ORDER_CODE_PREFIX + cmsSiteService.getCurrentSite().getUid(), "HO"))
				.append(generatedOrderCode).toString();
	}

}
