package com.sabmiller.webservice.creditadjustment.converters.populator;

import com.sabmiller.core.enums.InvoiceDiscrepancyProcessResultEnum;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.facades.invoice.SABMInvoiceDiscrepancyData;
import com.sabmiller.webservice.creditadjustment.CreditAdjustment;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;

/**
 * Created by zhuo.a.jiang on 27/8/18.
 */
public class CreditAdjustmentWsPopulator  implements Populator<CreditAdjustment.Invoice, SABMInvoiceDiscrepancyData> {

    private static final String CREDITADJUSTMENT_STATUS_APPROVED = "Approved";

    private static final String SEPARATOR = "|";

    /**
     * Populate the target instance with values from the source instance.
     *
     * @param source                        the source object
     * @param target the target to fill
     * @throws ConversionException if an error occurs
     */

    @Override
    public void populate(CreditAdjustment.Invoice source, SABMInvoiceDiscrepancyData target)
            throws ConversionException {

        /**
         * Set approved doesn't mean anything, just for code purpose, it has to match invoice number & exact amount
         */
        if(CREDITADJUSTMENT_STATUS_APPROVED.equalsIgnoreCase(StringUtils.trim(source.getStatus()))){
            target.setCreditAdjustmentStatus("Approved");
        }

        target.setActualTotalAmount(StringUtils.trim(source.getAmount()));
        target.setSoldTo(SabmStringUtils.addLeadingZeroes(StringUtils.trim(source.getSoldTo())));
        target.setInvoiceDate(StringUtils.trim(source.getPaymentDate()));
        target.setSapInvoiceNumber (StringUtils.trim(source.getInvoiceNumber()));
        target.setInvoiceType(StringUtils.trim(source.getType()));
        /* purchase Order number will be format like

        75022728612|8796093309393| Approved for credit adjustment
        */

       if(StringUtils.isNotEmpty(source.getPurchaseOrderNumber())){

           if(source.getPurchaseOrderNumber().contains(SEPARATOR)) {


               // don't use trim method , it doesn't remove any space in between of String
               final String[] array = StringUtils.split(StringUtils.trim(source.getPurchaseOrderNumber()),SEPARATOR);

               int arraySize = array.length;
               String invoiceNumber = null;
               String requestId =null;
               String processResultDescription = null;

               switch (arraySize) {
                   case 2:

                       invoiceNumber = array[0];
                        requestId = array[1];

                       break;
                   case 3:
                       invoiceNumber = array[0];
                        requestId = array[1];
                       processResultDescription = array[2];

                       break;
               }


               target.setInvoiceNumber(StringUtils.trim(invoiceNumber));
               target.setCreditAdjustmentRequestId(StringUtils.trim(requestId));
               target.setCreditAdjustmentStatusDescription(processResultDescription);
           }

           else {
               target.setInvoiceNumber(StringUtils.trim(source.getPurchaseOrderNumber()));
           }

        }


    }
}
