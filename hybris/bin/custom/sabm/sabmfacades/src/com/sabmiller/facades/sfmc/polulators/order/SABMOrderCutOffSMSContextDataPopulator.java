package com.sabmiller.facades.sfmc.polulators.order;

import com.sabmiller.facades.sfmc.context.SABMOrderCutOffSMSContextData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Created by zhuo.a.jiang.
 */
public class SABMOrderCutOffSMSContextDataPopulator implements Populator<Map, SABMOrderCutOffSMSContextData> {
    /**
     * Populate the target instance with values from the source instance.
     *
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException if an error occurs
     */

    private static final Logger LOG = LoggerFactory.getLogger(SABMOrderCutOffSMSContextDataPopulator.class);

    @Override
    public void populate(Map source, SABMOrderCutOffSMSContextData target) throws ConversionException {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");


        //cutOffTime format
//      final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

        try {
            final String cutoffDateTime = (String) source.get("cutOffTime");

            String[] array = cutoffDateTime.split(" ", 2);

            target.setAccountNumber((String) source.get("accountNumber"));
            target.setCutOffPreference(String.valueOf(source.get("cutOffPreference")) + " minutes");
            if(array.length ==2) {
                target.setCutOffDay(array[0]);
                /*target.setCutOffHour(array[1]);*/
                target.setCutOffTime(array[1]);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }
}
