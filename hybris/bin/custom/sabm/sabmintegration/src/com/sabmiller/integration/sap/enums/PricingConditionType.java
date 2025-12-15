/*
 */
package com.sabmiller.integration.sap.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * Enum for errors associated with a Pricing Condition Types.
 */
public enum PricingConditionType
{
	PRICE(new String[]
	{ "PR00", "YP02", "YP25" }),

	DISCOUNT(new String[]
	{ "YDB1", "YDW1", "YDA0", "YDB0", "YDC0", "YDD0", "YDE0", "YDF0", "YDG0", "YDJ0", "YDK0", "YDL0", "YDM0", "YDN0", "YDO0", "YDY0", "Z3S5", "YDI0", "YDV0", "ZDI0", "YDW0" }),

	GST(new String[]
	{ "MWST" }),

	LOYALTY_FEE(new String[]
	{ "Z3S1" }),

	AUTO_PAY_ADVANTAGE_DISCOUNT(new String[]
	{ "ZEPD" }),

	AUTO_PAY_ADVANTAGE_PLUS_DISCOUNT(new String[]
	{ "ZMSF" }),

	WET(new String[]
	{ "Z9W0" }),

	DELIVERY_COST(new String[]
	{ "Z3F1", "Y301", "Z3F3", "Z3S0" }),

	CONTAINER_DEPOSIT(new String[]
	{ "Z3S2", "Z3S4" }),

	COMPLEX_DEAL_TYPE(new String[]
	{ "Y401", "Y402", "Y403", "Y404" }),

	LIMITED_DEAL_TYPE(new String[]
	{ "YDX0" }),

	UNKNOWN_CONDTION_TYPE(new String[]
	{ "UNKNOWN" }),

	FREIGHT_LIMIT(new String[]
	{ "Z3F0" });


	private String[] sapCodeList;
	private static final Map<String, PricingConditionType> LOOKUPMAP = new HashMap<String, PricingConditionType>();

	static
	{
		for (final PricingConditionType conditionType : PricingConditionType.values())
		{
			for (final String sapCode : conditionType.getSapCodeList())
			{
				LOOKUPMAP.put(sapCode, conditionType);
			}
		}
	}

	private PricingConditionType(final String[] codeList)
	{
		this.sapCodeList = codeList;
	}



	public String[] getSapCodeList()
	{
		return sapCodeList;
	}

	public static PricingConditionType lookup(final String typeKey)
	{
		PricingConditionType conditionType = LOOKUPMAP.get(typeKey);
		if (conditionType == null)
		{
			conditionType = UNKNOWN_CONDTION_TYPE;
		}
		return conditionType;
	}
}
