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
package com.apb.controllers;

import de.hybris.platform.commerceorgaddon.constants.CommerceorgaddonConstants;
import de.hybris.platform.commerceorgaddon.controllers.ControllerConstants;

import com.apb.constants.ApbcommorgaddonConstants;

/**
 */
public interface ApbcommorgaddonControllerConstants extends ControllerConstants
{
	/**
	 * Class with view name constants
	 */
	interface Views
	{
		interface Pages
		{
			interface Error
			{
				String ErrorNotFoundPage = "pages/error/errorNotFoundPage";
			}

			interface MyCompany
			{
				String ADD_ON_PREFIX = "addon:";

				String VIEW_PAGE_PREFIX = ADD_ON_PREFIX + "/" + CommerceorgaddonConstants.EXTENSIONNAME + "/";
				String VIEW_ASAHI_PAGE_PREFIX = ADD_ON_PREFIX + "/" + ApbcommorgaddonConstants.EXTENSIONNAME + "/";

				String MyCompanyManageUserDetailPage = VIEW_ASAHI_PAGE_PREFIX + "pages/company/myCompanyManageUserDetailPage";
				String MyCompanyManageUserAddEditFormPage = VIEW_ASAHI_PAGE_PREFIX
						+ "pages/company/myCompanyManageUserAddEditFormPage";
				String MyCompanyManageUsersPage = VIEW_ASAHI_PAGE_PREFIX + "pages/company/myCompanyManageUsersPage";
				String MyCompanyManageUserResetPasswordPage = VIEW_ASAHI_PAGE_PREFIX + "pages/company/myCompanyManageUserPassword";

				String MyCompanyManageUserDisbaleConfirmPage = VIEW_PAGE_PREFIX
						+ "pages/company/myCompanyManageUserDisableConfirmPage";
				String MyCompanyManageUserCustomersPage = VIEW_PAGE_PREFIX + "pages/company/myCompanyManageUserCustomersPage";
				String MyCompanyManageUserPermissionsPage = VIEW_PAGE_PREFIX + "pages/company/myCompanyManageUserPermissionsPage";
				String MyCompanyRemoveDisableConfirmationPage = VIEW_PAGE_PREFIX
						+ "pages/company/myCompanyRemoveDisableConfirmationPage";
				String MyCompanyManageUserB2BUserGroupsPage = VIEW_PAGE_PREFIX + "pages/company/myCompanyManageUserB2BUserGroupsPage";

			}
		}
	}
}
