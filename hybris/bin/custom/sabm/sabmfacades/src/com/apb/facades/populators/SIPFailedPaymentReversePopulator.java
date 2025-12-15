package com.apb.facades.populators;

import com.apb.core.dao.sam.invoice.AsahiSAMInvoiceDao;
import com.sabmiller.core.model.AsahiSAMInvoiceModel;
import com.sabmiller.core.model.SIPFailedPaymentModel;
import com.apb.facades.sam.data.AsahiSAMPaymentData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class SIPFailedPaymentReversePopulator implements Populator<AsahiSAMPaymentData, SIPFailedPaymentModel>{
    @Resource
    private AsahiSAMInvoiceDao asahiSAMInvoiceDao;

    @Override
    public void populate(AsahiSAMPaymentData source, SIPFailedPaymentModel target) throws ConversionException {
        List<AsahiSAMInvoiceModel> invoices = new ArrayList<>();
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
        if(!CollectionUtils.isEmpty(source.getInvoice())){
            source.getInvoice().stream().forEach(invoice ->
                    {
                        invoices.add(asahiSAMInvoiceDao.getInvoiceByDocumentNumber(invoice.getDocumentNumber(), invoice.getLineNumber()));
                    }
            );
        }
        target.setInvoices(invoices);
    }
}
