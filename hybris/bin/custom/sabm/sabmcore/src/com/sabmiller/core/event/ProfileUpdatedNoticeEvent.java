/**
 *
 */
package com.sabmiller.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.core.model.user.UserModel;

import com.sabmiller.core.model.AsahiB2BUnitModel;

import java.io.Serial;


/**
 * Registration event, implementation of {@link AbstractCommerceUserEvent}
 */
public class ProfileUpdatedNoticeEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	@Serial
	private static final long serialVersionUID = 1L;

	private UserModel fromUser;

	private AsahiB2BUnitModel asahiB2bUnit;

	/**
	 * @return the asahiB2bUnit
	 */
	public AsahiB2BUnitModel getAsahiB2bUnit()
	{
		return asahiB2bUnit;
	}

	/**
	 * @param asahiB2bUnit
	 *           the asahiB2bUnit to set
	 */
	public void setAsahiB2bUnit(final AsahiB2BUnitModel asahiB2bUnit)
	{
		this.asahiB2bUnit = asahiB2bUnit;
	}

	/**
	 * @param fromUser
	 */
	public ProfileUpdatedNoticeEvent(final UserModel fromUser)
	{
		super();
		this.fromUser = fromUser;
	}

	/**
	 * @param fromUser
	 */
	public ProfileUpdatedNoticeEvent(final AsahiB2BUnitModel asahiB2bUnit)
	{
		super();
		this.asahiB2bUnit = asahiB2bUnit;
	}

	/**
	 * @return the fromUser
	 */
	public UserModel getFromUser()
	{
		return fromUser;
	}

	/**
	 * @param fromUser
	 *           the fromUser to set
	 */
	public void setFromUser(final UserModel fromUser)
	{
		this.fromUser = fromUser;
	}


}
