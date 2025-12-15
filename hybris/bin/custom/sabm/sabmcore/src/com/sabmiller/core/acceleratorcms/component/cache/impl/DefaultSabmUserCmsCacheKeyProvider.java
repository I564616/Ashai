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
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import com.sabmiller.core.model.BDECustomerModel;


public class DefaultSabmUserCmsCacheKeyProvider extends DefaultCmsCacheKeyProvider
{
	@Resource(name = "userService")
	private UserService userService;



	@Override
	public StringBuilder getKeyInternal(final HttpServletRequest request, final SimpleCMSComponentModel component)
	{
		final StringBuilder buffer = new StringBuilder(super.getKeyInternal(request, component));
		final UserModel currentUser = userService.getCurrentUser();
		buffer.append(currentUser instanceof BDECustomerModel ? "BDE" : "B2B");
		buffer.append(userService.isAnonymousUser(currentUser) ? "Anonumous" : "Aunthenticated-user");
		buffer.append(request.getContextPath());

		return buffer;
	}

}
