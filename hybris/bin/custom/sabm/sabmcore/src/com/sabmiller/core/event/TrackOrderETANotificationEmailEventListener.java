package com.sabmiller.core.event;

import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import com.sabm.core.model.TrackOrderETANotificationEmailProcessModel;

public class TrackOrderETANotificationEmailEventListener extends AbstractSiteEventListener<TrackOrderETANotificationEmailEvent> {


    @Resource(name = "modelService")
    private ModelService modelService;

    @Resource(name = "businessProcessService")
    private BusinessProcessService businessProcessService;

    @Override
    protected void onSiteEvent(final TrackOrderETANotificationEmailEvent event) {
        final TrackOrderETANotificationEmailProcessModel processModel = (TrackOrderETANotificationEmailProcessModel) businessProcessService
                .createProcess("trackOrderETANotificationEmailProcess" + "-" + System.currentTimeMillis(),
                        "trackOrderETANotificationEmailProcess");

        processModel.setSite(event.getSite());
        processModel.setCustomer(event.getCustomer());
        processModel.setLanguage(event.getLanguage());
        processModel.setCurrency(event.getCurrency());
        processModel.setStore(event.getBaseStore());
        processModel.setB2bUnit(event.getB2bUnit());
        processModel.setOrder((OrderModel) event.getOrder());
        processModel.setStartETA(event.getStartETA());
        processModel.setEndETA(event.getEndETA());
        modelService.save(processModel);
        businessProcessService.startProcess(processModel);

    }

    @Override
    protected boolean shouldHandleEvent(final TrackOrderETANotificationEmailEvent event) {
        return true;
    }
}
