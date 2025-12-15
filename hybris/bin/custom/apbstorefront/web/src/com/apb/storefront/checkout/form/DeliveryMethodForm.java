package com.apb.storefront.checkout.form;

public class DeliveryMethodForm {
	
	
	private String deliveryType;
	private String deferredDeliveryDate;
	
	public String getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}
	
	public String getDeferredDeliveryDate() {
		return deferredDeliveryDate;
	}
	public void setDeferredDeliveryDate(String deferredDeliveryDate) {
		this.deferredDeliveryDate = deferredDeliveryDate;
	}
}
