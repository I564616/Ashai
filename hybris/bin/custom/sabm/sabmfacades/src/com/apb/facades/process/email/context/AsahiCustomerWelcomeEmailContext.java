/**
 *
 */



package com.apb.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.model.process.AsahiCustomerWelcomeEmailProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.service.config.AsahiConfigurationService;



public class AsahiCustomerWelcomeEmailContext extends AbstractEmailContext<AsahiCustomerWelcomeEmailProcessModel>
{

	private Converter<UserModel, CustomerData> customerConverter;
	private CustomerData customerData;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Autowired
	private CMSSiteService cmsSiteService;

	@Override
	public void init(final AsahiCustomerWelcomeEmailProcessModel businessProcessModel, final EmailPageModel emailPageModel)
	{
		super.setBaseSite(getSite(businessProcessModel));
		super.init(businessProcessModel, emailPageModel);
		customerData = getCustomerConverter().convert(getCustomer(businessProcessModel));
		final String fromEmailAddress = asahiConfigurationService
				.getString(ApbCoreConstants.SELF_REGISTRATION_FROM_EMAIL + cmsSiteService.getCurrentSite().getUid(), "");


		final String displayFromName = asahiConfigurationService
				.getString(ApbCoreConstants.SELF_REGISTRATION_EMAIL_NAME + cmsSiteService.getCurrentSite().getUid(), "");


		put(FROM_EMAIL, fromEmailAddress);

		final LanguageModel language = getEmailLanguage(businessProcessModel);
		if (language != null)
		{
			put(EMAIL_LANGUAGE, language);
		}
		put(FROM_DISPLAY_NAME, displayFromName);

		put(ApbCoreConstants.CUSTOMERDATA, customerData);

		put("orderAccess", businessProcessModel.getOrderAccess());
		put("payAccess", businessProcessModel.getPayAccess());
		put("payerEmail", businessProcessModel.getPayerEmail());
		put("customerAccountName", businessProcessModel.getCustomerAccountName());
	}

	@Override
	protected BaseSiteModel getSite(final AsahiCustomerWelcomeEmailProcessModel businessProcessModel)
	{

		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final AsahiCustomerWelcomeEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final AsahiCustomerWelcomeEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}

	/**
	 * @return the customerConverter
	 */
	public Converter<UserModel, CustomerData> getCustomerConverter()
	{
		return customerConverter;
	}

	/**
	 * @param customerConverter
	 *           the customerConverter to set
	 */
	public void setCustomerConverter(final Converter<UserModel, CustomerData> customerConverter)
	{
		this.customerConverter = customerConverter;
	}

}
