/**
 *
 */
package com.sabmiller.core.handlers;

import com.sabmiller.core.model.SABMNotificationModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;


/**
 * @author g.charan.pandit.raj
 *
 */
public class NotificationUserstatusHandler implements DynamicAttributeHandler<Boolean, SABMNotificationModel>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler#get(de.hybris.platform.servicelayer.model.
	 * AbstractItemModel)
	 */
	@Override
	public Boolean get(final SABMNotificationModel notification)
	{
		final Boolean returnBooleanFalse = Boolean.FALSE;
		final Boolean returnBooleanTrue = Boolean.TRUE;
		if (notification.getUser().getActive().booleanValue() == returnBooleanTrue)
		{
			return returnBooleanTrue;
		}
		return returnBooleanFalse;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler#set(de.hybris.platform.servicelayer.model.
	 * AbstractItemModel, java.lang.Object)
	 */
	@Override
	public void set(final SABMNotificationModel notification, final Boolean userstatus)
	{
		throw new UnsupportedOperationException("Set of dynamic attribute 'userstatus' of SABMNotification is disabled!");
	}

}
