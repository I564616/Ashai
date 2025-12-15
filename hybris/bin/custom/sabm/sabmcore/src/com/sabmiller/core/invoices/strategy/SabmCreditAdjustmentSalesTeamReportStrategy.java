package com.sabmiller.core.invoices.strategy;

import com.sabmiller.core.model.InvoiceDiscrepancyRequestModel;

import java.io.File;

/**
 * Created by zhuo.a.jiang on 10/9/18.
 */
public interface SabmCreditAdjustmentSalesTeamReportStrategy {

     File getEmailData(final InvoiceDiscrepancyRequestModel invoiceDiscrepancyRequestModel);
}
