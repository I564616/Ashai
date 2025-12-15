/**
 *
 */
package com.sabmiller.core.cart.service.impl;

import de.hybris.platform.commerceservices.externaltax.ExternalTaxesService;
import de.hybris.platform.commerceservices.externaltax.impl.DefaultExternalTaxesService;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import jakarta.annotation.Resource;

import com.apb.core.util.AsahiSiteUtil;


/**
 * No calculations in hybris since everything will be coming from SAP. Hybris just need to sum up all the individual
 * entry totals.
 */
public class DefaultSABMExternalTaxesService extends DefaultExternalTaxesService implements ExternalTaxesService
{

	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commerceservices.externaltax.ExternalTaxesService#calculateExternalTaxes(de.hybris.platform.
	 * core.model.order.AbstractOrderModel)
	 */
	@Override
	public boolean calculateExternalTaxes(final AbstractOrderModel abstractOrder)
	{
		if(!asahiSiteUtil.isCub())
		{
		// no calculations in hybris since everything will be coming from SAP
			return super.calculateExternalTaxes(abstractOrder);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commerceservices.externaltax.ExternalTaxesService#clearSessionTaxDocument()
	 */
	@Override
	public void clearSessionTaxDocument()
	{
		if(!asahiSiteUtil.isCub())
		{
			super.clearSessionTaxDocument();
		}
		// no calculations in hybris since everything will be coming from SAP

	}



}
