/**
 *
 */
package com.sabmiller.webservice.customer.constants;

/**
 * @author joshua.a.antony
 *
 */
public enum CustomerImportConstants
{

	SOLD_TO("AG"), SHIP_TO("WE"), PAYER("RG"), CARRIER("BO"), PRIMARY_ADMIN_DEPT_ID("0005"), TOP_LEVEL_CUSTOMER("ZADP"), BRANCH_CUSTOMER(
			"ZALB"), UNLOADING_POINT_INVALID_VALUE("000000"), CARRIER_CUSTOMER_OWNED("ZCOC"), PAYMENT_REQUIRED("0001"), BLOCK_ACCOUNT(
					"01"), BLOCK_CHECKOUT("IN"), UNLOADING_POINT_DEFAULT_VALUE("1"), ALTERNATIVE_ADDRESS_PARTNER_ACCOUNT("ZA02");

	private final String code;

	private CustomerImportConstants(final String code)
	{
		this.code = code;
	}

	public String getCode()
	{
		return this.code;
	}

}
