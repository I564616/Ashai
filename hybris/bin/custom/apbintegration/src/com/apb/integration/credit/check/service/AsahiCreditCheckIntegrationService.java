package com.apb.integration.credit.check.service;

import com.apb.integration.data.ApbCreditCheckData;



/**
 * The Interface AsahiCreditCheckIntegrationService.
 */
@FunctionalInterface
public interface AsahiCreditCheckIntegrationService
{

	/**
	 * Gets the credit check.
	 *
	 * @param accNum the acc num
	 * @return the credit check
	 */
	public ApbCreditCheckData getCreditCheck(String accNum);
}
