/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.facades.customer.B2BUnitJson;
import com.sabmiller.facades.customer.CustomerJson;
import com.sabmiller.facades.customer.RegionJson;


/**
 * The Class SABMCustomerStatesPopulator.
 */
public class SABMCustomerStatesPopulator implements Populator<CustomerModel, CustomerJson>
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMCustomerStatesPopulator.class);

	/** The b2b unit service. */
	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	@Resource(name = "userService")
	private UserService userService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final CustomerModel source, final CustomerJson target) throws ConversionException
	{
		final UserModel currentUser = userService.getCurrentUser();
		if (!(currentUser instanceof B2BCustomerModel))
		{
			LOG.error("The currentUser [{}] is not a B2BCustomerModel! Impossible to populate States", source);
			return;
		}

		populatorStates(source, target, (B2BCustomerModel) currentUser);
	}

	/**
	 * Populator states.
	 *
	 * @param source
	 *           the source
	 * @param target
	 *           the target
	 */
	private void populatorStates(final CustomerModel source, final CustomerJson target, final B2BCustomerModel currentUser)
	{
		final B2BUnitModel zadpUnit = b2bUnitService.findTopLevelB2BUnit(currentUser);

		if (zadpUnit != null)
		{
			setStates(source, target, zadpUnit.getMembers());
		}
		else
		{
			setStates(source, target, currentUser.getGroups());
		}
	}

	/**
	 * Sets the states.
	 *
	 * @param source
	 *           the source
	 * @param target
	 *           the target
	 */
	private void setStates(final CustomerModel source, final CustomerJson target, final Set<? extends PrincipalModel> allMembers)
	{
		final Map<String, List<B2BUnitJson>> map = new HashMap<>();

		for (final PrincipalModel principalModel : allMembers)
		{
			if (principalModel instanceof AsahiB2BUnitModel)
			{
				continue;
			}
			
			if (principalModel instanceof B2BUnitModel)
			{
				final B2BUnitModel b2bUnit = (B2BUnitModel) principalModel;
				final AddressModel address = b2bUnitService.getContactAddressFormB2BUnit(b2bUnit);

				if (address != null && address.getRegion() != null)
				{
					if (!map.containsKey(address.getRegion().getIsocode()))
					{
						map.put(address.getRegion().getIsocode(), new ArrayList<>());
					}

					map.get(address.getRegion().getIsocode()).add(generateB2BUnit(source, b2bUnit, address));
				}
			}
		}

		final List<RegionJson> regionJsons = Lists.newArrayList();

		for (final Map.Entry<String, List<B2BUnitJson>> entry : map.entrySet())
		{
			final RegionJson regionJson = new RegionJson();
			regionJson.setIsocode(entry.getKey());
			regionJson.setB2bunits(entry.getValue());
			regionJsons.add(regionJson);
		}

		target.setStates(regionJsons);
	}

	/**
	 * Create B2BUnitJson.
	 *
	 * @param source
	 *           the source
	 * @param b2bUnit
	 *           the b2b unit
	 * @param address
	 *           the address
	 * @return B2BUnitJson
	 */
	private B2BUnitJson generateB2BUnit(final CustomerModel source, final B2BUnitModel b2bUnit, final AddressModel address)
	{
		final B2BUnitJson b2bUnitJson = new B2BUnitJson();
		b2bUnitJson.setActive(Boolean.TRUE.booleanValue());
		b2bUnitJson.setCode(b2bUnit.getUid());
		b2bUnitJson.setName(b2bUnit.getName());
		final String addressStr = (StringUtils.isNotEmpty(address.getStreetname()) ? address.getStreetname() : "")
				+ (StringUtils.isNotEmpty(address.getRegion().getName()) ? ", " + address.getRegion().getName() : "");
		b2bUnitJson.setAddress(addressStr);
		return b2bUnitJson;
	}
}
