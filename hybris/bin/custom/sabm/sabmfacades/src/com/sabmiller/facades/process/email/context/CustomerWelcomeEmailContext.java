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
package com.sabmiller.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * Velocity context for a customer email.
 */
public class CustomerWelcomeEmailContext extends CustomerEmailContext
{

	private String token;
	private String customerType;
	private String payerNumber;
	private String accountNumber;
	private String accountType;

	public String getAccountType()
	{
		return accountType;
	}

	public String getPayerNumber()
	{
		return payerNumber;
	}

	public String getAccountNumber()
	{
		return accountNumber;
	}

	private final String ZADP_ACCOUNT_GROUP = "ZADP";
	private final String ZALB_ACCOUNT_GROUP = "ZALB";

	public String getURLEncodedToken() throws UnsupportedEncodingException
	{
		return URLEncoder.encode(token, "UTF-8");
	}

	@Override
	public void init(final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(storeFrontCustomerProcessModel, emailPageModel);

		this.token = getCustomer(storeFrontCustomerProcessModel).getToken();

		this.customerType = getCustomerType(getCustomer(storeFrontCustomerProcessModel));

		if (this.customerType.equals("primaryAdmin"))
		{
			final CustomerModel customer = getCustomer(storeFrontCustomerProcessModel);
			for (final PrincipalGroupModel group : customer.getGroups())
			{
				if (group instanceof B2BUnitModel && ((B2BUnitModel) group).getAccountGroup().equals(ZADP_ACCOUNT_GROUP))
				{
					//this.accountNumber = ((B2BUnitModel) group).getUid();
					this.payerNumber = ((B2BUnitModel) group).getPayerId();
					this.accountType = ((B2BUnitModel) group).getPaymentRequired() ? "CASH" : "CREDIT";
					break;
				}
			}
			if (((B2BCustomerModel) customer).getDefaultB2BUnit() != null
					&& ((B2BCustomerModel) customer).getDefaultB2BUnit().getAccountGroup().equals(ZALB_ACCOUNT_GROUP))
			{
				this.accountNumber = ((B2BCustomerModel) customer).getDefaultB2BUnit().getUid();
			}
		}
	}


	public String getSecurePasswordUrlInWelcomeEmail() throws UnsupportedEncodingException
	{
		return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), getUrlEncodingAttributes(), true,
				"/login/pw/change", "token=" + getURLEncodedToken() + "&newUser=true");
	}

	public String getDisplaySecureResetPasswordUrl() throws UnsupportedEncodingException
	{
		return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), getUrlEncodingAttributes(), true,
				"/my-account/update-password");
	}

	public String getCustomerType(final CustomerModel customer)
	{
		if (customer.getPrimaryAdmin())
		{
			return "primaryAdmin";
		}
		else
		{
			final List currentGroupNames = new ArrayList();
			final String accountNumber = null;
			final String payerNumber = null;
			for (final PrincipalGroupModel group : customer.getGroups())
			{
				currentGroupNames.add(group.getUid());
			}
			if (currentGroupNames.contains(B2BConstants.B2BADMINGROUP))
			{
				return "b2bAdmin";
			}
			else
			{
				return "staffUser";
			}
		}
	}

	/**
	 * @return the token
	 */
	public String getToken()
	{
		return token;
	}



	/**
	 * @param token
	 *           the token to set
	 */
	public void setToken(final String token)
	{
		this.token = token;
	}

	/**
	 * @return the customerType
	 */
	public String getCustomerType()
	{
		return customerType;
	}

	/**
	 * @param customerType
	 *           the customerType to set
	 */
	public void setCustomerType(final String customerType)
	{
		this.customerType = customerType;
	}


}
