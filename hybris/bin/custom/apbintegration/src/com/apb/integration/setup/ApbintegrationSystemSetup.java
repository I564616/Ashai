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
package com.apb.integration.setup;

import static com.apb.integration.constants.ApbintegrationConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.apb.integration.constants.ApbintegrationConstants;
import com.apb.integration.service.ApbintegrationService;


@SystemSetup(extension = ApbintegrationConstants.EXTENSIONNAME)
public class ApbintegrationSystemSetup
{
	private final ApbintegrationService apbintegrationService;

	public ApbintegrationSystemSetup(final ApbintegrationService apbintegrationService)
	{
		this.apbintegrationService = apbintegrationService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		apbintegrationService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return ApbintegrationSystemSetup.class.getResourceAsStream("/apbintegration/sap-hybris-platform.png");
	}
}
