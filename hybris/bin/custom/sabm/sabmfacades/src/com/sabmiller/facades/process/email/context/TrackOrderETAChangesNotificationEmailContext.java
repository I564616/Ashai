/**
 *
 */
package com.sabmiller.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;

import org.apache.velocity.tools.generic.EscapeTool;

import com.sabm.core.model.TrackOrderETAChangesNotificationEmailProcessModel;


public class TrackOrderETAChangesNotificationEmailContext
		extends AbstractEmailContext<TrackOrderETAChangesNotificationEmailProcessModel>
{
	private CustomerData customerData;

	@Resource(name = "customerConverter")
	private Converter<UserModel, CustomerData> customerConverter;


	private String orderCode;



	private EscapeTool escTool;

	@Override
	public void init(final TrackOrderETAChangesNotificationEmailProcessModel businessProcessModel,
			final EmailPageModel emailPageModel)
	{
		super.init(businessProcessModel, emailPageModel);
		put(SECURE_BASE_URL, get(SECURE_BASE_URL) + "/login?targetUrl=");

		customerData = customerConverter.convert(businessProcessModel.getCustomer());
		orderCode = businessProcessModel.getOrderCode();
	}

	@Override
	protected BaseSiteModel getSite(final TrackOrderETAChangesNotificationEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}


	/**
	 * @return the orderCode
	 */
	public String getOrderCode()
	{
		return orderCode;
	}

	/**
	 * @param orderCode
	 *           the orderCode to set
	 */
	public void setOrderCode(final String orderCode)
	{
		this.orderCode = orderCode;
	}


	/**
	 * @return the customerData
	 */
	public CustomerData getCustomerData()
	{
		return customerData;
	}

	/**
	 * @param customerData
	 *           the customerData to set
	 */
	public void setCustomerData(final CustomerData customerData)
	{
		this.customerData = customerData;
	}


	/**
	 * @return the escTool
	 */
	public EscapeTool getEscTool()
	{
		escTool = new EscapeTool();
		return escTool;
	}

	@Override
	protected CustomerModel getCustomer(final TrackOrderETAChangesNotificationEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final TrackOrderETAChangesNotificationEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}

}
