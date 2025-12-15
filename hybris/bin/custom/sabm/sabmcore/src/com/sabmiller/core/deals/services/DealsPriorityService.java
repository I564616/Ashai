/**
 *
 */
package com.sabmiller.core.deals.services;

import java.util.List;
import java.util.Map;

import com.sabmiller.core.deals.vo.DealsResponse;
import com.sabmiller.core.deals.vo.DealsResponse.DealItem;


/**
 * @author joshua.a.antony
 *
 */
public interface DealsPriorityService
{

	public void mergeOverlappingDeals(DealsResponse discountResponse);

	public Map<String, List<DealItem>> fetchOverlappingRecords(final List<DealItem> items);

}
