package com.sabmiller.facades.populators;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import jakarta.annotation.Resource;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.commons.constants.SabmcommonsConstants;
import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.model.PlantCutOffModel;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.facades.order.data.TrackOrderData;

/**
 * Created by zhuo.a.jiang on 12/01/2018.
 */
public class SabmTrackOrderPopulator implements Populator<ConsignmentModel, TrackOrderData> {
    private static final Logger LOG = LoggerFactory.getLogger(SabmTrackOrderPopulator.class);

    private Map<ConsignmentStatus, String> consignmentStatusDisplayMapping;
    private Map<ConsignmentStatus, String> consignmentStatusIdMapping;
    private Converter<ConsignmentModel, ConsignmentData> consignmentConverter
            ;

 	@Resource(name = "baseStoreService")
 	private BaseStoreService baseStoreService;

	@Resource(name = "sabmDeliveryDateCutOffService")
	private SABMDeliveryDateCutOffService sabmDeliveryDateCutOffService;

    @Override
    public void populate(final ConsignmentModel consignmentModel, final TrackOrderData trackOrderData) throws ConversionException {

        trackOrderData.setStatus(getConsignmentStatusInfo(consignmentModel));
        trackOrderData.setStatusId(Integer.valueOf(getConsignmentStatusId(consignmentModel)));


        // populate ETA from SAP
        if (Objects.nonNull(consignmentModel.getConsignmentConfirmedDate())) {
            trackOrderData
                    .setConfirmedTime(consignmentModel.getConsignmentConfirmedDate());
        } else {
            trackOrderData
                    .setConfirmedTime(consignmentModel.getCreationtime());
        }
		trackOrderData.setBeingPickedTime(consignmentModel.getConsignmentBeingPickedDate());
		trackOrderData.setDispatchedTime(consignmentModel.getConsignmentDispatchedDate());


        // populate ETA from Retriever
        updateSubStatusAndETAForConsginment(consignmentModel,trackOrderData);

        trackOrderData.setOrderCode(consignmentModel.getOrder() != null ? consignmentModel.getOrder().getSapSalesOrderNumber() :"");
        trackOrderData.setRequestedDeliveryDate(consignmentModel.getOrder() != null ? consignmentModel.getOrder().getRequestedDeliveryDate(): new Date());

        trackOrderData.setConsignment(getConsignmentConverter().convert(consignmentModel));
        trackOrderData.setB2bUnitName(consignmentModel.getOrder()!=null&& consignmentModel.getOrder().getUnit()!=null ? consignmentModel.getOrder().getUnit().getName():"");
        trackOrderData.setTimeZone(consignmentModel.getOrder()!=null&& consignmentModel.getOrder().getUnit()!=null ? getPlantTimeZone(consignmentModel.getOrder().getUnit()):getBaseStoreTimeZone());
        Optional<ConsignmentEntryModel> foundConsignment = consignmentModel.getConsignmentEntries().stream().filter
                (entry -> !entry.getQuantity().equals(entry.getShippedQuantity())).findAny();
        trackOrderData.setOrderedAndDispatchedQuantityNotEqual(foundConsignment.isPresent());
        trackOrderData.setFirstETAMessageTime(consignmentModel.getConsignmentInTransitDate());
        trackOrderData.setNotDeliveredReason(consignmentModel.getNotDeliveredReason());

    }

    public Map<ConsignmentStatus, String> getConsignmentStatusDisplayMapping() {
        return consignmentStatusDisplayMapping;
    }

    public void setConsignmentStatusDisplayMapping(final Map<ConsignmentStatus, String> consignmentStatusDisplayMapping) {
        this.consignmentStatusDisplayMapping = consignmentStatusDisplayMapping;
    }

    protected String  getConsignmentStatusInfo(final ConsignmentModel consignmentModel){

        if (consignmentModel.getStatus()!= null) {
            return consignmentStatusDisplayMapping.get(consignmentModel.getStatus());
        }
        return null;

    }

    public Map<ConsignmentStatus, String> getConsignmentStatusIdMapping() {
        return consignmentStatusIdMapping;
    }
    public void setConsignmentStatusIdMapping(final Map<ConsignmentStatus, String> consignmentStatusIdMapping) {
        this.consignmentStatusIdMapping = consignmentStatusIdMapping;
    }

    protected String  getConsignmentStatusId(final ConsignmentModel consignmentModel){

        if (consignmentModel.getStatus()!= null) {
            return consignmentStatusIdMapping.get(consignmentModel.getStatus());
        }
        return null;

    }

