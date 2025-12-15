/**
 *
 */
package com.sabmiller.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.tools.generic.EscapeTool;

import com.sabm.core.model.OrderDeliveredNotificationEmailProcessModel;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.facades.populators.SABMOrderPopulator;
import com.sabmiller.facades.util.SabmFeatureUtil;

/**
 * @author marc.f.l.bautista
 *
 */
public class OrderDeliveredNotificationEmailContext extends AbstractEmailContext<OrderDeliveredNotificationEmailProcessModel>
{
	private CustomerData customerData;

	@Resource(name = "customerConverter")
	private Converter<UserModel, CustomerData> customerConverter;

	@Resource(name = "sabmFeatureUtil")
	private SabmFeatureUtil sabmFeatureUti;

	@Resource(name = "orderConverter")
	private Converter<OrderModel, OrderData> orderConverter;

	private OrderData orderData;

	private String timeStamp;

	private String deliveryAddress;

	private EscapeTool escTool;

	private String signature;


	@Override
	public void init(final OrderDeliveredNotificationEmailProcessModel businessProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(businessProcessModel, emailPageModel);
		put(SECURE_BASE_URL, get(SECURE_BASE_URL) + "/login?targetUrl=");
		orderData = orderConverter.convert(businessProcessModel.getOrder());
		orderData.setStatusDisplay( StringUtils.capitalize(businessProcessModel.getOrder().getStatusDisplay()));
		customerData = customerConverter.convert(businessProcessModel.getCustomer());
		timeStamp = businessProcessModel.getTimeStamp();
		deliveryAddress = businessProcessModel.getDeliveryAddress();
		signature = businessProcessModel.getSignature();

	}

	@Override
	protected BaseSiteModel getSite(final OrderDeliveredNotificationEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	/**
	 * @return the timeStamp
	 */
	public String getTimeStamp()
	{
		return timeStamp;
	}

	/**
	 * @param timeStamp
	 *           the timeStamp to set
	 */
	public void setTimeStamp(final String timeStamp)
	{
		this.timeStamp = timeStamp;
	}

	/**
	 * @return the deliveryAddress
	 */
	public String getDeliveryAddress()
	{
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress
	 *           the deliveryAddress to set
	 */
	public void setDeliveryAddress(final String deliveryAddress)
	{
		this.deliveryAddress = deliveryAddress;
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
	protected CustomerModel getCustomer(final OrderDeliveredNotificationEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final OrderDeliveredNotificationEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}

	public OrderData getOrderData() {
		return orderData;
	}

	public void setOrderData(final OrderData orderData) {
		this.orderData = orderData;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(final String signature) {
		this.signature = signature;
	}
}
