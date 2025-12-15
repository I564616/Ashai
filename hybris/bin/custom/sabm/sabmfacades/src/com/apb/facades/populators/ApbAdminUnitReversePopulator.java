package com.apb.facades.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Collection;
import java.util.HashSet;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.model.AsahiB2BUnitModel;


public class ApbAdminUnitReversePopulator implements Populator<CustomerData, B2BCustomerModel>
{

	@Autowired
	private B2BUnitService b2bUnitService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Override
	public void populate(final CustomerData customerData, final B2BCustomerModel asahiB2BUnitModel) throws ConversionException
	{
		if (!asahiSiteUtil.isCub())
		{
			final Collection<AsahiB2BUnitModel> adminUnits = new HashSet<>();
			if (null != asahiB2BUnitModel.getAdminUnits())
			{
				adminUnits.addAll(asahiB2BUnitModel.getAdminUnits());
			}
			if (BooleanUtils.isTrue(customerData.getIsAdminUser()))
			{
				adminUnits.add((AsahiB2BUnitModel) b2bUnitService.getUnitForUid(customerData.getUnit().getUid()));
			}
			else
			{
				adminUnits.remove(b2bUnitService.getUnitForUid(customerData.getUnit().getUid()));
			}

			asahiB2BUnitModel.setAdminUnits(adminUnits);
		}
	}
}
