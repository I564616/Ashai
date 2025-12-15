/**
 *
 */
package com.sabmiller.core.handlers;

import com.sabmiller.core.model.SABMNotificationModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import org.apache.commons.lang3.StringUtils;


/**
 * @author g.charan.pandit.raj
 *
 */
public class NotificationContactnumberHandler implements DynamicAttributeHandler<String, SABMNotificationModel>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler#get(de.hybris.platform.servicelayer.model.
	 * AbstractItemModel)
	 */
	@Override
	public String get(final SABMNotificationModel notification)
	{

		final String mobilenumber = notification.getUser().getMobileContactNumber();
		if (StringUtils.isEmpty(mobilenumber))
		{
			return null;
		}
		return mobilenumber;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler#set(de.hybris.platform.servicelayer.model.
	 * AbstractItemModel, java.lang.Object)
	 */
	@Override
	public void set(final SABMNotificationModel notification, final String mobileNumber)
	{
		throw new UnsupportedOperationException("Set of dynamic attribute 'mobileNumber' of SABMNotification is disabled!");
	}

}
