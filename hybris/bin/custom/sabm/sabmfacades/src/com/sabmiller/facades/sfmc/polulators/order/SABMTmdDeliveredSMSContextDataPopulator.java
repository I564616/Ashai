package com.sabmiller.facades.sfmc.polulators.order;

import com.sabmiller.facades.sfmc.context.SABMTmdDeliveredSMSContextData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Created by zhuo.a.jiang.
 */
public class SABMTmdDeliveredSMSContextDataPopulator implements Populator<Map, SABMTmdDeliveredSMSContextData> {
    /**
     * Populate the target instance with values from the source instance.
     *
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException if an error occurs
     */
    @Override
    public void populate(Map source, SABMTmdDeliveredSMSContextData target) throws ConversionException {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        target.setAccountNumber((String) source.get("accountNumber"));
        target.setOrderNumber((String) source.get("orderNumber"));
    }
}
