/**
 *
 */
package com.sabmiller.webservice.trackorders.handler.logger;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import jakarta.annotation.Resource;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.xml.transformer.UnmarshallingTransformer;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.sabmiller.webservice.enums.DataImportStatusEnum;
import com.sabmiller.webservice.enums.EntityTypeEnum;
import com.sabmiller.webservice.model.MasterImportModel;
import com.sabmiller.webservice.model.TrackOrderImportRecordModel;
import com.sabmiller.webservice.trackorders.TrackMyDeliveryNotification;


/**
 * @author ramsatish.jagajyothi
 *
 */

@Component
public class TrackOrderAsyncLogger
{


	private static final Logger LOG = LoggerFactory.getLogger(TrackOrderAsyncLogger.class.getName());


	@Resource
	private ModelService modelService;

	@Resource
	private SessionService sessionService;


	@Resource
	private UnmarshallingTransformer trackOrderUnmarshallingTransformer;



	final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");





	public Message saveTrackOrderPayload(final Message message)
	{

		if (!Registry.hasCurrentTenant())
		{
			Registry.activateMasterTenant();
		}
		if (!sessionService.hasCurrentSession())
		{
			sessionService.createNewSession();
		}

		// there is no unique payload id from Retriever, each Request will be recorded in System
		final String paylaodId = dateFormat.format(new Date());

        final MasterImportModel masterImportModel = modelService.create(MasterImportModel.class);

		masterImportModel.setPayloadId(paylaodId);
		masterImportModel.setPayload(message.getPayload().toString());

		masterImportModel.setEntity(EntityTypeEnum.CONSIGNMENT_UPDATE_RETRIEVER);

		masterImportModel.setStatus(DataImportStatusEnum.NEW);
		modelService.save(masterImportModel);

		try
		{
			final TrackMyDeliveryNotification trackMyDeliveryNotification = (TrackMyDeliveryNotification) trackOrderUnmarshallingTransformer
					.doTransform(message);
			saveTrackOrderImportEntity(trackMyDeliveryNotification, masterImportModel);

		}
		catch (final Exception e)
		{
			LOG.error("TrackOrderAsyncLogger payload logger error", e);

		}

		LOG.debug("TrackOrderAsyncLogger payload logger");

		return message;
	}

	private void saveTrackOrderImportEntity(final TrackMyDeliveryNotification trackMyDeliveryNotification,
			final MasterImportModel masterImportModel)
	{
		if (CollectionUtils.isNotEmpty(trackMyDeliveryNotification.getTrackedDelivery()))
 		{
             for (final TrackMyDeliveryNotification.TrackedDelivery consignment :trackMyDeliveryNotification.getTrackedDelivery()){
				LOG.debug("saveTrackOrderImportEntity SAP Delivery Number", consignment.getSAPDeliveryNumber());
                 final TrackOrderImportRecordModel recordModel = modelService
						.create(TrackOrderImportRecordModel.class);
				recordModel.setMessageType(consignment.getMessageType());
				recordModel.setSAPDeliveryNumber(consignment.getSAPDeliveryNumber());
				recordModel.setNextInQueueIndicator(
						String.valueOf(consignment.isNextInQueueIndicator() != null ? consignment.isNextInQueueIndicator() : ""));
				recordModel
						.setDateTime(String.valueOf(consignment.getDateTime() != null ? consignment.getDateTime().getValue() : ""));
				recordModel.setTimeZoneCode(consignment.getDateTime() != null ? consignment.getDateTime().getTimeZoneCode() : "");
				recordModel.setMasterRecord(masterImportModel);
                 recordModel.setNotDeliveredReason(consignment.getNotDeliveredReason());
				modelService.save(recordModel);
             }

	}
	}
}
