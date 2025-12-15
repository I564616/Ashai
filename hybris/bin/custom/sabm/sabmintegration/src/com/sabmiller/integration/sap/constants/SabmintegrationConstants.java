/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.integration.sap.constants;

/**
 * Global class for all Sabmintegration constants. You can add global constants for your extension into this class.
 */
public final class SabmintegrationConstants
{
	private SabmintegrationConstants()
	{
		//empty to avoid instantiating this constant class
	}

	public static final String SAP_SALES_ORDER_TYPE = "YSOR";
	public static final String SAP_PO_TYPE = "B2B";
	public static final String SAP_MATERIAL_SUBSTITUTION_FLAG = "X";
	public static final String SAP_FREE_GOOD_FLAG = "X";
	public static final String FREE_TEXT_ID_PAYMENT_TOKEN = "Z070";
	public static final String FREE_TEXT_ID_DELIVERY_INSTRUCTION = "Z050";
	public static final String SAP_VISA_CARD_CODE = "V";
	public static final String SAP_MASTER_CARD_CODE = "M";
	public static final String SAP_AMEX_CARD_CODE = "A";

}
