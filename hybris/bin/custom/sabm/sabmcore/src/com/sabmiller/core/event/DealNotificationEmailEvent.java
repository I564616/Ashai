/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

import java.io.Serial;


/**
 * @author raul.b.abatol.jr
 *
 */
public class DealNotificationEmailEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	@Serial
	private static final long serialVersionUID = 1L;

	private B2BUnitModel b2bUnit;

	public DealNotificationEmailEvent()
	{
		super();
	}

	/**
	 * @return the b2bUnit
	 */
	public B2BUnitModel getB2bUnit()
	{
		return b2bUnit;
	}

	/**
	 * @param b2bUnit the b2bUnit to set
	 */
	public void setB2bUnit(B2BUnitModel b2bUnit)
	{
		this.b2bUnit = b2bUnit;
	}


}
