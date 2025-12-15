/*
\ * [y] hybris Platform
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
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.model.process.ApbCompanyDetailsProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;

import jakarta.annotation.Resource;

import org.apache.velocity.tools.generic.DateTool;
import org.springframework.beans.factory.annotation.Autowired;

import com.apb.core.model.ApbCompanyDetailsEmailModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.facades.constants.ApbFacadesConstants;
import com.apb.facades.user.data.ApbCompanyData;


/**
 * Velocity context for a Request Registration email.
 */
public class ApbCompanyDetailsEmailContext extends AbstractEmailContext<ApbCompanyDetailsProcessModel>
{
	private Converter<ApbCompanyDetailsEmailModel, ApbCompanyData> companyDetailsEmailConverter;
	private ApbCompanyData apbCompanyData;
	private KeyGenerator keyGenerator;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Autowired
	private CMSSiteService cmsSiteService;

	private static final String COMPANY_DETAILS_EMAIL_CREDIT_TEAM = "company.details.to.email.";
	private static final String DEFAULT_COMPANY_DETAILS_EMAIL_CREDIT_TEAM = "Enquiries@asahidirect.com.au";

	@Override
	public void init(final ApbCompanyDetailsProcessModel apbCompanyDetailsProcessModel, final EmailPageModel emailPageModel)
	{
		super.setBaseSite(getSite(apbCompanyDetailsProcessModel));
		super.init(apbCompanyDetailsProcessModel, emailPageModel);

		final String fromEmailAddress = asahiConfigurationService
				.getString(ApbFacadesConstants.COMPANY_DETAILS_FROM_EMAIL + cmsSiteService.getCurrentSite().getUid(), "");

		final String fromDisplayName = asahiConfigurationService
				.getString(ApbFacadesConstants.COMPANY_DETAILS_EMAIL_NAME + cmsSiteService.getCurrentSite().getUid(), "");

		put(FROM_EMAIL, fromEmailAddress);

		final LanguageModel language = getEmailLanguage(apbCompanyDetailsProcessModel);
		if (language != null)
		{
			put(EMAIL_LANGUAGE, language);
		}
		put(FROM_DISPLAY_NAME, fromDisplayName);

		final CustomerModel customerModel = getCustomer(apbCompanyDetailsProcessModel);
		if (customerModel != null)
		{
			put(TITLE, (customerModel.getTitle() != null && customerModel.getTitle().getName() != null)
					? customerModel.getTitle().getName() : "");
			put(DISPLAY_NAME, customerModel.getDisplayName());
			put(EMAIL, getCustomerEmailResolutionService().getEmailForCustomer(customerModel));
		}

		final ApbCompanyDetailsEmailModel apbCompanyDetailsEmailModel = getApbCompanyDetailsEmail(apbCompanyDetailsProcessModel);
		apbCompanyData = getCompanyDetailsEmailConverter().convert(getApbCompanyDetailsEmail(apbCompanyDetailsProcessModel));

		if (apbCompanyDetailsEmailModel != null)
		{
			put(DISPLAY_NAME, apbCompanyDetailsProcessModel.getApbCompanyDetailsEmail().getAccountName());
			final String sendCompanyDetailsEmail = asahiConfigurationService.getString(
					COMPANY_DETAILS_EMAIL_CREDIT_TEAM + cmsSiteService.getCurrentSite().getUid(),
					DEFAULT_COMPANY_DETAILS_EMAIL_CREDIT_TEAM);
			put(EMAIL, sendCompanyDetailsEmail);
			put("apbCompanyData", apbCompanyData);
		}
		put(DATE_TOOL, new DateTool());
	}

	protected ApbCompanyDetailsEmailModel getApbCompanyDetailsEmail(
			final ApbCompanyDetailsProcessModel apbCompanyDetailsProcessModel)
	{
		return apbCompanyDetailsProcessModel.getApbCompanyDetailsEmail();
	}

	protected String generateReqestRegistrationCode()
	{
		final Object generatedValue = keyGenerator.generate();
		if (generatedValue instanceof String)
		{
			return (String) generatedValue;
		}
		else
		{
			return String.valueOf(generatedValue);
		}
	}

	@Override
	protected BaseSiteModel getSite(final ApbCompanyDetailsProcessModel storeFrontCustomerProcessModel)
	{
		return storeFrontCustomerProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final ApbCompanyDetailsProcessModel apbCompanyDetailsProcessModel)
	{
		return apbCompanyDetailsProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final ApbCompanyDetailsProcessModel apbCompanyDetailsProcessModel)
	{
		return apbCompanyDetailsProcessModel.getLanguage();
	}

	/**
	 * @return the companyDetailsEmailConverter
	 */
	public Converter<ApbCompanyDetailsEmailModel, ApbCompanyData> getCompanyDetailsEmailConverter()
	{
		return companyDetailsEmailConverter;
	}

	/**
	 * @param companyDetailsEmailConverter
	 *           the companyDetailsEmailConverter to set
	 */
	public void setCompanyDetailsEmailConverter(
			final Converter<ApbCompanyDetailsEmailModel, ApbCompanyData> companyDetailsEmailConverter)
	{
		this.companyDetailsEmailConverter = companyDetailsEmailConverter;
	}

}
