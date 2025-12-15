/**
 *
 */
package com.sabm.core.webservicelog.service;

import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import com.sabm.core.webservicelog.dao.SabmWebServiceLogDao;
import com.sabmiller.integration.model.WebServiceLogModel;


/**
 *
 */
public class DefaultSabmWebServiceLogService implements SabmWebServiceLogService
{

	@Resource(name = "sabmWebServiceLogDao")
	private SabmWebServiceLogDao sabmWebServiceLogDao;

	/**
	 * find the WebServiceLog by the create date and the batchSize
	 *
	 * @param requestedBefore
	 *           the date
	 * @param batchSize
	 *           the batch size
	 * @return list of @WebServiceLogModel
	 */
	@Override
	public List<WebServiceLogModel> findOldWebServiceLog(final Date requestedBefore, final int batchSize)
	{
		return sabmWebServiceLogDao.findOldWebServiceLog(requestedBefore, batchSize);
	}

}
