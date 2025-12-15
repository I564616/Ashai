package com.apb.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

import com.apb.core.model.ApbKegReturnEmailModel;

import java.io.Serial;


/**
 *
 */
public class ApbKegReturnEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{

	@Serial
	private static final long serialVersionUID = 1L;
	private ApbKegReturnEmailModel apbKegReturnEmail;

	/**
	 * @return the apbKegReturnEmail
	 */
	public ApbKegReturnEmailModel getApbKegReturnEmail()
	{
		return apbKegReturnEmail;
	}

	/**
	 * @param apbKegReturnEmail
	 *           the apbKegReturnEmail to set
	 */
	public void setApbKegReturnEmail(final ApbKegReturnEmailModel apbKegReturnEmail)
	{
		this.apbKegReturnEmail = apbKegReturnEmail;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}


}
