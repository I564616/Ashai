package com.sabmiller.core.invoices.converters.populators;

import com.sabmiller.core.model.InvoiceDiscrepancyRequestItemDetailModel;
import com.sabmiller.facades.invoice.SABMInvoiceItemData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Created by zhuo.a.jiang on 27/8/18.
 */
public class InvoiceDiscrepancyItemDataPopulator implements Populator<InvoiceDiscrepancyRequestItemDetailModel, SABMInvoiceItemData> {
    /**
     * Populate the target instance with values from the source instance.
     *
     * @param source the source object
     * @param target                      the target to fill
     * @throws ConversionException if an error occurs
     */
    @Override
    public void populate(InvoiceDiscrepancyRequestItemDetailModel source,
            SABMInvoiceItemData target) throws ConversionException {

        target.setItemDescriptionLine1(source.getItemDescriptionLine1());
        target.setItemDescriptionLine2(source.getItemDescriptionLine2());

        target.setDiscountReceived(source.getDiscountReceived()!=null ? String.valueOf(source.getDiscountReceived()): null);
        target.setDiscountExpected(source.getDiscountExpected()!=null ? String.valueOf(source.getDiscountExpected()): null);

        target.setQuantity(source.getQuantity());
    }
}
