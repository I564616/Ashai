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

import com.sabm.core.model.OrderCutoffNotificationEmailProcessModel;


/**
 * @author marc.f.l.bautista
 *
 */
public class OrderCutoffNotificationEmailContext extends AbstractEmailContext<OrderCutoffNotificationEmailProcessModel>
{
	private CustomerData customerData;
	private String cutoffDateTime;
	private String deliveryDate;

	@Resource(name = "customerConverter")
	private Converter<UserModel, CustomerData> customerConverter;

	@Override
	public void init(final OrderCutoffNotificationEmailProcessModel businessProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(businessProcessModel, emailPageModel);
		customerData = customerConverter.convert(businessProcessModel.getCustomer());
		setCutoffDateTime(businessProcessModel.getCutoffDateTime().substring(11) + " "
				+ businessProcessModel.getCutoffDateTime().substring(0, 10));
		setDeliveryDate(businessProcessModel.getDeliveryDate());
	}

	@Override
	protected BaseSiteModel getSite(final OrderCutoffNotificationEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final OrderCutoffNotificationEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final OrderCutoffNotificationEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
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
	 * @return the cutoffDateTime
	 */
	public String getCutoffDateTime()
	{
		return cutoffDateTime;
	}

	/**
	 * @param cutoffDateTime
	 *           the cutoffDateTime to set
	 */
	public void setCutoffDateTime(final String cutoffDateTime)
	{
		this.cutoffDateTime = cutoffDateTime;
	}

	/**
	 * @return the deliveryDate
	 */
	public String getDeliveryDate()
	{
		return deliveryDate;
	}

	/**
	 * @param deliveryDate
	 *           the deliveryDate to set
	 */
	public void setDeliveryDate(final String deliveryDate)
	{
		this.deliveryDate = deliveryDate;
	}

}
