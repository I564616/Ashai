package com.sabmiller.facades.order.populators;

import com.sabmiller.facades.order.data.OrderTemplateData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.SABMOrderTemplateModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OrderTemplateExtendedPopulator implements Populator<SABMOrderTemplateModel, OrderTemplateData> {

    private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

    @Override
    public void populate(final SABMOrderTemplateModel source, final OrderTemplateData target) throws ConversionException {
        List<OrderEntryData> orderEntryList = null;

        if (CollectionUtils.isEmpty(source.getEntries()))
        {
            orderEntryList = Converters.convertAll(source.getEntries(), getOrderEntryConverter());
            Collections.sort(orderEntryList, Comparator.comparingInt(OrderEntryData::getSequenceNumber));
        }

        target.setEntries(ListUtils.emptyIfNull(orderEntryList));
    }

    protected Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter() {
        return orderEntryConverter;
    }

    public void setOrderEntryConverter(Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter) {
        this.orderEntryConverter = orderEntryConverter;
    }
}
