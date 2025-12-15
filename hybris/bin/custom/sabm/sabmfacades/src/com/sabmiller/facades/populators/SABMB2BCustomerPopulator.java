/**
 *
 */
package com.sabmiller.facades.populators;


import com.sabmiller.core.jalo.BDECustomerImported;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.BDECustomerImportedModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.util.SabmUtils;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.BDECustomerImportedModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.facades.constants.SabmFacadesConstants;


/**
 * Convert the CustomerModel to CustomerData
 *
 * @author xiaowu.a.zhang
 * @data 2015-12-24
 */
public class SABMB2BCustomerPopulator implements Populator<CustomerModel, CustomerData>
{

	private SabmB2BUnitService b2bUnitService;

	private SABMB2BUnitPopulator sabmB2BUnitPopulator;

	private Converter<UserModel, CustomerData> customerConverter;

	@Resource
	private BaseSiteService baseSiteService;

	/**
	 * populate the attributes from CustomerModel to CustomerData
	 *
	 * @param source
	 * @param target
	 */
	@Override
	public void populate(final CustomerModel source, final CustomerData target) throws ConversionException
	{
		if(baseSiteService.getCurrentBaseSite()!=null && baseSiteService.getCurrentBaseSite().getUid().equalsIgnoreCase(SabmCoreConstants.CUB_STORE))
		{
		if (source instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = (B2BCustomerModel) source;

			target.setOrderLimit(customer.getOrderLimit());
			//			target.setReceiveUpdates(customer.getReceiveUpdates());

			target.setEmail(customer.getContactEmail());

			/*
			   solve issue for BDECustomerModel, don't get email from dynamic attribute.
			   this should apply to B2BCustomerModel as well, keep it only for BDECustomerModel now.
			 */
			if (source instanceof BDECustomerModel){
				target.setEmail(customer.getEmail());
			}

			this.setUserRoleByUserGroup(source, target);
			target.setReceiveUpdates(customer.getReceiveUpdates());
			target.setReceiveUpdatesForSms(customer.getReceiveUpdatesForSms());
			target.setRemenberPreviousUnit(source.getPreviousB2bUnit() != null ? source.getPreviousB2bUnit() : false);
			if (source.getCreatedBy() != null)
			{
				target.setCreatedBy(getCustomerConverter().convert(source.getCreatedBy()));
			}

			target.setPrimaryAdmin(customer.getPrimaryAdmin());

			target.setWelcomeEmailSentStatus(customer.getWelcomeEmailStatus());

			target.setPasswordIsSet(StringUtils.isNotEmpty(customer.getEncodedPassword()));

			// minimal properties are populated, as require by customer paginated page.
			final B2BUnitModel parent = getB2bUnitService().getParent(customer);
			if (parent != null)
			{
				final B2BUnitData b2BUnitData = (target.getUnit() != null) ? target.getUnit() : new B2BUnitData();
				getSabmB2BUnitPopulator().populate(parent, b2BUnitData);
				target.setUnit(b2BUnitData);

				target.setPrimaryAdminStatus(getB2bUnitService().findPrimaryAdminStatus(b2BUnitData.getUid()));
			}

			target.setIsZadp(false);
			if (getB2bUnitService().findTopLevelB2BUnit(customer) != null)
			{
				target.setIsZadp(true);
			}


			final B2BUnitModel defaultB2BUnit = customer.getDefaultB2BUnit();
			final B2BUnitData defaultB2BUnitData = new B2BUnitData();
			if (defaultB2BUnit != null)
			{
				getSabmB2BUnitPopulator().populate(defaultB2BUnit, defaultB2BUnitData);
				target.setDefaultB2bUnit(defaultB2BUnitData);
			}

			//list all of the b2bunits belong to current customer in personal detail page
			final Set<PrincipalGroupModel> groups = customer.getGroups();
			final List<B2BUnitData> b2bUnits = new ArrayList<B2BUnitData>();
			for (final PrincipalGroupModel principalGroupModel : groups)
			{
				if(principalGroupModel instanceof AsahiB2BUnitModel)
				{
					continue;
				}

				if (principalGroupModel instanceof B2BUnitModel)
				{
					final B2BUnitModel b2bUnit = (B2BUnitModel) principalGroupModel;
					final B2BUnitData b2BUnitData = new B2BUnitData();
					getSabmB2BUnitPopulator().populate(b2bUnit, b2BUnitData);
					b2bUnits.add(b2BUnitData);
				}
			}
			target.setB2bUnits(b2bUnits);

		}

		if (source instanceof BDECustomerModel || source instanceof BDECustomerImportedModel){
			target.setName(source.getName() + " (CUB)") ;
			// for any BDE user, regardless login via staff portal or imported from CSV file , their userRole = bde-user
			target.setUserRole("bde-user");

		}
		
		}
	}

	/**
	 *
	 * @param source
	 */
	private void setUserRoleByUserGroup(final CustomerModel source, final CustomerData target)
	{
		final Set<PrincipalGroupModel> principalGroupModels = source.getGroups();
		for (final PrincipalGroupModel principalGroupModel : principalGroupModels)
		{
			final String groupId = principalGroupModel.getUid();
			if (B2BConstants.B2BADMINGROUP.equals(groupId))
			{
				target.setUserRole(SabmFacadesConstants.USER_ROLE_ADMIN);
				target.setPersonalAssistant(SabmFacadesConstants.CHECKBOX_CHECKED_VALUE);
				break;
			}
			if (B2BConstants.B2BCUSTOMERGROUP.equals(groupId))
			{
				target.setUserRole(SabmFacadesConstants.USER_ROLE_STAFF_USERS);
			}
			if (SabmFacadesConstants.B2BORDERCUSTOMER.equals(groupId))
			{
				target.setCanPlaceOrder(SabmFacadesConstants.CHECKBOX_CHECKED_VALUE);
			}
			if (SabmFacadesConstants.B2BINVOICECUSTOMER.equals(groupId))
			{
				target.setCanViewPayInvoice(SabmFacadesConstants.CHECKBOX_CHECKED_VALUE);
			}
			if (SabmFacadesConstants.B2BASSISTANTGROUP.equals(groupId))
			{
				target.setPersonalAssistant(SabmFacadesConstants.CHECKBOX_CHECKED_VALUE);
			}

		}
	}





	/**
	 * @return the sabmB2BUnitPopulator
	 */
	public SABMB2BUnitPopulator getSabmB2BUnitPopulator()
	{
		return sabmB2BUnitPopulator;
	}

	/**
	 * @param sabmB2BUnitPopulator
	 *           the sabmB2BUnitPopulator to set
	 */
	public void setSabmB2BUnitPopulator(final SABMB2BUnitPopulator sabmB2BUnitPopulator)
	{
		this.sabmB2BUnitPopulator = sabmB2BUnitPopulator;
	}

	/**
	 * @return the customerConverter
	 */
	public Converter<UserModel, CustomerData> getCustomerConverter()
	{
		return customerConverter;
	}

	/**
	 * @param customerConverter
	 *           the customerConverter to set
	 */
	public void setCustomerConverter(final Converter<UserModel, CustomerData> customerConverter)
	{
		this.customerConverter = customerConverter;
	}

	/**
	 * @return the b2bUnitService
	 */
	public SabmB2BUnitService getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * @param b2bUnitService
	 *           the b2bUnitService to set
	 */
	public void setB2bUnitService(final SabmB2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}




}
