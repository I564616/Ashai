package com.apb.facades.credit.check;

import com.sabmiller.core.model.AsahiB2BUnitModel;

/**
 * The Interface ApbCreditCheckFacade.
 */
@FunctionalInterface
public interface  ApbCreditCheckFacade
{

	/**
	 * Gets the credit check.
	 *
	 * @param b2bUnit the b 2 b unit
	 * @param totalPrice the total price
	 * @return the credit check
	 */
	boolean getCreditCheck(AsahiB2BUnitModel b2bUnit,double totalPrice);
}
