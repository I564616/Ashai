package com.sabmiller.webservice.customer.helper;

import com.sabmiller.webservice.customer.Customer;
import de.hybris.platform.b2b.model.B2BUnitModel;

public interface DealAssigneeRecalculationStrategy {

    /**
     * Determines if a deal assignee recalculation is required based on the b2bUnit(current db) vs customer(new updates)
     * @param b2BUnit
     * @param customer
     * @return
     */
    boolean requiresDealAssigneeRecalculation(final B2BUnitModel b2BUnit, final Customer customer);

    void recalculateDealAssignees();

}
