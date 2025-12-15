package com.apb.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

import java.io.Serial;
import java.util.Set;

import com.apb.core.model.ApbEmailModel;
import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 *
 */
public class SuperRegisterEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	/**
	 *
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	private ApbEmailModel apbEmail;
	private Set<AsahiB2BUnitModel> asahiUnits;

	public ApbEmailModel getApbEmail() {
		return apbEmail;
	}
	public void setApbEmail(final ApbEmailModel apbEmail) {
		this.apbEmail = apbEmail;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the asahiUnits
	 */
	public Set<AsahiB2BUnitModel> getAsahiUnits()
	{
		return asahiUnits;
	}

	/**
	 * @param asahiUnits
	 *           the asahiUnits to set
	 */
	public void setAsahiUnits(final Set<AsahiB2BUnitModel> asahiUnits)
	{
		this.asahiUnits = asahiUnits;
	}






}
