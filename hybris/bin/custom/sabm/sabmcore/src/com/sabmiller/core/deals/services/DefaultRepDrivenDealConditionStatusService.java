/**
 *
 */
package com.sabmiller.core.deals.services;

import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;

import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import com.sabmiller.core.deals.dao.RepDrivenDealConditionStatusDao;
import com.sabmiller.core.model.RepDrivenDealConditionStatusModel;


/**
 * @author bonnie
 *
 */
public class DefaultRepDrivenDealConditionStatusService extends AbstractBusinessService
		implements RepDrivenDealConditionStatusService
{
	@Resource(name = "repDrivenDealConditionStatusDao")
	private RepDrivenDealConditionStatusDao repDrivenDealConditionStatusDao;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.deals.services.RepDrivenDealConditionStatusService#findRepDrivenDealConditionStatus(java.util.
	 * Date, int)
	 */
	@Override
	public List<RepDrivenDealConditionStatusModel> findRepDrivenDealConditionStatus(final Date createdBefore, final int batchSize)
	{
		return repDrivenDealConditionStatusDao.findOldRepDrivenDealConditionStatus(createdBefore, batchSize);
	}

	public void setRepDrivenDealConditionStatusDao(final RepDrivenDealConditionStatusDao repDrivenDealConditionStatusDao)
	{
		this.repDrivenDealConditionStatusDao = repDrivenDealConditionStatusDao;
	}



}
