/**
 *
 */
package com.sabmiller.core.b2bunit.converters.populator;

import de.hybris.platform.converters.Populator;

import com.sabmiller.core.model.SalesOrgDataModel;
import com.sabmiller.facades.b2bunit.data.SalesOrgData;


/**
 * @author joshua.a.antony
 *
 */
public class SalesOrgDataReversePopulator implements Populator<SalesOrgData, SalesOrgDataModel>
{

	@Override
	public void populate(final SalesOrgData source, final SalesOrgDataModel target)
	{
		if (source != null)
		{
			target.setCustomerGroup(source.getCustomerGroup());
			target.setCustomerStatisticGroup(source.getCustomerStatisticGroup());
			target.setGroupDescription(source.getGroupDescription());
			target.setSalesDistrictCode(source.getSalesDistrictCode());
			target.setSalesGroup(source.getSalesGroup());
			target.setSalesOfficeCode(source.getSalesOfficeCode());
			target.setPriceGroup(source.getPriceGroup());
			target.setShippingCondition(source.getShippingCondition());
		}
	}

}
