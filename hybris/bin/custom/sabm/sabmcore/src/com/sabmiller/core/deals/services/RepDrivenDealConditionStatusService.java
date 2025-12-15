/**
 *
 */
package com.sabmiller.core.deals.services;

import java.util.Date;
import java.util.List;

import com.sabmiller.core.model.RepDrivenDealConditionStatusModel;


/**
 * The Interface RepDrivenDealConditionStatusService.
 */
public interface RepDrivenDealConditionStatusService
{

	/**
	 * Find rep driven deal condition status.
	 *
	 * @param createdBefore
	 *           the created before
	 * @param batchSize
	 *           the batch size
	 * @return list of @RepDrivenDealConditionStatusModel
	 */
	List<RepDrivenDealConditionStatusModel> findRepDrivenDealConditionStatus(final Date createdBefore, final int batchSize);
}
