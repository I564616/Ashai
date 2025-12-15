/**
 *
 */
package com.sabmiller.facades.ordersplitting;

import de.hybris.platform.commercefacades.order.data.ConsignmentData;


/**
 * @author joshua.a.antony
 *
 */
public interface ConsignmentFacade
{

	public void processDeliveryConsignment(final ConsignmentData consignmentData);

	public void processDispatchConsignment(final ConsignmentData consignmentData);

	void updateConsignmentStatusFromRetriever(final ConsignmentData consignmentData) throws ConsignmentProcessException ;

}
