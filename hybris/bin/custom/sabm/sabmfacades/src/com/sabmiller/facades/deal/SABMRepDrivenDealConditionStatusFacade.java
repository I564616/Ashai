/**
 *
 */
package com.sabmiller.facades.deal;

import java.util.List;

import com.sabmiller.facades.deal.repdriven.data.RepDrivenDealConditionData;


/**
 * @author xue.zeng
 *
 */
public interface SABMRepDrivenDealConditionStatusFacade
{
	/**
	 * Save Rep-Driven deal modification audit
	 *
	 * @param uid
	 *           Customer Id.
	 * @param repDrivenDealConditions
	 *           This is a Collection deal audit
	 */
	void saveRepDrivenDealConditionStatus(String uid, List<RepDrivenDealConditionData> repDrivenDealConditions);
}
