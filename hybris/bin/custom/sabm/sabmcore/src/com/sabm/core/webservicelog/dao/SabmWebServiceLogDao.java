/**
 *
 */
package com.sabm.core.webservicelog.dao;

import java.util.Date;
import java.util.List;

import com.sabmiller.integration.model.WebServiceLogModel;


/**
 * @author zhangxiaowu
 * @date 09/12/2016
 */
public interface SabmWebServiceLogDao
{
	/**
	 * find the WebServiceLog by the create date and the batchSize
	 *
	 * @param requestedBefore
	 *           the date
	 * @param batchSize
	 *           the batch size
	 * @return list of @WebServiceLogModel
	 */
	public List<WebServiceLogModel> findOldWebServiceLog(final Date requestedBefore, final int batchSize);
}
