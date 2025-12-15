package com.sabmiller.integration.sap.invoice;

import com.sabmiller.integration.restclient.commons.DefaultSABMPostRestRequestHandler;
import com.sabmiller.integration.restclient.commons.SABMIntegrationException;
import com.sabmiller.integration.sap.invoices.discrepancy.request.InvoiceItemDataRequest;
import com.sabmiller.integration.sap.invoices.discrepancy.response.InvoiceItemDataResponse;

import java.util.List;

/**
 * Created by zhuo.a.jiang on 7/8/18.
 */
public class InvoiceItemDataRequestHandler
        extends DefaultSABMPostRestRequestHandler<InvoiceItemDataRequest, InvoiceItemDataResponse> {

    @Override
    public InvoiceItemDataResponse sendPostRequest(final InvoiceItemDataRequest request) throws SABMIntegrationException {

        if (this.getStubWebServiceEnabled()) {

            InvoiceItemDataResponse response = new InvoiceItemDataResponse();

            InvoiceItemDataResponse.Invoice invoice = new InvoiceItemDataResponse.Invoice();

            invoice.setSoldTo("0000851221");
            invoice.setInvoiceNumber("7502707688");
            invoice.setInvoiceDate("07092018");
            List<InvoiceItemDataResponse.Invoice.Item> list = invoice.getItem();


            InvoiceItemDataResponse.Invoice.Item invoiceItemData1 = new InvoiceItemDataResponse.Invoice.Item();
            invoiceItemData1.setItemID("1");
            invoiceItemData1.setItemDescription("BR Carlton Mid CAN | 375ML 1x30");
            invoiceItemData1.setMaterial("9320000022064");
            invoiceItemData1.setQuantity("3.000");
            invoiceItemData1.setUoM("CAS");
            invoiceItemData1.setUnitPrice("245.65");
            invoiceItemData1.setDiscount("18.80-");
            invoiceItemData1.setAmount("254.65");
            invoiceItemData1.setContainerDeposit("18.90");
            invoiceItemData1.setWet(null);
            invoiceItemData1.setLocalFreight("11.40");
            invoiceItemData1.setGST("Y");
            invoiceItemData1.setTotalExGST("275.95");
            invoiceItemData1.setLUCExGST("55.19");
            invoiceItemData1.setEan("000000000000087923");
            list.add(invoiceItemData1);


            InvoiceItemDataResponse.Invoice.Item invoiceItemData2 = new InvoiceItemDataResponse.Invoice.Item();
            invoiceItemData2.setItemID("2");
            invoiceItemData2.setItemDescription("CE Mercury Hard CAN 375ML 3x10");

            invoiceItemData2.setMaterial("9320000090292");
            invoiceItemData2.setQuantity("5.000");
            invoiceItemData2.setUoM("CAS");
            invoiceItemData2.setUnitPrice("43.50");
            invoiceItemData2.setDiscount("26.68-");
            invoiceItemData2.setAmount("130.50");
            invoiceItemData2.setContainerDeposit("11.34");
            invoiceItemData2.setWet("41.13");
            invoiceItemData2.setLocalFreight("2.30");
            invoiceItemData2.setGST("Y");
            invoiceItemData2.setTotalExGST("184.20");
            invoiceItemData2.setLUCExGST("61.40");
            invoiceItemData2.setEan("000000000000087940");
            list.add(invoiceItemData2);


            InvoiceItemDataResponse.Invoice.Item invoiceItemData3 = new InvoiceItemDataResponse.Invoice.Item();
            invoiceItemData3.setItemID("3");
            invoiceItemData3.setItemDescription("Pure Blond  275ML 3x10");

            invoiceItemData3.setMaterial("9320000090299");
            invoiceItemData3.setQuantity("7.000");
            invoiceItemData3.setUoM("CAS");
            invoiceItemData3.setUnitPrice("43.50");
            // invoiceItemData2.setDiscount("26.68-");
            invoiceItemData3.setAmount("210.00");
            // invoiceItemData2.setContainerDeposit("11.34");
            // invoiceItemData2.setWet("41.13");
            invoiceItemData3.setLocalFreight("5.05");
            invoiceItemData3.setGST("Y");
            invoiceItemData3.setTotalExGST("215.05");
            invoiceItemData3.setLUCExGST("43.01");
            invoiceItemData3.setEan("000000000000087945");
            list.add(invoiceItemData3);


            response.setInvoice(invoice);


            return response;

        }

        return super.sendPostRequest(request);
    }

}


