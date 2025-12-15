package com.sabmiller.storefront.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/**
 * Created by wei.yang.ng on 28/05/2016.
 */
public class ContactUsForm
{
	private String name;
	private String business_unit;
	private String preferred_contact;
	private String serviceMessage;
	private String request_type;
	private String phoneNumber;

	@NotNull
	@Size(min = 2)
	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	@NotNull
	@Size(min = 1)
	public String getBusiness_unit()
	{
		return business_unit;
	}

	public void setBusiness_unit(final String business_unit)
	{
		this.business_unit = business_unit;
	}

	@NotNull
	public String getPreferred_contact()
	{
		return preferred_contact;
	}

	public void setPreferred_contact(final String preferred_contact)
	{
		this.preferred_contact = preferred_contact;
	}

	@NotNull
	@Size(min = 1)
	public String getServiceMessage()
	{
		return serviceMessage;
	}

	public void setServiceMessage(final String serviceMessage)
	{
		this.serviceMessage = serviceMessage;
	}

	@NotNull
	@Size(min = 1)
	public String getRequest_type()
	{
		return request_type;
	}

	public void setRequest_type(final String request_type)
	{
		this.request_type = request_type;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	/**
	 * @param phoneNumber
	 *           the phoneNumber to set
	 */
	public void setPhoneNumber(final String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}
}
