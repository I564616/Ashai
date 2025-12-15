/**
 *
 */
package com.sabmiller.core.deals.dao;

import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.Date;
import java.util.List;

import com.sabmiller.core.model.RepDrivenDealConditionStatusModel;


/**
 * @author bonnie
 *
 */
public interface RepDrivenDealConditionStatusDao extends Dao
{
	/**
	 *
	 * @param createdBefore
	 *           Date before which user access history should be created
	 * @param batchSize
	 *           search result size
	 * @return list with found objects
	 */
	List<RepDrivenDealConditionStatusModel> findOldRepDrivenDealConditionStatus(Date createdBefore, int batchSize);


	/**
	 * Get RepDrivenDealConditionStatus object
	 *
	 * @param dealCode
	 *           Deal Code
	 * @param b2bUnitId
	 *           B2BUnit id
	 * @return RepDrivenDealConditionStatusModel
	 */

	public RepDrivenDealConditionStatusModel getRepDrivenDealCondition(final String dealCode, final String b2bUnitId);
}
