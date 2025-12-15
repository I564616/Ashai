package com.apb.core.place.order.strategy;

import jakarta.annotation.Resource;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiSiteUtil;

import de.hybris.platform.assistedservicefacades.hook.AssistedServicePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.order.impl.DefaultCommercePlaceOrderStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.order.InvalidCartException;


/**
 *
 */
public class AsahiCommercePlaceOrderStrategy extends DefaultCommercePlaceOrderStrategy
{
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Override
	protected void afterPlaceOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
			throws InvalidCartException
	{
		
		if(!asahiSiteUtil.isCub())
		{

   		if (getCommercePlaceOrderMethodHooks() != null && (parameter.isEnableHooks()))
   		{
   			for (final CommercePlaceOrderMethodHook commercePlaceOrderMethodHook : getCommercePlaceOrderMethodHooks())
   			{
   				if (commercePlaceOrderMethodHook instanceof AssistedServicePlaceOrderMethodHook)
   				{
   					commercePlaceOrderMethodHook.afterPlaceOrder(parameter, result);
   				}
   			}
   		}
		}
		else
		{
			super.afterPlaceOrder(parameter,result);
		}
	}
}
