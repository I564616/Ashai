package com.apb.core.cart.validation.strategy;

public interface AsahiBonusCartValidationStrategy {
	
	/* (non-Javadoc)
	 * This method gets the bonus products that can be added to the cart.
	 */
	long getAllowedBonusQuantity(String productCode);
}
