/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bcommercefacades.company.converters.populators.B2BCustomerPopulator;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import jakarta.annotation.Resource;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.util.SabmUtils;


/**
 * @author GQ485VQ
 *
 */
public class DefaultSABMB2BCustomerPopulator extends B2BCustomerPopulator
{
	@Resource(name = "asahiSiteUtil")
	private AsahiSiteUtil asahiSiteUtil;

	@Override
	public void populate(final CustomerModel source, final CustomerData target) throws ConversionException
	{
		super.populate(source, target);
		//Set b2bcustomer active/inactive based on disableduserlist for CUB
		if (source instanceof B2BCustomerModel && asahiSiteUtil.isCub())
		{
			target.setActive(SabmUtils.isCustomerActiveForCUB((B2BCustomerModel) source));
		}
	}
}
