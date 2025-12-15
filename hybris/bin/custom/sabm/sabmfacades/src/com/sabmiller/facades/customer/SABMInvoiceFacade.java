/**
 *
 */
package com.sabmiller.facades.customer;

import java.util.Date;
import java.util.List;

import com.sabmiller.facades.invoice.SABMInvoiceDiscrepancyData;
import com.sabmiller.facades.invoice.SABMInvoiceList;
import com.sabmiller.facades.invoice.SABMInvoiceValidationResult;

/**
 * The Interface SABMInvoiceFacade.
 */
public interface SABMInvoiceFacade {

    void sendPaymentConfirmationEmail(String trackingNumber);

    SABMInvoiceValidationResult validateInvoice(String b2bUnit, String invoiceNumber);

    SABMInvoiceList fetchInvoices(final String forUnit);

    SABMInvoiceDiscrepancyData getInvoiceData(String b2bUnit, String invoiceNumber);

    List<SABMInvoiceDiscrepancyData> fetchRaisedInvoicesForSelectedB2BUnit(final List<String>  b2bUnits, final Date dateFrom, final Date dateTo);

    boolean saveInvoiceDiscrepancyRequest(final SABMInvoiceDiscrepancyData request) ;

    void updateInvoiceDiscrepancyRequestWithProcessResult(final SABMInvoiceDiscrepancyData data) throws InvoiceUpdateException  ;
	
	public double getSurchargeAmtforInvoiceByTrackingNumber(String trackingNumber);
}
