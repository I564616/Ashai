/**
 *
 */
package com.sabmiller.core.interceptor;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;

import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.sabmiller.core.b2b.services.SabmB2BCustomerService;
import com.sabmiller.core.model.SabmMessageModel;
import com.sabmiller.core.model.SabmUserMessagesStatusModel;


/**
 * @author ramsatish.jagajyothi
 *
 */
@SuppressWarnings("rawtypes")
public class SabmMessageRemoveInterceptor implements RemoveInterceptor
{

	@Resource(name = "sabmB2BCustomerService")
	private SabmB2BCustomerService sabmB2BCustomerService;


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.interceptor.RemoveInterceptor#onRemove(java.lang.Object,
	 * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
	 */
	@Override
	public void onRemove(final Object o, final InterceptorContext ctx) throws InterceptorException
	{
		if (o instanceof SabmMessageModel)
		{
			final SabmMessageModel message = (SabmMessageModel) o;
			final String messageCode = message.getCode();
			final List<SabmUserMessagesStatusModel> allEntries = sabmB2BCustomerService.getAllUserMessageEntries(messageCode);
			if (CollectionUtils.isNotEmpty(allEntries))
			{
				ctx.getModelService().removeAll(allEntries);
			}
		}

	}

}
