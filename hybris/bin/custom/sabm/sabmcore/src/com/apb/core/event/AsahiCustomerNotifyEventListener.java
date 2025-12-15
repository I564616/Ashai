package com.apb.core.event;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.service.config.AsahiConfigurationService;

import de.hybris.platform.acceleratorservices.site.AbstractAcceleratorSiteEventListener;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.model.process.AsahiCustomerNotifyProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

public class AsahiCustomerNotifyEventListener extends AbstractAcceleratorSiteEventListener<AsahiCustomerNotifyEvent>  {

	private ModelService modelService;
	private BusinessProcessService businessProcessService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
	
	@Override
	protected SiteChannel getSiteChannelForEvent(final AsahiCustomerNotifyEvent event) {
		final BaseSiteModel site = event.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
		return site.getChannel();
	}

	@Override
	protected void onSiteEvent(final AsahiCustomerNotifyEvent event) {
		
		final String notifyType = event.getNotifyType();
		String businessProcess = StringUtils.EMPTY;
		if(notifyType.equalsIgnoreCase(asahiConfigurationService.getString(ApbCoreConstants.NO_DELIVERY, "NODEL"))){
			businessProcess = ApbCoreConstants.NO_DELIVERY_PROCESS;
		}
		else if(notifyType.equalsIgnoreCase(asahiConfigurationService.getString(ApbCoreConstants.ALT_CALLDAY_DELIVERY, "ALTCALL"))){
			businessProcess = ApbCoreConstants.ALT_CALLDAY_DELIVERY_PROCESS;
		}
		else if(notifyType.equalsIgnoreCase(asahiConfigurationService.getString(ApbCoreConstants.ALT_DELDATE_DELIVERY, "ALTDEL"))){
			businessProcess = ApbCoreConstants.ALT_DELDATE_DELIVERY_PROCESS;
		}
		final AsahiCustomerNotifyProcessModel asahiCustomerNotifyProcessModel = (AsahiCustomerNotifyProcessModel) getBusinessProcessService()
				.createProcess(businessProcess+"-" + event.getCustomer().getUid() + "-" + System.currentTimeMillis(),
						businessProcess);
		asahiCustomerNotifyProcessModel.setSite(event.getSite());
		asahiCustomerNotifyProcessModel.setLanguage(event.getLanguage());
		asahiCustomerNotifyProcessModel.setCurrency(event.getBaseStore().getDefaultCurrency());
		asahiCustomerNotifyProcessModel.setStore(event.getBaseStore());
		asahiCustomerNotifyProcessModel.setHoliday(event.getHoliday());
		asahiCustomerNotifyProcessModel.setCutOffDate(event.getCutOffDate());
		asahiCustomerNotifyProcessModel.setDeliveryDate(event.getDeliveryDate());
		asahiCustomerNotifyProcessModel.setCustomer(event.getCustomer());
		getModelService().save(asahiCustomerNotifyProcessModel);
		getBusinessProcessService().startProcess(asahiCustomerNotifyProcessModel);
	}

}
