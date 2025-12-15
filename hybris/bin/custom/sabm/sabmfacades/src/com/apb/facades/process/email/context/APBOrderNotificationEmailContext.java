/*
 * [y] hybris Platform
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
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.service.config.AsahiConfigurationService;
import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 * Velocity context for a order notification email.
 */
public class APBOrderNotificationEmailContext extends AbstractEmailContext<OrderProcessModel>
{
	/** The asahi configuration service. */
	@Resource
	private AsahiConfigurationService asahiConfigurationService;

	private Converter<OrderModel, OrderData> orderConverter;
	private OrderData orderData;
	private static final String DELIVERY_DATE = "deliveryDate";
	private static final String ORDER_PLACED = "orderPlaced";
	private static final String DEFAULT_VALUE = "NIL";
	private static final String SOLD_TO_CUSTOMER = "soldToCustomer";
	private static final String CUSTOMER_NO = "customerno";
	private static final String BDE_ORDER_EMAIL_GROUP = "bdeOrderEmailGroup";

	/**
	 * The method initialize the dynamic values for the email template
	 *
	 * @param orderProcessModel
	 * @param emailPageModel
	 */
	@Override
	public void init(final OrderProcessModel orderProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(orderProcessModel, emailPageModel);
		orderData = getOrderConverter().convert(orderProcessModel.getOrder());

		if (null != orderProcessModel.getOrder() && null != orderProcessModel.getOrder().getDeliveryRequestDate())
		{
			put(DELIVERY_DATE, setDateFormat(orderProcessModel.getOrder().getDeliveryRequestDate()));
		}
		else
		{
			put(DELIVERY_DATE, DEFAULT_VALUE);
		}
		if (null != orderProcessModel.getOrder() && null != orderProcessModel.getOrder().getCreationtime())
		{
			put(ORDER_PLACED, setDateFormat(orderProcessModel.getOrder().getCreationtime()));
		}
		else
		{
			put(ORDER_PLACED, DEFAULT_VALUE);
		}

		setSoldToCustomerDetails(orderProcessModel);
		if(BooleanUtils.isTrue(orderData.getBdeOrder())) {
			setBDEOrderDetails(orderProcessModel);
		}
	}

	/**
	 * @param orderProcessModel
	 */
	private void setBDEOrderDetails(final OrderProcessModel orderProcessModel)
	{
		put(DISPLAY_NAME,orderProcessModel.getBdeOrderCustomerFirstName());
		put(BDE_ORDER_EMAIL_GROUP, orderProcessModel.getBdeOrderEmailGroup());
	}

	/**
	 * Method to set customer display name and number in email context.
	 *
	 * @param orderProcessModel
	 */
	private void setSoldToCustomerDetails(final OrderProcessModel orderProcessModel)
	{

		if (null != orderProcessModel.getOrder() && null != orderProcessModel.getOrder().getUser())
		{
			final B2BCustomerModel user = (B2BCustomerModel) orderProcessModel.getOrder().getUser();

			if (null != user.getDefaultB2BUnit() && StringUtils.isNotEmpty(user.getDefaultB2BUnit().getLocName()))
			{
				put(SOLD_TO_CUSTOMER, user.getDefaultB2BUnit().getLocName());
			}
			else
			{
				put(SOLD_TO_CUSTOMER, DEFAULT_VALUE);
			}

			if (null != user.getDefaultB2BUnit() && user.getDefaultB2BUnit() instanceof AsahiB2BUnitModel
					&& StringUtils.isNotEmpty(((AsahiB2BUnitModel) user.getDefaultB2BUnit()).getAccountNum()))
			{
				put(CUSTOMER_NO, ((AsahiB2BUnitModel) user.getDefaultB2BUnit()).getAccountNum());
			}
			else
			{
				put(CUSTOMER_NO, DEFAULT_VALUE);
			}


		}
		else
		{
			put(SOLD_TO_CUSTOMER, DEFAULT_VALUE);
			put(CUSTOMER_NO, DEFAULT_VALUE);
		}
	}

	/**
	 * Method to get and set Date in String format
	 *
	 * @param deliveryRequestDate
	 * @return date in string format
	 */
	private String setDateFormat(final Date deliveryRequestDate)
	{
		return new SimpleDateFormat(this.asahiConfigurationService.getString(ApbCoreConstants.ASAHI_DATE_FORMAT_KEY,
				ApbCoreConstants.DEFER_DELIVERY_DATEPATTERN)).format(deliveryRequestDate);
	}

	@Override
	protected BaseSiteModel getSite(final OrderProcessModel orderProcessModel)
	{
		return orderProcessModel.getOrder().getSite();
	}

	@Override
	protected CustomerModel getCustomer(final OrderProcessModel orderProcessModel)
	{
		return (CustomerModel) orderProcessModel.getOrder().getUser();
	}

	protected Converter<OrderModel, OrderData> getOrderConverter()
	{
		return orderConverter;
	}

	public void setOrderConverter(final Converter<OrderModel, OrderData> orderConverter)
	{
		this.orderConverter = orderConverter;
	}

	public OrderData getOrder()
	{
		return orderData;
	}

	@Override
	protected LanguageModel getEmailLanguage(final OrderProcessModel orderProcessModel)
	{
		return orderProcessModel.getOrder().getLanguage();
	}


}
