package com.apb.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;

import org.apache.velocity.tools.generic.DateTool;
import org.springframework.beans.factory.annotation.Autowired;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.service.config.AsahiConfigurationService;


public class ApbCustomerEmailContext extends AbstractEmailContext<StoreFrontCustomerProcessModel>
{

	private Converter<UserModel, CustomerData> customerConverter;
	private CustomerData customerData;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Autowired
	private CMSSiteService cmsSiteService;

	@Override
	public void init(final StoreFrontCustomerProcessModel businessProcessModel, final EmailPageModel emailPageModel)
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

		final CustomerModel customerModel = getCustomer(businessProcessModel);
		if (customerModel != null)
		{
			put(TITLE, (customerModel.getTitle() != null && customerModel.getTitle().getName() != null)
					? customerModel.getTitle().getName() : "");
			put(DISPLAY_NAME, customerModel.getDisplayName());
			put(EMAIL, getCustomerEmailResolutionService().getEmailForCustomer(customerModel));
		}

		put(DATE_TOOL, new DateTool());
		put(ApbCoreConstants.CUSTOMERDATA, customerData);
	}

	@Override
	protected BaseSiteModel getSite(final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel)
	{
		return storeFrontCustomerProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel)
	{
		return storeFrontCustomerProcessModel.getCustomer();
	}

	protected Converter<UserModel, CustomerData> getCustomerConverter()
	{
		return customerConverter;
	}

	public void setCustomerConverter(final Converter<UserModel, CustomerData> customerConverter)
	{
		this.customerConverter = customerConverter;
	}

	public CustomerData getCustomer()
	{
		return customerData;
	}

	@Override
	protected LanguageModel getEmailLanguage(final StoreFrontCustomerProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}
}
