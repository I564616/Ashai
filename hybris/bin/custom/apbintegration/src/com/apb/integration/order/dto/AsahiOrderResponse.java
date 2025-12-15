package com.apb.integration.order.dto;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name = "orderResponse")
public class AsahiOrderResponse
{
	private String orderStatus;

   private String failureReason;

	public String getOrderStatus()
	{
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus)
	{
		this.orderStatus = orderStatus;
	}

	public String getFailureReason()
	{
		return failureReason;
	}

	public void setFailureReason(String failureReason)
	{
		this.failureReason = failureReason;
	}
   
   
}
