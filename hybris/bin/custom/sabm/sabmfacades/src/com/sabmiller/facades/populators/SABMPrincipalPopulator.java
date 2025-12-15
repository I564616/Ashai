/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.user.converters.populator.PrincipalPopulator;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.commercefacades.user.data.PrincipalGroupData;
import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.util.AsahiSiteUtil;
import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.B2BUnitGroupModel;
import com.sabmiller.core.util.SabmUtils;


/**
 * Add a new attribute the convert
 *
 * @author xue.zeng
 *
 */
public class SABMPrincipalPopulator extends PrincipalPopulator
{
	private static final Logger LOG = LoggerFactory.getLogger(SABMPrincipalPopulator.class);

	private static final String SGA_SITEID = "sga";

	/** The b2b unit service. */
	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	@Resource
	private SessionService sessionService;

	@Resource(name = "sabmConfigurationService")
	private SabmConfigurationService sabmConfigurationService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commercefacades.user.converters.populator.PrincipalPopulator#populate(de.hybris.platform.core.
	 * model.security.PrincipalModel, de.hybris.platform.commercefacades.user.data.PrincipalData)
	 */
	@Override
	public void populate(final PrincipalModel source, final PrincipalData target)
	{
		super.populate(source, target);

		if (!(source instanceof B2BCustomerModel))
		{
			return;
		}

		if (asahiSiteUtil.isCub())
		{
			populatePrincipalGroup(source.getGroups(), target);
			populateBranches((B2BCustomerModel) source, target);
			populateB2BUnit((B2BCustomerModel) source, target);
		}
		else
		{
			populateB2BUnitforALB((B2BCustomerModel) source, target);
		}
	}

	/**
	 * For users belonging to multiple B2BUnit, they have an ability to switch B2BUnits from the header. This method sets
	 * the currently selected B2BUnit {@link PrincipalGroupData} in the {@link PrincipalData} object
	 */
	protected void populateB2BUnit(final PrincipalModel source, final PrincipalData target)
	{
		if (!(source instanceof B2BCustomerModel))
		{
			return;
		}

		final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) source);

		if (selectedB2BUnit != null)

		{
			final String selectedB2BUnitId = selectedB2BUnit.getUid();


			for (final PrincipalGroupData pg : ListUtils.emptyIfNull(target.getBranches()))
			{
				if (selectedB2BUnitId.equals(pg.getUid()))

				{
					final B2BUnitGroupModel b2bGroupModel = selectedB2BUnit.getSapGroup();

					if (b2bGroupModel != null)
					{
						pg.setPrimaryGroupDescription(b2bGroupModel.getPrimaryGroupDescription());
						pg.setSubChannelDescription(b2bGroupModel.getSubChannelDescription());
					}
					else
					{
						pg.setPrimaryGroupDescription("");
						pg.setSubChannelDescription("");
					}
					pg.setPostCode(getPostCode(selectedB2BUnit));
					pg.setIsDepositApplicable(getDepositApplicableFlag(selectedB2BUnit));

					target.setCurrentB2BUnit(pg);
					sessionService.setAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT, selectedB2BUnit);
					return;
				}
			}

			if (SabmCoreConstants.ZADP.equals(selectedB2BUnit.getAccountGroup()))
			{
				final B2BCustomerModel user = (B2BCustomerModel) source;
				final B2BUnitModel defaultB2BUnit = user.getDefaultB2BUnit();
				if (defaultB2BUnit != null && !(defaultB2BUnit instanceof AsahiB2BUnitModel))
				{
					final PrincipalGroupData groupData = new PrincipalGroupData();
					groupData.setUid(defaultB2BUnit.getUid());
					groupData.setName(defaultB2BUnit.getDisplayName());
					groupData.setPostCode(getPostCode(defaultB2BUnit));
					groupData.setIsDepositApplicable(getDepositApplicableFlag(selectedB2BUnit));

					final B2BUnitGroupModel b2bGroupModel = defaultB2BUnit.getSapGroup();

					if (b2bGroupModel != null)
					{
						groupData.setPrimaryGroupDescription(b2bGroupModel.getPrimaryGroupDescription());
						groupData.setSubChannelDescription(b2bGroupModel.getSubChannelDescription());
					}
					else
					{
						groupData.setPrimaryGroupDescription("");
						groupData.setSubChannelDescription("");
					}

					target.setCurrentB2BUnit(groupData);
					sessionService.setAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT, defaultB2BUnit);
					return;
				}
			}

		}

		LOG.error("Unable to set the current B2BUnit for user [{}] ", target.getName());
	}


	/**
	 * @param defaultB2BUnit
	 */
	private String getPostCode(final B2BUnitModel defaultB2BUnit)
	{
		if (CollectionUtils.size(defaultB2BUnit.getAddresses()) > 0)
		{
			final AddressModel address = defaultB2BUnit.getAddresses().iterator().next();
			return address.getPostalcode();
		}
		return "";
	}

	/**
	 * @param defaultB2BUnit
	 */
	private boolean getDepositApplicableFlag(final B2BUnitModel defaultB2BUnit)
	{
		if (defaultB2BUnit.getDefaultShipTo() != null && defaultB2BUnit.getDefaultShipTo().getRegion() != null)
		{
			final String unitState = defaultB2BUnit.getDefaultShipTo().getRegion().getIsocodeShort();
			final List<String> statesList = sabmConfigurationService.getDepositApplicableStates();
			if (statesList != null)
			{
				for (final String state : statesList)
				{
					if (StringUtils.equalsIgnoreCase(state, unitState))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Convert Principal Group Data
	 */
	protected void populatePrincipalGroup(final Set<PrincipalGroupModel> principalGroups, final PrincipalData target)
	{
		target.setGroups(toPrincipalGroups(principalGroups, false));
	}

	/**
	 * Populate only the branches for B2BUnit. The branches differ to the groups in the sense that only B2BUnit are
	 * considered as Branches.Any other Groups that are not B2BUnit are skipped.
	 *
	 * If the customer belongs to ZADP Company (Top Level organization) - then we first fetch the top level organization
	 * and then set the members as branches
	 */
	protected void populateBranches(final B2BCustomerModel customer, final PrincipalData target)
	{
		final B2BUnitModel zadpB2BUnit = b2bUnitService.findTopLevelB2BUnit(customer);
		final boolean customerBelongsToZADP = (zadpB2BUnit != null);
		if (customerBelongsToZADP)
		{
			final Set<PrincipalGroupModel> principalGroups = new HashSet<PrincipalGroupModel>();
			for (final PrincipalModel model : SetUtils.emptyIfNull(zadpB2BUnit.getMembers()))
			{
				if (model instanceof B2BUnitModel && !SabmUtils.isUserDisabledForCUBAccount((B2BUnitModel) model, customer))
				{
					principalGroups.add((B2BUnitModel) model);
				}
			}
			target.setBranches(toPrincipalGroups(principalGroups, true));
		}
		else
		{
			final Set<PrincipalGroupModel> principalGroups = new HashSet<PrincipalGroupModel>();
			for(final PrincipalGroupModel principalGroupModel : customer.getGroups())
			{
				if(principalGroupModel instanceof AsahiB2BUnitModel)
				{
					continue;
				}
				else if (principalGroupModel instanceof B2BUnitModel
						&& !SabmUtils.isUserDisabledForCUBAccount((B2BUnitModel) principalGroupModel, customer))
				{
					principalGroups.add(principalGroupModel);
				}
			}

			target.setBranches(toPrincipalGroups(principalGroups, true));
		}

	}

	private List<PrincipalGroupData> toPrincipalGroups(final Set<PrincipalGroupModel> principalGroups,
			final boolean addOnlyB2BUnit)
	{
		if (CollectionUtils.isNotEmpty(principalGroups))
		{
			LOG.debug("User principal groups [{}]", principalGroups);
			// convert principal group the user
			final List<PrincipalGroupData> groupDatas = new ArrayList<PrincipalGroupData>(principalGroups.size());
			for (final PrincipalGroupModel principalGroupModel : principalGroups)
			{
				if(principalGroupModel instanceof AsahiB2BUnitModel)
				{
					continue;
				}
				if (addOnlyB2BUnit && !(principalGroupModel instanceof B2BUnitModel))
				{
					continue;
				}
				final PrincipalGroupData groupData = new PrincipalGroupData();
				groupData.setUid(principalGroupModel.getUid());
				groupData.setName(principalGroupModel.getDisplayName());
				groupDatas.add(groupData);
			}

			//Sort the branches alphabetically
			Collections.sort(groupDatas, new AbstractComparator<PrincipalGroupData>()
			{
				@Override
				protected int compareInstances(final PrincipalGroupData instance1, final PrincipalGroupData instance2)
				{
					if (instance1 != null && instance1.getName() != null && instance2!=null && instance2.getName()!=null)
					{
						return instance1.getName().compareTo(instance2.getName());
					}
					else
					{
						return 0;
					}
				}
			});

			return groupDatas;
		}
		LOG.warn("User principal groups is empty.");
		return Collections.emptyList();
	}


	protected void populateB2BUnitforALB(final PrincipalModel source, final PrincipalData target)
	{
		if (!(source instanceof B2BCustomerModel))
		{
			return;
		}

		final B2BCustomerModel user = (B2BCustomerModel) source;

		// Step 1: Check selectedB2BUnit (parent)
		final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent(user);

		if (selectedB2BUnit != null)
		{
			final PrincipalGroupData groupData = new PrincipalGroupData();
			groupData.setUid(selectedB2BUnit.getUid());
			groupData.setName(selectedB2BUnit.getDisplayName());
			groupData.setPostCode(getPostCode(selectedB2BUnit));
			groupData.setIsDepositApplicable(getDepositApplicableFlag(selectedB2BUnit));
			groupData.setPrimaryGroupDescription("");
			groupData.setSubChannelDescription("");
			target.setCurrentB2BUnit(groupData);
			return;
		}

		// Step 2: Fallback to defaultB2BUnit (only if it's Asahi and companyUid is 'sga')
		final B2BUnitModel defaultB2BUnit = user.getDefaultB2BUnit();

		if (defaultB2BUnit instanceof AsahiB2BUnitModel
				&& SGA_SITEID.equalsIgnoreCase(defaultB2BUnit.getCompanyUid()))
		{
			final PrincipalGroupData groupData = new PrincipalGroupData();
			groupData.setUid(defaultB2BUnit.getUid());
			groupData.setName(defaultB2BUnit.getDisplayName());
			groupData.setPostCode(getPostCode(defaultB2BUnit));
			groupData.setIsDepositApplicable(getDepositApplicableFlag(defaultB2BUnit));

			final B2BUnitGroupModel b2bGroupModel = defaultB2BUnit.getSapGroup();

			if (b2bGroupModel != null)
			{
				groupData.setPrimaryGroupDescription(b2bGroupModel.getPrimaryGroupDescription());
				groupData.setSubChannelDescription(b2bGroupModel.getSubChannelDescription());
			}
			else
			{
				groupData.setPrimaryGroupDescription("");
				groupData.setSubChannelDescription("");
			}

			target.setCurrentB2BUnit(groupData);
		}
		else
		{
			target.setCurrentB2BUnit(null);
			LOG.error("Unable to set the current B2BUnit for user [{}] ", target.getName());
		}
	}

}
