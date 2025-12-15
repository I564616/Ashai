/**
 *
 */
package com.apb.core.interceptor;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ThreadLocalRandom;

import com.sabmiller.core.model.PlanogramModel;


/**
 * @author Saumya.Mittal1
 *
 */
public class AsahiPlanogramPrepareInterceptor implements PrepareInterceptor<PlanogramModel>
{

	@Override
	public void onPrepare(final PlanogramModel model, final InterceptorContext context) throws InterceptorException
	{
		if (null != model && null == model.getCode())
		{
			model.setCode("default_" + ThreadLocalRandom.current().nextDouble());
		}
		if (StringUtils.isBlank(model.getUploadedBy()))
		{
			throw new InterceptorException("Please provide your name while uploading the default planogram");
		}
		if (StringUtils.isBlank(model.getDocumentName()))
		{
			throw new InterceptorException("Please provide a document name for the uploaded planogram");
		}

	}

}
