/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.apb.integration.constants;


/**
 * Global class for all Apbintegration constants. You can add global constants for your extension into this class.
 */
public final class ApbintegrationConstants extends GeneratedApbintegrationConstants
{
	public static final String EXTENSIONNAME = "apbintegration";

	private ApbintegrationConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension

    public static final String PLATFORM_LOGO_CODE = "apbintegrationPlatformLogo";
    
    public static final String URL = "URL";
	
	public static final String CONNECTION_TIMEOUT = "CONNECTION_TIMEOUT";
	
	public static final String CONNECTION_READ_TIMEOUT = "CONNECTION_READ_TIMEOUT";
	
	public static final String CONNECTION_REQUEST_TIMEOUT = "CONNECTION_REQUEST_TIMEOUT";
	
	public static final String CLIENT_STORE_PASSWORD = "CLIENT_STORE_PASSWORD";
	
	public static final String TRUST_STORE_PASSWORD = "TRUST_STORE_PASSWORD";
	
	public static final String CLIENT_STORE_FILE = "CLIENT_STORE_FILE";
	
	public static final String TRUST_STORE_FILE = "TRUST_STORE_FILE";
	
	public static final String CERTIFICATE_FILEPATH = "CERTIFICATE_FILEPATH";

	public static final String REQUEST_CONTENT_TYPE = "REQUEST_CONTENT_TYPE";
}
