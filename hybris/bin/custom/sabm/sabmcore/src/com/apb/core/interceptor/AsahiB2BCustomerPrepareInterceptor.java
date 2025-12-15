package com.apb.core.interceptor;


import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.SABMNotificationModel;
import com.sabmiller.core.notification.service.NotificationService;


public class AsahiB2BCustomerPrepareInterceptor implements PrepareInterceptor<B2BCustomerModel>
{


	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	@Resource(name = "notificationService")
	private NotificationService notificationService;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Override
	public void onPrepare(final B2BCustomerModel b2bCustomerModel, final InterceptorContext context) throws InterceptorException
	{

		if (null != b2bCustomerModel && null != b2bCustomerModel.getName())
		{
			b2bCustomerModel.setName(getCustomerName(b2bCustomerModel.getName()));
		}

		if (baseSiteService.getCurrentBaseSite() != null
				&& SabmCoreConstants.CUB_STORE.equalsIgnoreCase(baseSiteService.getCurrentBaseSite().getUid()))
		{
			if (context.isModified(b2bCustomerModel) && !context.isNew(b2bCustomerModel))
			{
				// The context of the current item
				final ItemModelContext itemContext = b2bCustomerModel.getItemModelContext();

				// Check if the model has any modifications
				if (itemContext.isUpToDate())
				{
					// The model has no modifications
					return;
				}
				if (StringUtils.isNotEmpty(itemContext.getOriginalValue("mobileContactNumber"))
						&& StringUtils.isEmpty(b2bCustomerModel.getMobileContactNumber()))
				{
					final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent(b2bCustomerModel);
					if (selectedB2BUnit == null)
					{
						return;
					}
					final List<SABMNotificationModel> notificationModels = notificationService
							.getNotificationForAllUnits(b2bCustomerModel);
					b2bCustomerModel.setReceiveUpdatesForSms(Boolean.FALSE);
					if (notificationModels == null || CollectionUtils.isEmpty(notificationModels))
					{
						return;
					}
					notificationModels.stream().forEach(notificationModel -> {
						notificationModel.getNotificationPreferences().stream().forEach(notficationPref -> {
							notficationPref.setSmsEnabled(Boolean.FALSE);
							modelService.save(notficationPref);
						});
					});
				}
			}
		}
	}

	private String getCustomerName(final String name)
	{

		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
}
