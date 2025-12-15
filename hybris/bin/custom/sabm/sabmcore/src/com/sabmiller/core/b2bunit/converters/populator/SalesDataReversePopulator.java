/**
 *
 */
package com.sabmiller.core.b2bunit.converters.populator;

import de.hybris.platform.converters.Populator;

import com.sabmiller.core.model.SalesDataModel;
import com.sabmiller.facades.b2bunit.data.SalesData;


/**
 * @author joshua.a.antony
 *
 */
public class SalesDataReversePopulator implements Populator<SalesData, SalesDataModel>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.converters.impl.AbstractConverter#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final SalesData source, final SalesDataModel target)
	{
		if (source != null)
		{
			target.setSalesOrgId(source.getSalesOrgId());
			target.setDefaultDeliveryPlant(source.getDefaultDeliveryPlant());
			target.setDeletionIndicator(source.getDeletionIndicator());
			target.setDistributionChannel(source.getDistributionChannel());
			target.setDivision(source.getDivision());
		}
	}

}
