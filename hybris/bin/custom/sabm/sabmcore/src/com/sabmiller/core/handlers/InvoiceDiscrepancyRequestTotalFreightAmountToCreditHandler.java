/**
 *
 */
package com.sabmiller.core.handlers;

import com.sabmiller.core.enums.InvoiceDiscrepancyType;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvoiceDiscrepancyRequestTotalFreightAmountToCreditHandler
        implements DynamicAttributeHandler<Double, InvoiceDiscrepancyRequestModel> {

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(InvoiceDiscrepancyRequestTotalFreightAmountToCreditHandler.class);

    @Override
    public Double get(InvoiceDiscrepancyRequestModel model) {

        double amountToCredit = 0.0;

        if (model.getType() != null && model.getType().equals(InvoiceDiscrepancyType.FREIGHT)) {

            amountToCredit = model.getTotalFreightDiscountCharged() - model.getTotalFreightDiscountExpected();

        }
        return amountToCredit;
    }

    @Override
    public void set(InvoiceDiscrepancyRequestModel model, Double d) {
        throw new UnsupportedOperationException("Set of dynamic attribute 'plantId' of InvoiceDiscrepancyRequestModel is disabled!");
    }
}
