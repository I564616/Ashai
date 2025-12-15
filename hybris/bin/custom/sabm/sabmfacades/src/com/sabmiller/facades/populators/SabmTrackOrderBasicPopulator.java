package com.sabmiller.facades.populators;

import com.sabmiller.facades.order.data.TrackOrderData;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Date;

public class SabmTrackOrderBasicPopulator extends SabmTrackOrderPopulator {

    @Override
    public void populate(ConsignmentModel consignmentModel, TrackOrderData trackOrderData) throws ConversionException {

        trackOrderData.setStatus(getConsignmentStatusInfo(consignmentModel));
        trackOrderData.setStatusId(Integer.valueOf(getConsignmentStatusId(consignmentModel)));


        // populate ETA from Retriever
        updateSubStatusAndETAForConsginment(consignmentModel, trackOrderData);

        trackOrderData.setOrderCode(consignmentModel.getOrder() != null ? consignmentModel.getOrder().getSapSalesOrderNumber() : "");
        trackOrderData.setRequestedDeliveryDate(consignmentModel.getOrder() != null ? consignmentModel.getOrder().getRequestedDeliveryDate() : new Date());

        trackOrderData.setTimeZone(consignmentModel.getOrder() != null && consignmentModel.getOrder().getUnit() != null ? getPlantTimeZone(consignmentModel.getOrder().getUnit()) : getBaseStoreTimeZone());

    }
}
