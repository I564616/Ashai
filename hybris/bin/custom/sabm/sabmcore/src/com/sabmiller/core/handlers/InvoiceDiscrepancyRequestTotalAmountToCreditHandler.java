/**
 *
 */
package com.sabmiller.core.handlers;

import com.sabmiller.core.enums.InvoiceDiscrepancyType;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestItemDetailModel;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvoiceDiscrepancyRequestTotalAmountToCreditHandler
        implements DynamicAttributeHandler<Double, InvoiceDiscrepancyRequestModel> {

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(InvoiceDiscrepancyRequestTotalAmountToCreditHandler.class);

    @Override
    public Double get(InvoiceDiscrepancyRequestModel model) {

        double amountToCredit = 0.0;

        double totalDiscountExpected = 0.0;

        double totalAmountToReceive = 0.0;

        if (model.getType() != null && model.getType().equals(InvoiceDiscrepancyType.PRICE)) {
            if (CollectionUtils.isNotEmpty(model.getItems())) {

                for (InvoiceDiscrepancyRequestItemDetailModel itemDetail : model.getItems()) {

                    totalDiscountExpected = totalDiscountExpected + itemDetail.getDiscountExpected() *  Double.valueOf(itemDetail.getQuantity());

                    totalAmountToReceive = totalAmountToReceive + itemDetail.getDiscountReceived() *  Double.valueOf(itemDetail.getQuantity());
                }

            }
        }

        amountToCredit  =  totalDiscountExpected - totalAmountToReceive;

        return amountToCredit;
    }

    @Override
    public void set(InvoiceDiscrepancyRequestModel model, Double d) {
        throw new UnsupportedOperationException("Set of dynamic attribute 'plantId' of InvoiceDiscrepancyRequestModel is disabled!");
    }
}
