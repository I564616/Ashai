package com.apb.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

import com.apb.core.model.ApbCompanyDetailsEmailModel;

import java.io.Serial;


/**
 *
 */
public class ApbCompanyDetailsEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{

	/**
	 *
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	private ApbCompanyDetailsEmailModel apbCompanyDetailsEmailModel;

	public ApbCompanyDetailsEmailModel getApbCompanyDetailsEmailModel()
	{
		return apbCompanyDetailsEmailModel;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public void setApbCompanyDetailsEmailModel(final ApbCompanyDetailsEmailModel apbCompanyDetailsEmailModel)
	{
		this.apbCompanyDetailsEmailModel = apbCompanyDetailsEmailModel;
	}
}
