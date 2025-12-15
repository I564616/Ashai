/**
 *
 */
package com.sabm.core.config;

import de.hybris.platform.b2b.model.B2BUnitModel;

import java.util.List;

import com.sabmiller.core.model.SabmPilotConditionModel;


/**
 * The Interface SabmConfigurationService.
 */
public interface SabmConfigurationService
{

	/**
	 * Gets the partial deal threshold.
	 *
	 * @return the partial deal threshold
	 */
	public Double getPartialDealThreshold();

	/**
	 * @return
	 */
	public List<String> getDepositApplicableStates();

	/**
	 * @return
	 */
	boolean isLowStockFlagEnforced();

	public boolean isEnableProductExclusion();
	
	public boolean isEnablePricingBOGOFDeals();

	boolean isBdeOrderingEnabled();

	List<B2BUnitModel> getB2BUnitsForPilotFunction(final String functionName);


	boolean logRetrieverRequest();

	List<SabmPilotConditionModel> getPilotConditions(String functionName);

	boolean isTrackMyDeliveryEnabledForCustomers();

	boolean isAutoPayEnabled();

	boolean isInvoiceDiscrepancyEnabled();

	List<String> getValidSapProductStatus();
}
