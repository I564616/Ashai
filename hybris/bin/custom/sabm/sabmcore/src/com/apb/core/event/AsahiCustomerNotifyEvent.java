package com.apb.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

import java.io.Serial;

public class AsahiCustomerNotifyEvent extends AbstractCommerceUserEvent<BaseSiteModel> {

	private String notifyType;
	private String holiday;
	private String cutOffDate;
	private String deliveryDate;
	
	public String getHoliday() {
		return holiday;
	}

	public void setHoliday(final String holiday) {
		this.holiday = holiday;
	}

	public String getCutOffDate() {
		return cutOffDate;
	}

	public void setCutOffDate(final String cutOffDate) {
		this.cutOffDate = cutOffDate;
	}

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(final String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	@Serial
	private static final long serialVersionUID = 1L;
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getNotifyType() {
		return notifyType;
	}

	public void setNotifyType(final String notifyType) {
		this.notifyType = notifyType;
	}

}
