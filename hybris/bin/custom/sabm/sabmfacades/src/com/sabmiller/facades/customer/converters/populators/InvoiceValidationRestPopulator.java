package com.sabmiller.facades.customer.converters.populators;

import com.sabmiller.facades.invoice.SABMInvoiceDiscrepancyData;
import com.sabmiller.integration.sap.invoices.discrepancy.response.InvoiceValidationResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.Assert;

/**
 * Created by zhuo.a.jiang on 10/8/18.
 */
public class InvoiceValidationRestPopulator implements Populator<InvoiceValidationResponse.Invoice, SABMInvoiceDiscrepancyData> {
    /**
     * Populate the target instance with values from the source instance.
     *
     * @param source            the source object
     * @param target the target to fill
     * @throws ConversionException if an error occurs
     */
    @Override
    public void populate(InvoiceValidationResponse.Invoice source, SABMInvoiceDiscrepancyData target) throws ConversionException {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");


        target.setInvoiceNumber(source.getInvoiceNumber());
        target.setSoldTo(source.getSoldTo());
        target.setValidationStatus(BooleanUtils.toBoolean(source.getStatus()));



    }
}
