/**
 *
 */
package com.sabmiller.facades.deal.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.enums.RepDrivenDealStatus;
import com.sabmiller.core.model.RepDrivenDealConditionStatusModel;
import com.sabmiller.facades.deal.SABMRepDrivenDealConditionStatusFacade;
import com.sabmiller.facades.deal.repdriven.data.RepDrivenDealConditionData;


/**
 * The Class SABMRepDrivenDealConditionStatusFacadeImpl.
 *
 * @author xue.zeng
 */
public class SABMRepDrivenDealConditionStatusFacadeImpl implements SABMRepDrivenDealConditionStatusFacade
{

	/** The model service. */
	@Resource(name = "modelService")
	private ModelService modelService;

	/** The user service. */
	@Resource(name = "userService")
	private UserService userService;

	/** The b2b commerce unit service. */
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	/** The b2b unit service. */
	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.deal.SABMRepDrivenDealConditionStatusFacade#saveRepDrivenDealConditionStatus(java.lang.
	 * String, java.util.List)
	 */
	@Override
	public void saveRepDrivenDealConditionStatus(final String uid, final List<RepDrivenDealConditionData> repDrivenDealConditions)
	{
		validateParameterNotNullStandardMessage("assignedTo", uid);
		//final B2BCustomerModel customerModel = b2bCommerceUnitService.getCustomerForUid(uid);
		final B2BUnitModel b2bUnit = b2bUnitService.getUnitForUid(uid);

		//final B2BUnitModel b2bUnit = customerModel.getDefaultB2BUnit();
		if (null != b2bUnit && CollectionUtils.isNotEmpty(repDrivenDealConditions))
		{
			final List<RepDrivenDealConditionStatusModel> dealConditionStatusModels = new ArrayList<RepDrivenDealConditionStatusModel>(
					repDrivenDealConditions.size());
			for (final RepDrivenDealConditionData repDrivenDealConditionData : repDrivenDealConditions)
			{
				final RepDrivenDealConditionStatusModel dealConditionStatus = new RepDrivenDealConditionStatusModel();
				dealConditionStatus.setAssignedTo(b2bUnit.getUid());
				dealConditionStatus.setDealConditionNumber(repDrivenDealConditionData.getDealConditionNumber());
				dealConditionStatus
						.setStatus(repDrivenDealConditionData.isStatus() ? RepDrivenDealStatus.UNLOCKED : RepDrivenDealStatus.LOCKED);
				dealConditionStatus.setChangedBy(userService.getCurrentUser());
				dealConditionStatus.setDate(Calendar.getInstance().getTime());
				dealConditionStatusModels.add(dealConditionStatus);
			}

			modelService.saveAll(dealConditionStatusModels);
		}

	}
}
