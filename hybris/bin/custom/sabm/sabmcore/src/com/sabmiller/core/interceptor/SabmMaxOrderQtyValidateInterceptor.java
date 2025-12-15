/**
 *
 */
package com.sabmiller.core.interceptor;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import com.sabmiller.core.enums.MaxOrderQtyRuleType;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.MaxOrderQtyModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;


/**
 * @author Ranjith.Karuvachery
 *
 */
public class SabmMaxOrderQtyValidateInterceptor implements ValidateInterceptor<MaxOrderQtyModel>
{
	@Override
	public void onValidate(final MaxOrderQtyModel model, final InterceptorContext context) throws InterceptorException
	{
		if (model != null)
		{
			if (MaxOrderQtyRuleType.CUSTOMER_RULE.equals(model.getRuleType()))
			{
				if (model.getB2bunit() == null)
				{
					throw new InterceptorException("B2bunit is mandatory for creating a new " + MaxOrderQtyRuleType.CUSTOMER_RULE);
				}
				else if (model.getB2bunit() instanceof AsahiB2BUnitModel)
				{
					throw new InterceptorException(
							"A valid CUB B2bunit is mandatory for creating a new " + MaxOrderQtyRuleType.CUSTOMER_RULE);
				}
			}
			else if (MaxOrderQtyRuleType.PLANT_RULE.equals(model.getRuleType()) && model.getPlant() == null)
			{
				throw new InterceptorException("Plant is mandatory for creating a new " + MaxOrderQtyRuleType.PLANT_RULE);
			}
			else if (model.getProduct() == null)
			{
				throw new InterceptorException("Please associate a valid SABMAlcoholVariantProductEANModel");
			}
		}
	}
}
