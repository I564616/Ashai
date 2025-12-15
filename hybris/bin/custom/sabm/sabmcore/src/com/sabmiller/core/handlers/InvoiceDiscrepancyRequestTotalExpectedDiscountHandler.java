/**
 *
 */
package com.sabmiller.core.handlers;

import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.enums.InvoiceDiscrepancyType;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestItemDetailModel;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestModel;

public class InvoiceDiscrepancyRequestTotalExpectedDiscountHandler
        implements DynamicAttributeHandler<Double, InvoiceDiscrepancyRequestModel> {

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(InvoiceDiscrepancyRequestTotalExpectedDiscountHandler.class);


    @Override
    public Double get(InvoiceDiscrepancyRequestModel model) {

        double totalAmountExpected = 0.0;

        if (model.getType() != null && model.getType().equals(InvoiceDiscrepancyType.PRICE)) {
            if (CollectionUtils.isNotEmpty(model.getItems())) {

                for (InvoiceDiscrepancyRequestItemDetailModel itemDetail : model.getItems()) {

                    totalAmountExpected = totalAmountExpected + ((itemDetail.getDiscountExpected()-itemDetail.getDiscountReceived()) * Double.valueOf(itemDetail.getQuantity()));

                }

            }
        }

        return totalAmountExpected;
    }

    @Override
    public void set(InvoiceDiscrepancyRequestModel model, Double d) {
        throw new UnsupportedOperationException("Set of dynamic attribute 'plantId' of InvoiceDiscrepancyRequestModel is disabled!");
    }
}
