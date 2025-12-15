package com.apb.core.interceptor;

import de.hybris.platform.b2b.interceptor.B2BUnitModelValidateInterceptor;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import jakarta.annotation.Resource;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 * ApbB2BUnitModelValidateInterceptor implement of ({@link B2BUnitModelValidateInterceptor}
 *
 * handle current user for hot folder
 *
 */
public class ApbB2BUnitModelValidateInterceptor extends B2BUnitModelValidateInterceptor
{
	private static final Logger LOG = LoggerFactory.getLogger(B2BUnitModelValidateInterceptor.class);
	
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	@Override
	public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		
		if(model instanceof AsahiB2BUnitModel)
		{
		final B2BUnitModel unit = (B2BUnitModel) model;

		if (!(unit instanceof AsahiB2BUnitModel) && ctx.getModelService().isNew(model)
				&& this.getB2bUnitService().getParent(unit) == null && !this.getUserService()
						.isMemberOfGroup(this.getUserService().getCurrentUser(), this.getUserService().getAdminUserGroup()))
		{
			throw new InterceptorException(this.getL10NService().getLocalizedString("error.b2bunit.root.create.nonadmin"));
		}

		if (unit.getApprovers() != null)
		{
			final Set<B2BCustomerModel> b2bCustomerModelSet = unit.getApprovers();
			if (CollectionUtils.isNotEmpty(b2bCustomerModelSet))
			{
				final UserGroupModel child = this.getUserService().getUserGroupForUID("b2bapprovergroup");
				for (final B2BCustomerModel b2bCustomerModel : b2bCustomerModelSet)
				{
					if (!this.getUserService().isMemberOfGroup(b2bCustomerModel, child))
					{
						LOG.warn(String.format("Removed approver %s from unit %s due to lack of membership of group %s", new Object[]
						{ b2bCustomerModel.getUid(), unit.getUid(), "b2bapprovergroup" }));
					}
				}
				unit.setApprovers(b2bCustomerModelSet);
			}
		}

		if (!unit.getActive().booleanValue() && !ctx.getModelService().isNew(model))
		{
			final Set<B2BUnitModel> b2bUnitModelSet = this.getB2bUnitService().getB2BUnits(unit);
			for (final B2BUnitModel b2bUnitModel : b2bUnitModelSet)
			{
				if (b2bUnitModel.getActive().booleanValue())
				{
					b2bUnitModel.setActive(Boolean.FALSE);
					this.getModelService().save(b2bUnitModel);
				}
			}
		}
	}
		else
		{
			super.onValidate(model, ctx);
		}
	}
}

