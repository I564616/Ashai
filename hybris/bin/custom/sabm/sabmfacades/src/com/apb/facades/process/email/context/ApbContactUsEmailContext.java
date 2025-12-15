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
import de.hybris.platform.commerceservices.model.process.ApbContactUsEmailProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;

import jakarta.annotation.Resource;

import org.apache.velocity.tools.generic.DateTool;
import org.springframework.beans.factory.annotation.Autowired;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.model.ContactUsQueryEmailModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.facades.constants.ApbFacadesConstants;
import com.apb.facades.contactust.data.ApbContactUsData;
import de.hybris.platform.enumeration.EnumerationService;
import org.apache.commons.lang3.StringUtils;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.enums.AsahiEnquiryType;
import com.sabmiller.core.enums.AsahiEnquirySubType;
/**
 * Velocity context for a Contact Us Query email.
 */
public class ApbContactUsEmailContext extends AbstractEmailContext<ApbContactUsEmailProcessModel>
{
	private Converter<ContactUsQueryEmailModel, ApbContactUsData> contactUsEmailConverter;
	private ApbContactUsData apbContactUsData;
	private KeyGenerator keyGenerator;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Autowired
	private CMSSiteService cmsSiteService;

	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Override
	public void init(final ApbContactUsEmailProcessModel apbContactUsEmailProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(apbContactUsEmailProcessModel, emailPageModel);

		final String fromDisplayName = asahiConfigurationService
				.getString(ApbCoreConstants.CONTACT_US_EMAIL_NAME + cmsSiteService.getCurrentSite().getUid(), "");
		final String fromEmailAddress = asahiConfigurationService
				.getString(ApbCoreConstants.CONTACT_US_FROM_EMAIL + cmsSiteService.getCurrentSite().getUid(), "");


		put(FROM_EMAIL, fromEmailAddress);

		final LanguageModel language = getEmailLanguage(apbContactUsEmailProcessModel);
		if (language != null)
		{
			put(EMAIL_LANGUAGE, language);
		}
		put(FROM_DISPLAY_NAME, fromDisplayName);

		final ContactUsQueryEmailModel contactUsQueryEmailModel = getContactUsQueryEmail(apbContactUsEmailProcessModel);
		apbContactUsData = getContactUsEmailConverter().convert(getContactUsQueryEmail(apbContactUsEmailProcessModel));
		if (contactUsQueryEmailModel != null)
		{
			put(ApbFacadesConstants.EMAIL_REFERENCE_NO,
					(contactUsQueryEmailModel.getReferenceNumber() != null ? contactUsQueryEmailModel.getReferenceNumber() : ""));
			put(DISPLAY_NAME, contactUsQueryEmailModel.getName());
			put(EMAIL, contactUsQueryEmailModel.getEmailAddress());
			put("apbContactUsData", apbContactUsData);

			String customerservice= "";
			String customerservicephone = "";

			if(asahiSiteUtil.isSga() && asahiConfigurationService.getBoolean("sga.contactus.update.available", false)) {

				if (StringUtils.isNotEmpty(contactUsQueryEmailModel.getEnquirySubType()))
				{
					put("enquiryTypeName",enumerationService.getEnumerationName(enumerationService.getEnumerationValue(AsahiEnquiryType.class, contactUsQueryEmailModel.getEnquiryType())));
					put("enquirySubTypeName",enumerationService.getEnumerationName(enumerationService.getEnumerationValue(AsahiEnquirySubType.class, contactUsQueryEmailModel.getEnquirySubType())));

					customerservice = asahiConfigurationService.getString(
							ApbCoreConstants.CONTACT_US_CUSTOMERSERVICE + cmsSiteService.getCurrentSite().getUid() + "." + contactUsQueryEmailModel.getEnquiryType() + "." + contactUsQueryEmailModel.getEnquirySubType(), "");
					customerservicephone = asahiConfigurationService.getString(
							ApbCoreConstants.CONTACT_US_CUSTOMERSERVICE_PHONE + cmsSiteService.getCurrentSite().getUid() + "." + contactUsQueryEmailModel.getEnquiryType() + "." + contactUsQueryEmailModel.getEnquirySubType(), "");
				}
				else
				{
					put("enquiryTypeName",enumerationService.getEnumerationName(enumerationService.getEnumerationValue(AsahiEnquiryType.class, contactUsQueryEmailModel.getEnquiryType())));


					customerservice = asahiConfigurationService.getString(
							ApbCoreConstants.CONTACT_US_CUSTOMERSERVICE + cmsSiteService.getCurrentSite().getUid() + "." + contactUsQueryEmailModel.getEnquiryType(), "");
					customerservicephone = asahiConfigurationService.getString(
							ApbCoreConstants.CONTACT_US_CUSTOMERSERVICE_PHONE + cmsSiteService.getCurrentSite().getUid() + "." + contactUsQueryEmailModel.getEnquiryType(), "");

				}
			}
			else
			{
				customerservice = asahiConfigurationService.getString(
						ApbCoreConstants.CONTACT_US_CUSTOMERSERVICE + cmsSiteService.getCurrentSite().getUid(), "");
				customerservicephone = asahiConfigurationService.getString(
						ApbCoreConstants.CONTACT_US_CUSTOMERSERVICE_PHONE + cmsSiteService.getCurrentSite().getUid(), "");
			}


			put("customerservice", customerservice);
			put("customerservicephone", customerservicephone);

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
	protected BaseSiteModel getSite(final ApbContactUsEmailProcessModel requestRegistrationProcessModel)
	{
		return requestRegistrationProcessModel.getSite();
	}




	@Override
	protected LanguageModel getEmailLanguage(final ApbContactUsEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}



	/**
	 * @return the contactUsEmailConverter
	 */
	public Converter<ContactUsQueryEmailModel, ApbContactUsData> getContactUsEmailConverter()
	{
		return contactUsEmailConverter;
	}

	/**
	 * @param contactUsEmailConverter
	 *           the contactUsEmailConverter to set
	 */
	public void setContactUsEmailConverter(final Converter<ContactUsQueryEmailModel, ApbContactUsData> contactUsEmailConverter)
	{
		this.contactUsEmailConverter = contactUsEmailConverter;
	}

	/**
	 * @return the apbContactUsData
	 */
	public ApbContactUsData getApbContactUsData()
	{
		return apbContactUsData;
	}

	/**
	 * @param apbContactUsData
	 *           the apbContactUsData to set
	 */
	public void setApbContactUsData(final ApbContactUsData apbContactUsData)
	{
		this.apbContactUsData = apbContactUsData;
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
	protected CustomerModel getCustomer(final ApbContactUsEmailProcessModel businessProcessModel)
	{
		return null;
	}

	protected ContactUsQueryEmailModel getContactUsQueryEmail(final ApbContactUsEmailProcessModel apbContactUsEmailProcessModel)
	{
		return apbContactUsEmailProcessModel.getContactUsQueryEmail();
	}
}
