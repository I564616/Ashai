/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.facades.customer.B2BUnitJson;
import com.sabmiller.facades.customer.CustomerJson;
import com.sabmiller.facades.customer.RegionJson;


/**
 * @author xue.zeng
 *
 */
public class SABMCustomerCurrentB2BUnitPopulator implements Populator<CustomerModel, CustomerJson>
{
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final CustomerModel source, final CustomerJson target) throws ConversionException
	{
		if (source instanceof B2BCustomerModel)
		{
			populateSelectedB2BUnit(source, target);
		}
	}

	/**
	 * @param source
	 * @param target
	 */
	private void populateSelectedB2BUnit(final CustomerModel source, final CustomerJson target)
	{
		final Set<PrincipalGroupModel> principalGroups = source.getGroups();
		CollectionUtils.filter(principalGroups,
				PredicateUtils.notPredicate(PredicateUtils.instanceofPredicate(B2BUserGroupModel.class)));
		for (final PrincipalGroupModel principalGroup : SetUtils.emptyIfNull(principalGroups))
		{
			if (principalGroup instanceof AsahiB2BUnitModel)
			{
				continue;
			}
			if (principalGroup instanceof B2BUnitModel)
			{
				final B2BUnitModel b2bUnit = (B2BUnitModel) principalGroup;
				final AddressModel address = b2bUnitService.getContactAddressFormB2BUnit(b2bUnit);
				if (address != null && address.getRegion() != null && (CollectionUtils.isEmpty(b2bUnit.getCubDisabledUsers())
						|| !b2bUnit.getCubDisabledUsers().contains(source.getUid())))
				{
					setSelectedB2BUnit(target, b2bUnit, address);
				}
			}
		}
	}

	/**
	 * Set the customer has B2BUnit for the selected state
	 *
	 * @param target
	 * @param b2bUnit
	 */
	private void setSelectedB2BUnit(final CustomerJson target, final B2BUnitModel b2bUnit, final AddressModel address)
	{
		boolean isExistRegion = false;
		for (final RegionJson regionJson : ListUtils.emptyIfNull(target.getStates()))
		{
			if (regionJson.getIsocode().equals(address.getRegion().getIsocode()))
			{
				isExistRegion = true;
				updateB2BUnitJson(b2bUnit, address, regionJson);
			}
		}
		if (!isExistRegion)
		{
			target.getStates().add(generateRegionJson(b2bUnit, address));
		}
	}

	/**
	 * Generate a new RegionJson
	 *
	 * @param b2bUnit
	 * @param address
	 * @return regionJson
	 */
	private RegionJson generateRegionJson(final B2BUnitModel b2bUnit, final AddressModel address)
	{
		final RegionJson regionJson = new RegionJson();
		regionJson.setIsocode(address.getRegion().getIsocode());
		final List<B2BUnitJson> b2bUnitJsons = Lists.newArrayList();
		b2bUnitJsons.add(generateB2BUnit(b2bUnit, address));
		regionJson.setB2bunits(b2bUnitJsons);
		return regionJson;
	}

	/**
	 * Update B2BUnitJson. If b2bUnit exists in regionJson, it will be set to b2bUnitJson.selected=true, If b2bUnit does
	 * not exist in regionJson, it will generate a new b2bUnitJson
	 *
	 * @param b2bUnit
	 * @param address
	 * @param regionJson
	 */
	private void updateB2BUnitJson(final B2BUnitModel b2bUnit, final AddressModel address, final RegionJson regionJson)
	{
		boolean isExistB2BUnit = false;
		for (final B2BUnitJson b2bUnitJson : ListUtils.emptyIfNull(regionJson.getB2bunits()))
		{
			if (b2bUnit.getUid().equals(b2bUnitJson.getCode()))
			{
				b2bUnitJson.setSelected(Boolean.TRUE.booleanValue());
				isExistB2BUnit = true;
				break;
			}
		}
		if (!isExistB2BUnit)
		{
			regionJson.getB2bunits().add(generateB2BUnit(b2bUnit, address));
		}
	}

	/**
	 * Create B2BUnitJson.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @param address
	 *           the address
	 * @return B2BUnitJson
	 */
	private B2BUnitJson generateB2BUnit(final B2BUnitModel b2bUnit, final AddressModel address)
	{
		final B2BUnitJson b2bUnitJson = new B2BUnitJson();
		b2bUnitJson.setActive(Boolean.FALSE.booleanValue());
		b2bUnitJson.setSelected(Boolean.TRUE.booleanValue());
		b2bUnitJson.setCode(b2bUnit.getUid());
		b2bUnitJson.setName(b2bUnit.getName());
		final String addressStr = (StringUtils.isNotEmpty(address.getStreetname()) ? address.getStreetname() : "")
				+ (StringUtils.isNotEmpty(address.getRegion().getName()) ? ", " + address.getRegion().getName() : "");
		b2bUnitJson.setAddress(addressStr);
		return b2bUnitJson;
	}
}
