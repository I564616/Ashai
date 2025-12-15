/**
 *
 */
package com.sabmiller.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.velocity.tools.generic.EscapeTool;

import com.sabm.core.model.OrderDispatchNotificationEmailProcessModel;
import com.sabmiller.commons.constants.SabmcommonsConstants;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.facades.util.SabmFeatureUtil;


/**
 * @author marc.f.l.bautista
 *
 */
public class OrderDispatchNotificationEmailContext extends AbstractEmailContext<OrderDispatchNotificationEmailProcessModel>
{
	private CustomerData customerData;
	private OrderData orderData;
	private ConsignmentData consignmentData;

	@Resource(name = "customerConverter")
	private Converter<UserModel, CustomerData> customerConverter;

	@Resource(name = "orderConverter")
	private Converter<OrderModel, OrderData> orderConverter;

	@Resource(name = "sabmFeatureUtil")
	private SabmFeatureUtil sabmFeatureUtil;

	private Converter<ConsignmentModel, ConsignmentData> consignmentConverter;

	private String requestedDeliveryDate;

	private boolean showTrackOrder;

	private EscapeTool escTool;

	@Override
	public void init(final OrderDispatchNotificationEmailProcessModel businessProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(businessProcessModel, emailPageModel);
		put(SECURE_BASE_URL, get(SECURE_BASE_URL) + "/login?targetUrl=");

		customerData = customerConverter.convert(businessProcessModel.getCustomer());

		//Populate consignment from order
		OrderModel order = businessProcessModel.getOrder();
		List<ConsignmentData> consignmentList = new ArrayList<ConsignmentData>();
		for (ConsignmentModel consignmentModel : order.getConsignments())
		{
			consignmentData = getConsignmentConverter().convert(consignmentModel);
			consignmentList.add(consignmentData);
		}
		orderData = orderConverter.convert(businessProcessModel.getOrder());
		orderData.setConsignments(consignmentList); // add consignments to orders
		requestedDeliveryDate = SabmDateUtils.getFormattedDate(orderData.getRequestedDeliveryDate());
		showTrackOrder = sabmFeatureUtil.isFeatureEnabledForUnit(SabmcommonsConstants.TRACK_DELIVERY_ORDER,
				businessProcessModel.getOrder().getUnit());
		if (showTrackOrder && orderData.getStatusDisplay().equalsIgnoreCase("dispatched"))
		{
			orderData.setStatusDisplay("beingdispatched");
		}
	}

	@Override
	protected BaseSiteModel getSite(final OrderDispatchNotificationEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final OrderDispatchNotificationEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final OrderDispatchNotificationEmailProcessModel businessProcessModel)
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
	 * @return the orderData
	 */
	public OrderData getOrderData()
	{
		return orderData;
	}

	/**
	 * @param orderData
	 *           the orderData to set
	 */
	public void setOrderData(final OrderData orderData)
	{
		this.orderData = orderData;
	}

	/**
	 * @return the requestedDeliveryDate
	 */
	public String getRequestedDeliveryDate()
	{
		return requestedDeliveryDate;
	}

	/**
	 * @return the showTrackOrder
	 */
	public boolean getShowTrackOrder()
	{
		return showTrackOrder;
	}

	/**
	 * @param showTrackOrder
	 *           the showTrackOrder to set
	 */
	public void setShowTrackOrder(final boolean showTrackOrder)
	{
		this.showTrackOrder = showTrackOrder;
	}

	/**
	 * @return the escTool
	 */
	public EscapeTool getEscTool()
	{
		escTool = new EscapeTool();
		return escTool;
	}

	/**
	 * @return the consignmentData
	 */
	public ConsignmentData getConsignmentData()
	{
		return consignmentData;
	}

	/**
	 * @param consignmentData
	 *           the consignmentData to set
	 */
	public void setConsignmentData(ConsignmentData consignmentData)
	{
		this.consignmentData = consignmentData;
	}

	/**
	 * @return the consignmentConverter
	 */
	public Converter<ConsignmentModel, ConsignmentData> getConsignmentConverter()
	{
		return consignmentConverter;
	}

	/**
	 * @param consignmentConverter
	 *           the consignmentConverter to set
	 */
	public void setConsignmentConverter(Converter<ConsignmentModel, ConsignmentData> consignmentConverter)
	{
		this.consignmentConverter = consignmentConverter;
	}



}