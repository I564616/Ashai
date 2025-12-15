package com.sabmiller.core.invoices.converters.reversePopulators;

import com.sabmiller.core.model.InvoiceDiscrepancyRequestItemDetailModel;
import com.sabmiller.core.util.SabmNumberUtils;
import com.sabmiller.facades.invoice.SABMInvoiceItemData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.log4j.Logger;

/**
 * Created by zhuo.a.jiang on 21/8/18.
 */
public class InvoiceDiscrepancyItemDataReversePopulator
        implements Populator<SABMInvoiceItemData, InvoiceDiscrepancyRequestItemDetailModel> {

    private static final Logger LOG = Logger.getLogger(InvoiceDiscrepancyRequestItemDetailModel.class);

    /**
     * Populate the target instance with values from the source instance.
     *
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException if an error occurs
     */
    @Override
    public void populate(SABMInvoiceItemData source, InvoiceDiscrepancyRequestItemDetailModel target) throws ConversionException {

        try {
            target.setItemDescriptionLine1(source.getItemDescriptionLine1());
            target.setItemDescriptionLine2(source.getItemDescriptionLine2());

            // round to only two decimal for both received amount and expected amount

            if (source.getDiscountExpected() != null) {
                String discountExpected = SabmNumberUtils.formattingDouble(Double.valueOf(source.getDiscountExpected()));
                target.setDiscountExpected(Double.valueOf(discountExpected));
            } else {
                target.setDiscountExpected(null);
            }

            if (source.getDiscountReceived() != null) {
                String discountReceived = SabmNumberUtils.formattingDouble(Double.valueOf(source.getDiscountReceived()));

                target.setDiscountReceived(Double.valueOf(discountReceived));

            } else {
                target.setDiscountReceived(null);
            }

            target.setDiscountReceived(source.getDiscountReceived() != null ? Double.valueOf(source.getDiscountReceived()) : null);

            target.setSkuCode(source.getMaterial());

            target.setQuantity(source.getQuantity() != null ? source.getQuantity() : null);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
