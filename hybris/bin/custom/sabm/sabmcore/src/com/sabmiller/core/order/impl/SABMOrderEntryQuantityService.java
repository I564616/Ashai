/**
 * 
 */
package com.sabmiller.core.order.impl;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.warehousing.orderentry.service.impl.DefaultOrderEntryQuantityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Saumya.Mittal1
 *
 */
public class SABMOrderEntryQuantityService extends DefaultOrderEntryQuantityService
{

	/** Prevent error on CUB Order Details Page due to data inconsistency for few consignmentEntries
	 *  where Consignment is empty for INTRANSIT Orders 
	 */
	
	protected static final Logger LOG = LoggerFactory.getLogger(SABMOrderEntryQuantityService.class);
			
			
	@Override
	public Long getQuantityAllocated(OrderEntryModel orderEntryModel) {
		
		 if (orderEntryModel.getConsignmentEntries() == null)
		 {
			 return Long.valueOf(0L);
		 }
		 LOG.info("In SABMOrderEntryQuantityService ####");
		 return 	Long.valueOf(orderEntryModel.getConsignmentEntries().stream().filter(consignmentEntry -> null != consignmentEntry.getConsignment() && !consignmentEntry.getConsignment().getStatus().equals(ConsignmentStatus.CANCELLED)).mapToLong(consEntry -> consEntry.getQuantity().longValue()).sum());

	}
}
