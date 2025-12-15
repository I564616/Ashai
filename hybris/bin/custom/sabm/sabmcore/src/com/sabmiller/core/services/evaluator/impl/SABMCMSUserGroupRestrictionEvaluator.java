/**
 *
 */
package com.sabmiller.core.services.evaluator.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.SABMCMSUserGroupRestrictionModel;
import com.sabmiller.core.model.SalesOrgDataModel;


/**
 * @author bonnie
 *
 */
public class SABMCMSUserGroupRestrictionEvaluator implements CMSRestrictionEvaluator<SABMCMSUserGroupRestrictionModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(SABMCMSUserGroupRestrictionEvaluator.class);
	protected UserService userService;
	protected B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator#evaluate(de.hybris.platform.cms2.
	 * model.restrictions.AbstractRestrictionModel, de.hybris.platform.cms2.servicelayer.data.RestrictionData)
	 */
	@Override
	public boolean evaluate(final SABMCMSUserGroupRestrictionModel sabmCmsUserGroupRestrictionModel, final RestrictionData context)
	{
		boolean evaluation = true;
		final UserModel currentUserModel = this.userService.getCurrentUser();
		B2BCustomerModel b2bCustomerModel = null;
		if (currentUserModel instanceof B2BCustomerModel)
		{
			b2bCustomerModel = (B2BCustomerModel) currentUserModel;

			//Evaluate B2BUnit  , it can't be EmployeeModel, which mean employee should not have SABMCMSUserGroupRestriction evaluation
			evaluation = evaluateB2BUnit(sabmCmsUserGroupRestrictionModel, b2bCustomerModel);
		}

	
		if (Boolean.FALSE.booleanValue() == evaluation)
		{
			return evaluation;
		}

		final B2BUnitModel parent = b2bUnitService.getParent(b2bCustomerModel);
		if (parent != null)
		{
			evaluation = evaluateFromParent(sabmCmsUserGroupRestrictionModel, parent);
		}


		return evaluation;
	}

	/**
	 * Evaluate from the parent
	 *
	 * @param sabmCmsUserGroupRestrictionModel
	 * @param parent
	 * @return evaluation
	 */
	protected boolean evaluateFromParent(final SABMCMSUserGroupRestrictionModel sabmCmsUserGroupRestrictionModel,
			final B2BUnitModel parent)
	{
		boolean evaluation = true;
		//Evaluate price group
		evaluation = evaluatePriceGroup(sabmCmsUserGroupRestrictionModel, parent);
		if (Boolean.FALSE.booleanValue() == evaluation)
		{
			return evaluation;
		}
		//Evaluate state
		evaluation = evaluateState(sabmCmsUserGroupRestrictionModel, parent);
		if (Boolean.FALSE.booleanValue() == evaluation)
		{
			return evaluation;
		}
		//Evaluate PrimaryBannerGroup
		evaluation = evaluatePrimaryBannerGroup(sabmCmsUserGroupRestrictionModel, parent);
		if (Boolean.FALSE.booleanValue() == evaluation)
		{
			return evaluation;
		}
		//Evaluate SecondaryBannerGroup
		evaluation = evaluateSecondaryBannerGroup(sabmCmsUserGroupRestrictionModel, parent);
		if (Boolean.FALSE.booleanValue() == evaluation)
		{
			return evaluation;
		}
		return true;
	}

	/**
	 * Evaluate Secondary Banner Group
	 *
	 * @param sabmCmsUserGroupRestrictionModel
	 * @param parent
	 * @return evaluation
	 */
	private boolean evaluateSecondaryBannerGroup(final SABMCMSUserGroupRestrictionModel sabmCmsUserGroupRestrictionModel,
			final B2BUnitModel parent)
	{
		final List<String> secondaryGroupKeys = sabmCmsUserGroupRestrictionModel.getSecondaryBannerGroup();
		if (CollectionUtils.isEmpty(secondaryGroupKeys))
		{
			return true;
		}

		boolean thisEvaluation = sabmCmsUserGroupRestrictionModel.getInverse().booleanValue();
		if (parent != null && parent.getSapGroup() != null)
		{
			final String currentSecondaryGroupKeysGroupKey = parent.getSapGroup().getSubGroupKey();
			if (secondaryGroupKeys.contains(currentSecondaryGroupKeysGroupKey))
			{
				thisEvaluation = !sabmCmsUserGroupRestrictionModel.getInverse().booleanValue();
			}
		}
		return thisEvaluation;
	}

	/**
	 * Evaluate Primary Banner Group
	 *
	 * @param sabmCmsUserGroupRestrictionModel
	 * @param parent
	 * @return evaluation
	 */
	private boolean evaluatePrimaryBannerGroup(final SABMCMSUserGroupRestrictionModel sabmCmsUserGroupRestrictionModel,
			final B2BUnitModel parent)
	{
		final List<String> primaryGroupKeys = sabmCmsUserGroupRestrictionModel.getPrimaryBannerGroup();
		if (CollectionUtils.isEmpty(primaryGroupKeys))
		{
			return true;
		}

		boolean thisEvaluation = sabmCmsUserGroupRestrictionModel.getInverse().booleanValue();
		if (parent != null && parent.getSapGroup() != null)
		{
			final String currentPrimaryGroupKey = parent.getSapGroup().getPrimaryGroupKey();
			if (primaryGroupKeys.contains(currentPrimaryGroupKey))
			{
				thisEvaluation = !sabmCmsUserGroupRestrictionModel.getInverse().booleanValue();
			}
		}
		return thisEvaluation;
	}

	/**
	 * Evaluate State
	 *
	 * @param sabmCmsUserGroupRestrictionModel
	 * @param parent
	 * @return evaluation
	 */
	protected boolean evaluateState(final SABMCMSUserGroupRestrictionModel sabmCmsUserGroupRestrictionModel,
			final B2BUnitModel parent)
	{
		final Collection<RegionModel> regions = sabmCmsUserGroupRestrictionModel.getState();
		if (CollectionUtils.isEmpty(regions))
		{
			return true;
		}

		boolean thisEvaluation = sabmCmsUserGroupRestrictionModel.getInverse().booleanValue();
		if (parent != null && CollectionUtils.isNotEmpty(parent.getShippingAddresses()))
		{
			final List<RegionModel> currenRegions = new ArrayList<RegionModel>();
			for (final AddressModel address : parent.getShippingAddresses())
			{
				currenRegions.add(address.getRegion());
			}

			for (final RegionModel region : regions)
			{
				if (currenRegions.contains(region))
				{
					thisEvaluation = !sabmCmsUserGroupRestrictionModel.getInverse().booleanValue();
				}
			}
		}

		return thisEvaluation;
	}

	/**
	 * Evaluate Price Group
	 *
	 * @param sabmCmsUserGroupRestrictionModel
	 * @param parent
	 * @return evaluation
	 */
	private boolean evaluatePriceGroup(final SABMCMSUserGroupRestrictionModel sabmCmsUserGroupRestrictionModel,
			final B2BUnitModel parent)
	{
		final Collection<String> priceGroups = sabmCmsUserGroupRestrictionModel.getPriceGroup();
		if (priceGroups == null || priceGroups.isEmpty())
		{
			return true;
		}
		String priceGroup = "";
		final SalesOrgDataModel salesOrgData = parent.getSalesOrgData();
		if (salesOrgData != null)
		{
			priceGroup = salesOrgData.getPriceGroup();
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Current PriceGroup: " + priceGroup);
			LOG.debug("Restricted PriceGroups: " + StringUtils.join(priceGroups, "; "));
		}

		boolean thisEvaluation = sabmCmsUserGroupRestrictionModel.getInverse().booleanValue();
		for (final String group : priceGroups)
		{
			if (StringUtils.equalsIgnoreCase(priceGroup, group))
			{
				thisEvaluation = !sabmCmsUserGroupRestrictionModel.getInverse().booleanValue();
			}
		}
		return thisEvaluation;
	}

	/**
	 * Evaluate B2BUnit
	 *
	 * @param sabmCmsUserGroupRestrictionModel
	 * @param b2bCustomerModel
	 * @return evaluation
	 */
	private boolean evaluateB2BUnit(final SABMCMSUserGroupRestrictionModel sabmCmsUserGroupRestrictionModel,
			final B2BCustomerModel b2bCustomerModel)
	{
		final Collection<UserGroupModel> groups = sabmCmsUserGroupRestrictionModel.getUserGroups();
		if (groups == null || groups.isEmpty())
		{
			return true;
		}
		final Set<PrincipalGroupModel> userGroups = new HashSet<PrincipalGroupModel>(b2bCustomerModel.getGroups());
		if (sabmCmsUserGroupRestrictionModel.isIncludeSubgroups())
		{
			userGroups.addAll(getSubgroups(userGroups));
		}

		final List<String> restrGroupNames = new ArrayList<String>();
		for (final UserGroupModel group : groups)
		{
			restrGroupNames.add(group.getUid());
		}

		final List<String> currentGroupNames = new ArrayList<String>();
		for (final PrincipalGroupModel group : userGroups)
		{
			currentGroupNames.add(group.getUid());
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Current UserGroups: " + StringUtils.join(currentGroupNames, "; "));
			LOG.debug("Restricted UserGroups: " + StringUtils.join(restrGroupNames, "; "));
		}

		boolean thisEvaluation = sabmCmsUserGroupRestrictionModel.getInverse().booleanValue();
		for (final String group : restrGroupNames)
		{
			if (currentGroupNames.contains(group))
			{
				thisEvaluation = !sabmCmsUserGroupRestrictionModel.getInverse().booleanValue();
			}
		}
		return thisEvaluation;
	}

	protected List<PrincipalGroupModel> getSubgroups(final Collection<PrincipalGroupModel> groups)
	{
		final List<PrincipalGroupModel> ret = new ArrayList<PrincipalGroupModel>(groups);
		for (final PrincipalGroupModel principalGroup : groups)
		{
			ret.addAll(getSubgroups(principalGroup.getGroups()));
		}
		return ret;
	}

	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, UserModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

}
