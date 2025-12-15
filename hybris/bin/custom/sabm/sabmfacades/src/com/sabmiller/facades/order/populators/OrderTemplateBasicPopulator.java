package com.sabmiller.facades.order.populators;

import com.sabmiller.facades.order.data.OrderTemplateData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.SABMOrderTemplateModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.util.Assert;

public class OrderTemplateBasicPopulator implements Populator<SABMOrderTemplateModel, OrderTemplateData> {

    @Override
    public void populate(final SABMOrderTemplateModel source,final OrderTemplateData target) throws ConversionException {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        target.setCode(source.getCode());
        target.setName(source.getName());

    }
}
