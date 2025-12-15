package com.sabmiller.facades.sfmc.polulators.order;

import com.sabmiller.facades.sfmc.context.SABMOrderDispatchedSMSContextData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.util.Assert;

import java.util.Map;

public class SABMOrderDispatchedSMSContextDataPopulator implements Populator<Map, SABMOrderDispatchedSMSContextData> {
    @Override
    public void populate(Map source, SABMOrderDispatchedSMSContextData target) throws ConversionException {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");


        target.setAccountNumber((String) source.get("accountNumber"));
        target.setOrderNumber((String) source.get("orderNumber"));
    }
}
