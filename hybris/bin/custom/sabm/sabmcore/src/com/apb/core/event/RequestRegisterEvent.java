package com.apb.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

import com.apb.core.model.ApbRequestRegisterEmailModel;

import java.io.Serial;


/**
 *
 */
public class RequestRegisterEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{

	/**
	 *
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	private ApbRequestRegisterEmailModel requestRegisterEmail;

	/**
	 * @return the requestRegisterEmail
	 */
	public ApbRequestRegisterEmailModel getRequestRegisterEmail()
	{
		return requestRegisterEmail;
	}

	/**
	 * @param requestRegisterEmail
	 *           the requestRegisterEmail to set
	 */
	public void setRequestRegisterEmail(final ApbRequestRegisterEmailModel requestRegisterEmail)
	{
		this.requestRegisterEmail = requestRegisterEmail;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}



}
