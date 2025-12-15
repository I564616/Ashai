package com.apb.facades.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.converters.populators.B2BCustomerReversePopulator;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.GenericSearchConstants.LOG;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.util.AsahiSiteUtil;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.sabmiller.core.enums.AsahiRole;


/**
 * @author Ganesh.Muddliyar
 * @see Apb customer reverse Populator
 */
public class AsahiCustomerReversePopulator extends B2BCustomerReversePopulator
{

	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;

	@Resource
	private ApbB2BUnitService apbB2BUnitService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	private static final Logger LOG = LoggerFactory.getLogger(AsahiCustomerReversePopulator.class);

	@Override
	public void populate(final CustomerData source, final B2BCustomerModel target) throws ConversionException
	{

		if(!asahiSiteUtil.isCub())
		{
			ServicesUtil.validateParameterNotNull(source, "Parameter source cannot be null.");
			ServicesUtil.validateParameterNotNull(target, "Parameter target cannot be null.");
			target.setEmail(source.getEmail());
			if (null != source.getContactNumber())
			{
				target.setContactNumber(StringUtils.deleteWhitespace(source.getContactNumber()));
			}
			else
			{
				target.setContactNumber(null);
			}
			target.setName(this.getCustomerNameStrategy().getName(source.getFirstName(), source.getLastName()));
			target.setFirstName(source.getFirstName());
			target.setLastName(source.getLastName());
			this.populateUid(source, target);
			this.populateTitle(source, target);
			populateUserRole(source, target);
			if(StringUtils.isEmpty(source.getUid())) {
				this.getB2BCommerceB2BUserGroupService().updateUserGroups(this.getB2BUserGroupsLookUpStrategy().getUserGroups(), source.getRoles(), target);
				this.populateDefaultUnit(source, target);
			}

			LOG.info("Customer details populated successfully : " + target.getName());
		}
		else
		{
			super.populate(source, target);
		}
	}

	private void populateUserRole(final CustomerData source, final B2BCustomerModel target)
	{
		if (null != source.getAsahiRole() && target instanceof B2BCustomerModel)
		{
			target.setAsahiRole(this.enumerationService.getEnumerationValue(AsahiRole.class, source.getAsahiRole().getCode()));
		}
	}


	@Override
	protected void populateDefaultUnit(final CustomerData source, final B2BCustomerModel target)
	{
		final B2BUnitModel oldDefaultUnit = getB2BUnitService().getParent(target);
		final B2BUnitModel defaultUnit = getB2BUnitService().getUnitForUid(source.getUnit().getUid());
		target.setDefaultB2BUnit(defaultUnit);
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(target.getGroups());
		groups.add(defaultUnit);
		target.setGroups(groups);
	}
}
