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
package com.sabmiller.merchantsuiteservices.constants;

/**
 * Global class for all Merchantsuiteservices constants. You can add global constants for your extension into this class.
 */
public final class MerchantsuiteservicesConstants extends GeneratedMerchantsuiteservicesConstants
{
	public static final String EXTENSIONNAME = "merchantsuiteservices";

	private MerchantsuiteservicesConstants()
	{
		//empty to avoid instantiating this constant class
	}
	// implement here constants used by this extension
	public static enum PAYMENT_MODE{
		INVOICE("INV"),
		CHECKOUT("CHCK");

		private String code;

		PAYMENT_MODE(final String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}

	// implement here constants used by this extension
	public static enum RESPONSE_STATUS{
		SUCCESS("INV"),
		FAILURE("CHCK");

		private String code;

		RESPONSE_STATUS(final String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}

	public static class PAYMENT_METHOD {
		public static final String CREDITCARD = "creditCard";
		public static final String BANKTRANSFER = "EFT";
	}

	// implement here constants used by this extension
}
