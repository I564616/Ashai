package com.sabmiller.webservice.creditadjustment.converters;

import com.sabmiller.core.model.InvoiceDiscrepancyRequestModel;
import com.sabmiller.webservice.creditadjustment.CreditAdjustment;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Created by zhuo.a.jiang on 27/8/18.
 */
public class CreditAdjustmentImportRecordReverseConverter  implements Populator<CreditAdjustment.Invoice, InvoiceDiscrepancyRequestModel> {
    /**
     * Populate the target instance with values from the source instance.
     *
     * @param invoice                        the source object
     * @param invoiceDiscrepancyRequestModel the target to fill
     * @throws ConversionException if an error occurs
     */
    @Override
    public void populate(CreditAdjustment.Invoice invoice, InvoiceDiscrepancyRequestModel invoiceDiscrepancyRequestModel)
            throws ConversionException {

    }
}
