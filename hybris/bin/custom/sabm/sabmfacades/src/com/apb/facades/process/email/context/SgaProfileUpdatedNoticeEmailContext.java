/**
 *
 */
package com.apb.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.model.process.SgaProfileUpdatedNoticeProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;


/**
 * Velocity context for a customer profiles updated notice email.
 *
 */
public class SgaProfileUpdatedNoticeEmailContext extends AbstractEmailContext<SgaProfileUpdatedNoticeProcessModel>
{
	public static final String CUSTOMER_ACCOUNT = "customerAccount";
	public static final String CUSTOMER_ACCOUNT_NAME = "customerAccountName";
	private static final String CUSTOMER_NAME = "customerName";

	@Resource(name = "customerConverter")
	private Converter<UserModel, CustomerData> customerConverter;

	@Override
	public void init(final SgaProfileUpdatedNoticeProcessModel storeFrontCustomerProcess, final EmailPageModel emailPageModel)
	{
		super.init(storeFrontCustomerProcess, emailPageModel);
		put(CUSTOMER_ACCOUNT, storeFrontCustomerProcess.getAsahiB2bUnit().getUid());
		put(CUSTOMER_ACCOUNT_NAME, storeFrontCustomerProcess.getAsahiB2bUnit().getName());
		put(CUSTOMER_NAME, storeFrontCustomerProcess.getCustomer().getName());
	}

	@Override
	protected BaseSiteModel getSite(final SgaProfileUpdatedNoticeProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final SgaProfileUpdatedNoticeProcessModel businessProcessModel)
	{
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final SgaProfileUpdatedNoticeProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}

}
