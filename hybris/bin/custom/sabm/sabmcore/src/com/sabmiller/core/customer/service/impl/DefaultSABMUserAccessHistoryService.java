/**
 *
 */
package com.sabmiller.core.customer.service.impl;

import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;

import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import com.sabmiller.core.customer.dao.SABMUserAccessHistoryDao;
import com.sabmiller.core.customer.service.SABMUserAccessHistoryService;
import com.sabmiller.core.model.SABMUserAccessHistoryModel;


/**
 * @author bonnie
 *
 */
public class DefaultSABMUserAccessHistoryService extends AbstractBusinessService implements SABMUserAccessHistoryService
{
	@Resource(name = "userAccessHistoryDao")
	private SABMUserAccessHistoryDao userAccessHistoryDao;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.customer.service.SABMUserAccessHistoryService#findOldUserAccessHistory(java.util.Date,
	 * int)
	 */
	@Override
	public List<SABMUserAccessHistoryModel> findOldUserAccessHistory(final Date createdBefore, final int batchSize)
	{
		return userAccessHistoryDao.findOldUserAccessHistory(createdBefore, batchSize);
	}

	public void setUserAccessHistoryDao(final SABMUserAccessHistoryDao userAccessHistoryDao)
	{
		this.userAccessHistoryDao = userAccessHistoryDao;
	}


}
