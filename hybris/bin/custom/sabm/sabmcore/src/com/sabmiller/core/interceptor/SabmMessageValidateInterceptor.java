/**
 *
 */
package com.sabmiller.core.interceptor;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.sabmiller.core.b2b.services.SabmB2BCustomerService;
import com.sabmiller.core.model.SabmMessageModel;
import com.sabmiller.core.model.SabmUserMessagesStatusModel;


/**
 * @author marc.f.l.bautista
 *
 */
public class SabmMessageValidateInterceptor implements ValidateInterceptor<SabmMessageModel>
{
	@Resource(name = "sabmB2BCustomerService")
	private SabmB2BCustomerService sabmB2BCustomerService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.interceptor.ValidateInterceptor#onValidate(java.lang.Object,
	 * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
	 */
	@Override
	public void onValidate(final SabmMessageModel model, final InterceptorContext context) throws InterceptorException
	{
		final String messageCode = model.getCode();
		if (model != null && context.isModified(model, SabmMessageModel.TEXT) && model.getExpiry().after(new Date()))
		{
			final List<SabmUserMessagesStatusModel> allEntries = sabmB2BCustomerService.getAllUserMessageEntries(messageCode);
			if (CollectionUtils.isNotEmpty(allEntries))
			{
				context.getModelService().removeAll(allEntries);
			}
		}
	}

}
