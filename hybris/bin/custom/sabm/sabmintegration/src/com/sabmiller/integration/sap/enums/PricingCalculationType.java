/*
 *
 */
package com.sabmiller.integration.sap.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * Enum for errors associated with a Pricing Calculation Types.
 */
public enum PricingCalculationType
{
	PERCENTAGE("A", "percentage"), FIXED_QUANTITY("B", "fixed"), QUANTITY("C", "perunit"), UNKNOWN_CAL_TYPE("unknown", "unknown");

	private String code;
	private String type;
	private static final Map<String, PricingCalculationType> LOOKUPMAP = new HashMap<String, PricingCalculationType>();

	static
	{
		for (final PricingCalculationType code : PricingCalculationType.values())
		{
			LOOKUPMAP.put(code.getCode(), code);
		}
	}

	private PricingCalculationType(final String code, final String type)
	{
		this.code = code;
		this.type = type;
	}

	public String getCode()
	{
		return code;
	}

	public String getType()
	{
		return type;
	}

	public static PricingCalculationType lookup(final String typeKey)
	{
		PricingCalculationType code = LOOKUPMAP.get(typeKey);
		if (code == null)
		{
			code = UNKNOWN_CAL_TYPE;
		}
		return code;
	}
}
