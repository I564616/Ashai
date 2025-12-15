package com.sabmiller.merchantsuiteservices.populator;

import de.hybris.platform.commercefacades.order.converters.populator.CreditCardPaymentInfoPopulator;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.collections4.CollectionUtils;

import com.sabmiller.core.model.InvoicePaymentModel;
import com.sabmiller.facades.payment.DebitPaymentData;
import com.sabmiller.merchantsuiteservices.data.InvoicePaymentData;

public class InvoicePaymentPopulator implements Populator<InvoicePaymentModel, InvoicePaymentData>
{

    private CreditCardPaymentInfoPopulator infoDataPopulator;
    private DebitPaymentInfoPopulator debitPaymentInfoPopulator;

    @Override
    public void populate(final InvoicePaymentModel invoicePaymentModel, final InvoicePaymentData invoicePaymentData) throws ConversionException
    {
        invoicePaymentData.setAmount(invoicePaymentModel.getAmount());
        invoicePaymentData.setInvoices(invoicePaymentModel.getInvoices());

        if (invoicePaymentModel.getPaymentInfo() != null && invoicePaymentModel.getPaymentInfo() instanceof CreditCardPaymentInfoModel)
        {
            final CCPaymentInfoData paymentInfoData = new CCPaymentInfoData();
            infoDataPopulator.populate((CreditCardPaymentInfoModel) invoicePaymentModel.getPaymentInfo(), paymentInfoData);
            invoicePaymentData.setPaymentInfo(paymentInfoData);
        } else if (invoicePaymentModel.getPaymentInfo() != null && invoicePaymentModel.getPaymentInfo() instanceof DebitPaymentInfoModel)
        {
            final DebitPaymentData paymentInfoData = new DebitPaymentData();
            debitPaymentInfoPopulator.populate((DebitPaymentInfoModel) invoicePaymentModel.getPaymentInfo(), paymentInfoData);
            invoicePaymentData.setDebitInfo(paymentInfoData);
        }

        if (invoicePaymentModel.getTransaction() != null
                && CollectionUtils.isNotEmpty(invoicePaymentModel.getTransaction().getEntries()))
        {
            final PaymentTransactionEntryModel entry = invoicePaymentModel.getTransaction().getEntries().get(0);
            invoicePaymentData.setReceiptNumber(entry.getRequestId());
        }

        if (invoicePaymentModel.getCurrency() != null)
        {
            final CurrencyData currencyData = new CurrencyData();
            currencyData.setIsocode(invoicePaymentModel.getCurrency().getIsocode());
            currencyData.setSymbol(invoicePaymentModel.getCurrency().getSymbol());
            currencyData.setName(invoicePaymentModel.getCurrency().getName());
            invoicePaymentData.setCurrency(currencyData);
        }
    }

    public void setInfoDataPopulator(final CreditCardPaymentInfoPopulator infoDataPopulator)
    {
        this.infoDataPopulator = infoDataPopulator;
    }

    public void setDebitPaymentInfoPopulator(final DebitPaymentInfoPopulator debitPaymentInfoPopulator)
    {
        this.debitPaymentInfoPopulator = debitPaymentInfoPopulator;
    }
}