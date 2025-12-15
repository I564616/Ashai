/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

import java.io.Serial;
import java.util.List;

import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;


/**
 *
 */
public class BusinessEnquiryEmailEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	@Serial
	private static final long serialVersionUID = 1L;

	private AbstractBusinessEnquiryData enquiry;
	private String requestType;
	private List<String> toEmails;
	private List<String> ccEmails;

	/**
	 *
	 */
	public BusinessEnquiryEmailEvent()
	{
		super();
	}

	/**
	 * @return the requestType
	 */
	public String getRequestType()
	{
		return requestType;
	}

	/**
	 * @param requestType
	 *           the requestType to set
	 */
	public void setRequestType(final String requestType)
	{
		this.requestType = requestType;
	}

	/**
	 * @return the enquiry
	 */
	public AbstractBusinessEnquiryData getEnquiry()
	{
		return enquiry;
	}

	/**
	 * @param enquiry
	 *           the enquiry to set
	 */
	public void setEnquiry(final AbstractBusinessEnquiryData enquiry)
	{
		this.enquiry = enquiry;
	}

	/**
	 * @return the toEmails
	 */
	public List<String> getToEmails()
	{
		return toEmails;
	}

	/**
	 * @param toEmails
	 *           the toEmails to set
	 */
	public void setToEmails(final List<String> toEmails)
	{
		this.toEmails = toEmails;
	}

	/**
	 * @return the ccEmails
	 */
	public List<String> getCcEmails()
	{
		return ccEmails;
	}

	/**
	 * @param ccEmails
	 *           the ccEmails to set
	 */
	public void setCcEmails(final List<String> ccEmails)
	{
		this.ccEmails = ccEmails;
	}
}
