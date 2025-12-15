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
import de.hybris.platform.commerceservices.model.process.ApbRequestRegistrationProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;

import jakarta.annotation.Resource;

import org.apache.velocity.tools.generic.DateTool;
import org.springframework.beans.factory.annotation.Autowired;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.model.ApbRequestRegisterEmailModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.facades.constants.ApbFacadesConstants;
import com.apb.facades.register.data.ApbRequestRegisterData;


/**
 * Velocity context for a Request Registration email.
 */
public class RequestRegistrationEmailContext extends AbstractEmailContext<ApbRequestRegistrationProcessModel>
{
	private Converter<ApbRequestRegisterEmailModel, ApbRequestRegisterData> requestRegistrationConverter;
	private ApbRequestRegisterData requestRegisterData;
	private KeyGenerator keyGenerator;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Autowired
	private CMSSiteService cmsSiteService;


	@Override
	public void init(final ApbRequestRegistrationProcessModel requestRegistrationProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(requestRegistrationProcessModel, emailPageModel);

		final String fromDisplayName = asahiConfigurationService
				.getString(ApbCoreConstants.REQUEST_REGISTER_EMAIL_NAME + cmsSiteService.getCurrentSite().getUid(), "");
		final String fromEmailAddress = asahiConfigurationService
				.getString(ApbCoreConstants.REQUEST_REGISTER_FROM_EMAIL + cmsSiteService.getCurrentSite().getUid(), "");


		put(FROM_EMAIL, fromEmailAddress);

		final LanguageModel language = getEmailLanguage(requestRegistrationProcessModel);
		if (language != null)
		{
			put(EMAIL_LANGUAGE, language);
		}
		put(FROM_DISPLAY_NAME, fromDisplayName);

		final ApbRequestRegisterEmailModel requestRegisterEmailModel = getApbRequestRegisterEmail(requestRegistrationProcessModel);
		requestRegisterData = getRequestRegistrationConverter()
				.convert(getApbRequestRegisterEmail(requestRegistrationProcessModel));
		if (requestRegisterEmailModel != null)
		{
			put(ApbFacadesConstants.EMAIL_REFERENCE_NO,
					(requestRegisterEmailModel.getReferenceNumber() != null ? requestRegisterEmailModel.getReferenceNumber() : ""));
			put(DISPLAY_NAME, requestRegisterEmailModel.getName());
			put(EMAIL, requestRegisterEmailModel.getEmailAddress());
			put("requestRegisterData", requestRegisterData);
		}
		put(DATE_TOOL, new DateTool());
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
	protected BaseSiteModel getSite(final ApbRequestRegistrationProcessModel requestRegistrationProcessModel)
	{
		return requestRegistrationProcessModel.getSite();
	}

	protected ApbRequestRegisterEmailModel getApbRequestRegisterEmail(
			final ApbRequestRegistrationProcessModel requestRegistrationProcessModel)
	{
		return requestRegistrationProcessModel.getRequestRegisterEmail();
	}


	@Override
	protected LanguageModel getEmailLanguage(final ApbRequestRegistrationProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}

	/**
	 * @return the requestRegistrationConverter
	 */
	public Converter<ApbRequestRegisterEmailModel, ApbRequestRegisterData> getRequestRegistrationConverter()
	{
		return requestRegistrationConverter;
	}

	/**
	 * @param requestRegistrationConverter
	 *           the requestRegistrationConverter to set
	 */
	public void setRequestRegistrationConverter(
			final Converter<ApbRequestRegisterEmailModel, ApbRequestRegisterData> requestRegistrationConverter)
	{
		this.requestRegistrationConverter = requestRegistrationConverter;
	}

	/**
	 * @return the requestRegisterData
	 */
	public ApbRequestRegisterData getRequestRegisterData()
	{
		return requestRegisterData;
	}

	/**
	 * @param requestRegisterData
	 *           the requestRegisterData to set
	 */
	public void setRequestRegisterData(final ApbRequestRegisterData requestRegisterData)
	{
		this.requestRegisterData = requestRegisterData;
	}

	@Override
	protected CustomerModel getCustomer(final ApbRequestRegistrationProcessModel businessProcessModel)
	{
		return null;
	}

	/**
	 * @param keyGenerator
	 */
	public void setKeyGenerator(final KeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}

}
