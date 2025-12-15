

/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.util.SabmUtils;


/**
 * SABMBusinessB2BUnitPopulator Populate the target instance from the source instance.
 */
public class SABMBusinessB2BUnitPopulator implements Populator<B2BUnitModel, B2BUnitData>
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMBusinessB2BUnitPopulator.class);

	@Override
	public void populate(final B2BUnitModel source, final B2BUnitData target) throws ConversionException
	{
		try
		{
			target.setUid(source.getUid());
			target.setName(source.getName());
			int activeUserNumber = 0;
			final Set<PrincipalModel> members = source.getMembers();
			for (final PrincipalModel member : members)
			{
				if (member instanceof B2BCustomerModel && !(member instanceof BDECustomerModel))
				{
					final B2BCustomerModel customerModel = (B2BCustomerModel) member;
					// Count the active user number of current B2bUnit
					if (customerModel.getActive() != null && customerModel.getActive().booleanValue())
					{
						if(SabmUtils.isCustomerActiveForAccountCUB(source,customerModel))
						{
						activeUserNumber++;
						}
					}
				}
			}
			target.setActiveUsers(activeUserNumber);
			if (source.getContactAddress() != null)
			{
				target.setContactAddress(populateAddress(source.getContactAddress()));
			}
			setAddresses(source, target);
		}
		catch (final Exception e)
		{
			LOG.error("Exception Occured while Populating B2BUnit for business unit page", e);
		}
	}

	public final AddressData populateAddress(final AddressModel source)
	{
		final AddressData target = new AddressData();
		target.setLine1(source.getLine1());
		target.setLine2(source.getLine2());
		target.setTown(source.getTown());
		target.setPostalCode(source.getPostalcode());
		final RegionModel regionModel = source.getRegion();
		if (regionModel != null)
		{
			final RegionData regionData = new RegionData();
			regionData.setIsocode(regionModel.getIsocode());
			regionData.setIsocodeShort(regionModel.getIsocodeShort());
			regionData.setName(regionModel.getName());
			if (regionModel.getCountry() != null)
			{
				regionData.setCountryIso(regionModel.getCountry().getIsocode());
			}
			target.setRegion(regionData);
		}
		return target;
	}

	public void setAddresses(final B2BUnitModel b2bUnitModel, final B2BUnitData b2bUnitData)
	{
		try
		{
			if (CollectionUtils.isNotEmpty(b2bUnitModel.getAddresses()))
			{
				final List<AddressData> addresses = new ArrayList<AddressData>();
				for (final AddressModel addressModel : b2bUnitModel.getAddresses())
				{
					if (Boolean.TRUE.equals(addressModel.getShippingAddress()))
					{
						addresses.add(populateAddress(addressModel));
					}
				}
				b2bUnitData.setAddresses(addresses);
			}
			else
			{
				LOG.warn("Attribute ShippingAddresses is null in B2BUnitModel: {}", b2bUnitModel);
				b2bUnitData.setAddresses(null);
			}
		}
		catch (final Exception e)
		{
			LOG.error("Addresses are not set for B2BUnitModel: {}", b2bUnitModel);
		}
	}
}