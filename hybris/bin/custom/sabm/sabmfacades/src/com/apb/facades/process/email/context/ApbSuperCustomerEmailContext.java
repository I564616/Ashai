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
package com.apb.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.model.process.StoreFrontSuperCustomerProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;

import jakarta.annotation.Resource;

import org.apache.velocity.tools.generic.DateTool;
import org.springframework.beans.factory.annotation.Autowired;

import com.apb.core.constants.ApbCoreConstants;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.apb.core.service.config.AsahiConfigurationService;


/**
 * Velocity context for a customer email.
 */
public class ApbSuperCustomerEmailContext extends AbstractEmailContext<StoreFrontSuperCustomerProcessModel>
{
	//private Converter<UserModel, CustomerData> customerConverter;
	private CustomerData customerData;

	public static final String UNIT_NAME = "unitName";

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Autowired
	private CMSSiteService cmsSiteService;

	@Override
	public void init(final StoreFrontSuperCustomerProcessModel storeFrontSuperCustomerProcessModel,
			final EmailPageModel emailPageModel)
	{
		super.setBaseSite(getSite(storeFrontSuperCustomerProcessModel));
		super.init(storeFrontSuperCustomerProcessModel, emailPageModel);

		final String fromEmailAddress = asahiConfigurationService
				.getString(ApbCoreConstants.SUPER_REGISTRATION_FROM_EMAIL + cmsSiteService.getCurrentSite().getUid(), "");

		final String fromDisplayName = asahiConfigurationService
				.getString(ApbCoreConstants.SUPER_REGISTRATION_EMAIL_NAME + cmsSiteService.getCurrentSite().getUid(), "");


		put(FROM_EMAIL, fromEmailAddress);

		final LanguageModel language = getEmailLanguage(storeFrontSuperCustomerProcessModel);
		if (language != null)
		{
			put(EMAIL_LANGUAGE, language);
		}
		put(FROM_DISPLAY_NAME, fromDisplayName);

		final CustomerModel customerModel = getCustomer(storeFrontSuperCustomerProcessModel);
		AsahiB2BUnitModel unit = new AsahiB2BUnitModel();
		if (customerModel != null)
		{
			put(TITLE, (customerModel.getTitle() != null && customerModel.getTitle().getName() != null)
					? customerModel.getTitle().getName() : "");

			final CustomerModel customerModelSuper = storeFrontSuperCustomerProcessModel.getCustomer();

			if (customerModelSuper instanceof B2BCustomerModel)
			{
				final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) customerModelSuper;
				unit = (AsahiB2BUnitModel) b2bCustomerModel.getDefaultB2BUnit();
			}
			put(EMAIL, getCustomerEmailResolutionService().getEmailForCustomer(customerModel));
			put(DATE_TOOL, new DateTool());
			put(UNIT_NAME, unit.getLocName());
			put(DISPLAY_NAME, customerModel.getDisplayName());
			//	customerData = getCustomerConverter().convert(getCustomer(storeFrontSuperCustomerProcessModel));
		}
	}

	@Override
	protected BaseSiteModel getSite(final StoreFrontSuperCustomerProcessModel storeFrontCustomerProcessModel)
	{
		return storeFrontCustomerProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final StoreFrontSuperCustomerProcessModel storeFrontSuperCustomerProcessModel)
	{
		return storeFrontSuperCustomerProcessModel.getCustomer();
	}

	//	protected Converter<UserModel, CustomerData> getCustomerConverter()
	//	{
	//		return customerConverter;
	//	}
	//
	//	@Required
	//	public void setCustomerConverter(final Converter<UserModel, CustomerData> customerConverter)
	//	{
	//		this.customerConverter = customerConverter;
	//	}

	public CustomerData getCustomer()
	{
		return customerData;
	}

	@Override
	protected LanguageModel getEmailLanguage(final StoreFrontSuperCustomerProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}
}
