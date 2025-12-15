/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sabmiller.core.b2b.services.SabmB2BCustomerService;
import com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade;
import com.sabmiller.facades.constants.SabmFacadesConstants;
import com.sabmiller.facades.customer.AdminCustomerJson;
import com.sabmiller.facades.customer.CustomerJson;
import com.sabmiller.facades.customer.PermissionsJson;


/**
 * @author xue.zeng
 *
 */
public class SABMCustomerJsonPopulator implements Populator<CustomerModel, CustomerJson>
{
	@Resource(name = "b2bCommerceUnitFacade")
	private SabmB2BCommerceUnitFacade b2bCommerceUnitFacade;
	@Resource(name = "sabmB2BCustomerService")
	private SabmB2BCustomerService sabmB2BCustomerService;
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
		if (source instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCustomer = (B2BCustomerModel) source;
			target.setExists(Boolean.TRUE.booleanValue());
			target.setActive(Boolean.TRUE.equals(b2bCustomer.getActive()));
			target.setFirstName(StringUtils.isNotEmpty(b2bCustomer.getFirstName()) ? b2bCustomer.getFirstName() : "");
			target.setSurName(StringUtils.isNotEmpty(b2bCustomer.getLastName()) ? b2bCustomer.getLastName() : "");
			target.setEmail(b2bCustomer.getEmail());
			target.setThisZADP(b2bCommerceUnitFacade.isCurrentB2BUnitExistOfUid(b2bCustomer.getUid()));

			/*
			 * SABMC-1014
			 */
			final CustomerModel currentUser = (CustomerModel) userService.getCurrentUser();
			if (source.getUid().equals(currentUser.getUid()))
			{
				if (source.getPrimaryAdmin() != null && !source.getPrimaryAdmin())
				{
					target.setSelf(Boolean.TRUE.booleanValue());
				}
			}

			populateRole(b2bCustomer, target);
			populateAdmin(b2bCustomer, target);
		}
	}

	/**
	 * Convert customer admin
	 *
	 * @param source
	 * @param target
	 */
	private void populateAdmin(final B2BCustomerModel source, final CustomerJson target)
	{
		final List<AdminCustomerJson> adminCustomerJsons = Lists.newArrayList();
		final List<B2BCustomerModel> adminCustomers = sabmB2BCustomerService.getUsersByGroups(source);

		for (final B2BCustomerModel b2bCustomerModel : ListUtils.emptyIfNull(adminCustomers))
		{
			final AdminCustomerJson adminCustomerJson = new AdminCustomerJson();
			adminCustomerJson.setName(b2bCustomerModel.getName());
			adminCustomerJson.setEmail(b2bCustomerModel.getEmail());
			adminCustomerJsons.add(adminCustomerJson);
		}

		target.setAdmins(adminCustomerJsons);
	}

	/**
	 * Convert customer role
	 *
	 * @param source
	 * @param target
	 */
	protected void populateRole(final B2BCustomerModel source, final CustomerJson target)
	{
		final Set<PrincipalGroupModel> roleModels = Sets.newConcurrentHashSet(source.getGroups());
		roleModels.removeIf(role -> role instanceof B2BUnitModel);
        roleModels.removeIf(role -> role instanceof B2BUserGroupModel);

		final PermissionsJson permissionsJson = new PermissionsJson();
		permissionsJson.setOrderLimit(source.getOrderLimit());
		for (final PrincipalGroupModel role : roleModels)
		{
			// only display allowed usergroups
			if (SabmFacadesConstants.B2BORDERCUSTOMER.equals(role.getUid()))
			{
				permissionsJson.setOrders(true);
				continue;
			}

			if (SabmFacadesConstants.B2BINVOICECUSTOMER.equals(role.getUid()))
			{
				permissionsJson.setPay(true);
				continue;
			}

			if (SabmFacadesConstants.B2BASSISTANTGROUP.equals(role.getUid()))
			{
				permissionsJson.setPa(true);
				continue;
			}
		}
		target.setPermissions(permissionsJson);
	}
}
