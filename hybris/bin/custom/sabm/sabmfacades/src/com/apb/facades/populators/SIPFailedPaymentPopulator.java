package com.apb.facades.populators;

import com.sabmiller.core.model.AsahiSAMInvoiceModel;
import com.sabmiller.core.model.SIPFailedPaymentModel;
import com.apb.facades.sam.data.AsahiSAMInvoiceData;
import com.apb.facades.sam.data.AsahiSAMPaymentData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class SIPFailedPaymentPopulator implements Populator<SIPFailedPaymentModel, AsahiSAMPaymentData> {
    @Autowired
    private EnumerationService enumerationService;

    @Override
    public void populate(SIPFailedPaymentModel source, AsahiSAMPaymentData target) throws ConversionException {
        List<AsahiSAMInvoiceData> invoices = new ArrayList<>();
        if(null != source.getPaymentReference()){
            target.setPaymentReference(source.getPaymentReference());
        }
        if(null != source.getPartialPaymentReason()){
            target.setPartialPaymentReason(source.getPartialPaymentReason());
        }
        if(null != source.getTotalAmount()){
            target.setTotalAmount(source.getTotalAmount());
        }
        if(null != source.getPaymentTransactionId()){
            target.setPaymentTransactionId(source.getPaymentTransactionId());
        }
        if(null != source.getTransactionDate()){
            target.getTransactionDate();
        }
        if (CollectionUtils.isNotEmpty(source.getInvoices()))
        {
            for (final AsahiSAMInvoiceModel invoice : source.getInvoices())
            {
                final AsahiSAMInvoiceData invoiceData = new AsahiSAMInvoiceData();
                invoiceData.setDocumentNumber(invoice.getDocumentNumber());
                invoiceData.setLineNumber(invoice.getLineNumber());
                invoiceData.setDocumentType(invoice.getDocumentType().getCode());
                invoiceData.setRemainingAmount(invoice.getRemainingAmount());
                invoiceData.setTotalPaidAmount(invoice.getPaidAmount());
                invoiceData.setDeliveryNumber(invoice.getDeliveryNumber());
                invoices.add(invoiceData);
            }
        }
        target.setInvoice(invoices);
    }
}

