/**
 *
 */
package com.sabmiller.facades.order.hook.impl;

import de.hybris.platform.commerceservices.order.hook.CommerceCartCalculationMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.util.AsahiSiteUtil;


/**
 * The Class SABMCommerceCartCalculationMethodHook.
 *
 * @author xue.zeng
 */
public class SABMCommerceCartCalculationMethodHook implements CommerceCartCalculationMethodHook
{

	private static final Logger LOG = LoggerFactory.getLogger(SABMCommerceCartCalculationMethodHook.class);
	/** The model service. */
	@Resource(name = "modelService")
	private ModelService modelService;

	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commerceservices.order.hook.CommerceCartCalculationMethodHook#afterCalculate(de.hybris.platform
	 * .commerceservices.service.data.CommerceCartParameter)
	 */
	@Override
	public void afterCalculate(final CommerceCartParameter parameter)
	{
		if(asahiSiteUtil.isCub())
		{
   		if (null != parameter)
   		{
   			final CartModel cart = parameter.getCart();
   			final List<AbstractOrderEntryModel> orderEntrys = cart.getEntries();
   			// Reset isChage=false entry in cart
   
   			for (final AbstractOrderEntryModel orderEntry : ListUtils.emptyIfNull(orderEntrys))
   			{
   				try
   				{
   					orderEntry.setIsChange(Boolean.FALSE);
   					modelService.refresh(orderEntry);
   					modelService.save(orderEntry);
   				}
   				catch (final Exception e)
   				{
   					LOG.error("Error while saving the entry:", e);
   				}
   			}
   		}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commerceservices.order.hook.CommerceCartCalculationMethodHook#beforeCalculate(de.hybris.
	 * platform.commerceservices.service.data.CommerceCartParameter)
	 */
	@Override
	public void beforeCalculate(final CommerceCartParameter parameter)
	{

	}
}
