/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;

import com.sabmiller.commons.constants.SabmcommonsConstants;
import com.sabmiller.facades.util.SabmFeatureUtil;


/**
 * Velocity context for a order notification email.
 */
public class OrderNotificationEmailContext extends AbstractEmailContext<OrderProcessModel>
{
	private Converter<OrderModel, OrderData> orderConverter;
	private OrderData orderData;
	private String deliveryDate;
	private String placedByName;
	private String bdeOrderEmailGroup;
	private boolean showTrackOrder;
	private String bdeOrderCustomerFirstName;



	/**
	 * @return the bdeOrderCustomerFirstName
	 */
	public String getBdeOrderCustomerFirstName()
	{
		return bdeOrderCustomerFirstName;
	}

	/**
	 * @param bdeOrderCustomerFirstName
	 *           the bdeOrderCustomerFirstName to set
	 */
	public void setBdeOrderCustomerFirstName(final String bdeOrderCustomerFirstName)
	{
		this.bdeOrderCustomerFirstName = bdeOrderCustomerFirstName;
	}

	
	@Resource(name = "sabmFeatureUtil")
	private SabmFeatureUtil sabmFeatureUtil;





	/** The date pattern output. */
	@Value(value = "${order.email.delivery.date.pattern:EEEE dd/MM/yyyy}")
	private String deliveryDateatternOutput;

	@Override
	public void init(final OrderProcessModel orderProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(orderProcessModel, emailPageModel);
		put(SECURE_BASE_URL, get(SECURE_BASE_URL) + "/login?targetUrl=");
		//put(MEDIA_BASE_URL, getSiteBaseUrlResolutionService().getMediaUrlForSite(getBaseSite(), true));
		orderData = getOrderConverter().convert(orderProcessModel.getOrder());
		deliveryDate = null != orderData.getRequestedDeliveryDate()
				? DateFormatUtils.format(orderData.getRequestedDeliveryDate(), deliveryDateatternOutput) : "";
		placedByName = orderProcessModel.getOrder().getUserDisplayName();
		bdeOrderEmailGroup = orderProcessModel.getBdeOrderEmailGroup() != null ? orderProcessModel.getBdeOrderEmailGroup() : "";
		showTrackOrder = sabmFeatureUtil.isFeatureEnabledForUnit(SabmcommonsConstants.TRACK_DELIVERY_ORDER,orderProcessModel.getOrder().getUnit());
		bdeOrderCustomerFirstName = orderProcessModel.getBdeOrderCustomerFirstName() != null
				? orderProcessModel.getBdeOrderCustomerFirstName()
				: "";

	}

	@Override
	protected BaseSiteModel getSite(final OrderProcessModel orderProcessModel)
	{
		return orderProcessModel.getOrder().getSite();
	}

	@Override
	protected CustomerModel getCustomer(final OrderProcessModel orderProcessModel)
	{
		if (BooleanUtils.isTrue(orderProcessModel.getOrder().getBdeOrder()))
		{
			return null;
		}
		else
		{
		return (CustomerModel) orderProcessModel.getOrder().getUser();
		}
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

	public String getDeliveryDate()
	{
		return deliveryDate;
	}

	@Override
	protected LanguageModel getEmailLanguage(final OrderProcessModel orderProcessModel)
	{
		return orderProcessModel.getOrder().getLanguage();
	}

	/**
	 * @return the placedByName
	 */
	public String getPlacedByName()
	{
		return placedByName;
	}

	/**
	 * @param placedByName
	 *           the placedByName to set
	 */
	public void setPlacedByName(final String placedByName)
	{
		this.placedByName = placedByName;
	}

	/**
	 * @return the bdeOrderEmailGroup
	 */
	public String getBdeOrderEmailGroup()
	{
		return bdeOrderEmailGroup;
	}

	/**
	 * @param bdeOrderEmailGroup
	 *           the bdeOrderEmailGroup to set
	 */
	public void setBdeOrderEmailGroup(final String bdeOrderEmailGroup)
	{
		this.bdeOrderEmailGroup = bdeOrderEmailGroup;
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
}
