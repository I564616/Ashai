package com.sabmiller.core.comparators;

import java.util.Comparator;
import java.util.Date;

import com.sabmiller.facades.order.data.TrackOrderData;

/**
 * Created by zhuo.a.jiang on 29/01/2018.
 */
public class TrackOrderDataComparator  implements Comparator<TrackOrderData> {
    @Override
    public int compare(final TrackOrderData o1, final TrackOrderData o2) {

        //TODO -- the below logic need to be re-written for correct behaviour
        final Date trackOrderETAData1 = o1.getStartETA();
        final Date trackOrderETAData2 = o2.getStartETA();


        final Date trackOrderArrivedData1 = o1.getArrivedTime();
        final Date trackOrderArrivedData2 = o2.getArrivedTime();

        if (trackOrderETAData1 == null && trackOrderETAData2 == null)
        {
            return 0;
        }

        if (trackOrderETAData1 == null)
        {
            return -1;
        }

        if (trackOrderETAData2 == null)
        {
            return 1;
        }

        //first by etaTime
        int etaResult = trackOrderETAData1.compareTo(trackOrderETAData2);
        if (etaResult != 0)
        {
            return etaResult;
        }

        // Next by ArrivedTime
        int arrivedTimeResult = trackOrderArrivedData1.compareTo(trackOrderArrivedData2);
        if (arrivedTimeResult != 0)
        {
            return arrivedTimeResult;
        }


        return 0;
    }
}
