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
import de.hybris.platform.commerceservices.model.process.ApbKegReturnEmailProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;

import jakarta.annotation.Resource;

import org.apache.velocity.tools.generic.DateTool;
import org.springframework.beans.factory.annotation.Autowired;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.model.ApbKegReturnEmailModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.facades.constants.ApbFacadesConstants;
import com.apb.facades.kegreturn.data.ApbKegReturnData;


/**
 * Velocity context for a Keg Return email.
 */
public class ApbKegReturnEmailContext extends AbstractEmailContext<ApbKegReturnEmailProcessModel>
{
	private Converter<ApbKegReturnEmailModel, ApbKegReturnData> kegReturnEmailConverter;
	private ApbKegReturnData apbKegReturnData;
	private KeyGenerator keyGenerator;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Autowired
	private CMSSiteService cmsSiteService;


	@Override
	public void init(final ApbKegReturnEmailProcessModel ApbKegReturnEmailProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(ApbKegReturnEmailProcessModel, emailPageModel);

		final String fromDisplayName = asahiConfigurationService
				.getString(ApbCoreConstants.KEG_RETURN_EMAIL_NAME + cmsSiteService.getCurrentSite().getUid(), "");
		final String fromEmailAddress = asahiConfigurationService
				.getString(ApbCoreConstants.KEG_RETURN_FROM_EMAIL + cmsSiteService.getCurrentSite().getUid(), "");
		put(FROM_EMAIL, fromEmailAddress);
		final LanguageModel language = getEmailLanguage(ApbKegReturnEmailProcessModel);
		if (language != null)
		{
			put(EMAIL_LANGUAGE, language);
		}
		put(FROM_DISPLAY_NAME, fromDisplayName);

		final ApbKegReturnEmailModel kegReturnEmailModel = getKegReturnEmail(ApbKegReturnEmailProcessModel);
		apbKegReturnData = getKegReturnEmailConverter().convert(getKegReturnEmail(ApbKegReturnEmailProcessModel));
		if (kegReturnEmailModel != null)
		{
			put(ApbFacadesConstants.EMAIL_REFERENCE_NO,
					(kegReturnEmailModel.getReferenceNumber() != null ? kegReturnEmailModel.getReferenceNumber() : ""));
			//put(DISPLAY_NAME, kegReturnEmailModel.getName());
			put(EMAIL, kegReturnEmailModel.getEmailAddress());
			put("apbKegReturnData", apbKegReturnData);

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
	protected BaseSiteModel getSite(final ApbKegReturnEmailProcessModel requestRegistrationProcessModel)
	{
		return requestRegistrationProcessModel.getSite();
	}




	@Override
	protected LanguageModel getEmailLanguage(final ApbKegReturnEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}


	/**
	 * @return the apbContactUsData
	 */
	public ApbKegReturnData getApbContactUsData()
	{
		return apbKegReturnData;
	}

	/**
	 * @param apbContactUsData
	 *           the apbContactUsData to set
	 */
	public void setApbContactUsData(final ApbKegReturnData apbContactUsData)
	{
		this.apbKegReturnData = apbContactUsData;
	}

	/**
	 * @return the keyGenerator
	 */
	public KeyGenerator getKeyGenerator()
	{
		return keyGenerator;
	}

	/**
	 * @param keyGenerator
	 *           the keyGenerator to set
	 */
	public void setKeyGenerator(final KeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}

	/**
	 * @return the asahiConfigurationService
	 */
	public AsahiConfigurationService getAsahiConfigurationService()
	{
		return asahiConfigurationService;
	}

	/**
	 * @param asahiConfigurationService
	 *           the asahiConfigurationService to set
	 */
	public void setAsahiConfigurationService(final AsahiConfigurationService asahiConfigurationService)
	{
		this.asahiConfigurationService = asahiConfigurationService;
	}

	/**
	 * @return the cmsSiteService
	 */
	public CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	/**
	 * @param cmsSiteService
	 *           the cmsSiteService to set
	 */
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	@Override
	protected CustomerModel getCustomer(final ApbKegReturnEmailProcessModel businessProcessModel)
	{
		// TODO Auto-generated method stub
		return null;
	}

	protected ApbKegReturnEmailModel getKegReturnEmail(final ApbKegReturnEmailProcessModel apbKegReturnEmailProcessModel)
	{
		return apbKegReturnEmailProcessModel.getApbKegReturnEmail();
	}



	/**
	 * @return the kegReturnEmailConverter
	 */
	public Converter<ApbKegReturnEmailModel, ApbKegReturnData> getKegReturnEmailConverter()
	{
		return kegReturnEmailConverter;
	}

	/**
	 * @param kegReturnEmailConverter
	 *           the kegReturnEmailConverter to set
	 */
	public void setKegReturnEmailConverter(final Converter<ApbKegReturnEmailModel, ApbKegReturnData> kegReturnEmailConverter)
	{
		this.kegReturnEmailConverter = kegReturnEmailConverter;
	}

	/**
	 * @return the apbKegReturnData
	 */
	public ApbKegReturnData getApbKegReturnData()
	{
		return apbKegReturnData;
	}

	/**
	 * @param apbKegReturnData
	 *           the apbKegReturnData to set
	 */
	public void setApbKegReturnData(final ApbKegReturnData apbKegReturnData)
	{
		this.apbKegReturnData = apbKegReturnData;
	}

}
