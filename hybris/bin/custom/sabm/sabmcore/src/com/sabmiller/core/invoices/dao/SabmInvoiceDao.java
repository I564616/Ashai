package com.sabmiller.core.invoices.dao;

import com.sabmiller.core.model.InvoiceDiscrepancyRequestModel;
import de.hybris.platform.b2b.model.B2BUnitModel;

import java.util.Date;
import java.util.List;

/**
 * Created by zhuo.a.jiang on 27/8/18.
 */
public interface SabmInvoiceDao {

    List<InvoiceDiscrepancyRequestModel> getRaisedInvoiceDiscrepancy(B2BUnitModel b2bUnit);

    List<InvoiceDiscrepancyRequestModel> getAllRaisedInvoiceDiscrepancy();

    List<InvoiceDiscrepancyRequestModel> getRaisedInvoiceDiscrepancyByDateRange(final B2BUnitModel b2bUnit, final Date dateFrom,
            final Date dateTo);

    List<InvoiceDiscrepancyRequestModel> findRaisedInvoiceDiscrepancyByInvoiceNumberAndRequestId(final String invoiceNumber,
            final String requestId);

    List<InvoiceDiscrepancyRequestModel> findRaisedInvoiceDiscrepancyByInvoiceNumberAndSapInvoiceNumber(final String invoiceNumber,
            final String sapInvoiceNumber);

}