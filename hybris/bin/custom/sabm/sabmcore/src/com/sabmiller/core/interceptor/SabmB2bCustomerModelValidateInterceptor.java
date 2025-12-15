/**
 *
 */
package com.sabmiller.core.interceptor;

import de.hybris.platform.b2bcommercefacades.interceptor.B2BCustomerModelValidateInterceptor;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import com.sabmiller.core.constants.SabmCoreConstants;


/**
 * @author Ranjith.Karuvachery
 *
 */
public class SabmB2bCustomerModelValidateInterceptor extends B2BCustomerModelValidateInterceptor
{

	@Override
	public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = (B2BCustomerModel) model;
			for (final PrincipalGroupModel group : customer.getGroups())
			{
				if (group instanceof B2BUnitModel && SabmCoreConstants.ZA02.equals(((B2BUnitModel) group).getAccountGroup()))
				{
					throw new InterceptorException(getL10NService().getLocalizedString("error.b2bcustomer.b2bunit.invalid"));
				}
			}
		}
		super.onValidate(model, ctx);
	}
}
