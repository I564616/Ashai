/**
 *
 */
package com.sabmiller.core.services.evaluator.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.SABMCMSUserGroupRestrictionModel;


/**
 * @author marc.f.l.bautista
 *
 */
public class SABMMessageUserGroupRestrictionEvaluator extends SABMCMSUserGroupRestrictionEvaluator
{
	private static final Logger LOG = LoggerFactory.getLogger(SABMMessageUserGroupRestrictionEvaluator.class);

	@Override
	public boolean evaluate(final SABMCMSUserGroupRestrictionModel sabmCmsUserGroupRestrictionModel, final RestrictionData context)
	{
		boolean evaluation = true;
		final UserModel currentUserModel = this.userService.getCurrentUser();
		B2BCustomerModel b2bCustomerModel = null;
		if (currentUserModel instanceof B2BCustomerModel)
		{
			b2bCustomerModel = (B2BCustomerModel) currentUserModel;
		}

		final B2BUnitModel parent = b2bUnitService.getParent(b2bCustomerModel);
		if (parent != null)
		{
			evaluation = evaluateB2BUnit(sabmCmsUserGroupRestrictionModel, parent);
			if (Boolean.FALSE.booleanValue() == evaluation)
			{
				return evaluation;
			}

			evaluation = evaluateFromParent(sabmCmsUserGroupRestrictionModel, parent);
		}

		return evaluation;
	}

	@Override
	protected boolean evaluateState(final SABMCMSUserGroupRestrictionModel sabmCmsUserGroupRestrictionModel,
			final B2BUnitModel parent)
	{
		final Collection<RegionModel> regions = sabmCmsUserGroupRestrictionModel.getState();
		if (CollectionUtils.isEmpty(regions))
		{
			return true;
		}

		boolean thisEvaluation = sabmCmsUserGroupRestrictionModel.getInverse().booleanValue();
		if (parent != null && parent.getDefaultShipTo() != null)
		{
			if (regions.contains(parent.getDefaultShipTo().getRegion()))
			{
				thisEvaluation = !sabmCmsUserGroupRestrictionModel.getInverse().booleanValue();
			}
		}

		return thisEvaluation;
	}

	private boolean evaluateB2BUnit(final SABMCMSUserGroupRestrictionModel sabmCmsUserGroupRestrictionModel,
			final B2BUnitModel parent)
	{
		final Collection<UserGroupModel> groups = sabmCmsUserGroupRestrictionModel.getUserGroups();
		if (groups == null || groups.isEmpty())
		{
			return true;
		}

		boolean thisEvaluation = sabmCmsUserGroupRestrictionModel.getInverse().booleanValue();
		for (final UserGroupModel group : groups)
		{
			B2BUnitModel b2bUnitGroup = null;
			if (group instanceof B2BUnitModel)
			{
				b2bUnitGroup = (B2BUnitModel) group;

				if ((b2bUnitGroup.getAccountGroup().equalsIgnoreCase("ZALB")
						|| b2bUnitGroup.getAccountGroup().equalsIgnoreCase("ZADP"))
						&& b2bUnitGroup.getAccountGroup().equalsIgnoreCase(parent.getAccountGroup()) && b2bUnitGroup.getUid().equals(parent.getUid()))
				{
					thisEvaluation = !sabmCmsUserGroupRestrictionModel.getInverse().booleanValue();
				}
			}
		}

		return thisEvaluation;
	}
}
