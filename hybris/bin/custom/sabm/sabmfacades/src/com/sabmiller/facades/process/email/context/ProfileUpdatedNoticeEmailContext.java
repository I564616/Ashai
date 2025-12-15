/**
 *
 */
package com.sabmiller.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;


/**
 * Velocity context for a customer profiles updated notice email.
 *
 */
public class ProfileUpdatedNoticeEmailContext extends AbstractEmailContext<StoreFrontCustomerProcessModel>
{
	private CustomerData fromUser;

	@Resource(name = "customerConverter")
	private Converter<UserModel, CustomerData> customerConverter;

	@Override
	public void init(final StoreFrontCustomerProcessModel storeFrontCustomerProcess, final EmailPageModel emailPageModel)
	{
		super.init(storeFrontCustomerProcess, emailPageModel);
		this.fromUser = customerConverter.convert(storeFrontCustomerProcess.getUser());
	}

	@Override
	protected BaseSiteModel getSite(final StoreFrontCustomerProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final StoreFrontCustomerProcessModel businessProcessModel)
	{
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final StoreFrontCustomerProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}

	/**
	 * @return the fromUser
	 */
	public CustomerData getFromUser()
	{
		return fromUser;
	}

}