    protected String updateSubStatusAndETAForConsginment (final ConsignmentModel consignmentModel,final TrackOrderData trackOrderData){

        final String subStatus = "";

        if (consignmentModel.getStatus() != null){
             /*
              when consignment is in "Confirmed, Being picked, Dispatched", the subStatus is alwasy "Delivery Window"
             */
            if (consignmentModel.getStatus().equals(ConsignmentStatus.CREATED)
            ||  consignmentModel.getStatus().equals(ConsignmentStatus.PROCESSING)
            ||  consignmentModel.getStatus().equals(ConsignmentStatus.SHIPPED)){

                trackOrderData.setSubStatus(SabmcommonsConstants.TRACK_DELIVERY_ORDER_SUBSTATUS_DELIVERY_WINDOW);


                // initally put startETA and endETA
                // trackOrderData.setStartETA(consignmentModel.getOrder() !=null ?consignmentModel.getOrder().getRequestedDeliveryDate(): null);
                // trackOrderData.setEndETA(consignmentModel.getOrder() !=null ?consignmentModel.getOrder().getRequestedDeliveryDate(): null);

            }
            /*
            when consignment is "In Transit" , the subStatus can be either "Estimated time of Arrival" or "Arrived"
             */
            if (consignmentModel.getStatus().equals(ConsignmentStatus.INTRANSIT)) {

                consignmentStatusValidation (consignmentModel);

                if (BooleanUtils.isNotTrue(consignmentModel.getInTransitArrived())) {
                    trackOrderData.setSubStatus(SabmcommonsConstants.TRACK_DELIVERY_ORDER_SUBSTATUS_ESTIMATED_TIME_OF_ARRIVAL);

                    Date roundedDate = null;
                    if (consignmentModel.getEstimatedArrivedTime() != null) {
						roundedDate = SabmDateUtils.roundDateToNearestQuarterHour( consignmentModel.getEstimatedArrivedTime());
                        trackOrderData.setStartETA(SabmDateUtils
                                .minusMinutes(roundedDate, Integer.valueOf(Config.getString("trackorder.ETA.time.window.minnutes", ""))));
                        trackOrderData.setEndETA(SabmDateUtils
                                .plusMinutes(roundedDate, Integer.valueOf(Config.getString("trackorder.ETA.time.window.minnutes", ""))));

                        if (BooleanUtils.isTrue(consignmentModel.getInTransitNextInQueue())) {
                            trackOrderData.setStartETA(SabmDateUtils.minusMinutes(roundedDate,
                                    Integer.valueOf(Config.getString("trackorder.ETA.time.window.minnutes.nextDelivery", ""))));
                            trackOrderData.setEndETA(SabmDateUtils.plusMinutes(roundedDate,
                                    Integer.valueOf(Config.getString("trackorder.ETA.time.window.minnutes.nextDelivery", ""))));
                        }
                    }


                } else if(BooleanUtils.isTrue(consignmentModel.getInTransitArrived())){
                    trackOrderData.setSubStatus(SabmcommonsConstants.TRACK_DELIVERY_ORDER_SUBSTATUS_ARRIVED);
                    if (consignmentModel.getEstimatedArrivedTime() != null) {
						trackOrderData.setArrivedTime(consignmentModel.getEstimatedArrivedTime());

                    }
                }

            }

             /*
             when consignment is "Delivered" , the subStatus is "Delivery Completed at"
             */
            if (consignmentModel.getStatus().equals(ConsignmentStatus.DELIVERED)){
                trackOrderData.setSubStatus(SabmcommonsConstants.TRACK_DELIVERY_ORDER_SUBSTATUS_DELIVERED);
				trackOrderData.setDeliveredTime(consignmentModel.getConsignmentDeliveredDate());
                trackOrderData.setSignature(consignmentModel.getSignature());
            }

            /*
            when consignment is "Not Delivered" , the subStatus is "Next Available Delivery Date"
             */

            if (consignmentModel.getStatus().equals(ConsignmentStatus.NOTDELIVERED)){
                //DO nothing
            }

        }
        return subStatus;

    }

    public Converter<ConsignmentModel, ConsignmentData> getConsignmentConverter() {
        return consignmentConverter;
    }

    public void setConsignmentConverter(final Converter<ConsignmentModel, ConsignmentData> consignmentConverter) {
        this.consignmentConverter = consignmentConverter;
    }

    private void consignmentStatusValidation(final ConsignmentModel consignmentModel){

        if  (BooleanUtils.isTrue(consignmentModel.getInTransitNextInQueue()) && BooleanUtils.isTrue(consignmentModel.getInTransitArrived())){

            LOG.error("consignment code {} from order {} has invalid status : nextInQueue: {} and arrived: {} ",  consignmentModel.getCode(),   consignmentModel.getOrder().getSapSalesOrderNumber(),
                    consignmentModel.getInTransitNextInQueue() , consignmentModel.getInTransitArrived());

        }
    }






	// get plant timezone

    protected String getPlantTimeZone(final B2BUnitModel b2bUnit) {

        String timeZone=null;

        if (b2bUnit.getPlant() != null && CollectionUtils.isNotEmpty(b2bUnit.getPlant().getCutOffs())) {

            for (PlantCutOffModel plantCutOffForTZ : b2bUnit.getPlant().getCutOffs()){

                if (plantCutOffForTZ != null && plantCutOffForTZ.getTimeZone() != null) {

                    TimeZone plantCutOffTimeZone = TimeZone.getTimeZone(plantCutOffForTZ.getTimeZone().getCode());

                    timeZone = plantCutOffTimeZone.getID();
                    if(timeZone!=null){
                        return timeZone;
                    }
                }
            }

        }
      if(timeZone==null){

            timeZone= getBaseStoreTimeZone();
      }
      return timeZone;
    }


	protected String getBaseStoreTimeZone() {

	final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid("sabmStore");


			TimeZone storeTimeZone = null;

			//Getting BaseStore timezone
			if (baseStore != null && baseStore.getTimeZone() != null)
			{
				storeTimeZone = TimeZone.getTimeZone(baseStore.getTimeZone().getCode());
			}
			return storeTimeZone.getID();
		}

   



}
