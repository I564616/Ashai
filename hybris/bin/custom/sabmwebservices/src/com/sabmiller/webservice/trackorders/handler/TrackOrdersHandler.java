package com.sabmiller.webservice.trackorders.handler;

import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.text.SimpleDateFormat;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.facades.ordersplitting.ConsignmentFacade;
import com.sabmiller.facades.ordersplitting.ConsignmentProcessException;
import com.sabmiller.integration.enums.ErrorEventType;
import com.sabmiller.integration.facade.ErrorEventFacade;
import com.sabmiller.webservice.enums.DataImportStatusEnum;
import com.sabmiller.webservice.enums.EntityTypeEnum;
import com.sabmiller.webservice.enums.OperationEnum;
import com.sabmiller.webservice.importer.AbstractImportHandler;
import com.sabmiller.webservice.model.ImportRecordModel;
import com.sabmiller.webservice.response.TrackOrdersResponse;
import com.sabmiller.webservice.trackorders.TrackMyDeliveryNotification;

/**
 * Created by zhuo.a.jiang on 19/12/2017.
 */
public class TrackOrdersHandler extends AbstractImportHandler<TrackMyDeliveryNotification, TrackOrdersResponse, ImportRecordModel>
{

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Resource(name = "sabConsignmentFacade")
    private ConsignmentFacade consignmentFacade;

    @Resource(name = "errorEventFacade")
    private ErrorEventFacade errorEventFacade;

    @Resource(name = "sabmConfigurationService")
    private SabmConfigurationService sabmConfigurationService;

    @Resource(name = "modelService")
    private ModelService modelService;

    @Resource(name = "trackOrderConsignmentConverter")
    private Converter<TrackMyDeliveryNotification.TrackedDelivery, ConsignmentData> trackOrderConsignmentConverter;

    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    @Override
    public Converter<TrackOrdersResponse, ImportRecordModel> getImportRecordReverseConverter() {
        return null;
    }



    @Override
    public EntityTypeEnum getEntityType() {
        return null;
    }

    @Override
    public TrackOrdersResponse importEntity(final TrackMyDeliveryNotification trackMyDeliveryNotification) {

		LOG.debug("TrackOrdersHandler importEntity");
   	 Exception consignmentProcessException = null;
		if (CollectionUtils.isNotEmpty(trackMyDeliveryNotification.getTrackedDelivery()))
		{
            for (final TrackMyDeliveryNotification.TrackedDelivery consignment :trackMyDeliveryNotification.getTrackedDelivery()){
                final ConsignmentData consignmentData = trackOrderConsignmentConverter.convert(consignment);


                try {
                    consignmentFacade.updateConsignmentStatusFromRetriever(consignmentData);
                } catch (final ConsignmentProcessException e) {

                    errorEventFacade.createErrorEntry(e, "retriever", null, ErrorEventType.RETRIEVER , e.getMessage());

					consignmentProcessException = e;
                }
            }
        }


		return generateResponse(trackMyDeliveryNotification, consignmentProcessException, false);
    }

    public String handleXSDValidationError(final String message)
    {
        LOG.error("XSD Valdiation Error occurred for Track orders request from 3rd party Retriever/Spectrum");

        return "XSD Valdiation Error occurred for Track orders request from 3rd party Retriever/Spectrum";
    }


    public TrackOrdersResponse logTrackOrdersRequestStatus(final TrackOrdersResponse trackOrdersResponse){
        return trackOrdersResponse;
    }



    @Override
    public TrackOrdersResponse generateResponse(final TrackMyDeliveryNotification trackMyDeliveryNotification, final Exception e, final Boolean entityExist)
    {

            final TrackOrdersResponse response = new TrackOrdersResponse();

               if( e == null ){
                   response.setCode("01");
                   response.setStatus(DataImportStatusEnum.SUCCESS);
                   response.setOperation(OperationEnum.CREATE);

               }
                else{
                   response.setCode("02");
                   response.setError(e.getMessage());
                   response.setStatus(DataImportStatusEnum.ERROR );

               }


            return response;
    }

}
