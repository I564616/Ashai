package com.sabmiller.webservice.trackorders.converters.populator;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.sabmiller.webservice.trackorders.TrackMyDeliveryNotification;

/**
 * Created by zhuo.a.jiang on 2/02/2018.
 */
public class TrackOrderConsignmentPopulator implements Populator<TrackMyDeliveryNotification.TrackedDelivery, ConsignmentData> {

    private static final String MESSSAGETYPE_ETA = "ETA";
    private static final String MESSSAGETYPE_ARRIVED = "Arrived";
    private static final String MESSSAGETYPE_DELIVERED = "Delivered";
    private static final String MESSSAGETYPE_NOT_DELIVERED = "NotDelivered";
	private static final String MESSAGETYPE_NOTDELIVERED_TEMP = "NotDeliveredTemp";
	static final Logger LOG = LoggerFactory.getLogger(TrackOrderConsignmentPopulator.class.getName());

    private static final String NOT_DELIVERED_COMPLETED_ON_PAPER = "Completed on Paper";
    @Override
    public void populate(final TrackMyDeliveryNotification.TrackedDelivery source, final ConsignmentData target)
            throws ConversionException {

        final String messageType = source.getMessageType() != null ? source.getMessageType() : "";
        final String consignmentCode = source.getSAPDeliveryNumber() !=null ?source.getSAPDeliveryNumber():"";
        final String customerName = source.getCustomerName()!=null? source.getCustomerName():"";
        final boolean nextInQueueIndicator = source.isNextInQueueIndicator()!=null? source.isNextInQueueIndicator():false;
		final String notDeliveredReason = source.getNotDeliveredReason() != null ? source.getNotDeliveredReason() : "";

        final TrackMyDeliveryNotification.TrackedDelivery.DateTime time = source.getDateTime();

        if (ObjectUtils.isEmpty(messageType) || ObjectUtils.isEmpty(consignmentCode)){
            throw new ConversionException("the Message type is empty");
        }

        if (time.getValue() == null){
            throw new ConversionException("there is no date time");
        }
		  final Date date = time.getValue()
				  .toGregorianCalendar(TimeZone.getTimeZone(StringUtils.replace(time.getTimeZoneCode(), "UTC", "GMT")), null, null)
				  .getTime();


		  target.setCode(consignmentCode);

        switch (messageType)
        {
            case MESSSAGETYPE_ETA:
                target.setEstimatedArrivedTime(date);
                target.setStatus(ConsignmentStatus.INTRANSIT);

                if (BooleanUtils.isTrue(nextInQueueIndicator)){
                    target.setInTransitNextInQueue(true);
                }

            break;

			case MESSAGETYPE_NOTDELIVERED_TEMP:
				target.setEstimatedArrivedTime(null);
                if (notDeliveredReason.equalsIgnoreCase(NOT_DELIVERED_COMPLETED_ON_PAPER)) {
                    target.setConsignmentDeliveredDate(date);
                    target.setInTransitArrived(true);
                    target.setStatus(ConsignmentStatus.DELIVERED);
                    target.setSignature(NOT_DELIVERED_COMPLETED_ON_PAPER);
                } else {
                    target.setStatus(ConsignmentStatus.INTRANSIT);
                    target.setInTransitNextInQueue(false);
                    target.setInTransitArrived(false);
                }

                target.setNotDeliveredReason(notDeliveredReason);

             break;

            case MESSSAGETYPE_ARRIVED:
                target.setEstimatedArrivedTime(date);
                target.setInTransitArrived(true);
                target.setStatus(ConsignmentStatus.INTRANSIT);
                break;

            case MESSSAGETYPE_DELIVERED:
                target.setConsignmentDeliveredDate(date);
                target.setInTransitArrived(true);
                target.setSignature(customerName);
                target.setStatus(ConsignmentStatus.DELIVERED);

                break;

            case MESSSAGETYPE_NOT_DELIVERED:

                if (notDeliveredReason.equalsIgnoreCase(NOT_DELIVERED_COMPLETED_ON_PAPER)) {
                    target.setConsignmentDeliveredDate(date);
                    target.setInTransitArrived(true);
                    target.setStatus(ConsignmentStatus.DELIVERED);
                    target.setSignature(NOT_DELIVERED_COMPLETED_ON_PAPER);
                } else {
                    target.setStatus(ConsignmentStatus.NOTDELIVERED);
                    target.setInTransitArrived(false);
                    target.setEstimatedArrivedTime(null);
                    target.setInTransitNextInQueue(false);
                }
                target.setNotDeliveredReason(notDeliveredReason);
                break;
        }


    }
}
