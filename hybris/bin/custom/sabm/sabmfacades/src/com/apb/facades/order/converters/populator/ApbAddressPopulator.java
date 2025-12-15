package com.apb.facades.order.converters.populator;

import de.hybris.platform.commercefacades.user.converters.populator.AddressPopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.store.services.BaseStoreService;

import jakarta.annotation.Resource;

import com.apb.core.util.ApbAddressTimeUtil;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.apb.core.util.AsahiSiteUtil;


/**
 * ApbAddressPopulator implementation of {@link ApbAddressPopulator}
 */
public class ApbAddressPopulator extends AddressPopulator
{
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Override
	public void populate(final AddressModel source, final AddressData target)
	{
		super.populate(source, target);
		if(!asahiSiteUtil.isCub())
		{
   		target.setRecordId(source.getAddressRecordid());
   		
   		target.setDefaultAddress(source.getDefaultAddress());
   
   		if (null != source.getEclDeliveryTimeslotFrom())
   		{
   			target.setDeliveryTimeSlotFrom(ApbAddressTimeUtil.getDeliveryTimeStringFull(source.getEclDeliveryTimeslotFrom()));
   		}
   		if (null != source.getEclDeliveryTimesLotto())
   		{
   			target.setDeliveryTimeSlotTo(ApbAddressTimeUtil.getDeliveryTimeStringFull(source.getEclDeliveryTimesLotto()));
   		}
   		target.setDeliveryInstruction(source.getEclDeliveryInstruction());
   		target.setDeliveryCalendar(source.getDeliveryCalendar());
   		target.setStreetname(source.getStreetname());
   		target.setStreetnumber(source.getStreetnumber());
   		if (null != source.getAddressType())
   		{
   			target.setBackendAddressType(source.getAddressType());
   		}
   
   		target.setDefaultAddress(source.getDefaultAddress());
		}
	}
}
