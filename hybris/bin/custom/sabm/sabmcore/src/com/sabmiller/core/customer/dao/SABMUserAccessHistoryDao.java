/**
 *
 */
package com.sabmiller.core.customer.dao;

import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.Date;
import java.util.List;

import com.sabmiller.core.model.SABMUserAccessHistoryModel;


/**
 * @author bonnie
 *
 */
public interface SABMUserAccessHistoryDao extends Dao
{
	/**
	 *
	 * @param createdBefore
	 *           Date before which user access history should be created
	 * @param batchSize
	 *           search result size
	 * @return list with found objects
	 *
	 */
	List<SABMUserAccessHistoryModel> findOldUserAccessHistory(Date createdBefore, int batchSize);
}
