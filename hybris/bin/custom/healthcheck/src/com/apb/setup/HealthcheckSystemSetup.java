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
package com.apb.setup;

import static com.apb.constants.HealthcheckConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.apb.constants.HealthcheckConstants;
import com.apb.service.HealthcheckService;


@SystemSetup(extension = HealthcheckConstants.EXTENSIONNAME)
public class HealthcheckSystemSetup
{
	private final HealthcheckService healthcheckService;

	public HealthcheckSystemSetup(final HealthcheckService healthcheckService)
	{
		this.healthcheckService = healthcheckService;
	}

	@SystemSetup(process = SystemSetup.Process.ALL, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		healthcheckService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return HealthcheckSystemSetup.class.getResourceAsStream("/healthcheck/sap-hybris-platform.png");
	}
}
