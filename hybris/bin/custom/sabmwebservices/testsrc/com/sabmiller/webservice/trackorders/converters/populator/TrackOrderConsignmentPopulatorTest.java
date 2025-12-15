package com.sabmiller.webservice.trackorders.converters.populator;

import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabmiller.webservice.trackorders.TrackMyDeliveryNotification;

/**
 * Created by zhuo.a.jiang on 6/02/2018.
 */


public class TrackOrderConsignmentPopulatorTest {

    @InjectMocks
    private TrackOrderConsignmentPopulator tranckOrderConsignmentPopulator;

    @Mock
    TrackMyDeliveryNotification.TrackedDelivery trackedDelivery ;

    XMLGregorianCalendar xmlGregCal;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null;
        try {
            date = format.parse("2014-04-24 11:15:00");

            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(date);

            xmlGregCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

        }
        catch (ParseException e) {
            e.printStackTrace();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }



    }

    @Test(expected = ConversionException.class)
    public void populateWithMessageTypeIsNull() throws Exception {

        TrackMyDeliveryNotification.TrackedDelivery.DateTime time = new TrackMyDeliveryNotification.TrackedDelivery.DateTime();
        time.setValue(xmlGregCal);

        Mockito.when(trackedDelivery.getMessageType()).thenReturn(null);
        Mockito.when(trackedDelivery.getSAPDeliveryNumber()).thenReturn("01234567");
        Mockito.when(trackedDelivery.getCustomerName()).thenReturn(null);
        Mockito.when(trackedDelivery.isNextInQueueIndicator()).thenReturn(false);
        Mockito.when(trackedDelivery.getDateTime()).thenReturn(time);


        ConsignmentData consignmentData = new ConsignmentData();

        tranckOrderConsignmentPopulator.populate(trackedDelivery,consignmentData);


    }

}