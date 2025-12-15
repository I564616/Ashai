package com.sabmiller.core.invoices.services;

import com.sabmiller.core.model.InvoiceDiscrepancyRequestModel;
import de.hybris.platform.b2b.model.B2BUnitModel;

import java.util.Date;
import java.util.List;

/**
 * Created by zhuo.a.jiang on 27/8/18.
 */
public interface SabmInvoiceService {

    List<InvoiceDiscrepancyRequestModel> getRaisedInvoiceDiscrepancyForB2BUnit(final List<B2BUnitModel> b2bUnits);

    List<InvoiceDiscrepancyRequestModel> getRaisedInvoiceDiscrepancyForB2BUnitsAndForDateRange(final List<B2BUnitModel> b2bUnits,
            final Date dateFrom, final Date dateTo);

    List<InvoiceDiscrepancyRequestModel> findRaisedInvoiceDiscrepancyByInvoiceNumber(final String invoiceNumber);

    List<InvoiceDiscrepancyRequestModel> findRaisedInvoiceDiscrepancyByInvoiceNumberAndSapInvoiceNumber(final String invoiceNumber,
            final String sapInvoiceNumber);

    List<InvoiceDiscrepancyRequestModel> findRaisedInvoiceDiscrepancyByInvoiceNumberAndRequestId(final String invoiceNumber,final String requestId ) ;

    }
