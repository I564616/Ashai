/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.core.acceleratorcms.component.cache.impl;

import de.hybris.platform.acceleratorcms.component.cache.impl.DefaultCmsCacheKeyProvider;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Date;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import com.sabmiller.core.constants.SabmCoreConstants;


public class DefaultSabmB2BUnitCmsCacheKeyProvider extends DefaultCmsCacheKeyProvider
{

	/** The session service. */
	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "userService")
	private UserService userService;



	@Override
	public StringBuilder getKeyInternal(final HttpServletRequest request, final SimpleCMSComponentModel component)
	{
		final StringBuilder buffer = new StringBuilder(super.getKeyInternal(request, component));

		B2BUnitModel b2bunit = null;
		if (sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT) != null)
		{
			b2bunit = (B2BUnitModel) sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT);
		}
		if (b2bunit != null)
		{
			buffer.append(b2bunit.getUid());
		Date currentDeliveryDate = null;
		if (sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE) != null)
		{
			currentDeliveryDate = (Date) sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);
		}
		buffer.append(currentDeliveryDate == null ? null : currentDeliveryDate.getTime());
			if (b2bunit.getRefreshEntitiesLastUpdatedTime() != null)
		{
				buffer.append(b2bunit.getRefreshEntitiesLastUpdatedTime().getTime());
		}
		}
		return buffer;
	}


}
