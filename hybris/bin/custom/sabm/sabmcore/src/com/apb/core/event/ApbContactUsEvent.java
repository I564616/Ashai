package com.apb.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

import com.apb.core.model.ContactUsQueryEmailModel;

import java.io.Serial;


/**
 *
 */
public class ApbContactUsEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	/**
	 *
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	private ContactUsQueryEmailModel contactUsQueryEmail;



	/**
	 * @return the contactUsQueryEmail
	 */
	public ContactUsQueryEmailModel getContactUsQueryEmail()
	{
		return contactUsQueryEmail;
	}



	/**
	 * @param contactUsQueryEmail
	 *           the contactUsQueryEmail to set
	 */
	public void setContactUsQueryEmail(final ContactUsQueryEmailModel contactUsQueryEmail)
	{
		this.contactUsQueryEmail = contactUsQueryEmail;
	}



	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

}
