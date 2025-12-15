package com.apb.facades.populators;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.converters.populators.B2BUnitPopulator;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.enums.AddressType;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.constants.SabmCoreConstants;


/**
 * Populator to convert AsahiB2BunitModel to AsahiB2BunitData.
 */
public class AsahiB2BUnitPopulator extends B2BUnitPopulator
{
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	@Override
	public void populate(final B2BUnitModel source, final B2BUnitData target) throws ConversionException
	{
		super.populateUnit(source, target);
		if(!asahiSiteUtil.isCub() && source instanceof AsahiB2BUnitModel)
		{
			populateAsahiAttribute((AsahiB2BUnitModel) source, target);
			populateUnitAddresses((AsahiB2BUnitModel) source, target);
		}
	}

	private void populateAsahiAttribute(final AsahiB2BUnitModel source, final B2BUnitData target)
	{
		if (StringUtils.isNotEmpty(source.getAccountNum()))
		{
			target.setAccountNumber(source.getAccountNum());
		}

	}


	private void populateUnitAddresses(final AsahiB2BUnitModel source, final B2BUnitData target)
	{
		Collection<AddressModel> addresses = new ArrayList<>();

		if (this.asahiSiteUtil.isApb() && CollectionUtils.isNotEmpty(source.getAddresses()))
		{
			addresses = source.getAddresses().stream()
					.filter(address -> null != address.getAddressType() && !address.getAddressType().equals(AddressType.INVOICE))
					.collect(Collectors.toList());

		}

		if (this.asahiSiteUtil.isSga() && null != source && CollectionUtils.isNotEmpty(source.getShipToAccounts()))
		{
			for (final AsahiB2BUnitModel b2bUnitModel : source.getShipToAccounts())
			{
				if (CollectionUtils.isNotEmpty(b2bUnitModel.getAddresses()))
				{
					addresses.add((AddressModel) ((List) b2bUnitModel.getAddresses()).get(0));
				}
			}
		}

		target.setAddresses(Converters.convertAll((addresses), this.getAddressConverter()));

	}


}
