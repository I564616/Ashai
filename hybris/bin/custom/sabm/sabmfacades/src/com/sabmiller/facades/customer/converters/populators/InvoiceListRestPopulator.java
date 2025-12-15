package com.sabmiller.facades.customer.converters.populators;

import com.sabmiller.facades.invoice.SABMInvoiceList;
import com.sabmiller.integration.sap.invoices.discrepancy.response.InvoiceListResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * Created by zhuo.a.jiang on 9/8/18.
 */
public class InvoiceListRestPopulator implements Populator<InvoiceListResponse.Invoice, SABMInvoiceList> {
    /**
     * Populate the target instance with values from the source instance.
     *
     * @param source         the source object
     * @param target the target to fill
     * @throws ConversionException if an error occurs
     */
    @Override
    public void populate(InvoiceListResponse.Invoice source, SABMInvoiceList target) throws ConversionException {

        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        target.setCustomerSoldTo(StringUtils.trimToEmpty(source.getSoldTo()));
        target.setInvoices(source.getInvoiceNumber());
        target.setDateStart(source.getDateStart());
        target.setDateEnd(source.getDateEnd());
    }
}
