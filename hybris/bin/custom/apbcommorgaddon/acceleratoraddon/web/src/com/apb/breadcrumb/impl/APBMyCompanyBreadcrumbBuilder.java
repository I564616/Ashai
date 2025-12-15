package com.apb.breadcrumb.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;


import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceorgaddon.breadcrumb.impl.MyCompanyBreadcrumbBuilder;

public class APBMyCompanyBreadcrumbBuilder extends MyCompanyBreadcrumbBuilder{
	
	
	public List<Breadcrumb> createManageUserDetailsBreadcrumb(final String uid)
	{
		final List<Breadcrumb> breadcrumbs = createManageUserBreadcrumb();
		breadcrumbs.add(new Breadcrumb(String.format("/my-company/organization-management/manage-users/details?user=%s",
				urlEncode(uid)), getMessageSource().getMessage("text.company.manageUsers.details.breadcrumb", new Object[]
		{ uid }, "Manage {0} Customer", getI18nService().getCurrentLocale()), null));
		return breadcrumbs;
	}
	

}
