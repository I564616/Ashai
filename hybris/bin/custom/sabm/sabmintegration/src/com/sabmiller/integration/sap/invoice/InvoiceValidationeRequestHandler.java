package com.sabmiller.integration.sap.invoice;

import com.sabmiller.integration.restclient.commons.DefaultSABMPostRestRequestHandler;
import com.sabmiller.integration.restclient.commons.SABMIntegrationException;
import com.sabmiller.integration.sap.invoices.discrepancy.request.InvoiceValidationRequest;
import com.sabmiller.integration.sap.invoices.discrepancy.response.InvoiceValidationResponse;

/**
 * Created by zhuo.a.jiang on 7/8/18.
 */
public class InvoiceValidationeRequestHandler
        extends DefaultSABMPostRestRequestHandler<InvoiceValidationRequest, InvoiceValidationResponse> {

    @Override
    public InvoiceValidationResponse sendPostRequest(final InvoiceValidationRequest request) throws SABMIntegrationException {

        if (this.getStubWebServiceEnabled()) {

            InvoiceValidationResponse response = new InvoiceValidationResponse();

            InvoiceValidationResponse.Invoice invoice = new InvoiceValidationResponse.Invoice();

            invoice.setSoldTo("0000851221");
            invoice.setInvoiceNumber("7502707688");
            invoice.setStatus("true");

            response.setInvoice(invoice);

            return response;

        }

        return super.sendPostRequest(request);
    }

}


