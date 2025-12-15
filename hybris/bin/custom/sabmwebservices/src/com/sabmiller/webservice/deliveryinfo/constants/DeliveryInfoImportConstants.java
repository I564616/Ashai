/**
 *
 */
package com.sabmiller.webservice.deliveryinfo.constants;

/**
 * @author joshua.a.antony
 *
 */
public enum DeliveryInfoImportConstants
{

	DELIVERY_ACTION_CODE_PROCESSING_1("1"), DELIVERY_ACTION_CODE_PROCESSING_2("2"),

	DELIVERY_ACTION_CODE_CANCELLED("3"), DELIVERY_TYPE_YSOR("YSOR");

	private final String code;

	private DeliveryInfoImportConstants(final String code)
	{
		this.code = code;
	}

	public String getCode()
	{
		return code;
	}

}
