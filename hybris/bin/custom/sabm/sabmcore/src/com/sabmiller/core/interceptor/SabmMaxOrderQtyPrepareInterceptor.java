/**
 *
 */
package com.sabmiller.core.interceptor;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

import jakarta.annotation.Resource;

import java.util.concurrent.ThreadLocalRandom;

import com.sabmiller.core.model.MaxOrderQtyModel;
import com.sabmiller.core.product.SabmProductService;


/**
 * @author Ranjith.Karuvachery
 *
 */
public class SabmMaxOrderQtyPrepareInterceptor implements PrepareInterceptor<MaxOrderQtyModel>
{
	/** The product service. */
	@Resource(name = "productService")
	private SabmProductService productService;
	@Override
	public void onPrepare(final MaxOrderQtyModel maxOrderQty, final InterceptorContext context) throws InterceptorException
	{
		if (null != maxOrderQty && context.isNew(maxOrderQty) && maxOrderQty.getProduct() != null && maxOrderQty.getRuleType() != null && maxOrderQty.getStartDate() != null && maxOrderQty.getEndDate() != null)
		{
			maxOrderQty
			.setDefaultAvgMaxOrderQty(productService.getAverageQuantity(maxOrderQty));
			if(maxOrderQty.getCode() ==null) {
				maxOrderQty.setCode("alb"+ThreadLocalRandom.current().nextDouble());
			}

		}
	}
}
