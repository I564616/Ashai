/**
 *
 */
package com.sabmiller.core.customer.service;

import java.util.Date;
import java.util.List;

import com.sabmiller.core.model.SABMUserAccessHistoryModel;


/**
 * @author bonnie
 *
 */
public interface SABMUserAccessHistoryService
{
	/**
	 *
	 * @param createdBefore
	 * @param batchSize
	 * @return list of @SABMUserAccessHistoryModel
	 */
	List<SABMUserAccessHistoryModel> findOldUserAccessHistory(final Date createdBefore, final int batchSize);
}
