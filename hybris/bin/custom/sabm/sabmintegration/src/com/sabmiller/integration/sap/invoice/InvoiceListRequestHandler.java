package com.sabmiller.integration.sap.invoice;

import com.sabmiller.integration.restclient.commons.DefaultSABMPostRestRequestHandler;
import com.sabmiller.integration.restclient.commons.SABMIntegrationException;
import com.sabmiller.integration.sap.invoices.discrepancy.request.InvoiceListRequest;
import com.sabmiller.integration.sap.invoices.discrepancy.response.InvoiceListResponse;

/**
 * Created by zhuo.a.jiang on 7/8/18.
 */
public class InvoiceListRequestHandler
        extends DefaultSABMPostRestRequestHandler<InvoiceListRequest, InvoiceListResponse> {

    @Override
    public InvoiceListResponse sendPostRequest(final InvoiceListRequest request) throws SABMIntegrationException {

        if (this.getStubWebServiceEnabled()) {

            InvoiceListResponse response = new InvoiceListResponse();

            InvoiceListResponse.Invoice invoice = new InvoiceListResponse.Invoice();

            invoice.setSoldTo("0000851221");

            invoice.setDateStart("01082018");
            invoice.setDateEnd("03082018");

            invoice.getInvoiceNumber().add("7502707688");
            invoice.getInvoiceNumber().add("7502707685");
            invoice.getInvoiceNumber().add("7502693638");

            invoice.getInvoiceNumber().add("11111111111");
            invoice.getInvoiceNumber().add("11111111112");
            invoice.getInvoiceNumber().add("11111111113");
            invoice.getInvoiceNumber().add("11111111114");
            invoice.getInvoiceNumber().add("11111111115");
            invoice.getInvoiceNumber().add("11111111116");
            invoice.getInvoiceNumber().add("11111111117");
            invoice.getInvoiceNumber().add("11111111118");
            invoice.getInvoiceNumber().add("11111111119");
            invoice.getInvoiceNumber().add("11111111110");
            invoice.getInvoiceNumber().add("11111111111");
            invoice.getInvoiceNumber().add("11111111112");

            response.setInvoice(invoice);
            return response;

        }

        return super.sendPostRequest(request);
    }

}


